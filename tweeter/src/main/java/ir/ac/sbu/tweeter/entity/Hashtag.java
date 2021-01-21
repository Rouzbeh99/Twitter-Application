package ir.ac.sbu.tweeter.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@Table(name = "TW_HASHTAG")
@AllArgsConstructor
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String value;

    @ManyToMany
    private List<Tweet> tweets;
}
