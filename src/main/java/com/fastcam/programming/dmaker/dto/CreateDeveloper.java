package com.fastcam.programming.dmaker.dto;

import com.fastcam.programming.dmaker.entity.Developer;
import com.fastcam.programming.dmaker.entity.StatusCode;
import com.fastcam.programming.dmaker.type.DeveloperLevel;
import com.fastcam.programming.dmaker.type.DeveloperSkillType;
import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateDeveloper {
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {
        @NotNull
        private DeveloperLevel developerLevel;
        @NotNull
        private DeveloperSkillType developerSkillType;
        @NotNull
        @Min(0)
        @Max(20)
        private Integer experienceYears;
        @NotNull
        @Size(min=3, max = 50, message = "memberId size must 3~50")
        private String memberId;
        @NotNull
        @Size(min= 3, max = 20, message = "name size must 3~20")
        private String name;
        private Integer age;
        @NotNull
        private StatusCode statusCode;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private DeveloperLevel developerLevel;
        private DeveloperSkillType developerSkillType;
        private Integer experienceYear;
        private String memberId;

        public static Response fromEntity(Developer developer) {
            return Response.builder()
                    .developerLevel(developer.getDeveloperLevel())
                    .developerSkillType(developer.getDeveloperSkillType())
                    .experienceYear(developer.getExperienceYears())
                    .memberId(developer.getMemberId())
                    .build();
        }
    }
}
