package com.example.educationloan.service;

import com.example.educationloan.dto.RegisterDTO;
import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.exception.*;
import com.example.educationloan.repository.RoleRepository;
import com.example.educationloan.repository.UserRepository;
import com.example.educationloan.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserInterface {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();
    private String generateUsername(String firstName, String lastName) {
        String username;
        do {
            int randomNumber = random.nextInt(9000) + 1000;
            username = (firstName + "_" + lastName + "_" + randomNumber).toLowerCase();
        } while (userRepository.existsByUsername(username));
        return username;
    }

    @Transactional
    public User createUser( String email, String password, String firstName, String lastName) {
        // 2. Auto-generate unique username
        String username = generateUsername(firstName, lastName);
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateResourceException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists: " + email);
        }
        Role defaultRole = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found: " + RoleEnum.USER));
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true)
                .isEmailVerified(false)
                .build();

        user.addRole(defaultRole, "system");
        userRepository.save(user);
        log.info("User {} with email {} created successfully", username, email);

        return userRepository.findByIdWithRoles(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found after creation"));
    }


    // uses findByIdWithRoles to eagerly fetch userRoles — prevents LazyInitializationException
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("User with id {} deleted successfully", id);
            return true;
        }
        log.info("User with id {} does  not exists:",id);
        return false;
    }

    // fetches user with roles eagerly before calling toUserDTO()
    @Transactional
    public User assignRoleToUser(Long userId, RoleEnum roleName) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + roleName));

        boolean alreadyHasRole = user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRole().equals(role));

        if (!alreadyHasRole) {
            user.addRole(role, "system");
            userRepository.save(user);
            log.info("Role {} assigned to user {}", roleName, user.getUsername());
        } else {
            log.info("User {} already has role {}", user.getUsername(), roleName);
        }
        return userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }


    @Transactional
    public User updateUser(Long id, String username, String email, String password, String firstName, String lastName) {
        User user = getUserById(id);

        Optional.ofNullable(username).filter(u -> !u.isBlank()).ifPresent(uname -> {
            if (userRepository.existsByUsername(uname) && !user.getUsername().equals(uname)) {
                throw new RuntimeException("Username already exists: " + uname);
            }
            user.setUsername(uname);
        });

        Optional.ofNullable(email).filter(e -> !e.isBlank()).ifPresent(e -> {
            if (userRepository.existsByEmail(e) && !user.getEmail().equals(e)) {
                throw new RuntimeException("Email already exists: " + e);
            }
            user.setEmail(e);
        });

        Optional.ofNullable(password).filter(p -> !p.isBlank()).map(passwordEncoder::encode).ifPresent(user::setPassword);
        Optional.ofNullable(firstName).filter(fn -> !fn.isBlank()).ifPresent(user::setFirstName);
        Optional.ofNullable(lastName).filter(ln -> !ln.isBlank()).ifPresent(user::setLastName);
        userRepository.save(user);
        log.info("User {} updated successfully", user.getUsername());
        return user;
    }

    @Transactional
    public User patchUser(Long id, String username, String email, String password, String firstName, String lastName) {
        User existingUser = getUserById(id);
        Optional.ofNullable(username).ifPresent(existingUser::setUsername);
        Optional.ofNullable(email).ifPresent(existingUser::setEmail);
        Optional.ofNullable(password).filter(p -> !p.isBlank()).map(passwordEncoder::encode).ifPresent(existingUser::setPassword);
        Optional.ofNullable(firstName).ifPresent(existingUser::setFirstName);
        Optional.ofNullable(lastName).ifPresent(existingUser::setLastName);
        return userRepository.save(existingUser);
    }

    public Optional<User> getUserById1(Long userId) {
        return userRepository.findByIdWithRoles(userId);
    }

    // fetches user with roles eagerly before assigning multiple roles--------------------------------------------------
    @Transactional
    public User assignRolesUser1(Long userId, List<RoleEnum> roleNames) {
        User user = userRepository.findByIdWithRoles(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        List<RoleEnum> alreadyAssigned = new ArrayList<>();
        for (RoleEnum roleName : roleNames) {
            Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + roleName));
            boolean alreadyHasRole = user.getUserRoles().stream().anyMatch(userRole -> userRole.getRole().equals(role));
            if (!alreadyHasRole) {
                user.addRole(role, "system");
                log.info("Role {} assigned to user {}", roleName, user.getUsername());
            } else {
                alreadyAssigned.add(roleName);
            }
        }
        userRepository.save(user);
        User updatedUser = userRepository.findByIdWithRoles(userId).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        if (!alreadyAssigned.isEmpty()) {
            throw new RoleAlreadyAssignedException("User " + updatedUser.getUsername() +" with id:"+updatedUser.getId()+ " already has roles: " + alreadyAssigned);
        }

        return updatedUser;
    }



    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public List<User> filterUsersByRoleAndStatus(RoleEnum roleName, Boolean isActive) {
        return userRepository.findByRoleAndStatus(roleName, isActive);
    }

    @Transactional(readOnly = true)
    public boolean isUserAdmin(Long userId) {
        User user = getUserById(userId);
        return user.getUserRoles().stream().map(UserRole::getRole)
                .anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));
    }

    @Transactional(readOnly = true)
    public boolean isUserEmployee(Long userId) {
        User user = getUserById(userId);
        return user.getUserRoles().stream().map(UserRole::getRole)
                .anyMatch(role -> role.getName().equals(RoleEnum.EMPLOYEE));
    }

    @Transactional(readOnly = true)
    public boolean doesUserHaveRole(Long id, RoleEnum roleName) {
        User user = getUserById(id);
        if(user.getUserRoles().stream().noneMatch(role->role.getRole().getName().equals(roleName))){
            throw new RoleNotAssignedException("Role is not assigned to get user id with the particular role:"+id);
        }
        return user.getUserRoles().stream().map(UserRole::getRole)
                .anyMatch(role -> role.getName() == roleName);
    }

    @Transactional
    public void activateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(true);
        userRepository.save(user);
        log.info("User {} with email {} activated successfully", user.getUsername(), user.getEmail());
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = getUserById(id);
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User {} with email {} deactivated successfully", user.getUsername(), user.getEmail());
    }

    @Transactional
    public void verifyEmail(Long id,String email) {
        User user = userRepository.findByIdAndEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setIsEmailVerified(true);
        userRepository.save(user);
        log.info("User {} with emailId {} verified successfully", user.getUsername(), user.getEmail());
    }

    @Transactional
    public void updatePassword(Long id, String newPassword) {
        if(newPassword==null ||newPassword.isBlank()){
            throw new PasswordBlankException("password cannot be null or blank for updation");
        }
        User user = getUserById(id);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated successfully for user {} with email {}", user.getUsername(), user.getEmail());
    }

    @Transactional
    public void removeRoleFromUser(Long userId, RoleEnum roleName) {
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + roleName));

        UserRole userRoleToRemove = user.getUserRoles().stream()
                .filter(userRole -> userRole.getRole().equals(role))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " does not have role: " + roleName));

        user.getUserRoles().remove(userRoleToRemove);
        role.getUserRoles().remove(userRoleToRemove);
        userRepository.save(user);
        log.info("Role {} removed from user {}", roleName, user.getUsername());
    }


    public List<Role> getRolesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return roleRepository.findRolesByUserId(userId);
    }

    public Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId) .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
    }

    public void removeUserRole(Long userRoleId) {
        if (!userRoleRepository.existsById(userRoleId)) {
            throw new ResourceNotFoundException("UserRole not found with id: " + userRoleId);
        }
        userRoleRepository.deleteById(userRoleId);
    }

    public void removeUserRole1(Long userId, Long roleId) {
        UserRole userRole = userRoleRepository.findByUserIdAndRoleId(userId, roleId)
                .orElseThrow(() -> new ResourceNotFoundException("UserRole not found for userId: " + userId + " and roleId: " + roleId));
        userRoleRepository.delete(userRole);
    }


    @Transactional
    public User registerUser(RegisterDTO request) {
        return createUser(
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName()
        );
    }

}