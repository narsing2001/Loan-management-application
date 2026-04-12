package com.example.LoanTypeStrategyInterest.enums.enum1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InternationalCourseType implements CoursesDomain{

    // International Professional Courses -----------------------------------------------------------
    USM_LE("USMLE", "US Medical Licensing Exam (12.5-13%)"),
    BAR("Bar", "International Bar Examination (12.5-13%)"),
    CPA("CPA US/UK", "Certified Public Accountant from US/UK (12.5-13%)"),
    ACTUARIAL("Actuarial Intl", "International Actuarial Science (12.5-13%)"),


    EXEC_EDU("Exec Edu", "Executive education abroad (13.5-14%)"),
    PROF_DEV("Prof Dev", "Professional development abroad (13.5-14%)"),
    SHORT_TERM("Short-term", "Short-term professional courses abroad (13.5-14%)"),
    CERTIFICATION("Cert Intl", "Industry-specific certifications abroad (13.5-14%)"),

    // International Vocational/Skill Development --------------------------------------------------------
    AVIATION("Aviation", "Pilot training abroad (13-13.5%)"),
    MARITIME("Maritime", "Maritime courses abroad (13-13.5%)"),

    TECH_BOOTCAMP("Tech Bootcamp", "International tech bootcamps (13.5-14%)"),
    HEALTHCARE_CERT("Healthcare Cert", "Healthcare certifications abroad (13.5-14%)"),
    HOSPITALITY("Hospitality", "Hospitality programs abroad (13.5-14%)"),


    INTENSIVE("Intensive", "3-6 month intensive programs abroad (14-14.5%)"),
    LANG_SKILL("Lang+Skill", "Language + skill combo programs abroad (14-14.5%)"),
    INTERNSHIP("Internship Prep", "International internship preparation (14-14.5%)"),
    INDUSTRY_CERT("Industry Cert", "Industry-specific certifications abroad (14-14.5%)"),

    // STEM Programs -----------------------------------------------------------
    COMPUTER_SCIENCE("CS", "Computer Science & IT (12.5-13%)"),
    DATA_SCIENCE("Data Science", "Data Science & Analytics (12.5-13%)"),
    CYBERSECURITY("Cybersecurity", "Cybersecurity programs (12.5-13%)"),
    ENGINEERING("Engineering", "Mechanical, Civil, Electrical, Aerospace Engineering (12.5-13%)"),

    // Business & Management ---------------------------------------------------
    MBA("MBA", "Master of Business Administration (13-13.5%)"),
    FINANCE("Finance", "Finance & Investment programs (13-13.5%)"),
    ACCOUNTING("Accounting", "CPA/CFA Accounting programs (13-13.5%)"),
    ENTREPRENEURSHIP("Entrepreneurship", "Innovation & Startup programs (13-13.5%)"),

    // Healthcare --------------------------------------------------------------
    MEDICINE("MD/USMLE", "Medical Doctor & USMLE prep (13.5-14%)"),
    NURSING("Nursing", "BSN/MSN Nursing programs (13.5-14%)"),
    PHARMACY("Pharm.D", "Doctor of Pharmacy (13.5-14%)"),
    PUBLIC_HEALTH("MPH", "Master of Public Health (13.5-14%)"),

    // Arts, Media & Design ----------------------------------------------------
    FILM_MEDIA("Film & Media", "Film and Media Production (13.5-14%)"),
    FASHION("Fashion Design", "Fashion Design programs (13.5-14%)"),
    CULINARY("Culinary Arts", "Culinary Arts programs (13.5-14%)"),
    GRAPHIC_DESIGN("Graphic Design", "Digital & Visual Design programs (13.5-14%)"),

    // Short-Term Certificates -------------------------------------------------
    PROJECT_MANAGEMENT("Project Mgmt", "Project Management certifications (14-14.5%)"),
    DIGITAL_MARKETING("Digital Marketing", "Digital Marketing certifications (14-14.5%)"),
    AI_MACHINE_LEARNING("AI/ML", "Artificial Intelligence & Machine Learning (14-14.5%)"),
    CLOUD_COMPUTING("Cloud Computing", "Cloud & DevOps certifications (14-14.5%)");

    private final String displayName;
    private final String description;
}
