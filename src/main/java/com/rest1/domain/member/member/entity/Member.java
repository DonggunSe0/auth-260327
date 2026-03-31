package com.rest1.domain.member.member.entity;

import com.rest1.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Entity
public class Member extends BaseEntity {

    @Column(unique = true) // username은 유니크해야 한다.
    private String username;
    private String password;
    private String nickname;
    @Column(unique = true) // apiKey도 유니크해야 한다. DB에서 인덱스가 생성되어 검색 속도가 빨라진다.
    private String apiKey; //apiKey 도입 apikey도 유니크 해야함

    public Member(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.apiKey = UUID.randomUUID().toString();
        // UUID.randomUUID()는 고유한 식별자를 생성하는 방법 중 하나로, 랜덤한 UUID를 생성하여 apiKey로 사용한다.
    }

    public String getName() {
        return nickname;
    }
}



