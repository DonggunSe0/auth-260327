package com.rest1.domain.member.member.repository;

import com.rest1.domain.member.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    Optional<Member> findByApiKey(@NotBlank @Size(min=30,max=40) String apiKey);
    //optional로 받은 이유?
    // findByUsername은 username이 유니크하다고 가정하기 때문에,
    // 결과가 0개 또는 1개일 수 있다. 따라서 Optional로 감싸서 반환하여,
    // 결과가 없을 때 null 대신 Optional.empty()를 반환할 수 있도록 한다.
}
