package com.fastcam.programming.dmaker.entity;

import com.fastcam.programming.dmaker.dto.EditDeveloper;
import com.fastcam.programming.dmaker.type.DeveloperLevel;
import com.fastcam.programming.dmaker.type.DeveloperSkillType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DeveloperLevel developerLevel;

    @Enumerated(EnumType.STRING)
    private DeveloperSkillType developerSkillType;

    private Integer experienceYears;
    private String memberId;
    private String name;
    private int age;

    @Enumerated(EnumType.STRING)
    private StatusCode statusCode;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updateAt;

    public void update(EditDeveloper.Request request) {
        this.developerLevel = request.getDeveloperLevel();
        this.developerSkillType = request.getDeveloperSkillType();
        this.experienceYears = request.getExperienceYears();
    }

    public void statusChange() {
        this.statusCode = StatusCode.RETIRED;

    }

}
