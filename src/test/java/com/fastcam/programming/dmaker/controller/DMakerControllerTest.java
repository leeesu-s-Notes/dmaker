package com.fastcam.programming.dmaker.controller;

import com.fastcam.programming.dmaker.dto.DeveloperDto;
import com.fastcam.programming.dmaker.service.DMakerService;
import com.fastcam.programming.dmaker.type.DeveloperLevel;
import com.fastcam.programming.dmaker.type.DeveloperSkillType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DMakerController.class)
class DMakerControllerTest {
    // Controller에다가 요청값을 호출해줘야하는데
    // 컨트롤러에 있는 메서드를 직접 호출하면 파라미터들이 validation 되고 바인딩되고, 복잡하기에
    //MockMvc로 호출을 가상으로 만들어 줄 수 있다.
    @Autowired
    private MockMvc mockMvc;

    @MockBean // mokito와 유사, controller가 DmakerService를 의존하기에 service를가짜빈으로 등록해서 올려준다.
    private DMakerService dMakerService;

    /*
    주로 사용하게 될 미디어 타입을 넣어줬다.
     */
    protected MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8);


    @Test
    void getAllDevelopers() throws Exception {
        /* dMakerService가 해당 메서드를 호출 할 떄
        List로 된 DeveloperDto를 줘야함.
        */
        DeveloperDto juniorDeveloperDto = DeveloperDto.builder()
                .developerSkillType(DeveloperSkillType.BACK_END)
                .developerLevel(DeveloperLevel.JUNIOR)
                .memberId("memberId1").build();
        DeveloperDto seniorDeveloperDto = DeveloperDto.builder()
                .developerSkillType(DeveloperSkillType.FRONT_END)
                .developerLevel(DeveloperLevel.SENIOR)
                .memberId("memberId2").build();
        given(dMakerService.getAllEmployedDevelopers())
                .willReturn(Arrays.asList(juniorDeveloperDto, seniorDeveloperDto));

        /*
        get으로 developers를 컨텐츠 타입이 json이기에 json타입으로 만들어 준다.
        가짜 mockMvc가 get으로 호출하고, 호출하면서 컨텐츠 타입은 json, utf-8인코팅
        andExpect
         */
        mockMvc.perform(get("/developers").contentType(contentType))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(
                        jsonPath("$.[0].developerSkillType",
                                is(DeveloperSkillType.BACK_END.name()))
                ).andExpect(
                        jsonPath("$.[0].developerLevel",
                                is(DeveloperLevel.JUNIOR.name()))
                ).andExpect(
                        jsonPath("$.[1].developerSkillType",
                                is(DeveloperSkillType.FRONT_END.name()))
                ).andExpect(
                        jsonPath("$.[1].developerLevel",
                                is(DeveloperLevel.SENIOR.name()))
                );

    }



}