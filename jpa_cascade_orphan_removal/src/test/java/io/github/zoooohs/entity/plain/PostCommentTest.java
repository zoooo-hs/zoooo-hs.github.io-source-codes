package io.github.zoooohs.entity.plain;

import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
public class PostCommentTest {

    @Autowired
    EntityManager entityManager;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        // 게시글 작성
        post = Post.builder().content("오늘은 날씨가 좋다.").build();
        entityManager.persist(post);

        // 게시글에 댓글 작성
        comment = Comment.builder().post(post).content("비 오던데요?").build();
        entityManager.persist(comment);
        entityManager.flush();
    }

    @DisplayName("Comment의 참조 무결성으로 인해 삭제시 예외 발생")
    @Test
    void ReferenceIntegrityFailure() {
        try {
            entityManager.remove(post);
            entityManager.flush();
        } catch (Exception e) {
            // 예외 안을 타고 들어가 참조 무결성 예외를 찾기
            Throwable throwable = e;
            while (throwable != null) {
                if (throwable.getClass() == JdbcSQLIntegrityConstraintViolationException.class) {
                    return;
                }
                throwable = throwable.getCause();
            }
        }
        Assertions.fail();
    }

    @DisplayName("참조 무결성을 지키기 위해 자식 엔티티를 지우고 부모 엔티티를 삭제")
    @Test
    void deleteChildFirst() {
        // comment 먼저 삭제
        entityManager.remove(comment);
        entityManager.remove(post);
        entityManager.flush();
    }
}
