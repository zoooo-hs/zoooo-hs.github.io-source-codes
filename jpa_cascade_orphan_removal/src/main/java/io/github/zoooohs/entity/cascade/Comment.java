package io.github.zoooohs.entity.cascade;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "comment_cascade")
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "post_id")
    private Post post;

    @Builder
    public Comment(String content, Post post) {
        this.content = content;
        this.post = post;
    }

    public void setPost(Post post) {
        this.post = post;
        if (post == null) return;
        this.post.getComments().add(this);
    }
}
