package com.rest1.domain.member.member.controller;

import com.rest1.domain.member.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1MemberControllerTest {

    // MockMvc는 스프링 MVC 테스트 프레임워크에서 제공하는 클래스입니다.
    // 이를 통해 실제 웹 서버를 띄우지 않고도 컨트롤러의 HTTP 요청과 응답을
    // 시뮬레이션하여 테스트할 수 있습니다.
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 가입")
    void t1() throws Exception {
        String username = "newUser";
        String password = "1234";
        String nickname = "새유저";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "%s",
                                            "password": "%s",
                                            "nickname": "%s"
                                        }
                                        """.formatted(username, password, nickname))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isCreated()) // HTTP 201 Created
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("회원 가입이 완료되었습니다. %s".formatted(nickname)))
                .andExpect(jsonPath("$.data.memberDto.id").value(6)) // 새로 가입한 회원의 ID는 6이어야 합니다.
                .andExpect(jsonPath("$.data.memberDto.createDate").exists())
                .andExpect(jsonPath("$.data.memberDto.modifyDate").exists())
                .andExpect(jsonPath("$.data.memberDto.name").value(nickname));
    }

    @Test
    @DisplayName("회원 가입, 이미 존재하는 username으로 가입 - user1")
    void t2() throws Exception {
        String username = "user1"; // 이미 존재하는 username
        String password = "1234";
        String nickname = "새유저";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "%s",
                                            "password": "%s",
                                            "nickname": "%s"
                                        }
                                        """.formatted(username, password, nickname))
                )
                .andDo(print());
        //이미 존재 하는 것과 충돌은 409 Conflict
        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("join"))
                .andExpect(status().isConflict()) // HTTP 201 Created
                .andExpect(jsonPath("$.resultCode").value("409-1"))
                .andExpect(jsonPath("$.msg").value("이미 사용중인 아이디입니다."));


    }
}
