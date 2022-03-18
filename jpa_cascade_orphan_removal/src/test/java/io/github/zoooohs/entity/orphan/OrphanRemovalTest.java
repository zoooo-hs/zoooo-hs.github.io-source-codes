package io.github.zoooohs.entity.orphan;

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
public class OrphanRemovalTest {

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

    @DisplayName("부모에서 자식과의 관계를 제거하여 고아 제거")
    @Test
    void orphanFromParent() {
        post = entityManager.find(Post.class, post.getId());
        post.getComments().clear();
        Assertions.assertEquals(0, post.getComments().size());
        entityManager.flush();
        entityManager.clear();

        Comment actual = entityManager.find(Comment.class, comment.getId());
        Assertions.assertNull(actual);
    }

    @DisplayName("부모를 제거해서 고아 제거 발동")
    @Test
    void orphanFromParentDeleted() {
        post = entityManager.find(Post.class, post.getId());
        entityManager.remove(post);
        entityManager.flush();

        Comment actual = entityManager.find(Comment.class, comment.getId());
        Assertions.assertNull(actual);
    }
}
