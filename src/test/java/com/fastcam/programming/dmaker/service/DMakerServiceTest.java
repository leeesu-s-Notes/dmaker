package com.fastcam.programming.dmaker.service;

import com.fastcam.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcam.programming.dmaker.entity.Developer;
import com.fastcam.programming.dmaker.entity.StatusCode;
import com.fastcam.programming.dmaker.repository.DeveloperRepository;
import com.fastcam.programming.dmaker.repository.RetiredRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.fastcam.programming.dmaker.type.DeveloperLevel.SENIOR;
import static com.fastcam.programming.dmaker.type.DeveloperSkillType.FRONT_END;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DMakerServiceTest {
    @Mock // 이 두개의 레파지토리를 가상의 Mock으로 InjectMocks라고 되어있는 클래스를 생성할때 자동으로 이
    // Mock을 넣어주게 된다.
    private DeveloperRepository developerRepository;
    @Mock
    private RetiredRepository retiredRepository;
    @InjectMocks // Mock은 가짜를 넣어준다. Service와 Inject해준다.
    private DMakerService dMakerService;
    @Test
    public void testSomething() {
        // findByMemberId를 하면 이 응답을 받게 됨.
        given(developerRepository.findByMemberId(anyString())) // anyString 아무문자만 넣어주면
                .willReturn(Optional.of(Developer.builder() // 이런 응답을 주도록 Mocking, 가짜동작을 정의해놓겠다.
                                .developerLevel(SENIOR)
                                .developerSkillType(FRONT_END)
                                .experienceYears(12)
                                .statusCode(StatusCode.EMPLOYED)
                                .name("name")
                                .build()));

        DeveloperDetailDto developerDetail = dMakerService.getDeveloperDetail("memberId");
        assertEquals(SENIOR, developerDetail.getDeveloperLevel());
        assertEquals(FRONT_END, developerDetail.getDeveloperSkillType());
        assertEquals(12, developerDetail.getExperienceYears());
        // DB에서 값을 가져오게 되면 어떤 값이 있는지 알 수 없기 떄문에 이런 검증을 하기 어렵다

    }
}