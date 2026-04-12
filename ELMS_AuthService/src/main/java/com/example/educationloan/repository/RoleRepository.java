package com.example.educationloan.repository;

import com.example.educationloan.entity.Role;
import com.example.educationloan.entity.User;
import com.example.educationloan.entity.UserRole;
import com.example.educationloan.enumconstant.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleEnum name);


    @Query(value = "SELECT ur.role FROM User u JOIN u.userRoles ur WHERE u.id = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);

    List<UserRole> findByRoleId(Long roleId);
    Optional<Role> findById(Long id);


}


