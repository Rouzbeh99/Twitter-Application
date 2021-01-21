package ir.ac.sbu.tweeter.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
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
    private List<User> likedBy;

    @ManyToMany()
    @ToString.Exclude
    private List<User> retweetedBy;

    @ManyToMany(mappedBy = "tweets")
    @ToString.Exclude
    private List<Hashtag> hashtags;

    @ManyToMany()
    @ToString.Exclude
    private List<Username> mentions;

    @OneToMany
    @NotEmpty
    private List<Reply> replies;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinTable(
            name = "WH_TWEET_MEDIA",
            joinColumns = @JoinColumn(name = "TWEET_ID"),
            inverseJoinColumns = @JoinColumn(name = "MEDIA_ID")
    )
    private Media media;
}
