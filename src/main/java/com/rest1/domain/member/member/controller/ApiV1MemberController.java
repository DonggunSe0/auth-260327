package com.rest1.domain.member.member.controller;

import com.rest1.domain.member.member.dto.MemberDto;
import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.member.member.service.MemberService;
import com.rest1.global.exception.ServiceException;
import com.rest1.global.rsData.RsData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class ApiV1MemberController {

    private final MemberService memberService;
   // record 는 자바 16부터 도입된 기능으로, 불변 객체를 간편하게 만들 수 있도록 도와주는 클래스입니다.
   // record는 클래스와 유사하지만,
   // 필드와 생성자, equals(), hashCode(), toString() 메서드를 자동으로 생성해줍니다.
   // 이를 통해 코드의 양을 줄이고 가독성을 높일 수 있습니다.
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

    @PostMapping()
    public RsData<MemberDto> join(
           @RequestBody @Valid JoinReqBody reqBody
    ) {

        memberService.findByUsername(reqBody.username)
                .ifPresent(member -> {throw new ServiceException("409-1","이미 사용중인 아이디입니다.");});

        Member member = memberService.join(reqBody.username, reqBody.password, reqBody.nickname);

        return new RsData(
                "201-1",
                "회원 가입이 완료되었습니다. %s".formatted(reqBody.nickname),
                new JoinResBody(
                        new MemberDto(member)
                )
        );
    }
}
