package com.fastcam.programming.dmaker.service;

import com.fastcam.programming.dmaker.dto.CreateDeveloper;
import com.fastcam.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcam.programming.dmaker.dto.DeveloperDto;
import com.fastcam.programming.dmaker.dto.EditDeveloper;
import com.fastcam.programming.dmaker.entity.Developer;
import com.fastcam.programming.dmaker.entity.RetiredDeveloper;
import com.fastcam.programming.dmaker.code.StatusCode;
import com.fastcam.programming.dmaker.exception.DMakerException;
import com.fastcam.programming.dmaker.repository.DeveloperRepository;
import com.fastcam.programming.dmaker.repository.RetiredRepository;
import lombok.NonNull;
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
        // 저장한 developer결과를 response로
        return CreateDeveloper.Response.fromEntity(
                developerRepository.save(createDeveloperFromRequest(request)));
    }

    private Developer createDeveloperFromRequest(CreateDeveloper.Request request) {
        return Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYears())
                .memberId(request.getMemberId())
                .statusCode(StatusCode.EMPLOYED)
                .name(request.getName())
                .age(request.getAge())
                .build();
    }
    @Transactional(readOnly = true)
    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(StatusCode.EMPLOYED)
                //developerRepository.findAll()
                .stream()
                //.filter(s -> s.getStatusCode().equals("EMPLOYED"))
                .map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public DeveloperDetailDto getDeveloperDetail(String memberId) {
        return DeveloperDetailDto.fromEntity(getDeveloperByMemberId(memberId));

    }
    // get ~ 있어야 한다는 네이밍 컨벤션으로 만들어 사용하기도 한다.
    private Developer getDeveloperByMemberId(String memberId) {
        return developerRepository.findByMemberId(memberId)
                .orElseThrow(()-> new DMakerException(NO_DEVELOPER));
    }
    @Transactional
    public DeveloperDetailDto editDeveloper(
            String memberId, EditDeveloper.Request request) {
        request.getDeveloperLevel().validateExperienceYears(
                request.getExperienceYears());
        // 회원이 없다면 NO_DEVELOPER
         Developer developer = getDeveloperByMemberId(memberId);

         developer.update(request);


         return DeveloperDetailDto.fromEntity(developer);
         /*
         return DeveloperDetailDto.fromEntity(
                    getUpdatedDeveloperFromRequest(
                             request, getDeveloperByMemberId(memberId)
                    )
         );
     }
          */

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






    private void validateCreateDeveloperRequest(@NonNull CreateDeveloper.Request request) {
        request.getDeveloperLevel().validateExperienceYears(
                request.getExperienceYears()
        );

        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DMakerException(DUPLICATED_MEMBER_ID);
                }));
    }

        /*
    private Developer getUpdatedDeveloperFromRequest(EditDeveloper.Request request, Developer developer) {
        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());

        return developer;
    }
    */
}