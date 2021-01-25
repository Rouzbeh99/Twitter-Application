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
    private String uuid;

    @ManyToOne()
    @JoinColumn
    private User owner;

    @ManyToMany(mappedBy = "likedTweets",fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<User> likedBy = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<User> retweetedBy = new ArrayList<>();

    @ElementCollection
    @ToString.Exclude
    @CollectionTable(
            name = "HASHTAG",
            joinColumns=@JoinColumn(name = "TWEET_ID")
    )
    @Column(name="hashtag")
    private List<String> hashtags = new ArrayList<>();

    @ElementCollection
    @ToString.Exclude
    @CollectionTable(
            name = "Tweet_Mention",
            joinColumns = @JoinColumn(name = "Tweet_ID"))
    @Column(name = "Mention")
    private List<String> mentions = new ArrayList<>();

    @OneToMany
    @ToString.Exclude
    @Builder.Default
    private List<Reply> replies = new ArrayList<>();

//    @OneToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinTable(
//            name = "WH_TWEET_MEDIA",
//            joinColumns = @JoinColumn(name = "TWEET_ID"),
//            inverseJoinColumns = @JoinColumn(name = "MEDIA_ID")
//    )
//    private Media media;
}
