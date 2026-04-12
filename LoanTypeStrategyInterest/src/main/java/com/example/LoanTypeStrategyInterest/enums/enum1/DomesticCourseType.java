package com.example.LoanTypeStrategyInterest.enums.enum1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DomesticCourseType implements CoursesDomain {

    // Professional Courses -----------------------------------------------------------
    MEDICAL("MBBS", "Bachelor of Medicine & Surgery (11-11.5%)"),
    LAW("LLB", "Bachelor of Law (11-11.5%)"),
    CA("CA", "Chartered Accountant (11.5-12%)"),
    CS("CS", "Company Secretary (11.5-12%)"),
    CMA("CMA", "Cost & Management Accountant (11.5-12%)"),
    ACTUARIAL("Actuarial", "Actuarial Science (11.5-12%)"),

    NURSING("Nursing", "Domestic Nursing programs (12-12.5%)"),
    PHARMACY("B.Pharm", "Bachelor of Pharmacy (12-12.5%)"),
    DENTAL("BDS", "Bachelor of Dental Surgery (12-12.5%)"),
    AYURVEDA("BAMS", "Bachelor of Ayurvedic Medicine & Surgery (12-12.5%)"),
    HOMEOPATHY("BHMS", "Bachelor of Homeopathy Medicine & Surgery (12-12.5%)"),
    ACCOUNTING("Accounting", "Domestic Accounting programs (12-12.5%)"),
    ENGINEERING("Engineering", "Engineering programs (12-12.5%)"),
    ARCHITECTURE("B.Arch", "Bachelor of Architecture (12-12.5%)"),

    MANAGEMENT("MBA", "Master of Business Administration (12.5-13%)"),
    EXEC_EDU("Exec Edu", "Executive education (12.5-13%)"),
    PROF_DEV("Prof Dev", "Professional development (12.5-13%)"),
    SHORT_TERM("Short-term", "Short-term professional courses (12.5-13%)"),
    CERTIFICATION("Certification", "Industry-specific certifications (12.5-13%)"),

    // Vocational/Skill Development --------------------------------------------------------
    AVIATION("Aviation", "Pilot training (12-12.5%)"),
    MARITIME("Maritime", "Maritime courses (12-12.5%)"),
    CULINARY("Culinary", "Culinary Arts (12-12.5%)"),
    FASHION("Fashion", "Fashion Design (12-12.5%)"),
    HOTEL_MANAGEMENT("Hotel Mgmt", "Hotel Management programs (12-12.5%)"),
    JOURNALISM("Journalism", "Mass Communication & Journalism (12-12.5%)"),
    TEACHER_EDUCATION("B.Ed", "Bachelor of Education (12-12.5%)"),

    TECH_BOOTCAMP("Tech Bootcamp", "Domestic tech bootcamps (13-13.5%)"),
    HEALTHCARE_CERT("Healthcare Cert", "Healthcare certifications (13-13.5%)"),
    HOSPITALITY("Hospitality", "Hospitality Management (13-13.5%)"),
    FILM_MEDIA("Film & Media", "Film and Media Production (13-13.5%)"),
    AGRICULTURE("B.Sc Agriculture", "Agricultural Science programs (13-13.5%)"),

    INTENSIVE("Intensive", "3-6 month intensive programs (13.5-14%)"),
    LANGUAGE_SKILL("Lang+Skill", "Language + skill combo programs (13.5-14%)"),
    INTERNSHIP("Internship Prep", "Internship preparation (13.5-14%)"),
    INDUSTRY_CERT("Industry Cert", "Industry-specific certifications (13.5-14%)");

    private final String displayName;
    private final String description;
}
