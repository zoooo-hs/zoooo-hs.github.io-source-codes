package io.github.zoooohs.entity.cascade;

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
public class CascadeTest {

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
        comment = Comment.builder().content("비 오던데요?").build();
        comment.setPost(post);
        entityManager.persist(comment);
        entityManager.flush();
    }

    @DisplayName("cascade = REMOVE로 부모 엔티티 삭제시 자식 엔티티도 함께 삭제")
    @Test
    void cascadeRemove() {
        entityManager.remove(post);
        entityManager.flush();

        Comment actual = entityManager.find(Comment.class, comment.getId());
        Assertions.assertNull(actual);
    }

}
