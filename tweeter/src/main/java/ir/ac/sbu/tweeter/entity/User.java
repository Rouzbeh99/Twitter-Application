package ir.ac.sbu.tweeter.entity;


import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@Table(name = "TW_USER")
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String username;

    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<Tweet> likedTweets = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<User> followers = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<User> followings = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<Tweet> tweets = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<Tweet> timeline = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @Builder.Default
    private List<Tweet> retweets = new ArrayList<>();




//    @OneToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinTable(
//            name = "WH_USER_MEDIA",
//            joinColumns = @JoinColumn(name = "USER_ID"),
//            inverseJoinColumns = @JoinColumn(name = "MEDIA_ID")
//    )
//    private Media profilePicture;


}
