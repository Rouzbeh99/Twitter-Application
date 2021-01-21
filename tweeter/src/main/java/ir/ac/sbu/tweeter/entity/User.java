package ir.ac.sbu.tweeter.entity;


import lombok.*;

import javax.persistence.*;
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

    private String name;
    private String password;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(unique = true)
    private Username username;

    @ManyToMany()
    @ToString.Exclude
    private List<Tweet> likedTweets;

    @ManyToMany()
    @ToString.Exclude
    private List<User> followers;

    @ManyToMany()
    @ToString.Exclude
    private List<User> followings;

    @OneToMany
    @ToString.Exclude
    private List<Tweet> tweets;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinTable(
            name = "WH_USER_MEDIA",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "MEDIA_ID")
    )
    private Media profilePicture;


}
