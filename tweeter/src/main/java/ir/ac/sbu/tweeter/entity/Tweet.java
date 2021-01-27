package ir.ac.sbu.tweeter.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@Table(name = "TW_TWEET")
@AllArgsConstructor
public class Tweet implements Comparable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String body;

    @Column(columnDefinition = "TIMESTAMP")
    @EqualsAndHashCode.Include
    private LocalDateTime time;

    @EqualsAndHashCode.Include
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


    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinTable(
            name = "WH_TWEET_MEDIA",
            joinColumns = @JoinColumn(name = "TWEET_ID"),
            inverseJoinColumns = @JoinColumn(name = "MEDIA_ID")
    )
    private Media media;

    @Override
    public int compareTo(Object o) {
        return this.getTime().compareTo(((Tweet) o).getTime());
    }
}
