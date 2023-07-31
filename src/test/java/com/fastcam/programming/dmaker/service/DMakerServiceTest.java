package com.fastcam.programming.dmaker.service;

import com.fastcam.programming.dmaker.dto.CreateDeveloper;
import com.fastcam.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcam.programming.dmaker.entity.Developer;
import com.fastcam.programming.dmaker.code.StatusCode;
import com.fastcam.programming.dmaker.exception.DMakerException;
import com.fastcam.programming.dmaker.repository.DeveloperRepository;
import com.fastcam.programming.dmaker.repository.RetiredRepository;
import com.fastcam.programming.dmaker.type.DeveloperLevel;
import com.fastcam.programming.dmaker.type.DeveloperSkillType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.fastcam.programming.dmaker.contant.DMakerConstant.MAX_JUNIOR_EXPERIENCE_YEARS;
import static com.fastcam.programming.dmaker.contant.DMakerConstant.MIN_SENIOR_EXPERIENCE_YEARS;
import static com.fastcam.programming.dmaker.exception.DMakerErrorCode.DUPLICATED_MEMBER_ID;
import static com.fastcam.programming.dmaker.exception.DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED;
import static com.fastcam.programming.dmaker.type.DeveloperLevel.*;
import static com.fastcam.programming.dmaker.type.DeveloperSkillType.FRONT_END;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DMakerServiceTest {
    @Mock // 이 두개의 레파지토리를 가상의 Mock으로 InjectMocks라고 되어있는 클래스를 생성할때 자동으로 이
    // Mock을 넣어주게 된다.
    private DeveloperRepository developerRepository;
    @Mock
    private RetiredRepository retiredRepository;
    @InjectMocks // Mock은 가짜를 넣어준다. Service와 Inject해준다.
    private DMakerService dMakerService;

    private final Developer defaultDeveloper = Developer.builder() // 이런 응답을 주도록 Mocking, 가짜동작을 정의해놓겠다.
            .developerLevel(SENIOR)
            .developerSkillType(FRONT_END)
            .experienceYears(12)
            .statusCode(StatusCode.EMPLOYED)
            .name("name")
            .build();

    private CreateDeveloper.Request getCreateRequest(
            DeveloperLevel developerLevel,
            DeveloperSkillType developerSkillType,
            Integer experienceYears
    ) {
        return CreateDeveloper.Request.builder()
                        .developerLevel(developerLevel)
                        .developerSkillType(developerSkillType)
                        .experienceYears(experienceYears)
                        .memberId("memberId")
                        .name("name")
                        .age(32)
                        .build();
    }
    @Test
    public void testSomething() {
        // findByMemberId를 하면 이 응답을 받게 됨.
        //given
        given(developerRepository.findByMemberId(anyString())) // anyString 아무문자만 넣어주면
                .willReturn(Optional.of(defaultDeveloper));
        //when
        DeveloperDetailDto developerDetail = dMakerService.getDeveloperDetail("memberId");

        //then
        assertEquals(SENIOR, developerDetail.getDeveloperLevel());
        assertEquals(FRONT_END, developerDetail.getDeveloperSkillType());
        assertEquals(12, developerDetail.getExperienceYears());
        // DB에서 값을 가져오게 되면 어떤 값이 있는지 알 수 없기 떄문에 이런 검증을 하기 어렵다

    }


    @Test
    void createDeveloperTest_success() {
        //given
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.empty()); // 중복 값이 없어야하므로 null이 나와야함.
        given(developerRepository.save(any()))
                .willReturn(defaultDeveloper);
        ArgumentCaptor<Developer> captor =
                ArgumentCaptor.forClass(Developer.class);
        //when
        dMakerService.createDeveloper(
                getCreateRequest(SENIOR, FRONT_END, 12));
        //then
        verify(developerRepository, times(1))
                .save(captor.capture());
        Developer saveDeveloper = captor.getValue();
        assertEquals(SENIOR, saveDeveloper.getDeveloperLevel());
        assertEquals(FRONT_END, saveDeveloper.getDeveloperSkillType());
        assertEquals(12, saveDeveloper.getExperienceYears());

    }

    @Test
    void createDeveloperTest_failed_with_duplicated() {
        //given
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper)); // 중복 값이 없어야하므로 null이 나와야함.
        ArgumentCaptor<Developer> captor =
                ArgumentCaptor.forClass(Developer.class);
        //when
        //then
        // 던진 throws를 dMakerException에 받는다.
        DMakerException dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(
                        getCreateRequest(SENIOR, FRONT_END, 12))
        );
        assertEquals(DUPLICATED_MEMBER_ID, dMakerException.getDMakerErrorCode());
    }
    @Test
    void createDeveloperTest_fail_with_unmatched_level() {
        //given
        //when
        //then
        DMakerException dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(
                        getCreateRequest(JUNIOR, FRONT_END,
                                MAX_JUNIOR_EXPERIENCE_YEARS + 1))
        );
        assertEquals(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED,
                dMakerException.getDMakerErrorCode()
        );
        dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(
                        getCreateRequest(JUNGNIOR, FRONT_END,
                                MIN_SENIOR_EXPERIENCE_YEARS + 1))
        );
        assertEquals(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED,
                dMakerException.getDMakerErrorCode()
        );
        dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(
                        getCreateRequest(SENIOR, FRONT_END,
                                MIN_SENIOR_EXPERIENCE_YEARS - 1))
        );
        assertEquals(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED,
                dMakerException.getDMakerErrorCode()
        );
    }
}