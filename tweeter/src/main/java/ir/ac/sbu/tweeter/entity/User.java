package ir.ac.sbu.tweeter.entity;


import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @ManyToMany()
    @ToString.Exclude
    @Builder.Default
    private List<Tweet> likedTweets = new ArrayList<>();

    @ManyToMany()
    @ToString.Exclude
    @Builder.Default
    private List<User> followers = new ArrayList<>();

    @ManyToMany()
    @ToString.Exclude
    @Builder.Default
    private List<User> followings = new ArrayList<>();

    @OneToMany
    @ToString.Exclude
    @Builder.Default
    private List<Tweet> tweets = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinTable(
            name = "WH_USER_MEDIA",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "MEDIA_ID")
    )
    private Media profilePicture;


}
