package com.example.educationloan.repository;

import com.example.educationloan.entity.User;
import com.example.educationloan.enumconstant.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Fetch user with roles eagerly — fixes LazyInitializationException
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);

    // Fixed — was findByRoles_Name which caused startup crash
    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") RoleEnum roleName);

    //Fixed — was auto-derived method that failed because User has no 'roles' field
    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.name = :roleName")
    List<User> findBySpecificRoleName(@Param("roleName") RoleEnum roleName);

    //Filter users by role and active status
    @Query("SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.name = :roleName AND u.isActive = :isActive")
    List<User> findByRoleAndStatus(@Param("roleName") RoleEnum roleName, @Param("isActive") Boolean isActive);

    @Query("SELECT u FROM User u WHERE u.id = :id AND LOWER(u.email) = LOWER(:email)")
    Optional<User> findByIdAndEmail(@Param("id") Long id, @Param("email") String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
}