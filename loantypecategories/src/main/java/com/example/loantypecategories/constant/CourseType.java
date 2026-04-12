package com.example.loantypecategories.constant;

import lombok.Getter;

@Getter
public enum CourseType {

    UNDERGRADUATE("Undergraduate", "Bachelor's degree programs"),
    POSTGRADUATE("Postgraduate", "Master's degree programs"),
    DOCTORAL_PHD("Doctoral/PhD", "Research doctorate programs"),
    VOCATIONAL_SKILL("Vocational/Skill Development", "Short-term skill & certification courses"),
    PROFESSIONAL("Professional", "MBA, Medical, Engineering and similar programs"),
    MBA("professional","master of the business administration");

    private final String displayName;
    private final String description;

    CourseType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }


}
