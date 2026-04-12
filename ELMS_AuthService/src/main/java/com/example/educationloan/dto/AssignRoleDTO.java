package com.example.educationloan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleDTO {
    private Long userId;
    private Long roleId;
    private String assignedBy;
}
