package com.rest1.domain.member.member.controller;

import com.rest1.domain.member.member.dto.MemberDto;
import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.member.member.service.MemberService;
import com.rest1.global.exception.ServiceException;
import com.rest1.global.rq.Rq;
import com.rest1.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class ApiV1MemberController {

    private final MemberService memberService;
   // record 는 자바 16부터 도입된 기능으로, 불변 객체를 간편하게 만들 수 있도록 도와주는 클래스입니다.
   // record는 클래스와 유사하지만,
   // 필드와 생성자, equals(), hashCode(), toString() 메서드를 자동으로 생성해줍니다.
   // 이를 통해 코드의 양을 줄이고 가독성을 높일 수 있습니다.
    private final Rq rq;
    record  JoinReqBody(
            @NotBlank
            @Size(min=2, max=30)
            String username,

            @NotBlank
            @Size(min=2, max=30)
            String password,

            @NotBlank
            @Size(min=2, max=30)
            String nickname
    ) {}

    record  JoinResBody(
         MemberDto memberDto
    ) {}

    @PostMapping("/join")
    public RsData<MemberDto> join(
           @RequestBody @Valid JoinReqBody reqBody
    ) {

        Member member = memberService.join(reqBody.username, reqBody.password, reqBody.nickname);

        return new RsData(
                "201-1",
                "회원 가입이 완료되었습니다. %s".formatted(reqBody.nickname),
                new JoinResBody(
                        new MemberDto(member)
                )
        );
    }

    record  LoginReqBody(
            @NotBlank
            @Size(min=2, max=30)
            String username,

            @NotBlank
            @Size(min=2, max=30)
            String password

    ) {}

    record  LoginResBody(
            MemberDto memberDto,
            String apiKey
    ) {}

    @PostMapping("/login")
    public RsData<MemberDto> login(
            @RequestBody @Valid LoginReqBody reqBody
    ) {
        Member member = memberService.findByUsername(reqBody.username).orElseThrow(
                () -> new ServiceException("401-1","존재하지 않는 아이디입니다.")
        );

        if(!member.getPassword().equals(reqBody.password)) {
            throw new ServiceException("401-2","비밀번호가 일치하지 않습니다.");
        }

        return new RsData(
                "200-1",
                "%s님 환영합니다.".formatted(reqBody.username),
                new LoginResBody(
                        new MemberDto(member),
                        member.getApiKey())
        );
    }

    record  MeResBody(
            MemberDto memberDto
    ) {}

    @GetMapping("/me")
    public RsData<MemberDto> me() {

        Member actor = rq.getActor();

        return new RsData(
                "200-1",
                "OK",
                new MeResBody(
                        new MemberDto(actor)
        )
        );
    }
}
