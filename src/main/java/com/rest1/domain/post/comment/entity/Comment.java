package com.rest1.domain.post.comment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rest1.domain.member.member.entity.Member;
import com.rest1.domain.post.post.entity.Post;
import com.rest1.global.exception.ServiceException;
import com.rest1.global.jpa.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Comment extends BaseEntity {

    private String content;
    @ManyToOne
    @JsonIgnore
    private Post post;
    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;

    public Comment (Member writer, String content, Post post) {
        this.writer = writer;
        this.content = content;
        this.post = post;
    }


    public void update(String content) {
        this.content = content;
    }

    public void checkActorModify(Member actor) {
        if (!this.writer.getId().equals(actor.getId())) {
            throw new ServiceException("403-1", "댓글 수정 권한이 없습니다.");
        }
    }

    public void checkActorDelete(Member actor) {
        if (!this.writer.getId().equals(actor.getId())) {
            throw new ServiceException("403-2", "댓글 삭제 권한이 없습니다.");
        }
    }
}
