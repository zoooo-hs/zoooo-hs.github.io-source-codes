package io.github.zoooohs.entity.plain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Entity(name = "post_plain")
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String content;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Collection<Comment> comments = new ArrayList<>();

    @Builder
    public Post(String content) {
        this.content = content;
    }
}
