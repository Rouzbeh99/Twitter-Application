package ir.ac.sbu.tweeter.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@Table(name = "TW_TWEET")
@AllArgsConstructor
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String body;
    private LocalDateTime time;

    @Column(unique = true)
    private String uuid;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(unique = true)
    private User owner;

    @ManyToMany(mappedBy = "likedTweets")
    @ToString.Exclude
    @Builder.Default
    private List<User> likedBy = new ArrayList<>();

    @ManyToMany()
    @ToString.Exclude
    @Builder.Default
    private List<User> retweetedBy = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "Tweet_Hashtag",
            joinColumns = @JoinColumn(name = "Tweet_ID"))
    @Column(name = "Hashtag")
    private List<String> hashtags = new ArrayList<>();

    @OneToMany
    @NotEmpty
    @Builder.Default
    private List<Reply> replies = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinTable(
            name = "WH_TWEET_MEDIA",
            joinColumns = @JoinColumn(name = "TWEET_ID"),
            inverseJoinColumns = @JoinColumn(name = "MEDIA_ID")
    )
    private Media media;
}
