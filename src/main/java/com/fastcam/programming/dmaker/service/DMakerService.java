package com.fastcam.programming.dmaker.service;

import com.fastcam.programming.dmaker.dto.CreateDeveloper;
import com.fastcam.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcam.programming.dmaker.dto.DeveloperDto;
import com.fastcam.programming.dmaker.dto.EditDeveloper;
import com.fastcam.programming.dmaker.entity.Developer;
import com.fastcam.programming.dmaker.entity.RetiredDeveloper;
import com.fastcam.programming.dmaker.entity.StatusCode;
import com.fastcam.programming.dmaker.exception.DMakerException;
import com.fastcam.programming.dmaker.repository.DeveloperRepository;
import com.fastcam.programming.dmaker.repository.RetiredRepository;
import com.fastcam.programming.dmaker.type.DeveloperLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.fastcam.programming.dmaker.exception.DMakerErrorCode.*;

@Service
@RequiredArgsConstructor // DeveloperRepository를 injection
public class DMakerService {
    private final DeveloperRepository developerRepository;
    private final RetiredRepository retiredRepository;

    // ACID
    @Transactional
    public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request) {

        validateCreateDeveloperRequest(request);
        // business logic start
        Developer developer = Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYears())
                .memberId(request.getMemberId())
                .statusCode(StatusCode.EMPLOYED)
                .name(request.getName())
                .age(request.getAge())
                .build();
        developerRepository.save(developer);
        return CreateDeveloper.Response.fromEntity(developer);

    }
    @Transactional
    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(StatusCode.EMPLOYED)
                //developerRepository.findAll()
                .stream()
                //.filter(s -> s.getStatusCode().equals("EMPLOYED"))
                .map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }
    @Transactional
    public DeveloperDetailDto getDeveloperDetail(String memberId) {
        return developerRepository.findByMemberId(memberId)
                .map(DeveloperDetailDto::fromEntity)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));

    }
    @Transactional
    public DeveloperDetailDto editDeveloper(
            String memberId, EditDeveloper.Request request) {
        validateDeveloperLevel(request.getDeveloperLevel(),request.getExperienceYears());
        // 회원이 없다면 NO_DEVELOPER
         Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(()-> new DMakerException(NO_DEVELOPER));

         developer.update(request);
         return DeveloperDetailDto.fromEntity(developer);

    }

    @Transactional
    public DeveloperDetailDto deleteDeveloper(String memberId) {
        // 1. EMPLOYED -> RETIRED
        Developer developer = developerRepository.findByMemberId(memberId).orElseThrow(
                ()-> new DMakerException(NO_DEVELOPER));
        developer.statusChange();

        // 2. save into Retired
        RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
                .memberId(developer.getMemberId())
                .name(developer.getName())
                .build();
        retiredRepository.save(retiredDeveloper);
        return DeveloperDetailDto.fromEntity(developer);
    }





    private void validateCreateDeveloperRequest(CreateDeveloper.Request request) {
        // business validation
        validateDeveloperLevel(request.getDeveloperLevel(),
                request.getExperienceYears());

        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DMakerException(DUPLICATED_MEMBER_ID);
                }));

    }
    private void validateDeveloperLevel(DeveloperLevel developerLevel, int experienceYears) {
        if(developerLevel == DeveloperLevel.SENIOR
            && experienceYears < 10) {
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
        if(developerLevel == DeveloperLevel.JUNGNIOR && experienceYears > 4) {
            throw new DMakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
    }
}