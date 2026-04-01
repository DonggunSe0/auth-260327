package com.rest1.domain.member.member.controller;

import com.rest1.domain.member.member.entity.Member;
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
                        post("/api/v1/members/join")
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
                        post("/api/v1/members/join")
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

    @Test
    @DisplayName("로그인")
    void t3() throws Exception {
        String username = "user1"; //로그인 시에 username,password만 필요
        String password = "1234";

        //아이디랑 패스워드를 query string으로 받는 거 안좋고 body로 json으로 받는 게 좋음. 그래서 post로 보냄.
        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/members/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "%s",
                                            "password": "%s"
                                        
                                        }
                                        """.formatted(username, password))
                )
                .andDo(print());

        Member member = memberRepository.findByUsername(username).get();

        resultActions
                .andExpect(handler().handlerType(ApiV1MemberController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(status().isOk()) // HTTP 201 Created
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%s님 환영합니다.".formatted(username)))
                .andExpect(jsonPath("$.data.apiKey").exists()) // 로그인 성공 시 API 키가 응답에 포함되어야 합니다.(필수)
                .andExpect(jsonPath("$.data.memberDto.id").value(member.getId())) // 로그인한 회원의 정보 제공
                //단, username과 password는 제공하면 안된다.
                .andExpect(jsonPath("$.data.memberDto.createDate").value(member.getCreateDate().toString()))
                .andExpect(jsonPath("$.data.memberDto.modifyDate").value(member.getModifyDate().toString()))
                .andExpect(jsonPath("$.data.memberDto.name").value(member.getName()));

    }
}
