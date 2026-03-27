package com.rest1.domain.member.member.entity;

import com.rest1.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Member extends BaseEntity {
    private String username;
    private String password;
    private String nickname;
}

