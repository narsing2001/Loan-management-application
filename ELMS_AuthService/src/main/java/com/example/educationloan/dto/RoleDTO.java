package com.example.educationloan.dto;


import com.example.educationloan.entity.Role;
import com.example.educationloan.enumconstant.RoleEnum;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {

    private Long id;
    private RoleEnum name;
    public RoleDTO toRoleDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getRoleId())
                .name(role.getName())
                .build();
    }

}