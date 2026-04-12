package com.example.educationloan.service;

import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.enumconstant.RoleEnum;
import com.example.educationloan.exception.*;
import com.example.educationloan.repository.RoleRepository;
import com.example.educationloan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RoleService implements RoleInterface {

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final UserRepository userRepository;


    // Get role by name-------------------------------------------------------------

    @Transactional(readOnly = true)
    public Optional<Role> getByRoleName(RoleEnum roleName) {
        return roleRepository.findByName(roleName);
    }

    // Get all roles------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    //Get all roles assigned to specific user------------------------------------------

    @Transactional(readOnly = true)
    public List<Role> getRolesByUserId(Long userId) {
        User user = userService.getUserById(userId);
        return user.getUserRoles().stream().map(UserRole::getRole).toList();
    }

    //Create new role or get existing role------------------------------------------------

    @Transactional
    public Role createOrGetRole(RoleEnum name) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role newRole = Role.builder()
                               .name(name)
                               .build();
            Role savedRole = roleRepository.save(newRole);
            log.info("Created new role with name {} and id {}", savedRole.getName(), savedRole.getRoleId());
            return savedRole;
        });
    }

    // Remove a specific role from a user--------------------------------------------

    @Transactional
    public User removeRoleFromUser(Long userId, RoleEnum roleName) {

     //find role
        Role role=roleRepository.findByName(roleName).orElseThrow(() -> new ResourceNotFoundException("Role not found with name:" + roleName));
     // find user or throw
      User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found with this id:"+userId));
      //if user has at most 1 i.e= only one role don't remove, instead use update api
      if(user.getUserRoles().size()==1){
          throw new LastRoleException("Cannot remove the last role '" + roleName + "' from user with id: " + userId + ". User must have at least one role assigned.");
      }
      boolean isRoleAssigned=user.getUserRoles().stream().anyMatch(userRole -> userRole.getRole().getName().equals(roleName));
      if(!isRoleAssigned){
          throw new RoleNotAssignedException("Role:"+roleName+" is not assigned to user with id:"+userId);
      }
      //find the specific UserRole to remove or throw
        UserRole userRoleToRemove=user.getUserRoles().stream().filter(userRole -> userRole.getRole().equals(role))
                .findFirst().orElseThrow(() -> new RoleNotAssignedException("User with id " + userId + " does not have role: " + roleName));
      //remove the role from both side of the relationship
        user.getUserRoles().remove(userRoleToRemove);
        role.getUserRoles().remove(userRoleToRemove);
        User user1=userRepository.save(user);
        log.info("Role {} removed from user with id {}",roleName,userId);
     return  user1;
    }



    //Fetch all user who has role provided in the url path----------------------------------
    @Transactional(readOnly = true)
    public List<User> getUserWithRole(String roleName) {
        return userRepository.findByRoleName(RoleEnum.valueOf(roleName.toUpperCase()));
    }

    //Fetch all users with a specific role--------------------------------------------------------

    @Transactional(readOnly = true)
    public List<User> getUsersByRoleName(RoleEnum roleName) {
        return userRepository.findBySpecificRoleName(roleName);
    }

    //Update role of a specific user (replaces all existing roles)------------------------------

    @Transactional
    public User updateUserRole(Long userId, RoleEnum oldRole,RoleEnum newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

       Role currentRole=roleRepository.findByName(newRole)
               .orElseThrow(()->new ResourceNotFoundException("Role not found: "+newRole));

       /*
       *for updating specific role to the specific role,check if old role is actually assigned to the user
        */
       boolean isOldRoleAssigned=user.getUserRoles().stream()
               .anyMatch(userRole->userRole.getRole().getName().equals(oldRole));

       if(!isOldRoleAssigned){
           throw new RoleNotAssignedException("Role:"+oldRole+" is not assigned to user with id:"+userId);
       }
       //check if new role is already assigned to avoid duplicates
        boolean isNewRoleAlreadyAssign=user.getUserRoles().stream()
                .anyMatch(userRole -> userRole.getRole().getName().equals(newRole));

       if(isNewRoleAlreadyAssign){
           throw new RoleAlreadyAssignedException("Role:"+newRole+" is already assigned to user with id:"+userId);
       }
       //remove only the old role if present
        user.getUserRoles().removeIf(userRole -> userRole.getRole().getName().equals(oldRole));
       //add the new role
        user.addRole(currentRole,"system");
        return userRepository.save(user);
    }

}