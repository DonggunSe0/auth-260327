package com.rest1.global.rq;

import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.member.member.service.MemberService;
import com.rest1.global.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class Rq {
    private final MemberService memberService;
    private final HttpServletRequest request;
    //그래서 controller에서 Rq를 주입받아 getActor() 메서드를 호출하면, 현재 요청의 Authorization 헤더에서 API 키를 추출하여
    // 해당 API 키에 해당하는 회원 정보를 반환하는 방식으로 인증을 처리할 수 있습니다.
    //새로운 요청을 처리할 때마다 Rq 객체가 새로 생성되고,
    // 각 요청마다 독립적인 상태를 유지할 수 있도록 @RequestScope 어노테이션이 사용됩니다.

    public Member getActor() {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || authorization.isEmpty()) {
            throw new ServiceException("401-1", "헤더에 인증 정보가 없습니다.");
        }

        if(!authorization.startsWith("Bearer ")){
            throw new ServiceException("401-2", "헤더의 인증 정보 형식이 올바르지 않습니다.");
        }

        Member actor = memberService.findByApiKey(authorization.replace("Bearer ", ""))
                .orElseThrow(() -> new ServiceException("401-3", "API키가 올바르지 않습니다."));


        return actor;
    }
}

//rq는 요청 처리 전문가