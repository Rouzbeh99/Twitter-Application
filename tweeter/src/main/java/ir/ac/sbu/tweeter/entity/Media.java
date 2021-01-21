package ir.ac.sbu.tweeter.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@Table(name = "TW_MEDIA")
@AllArgsConstructor
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String value;

    @Column(unique = true)
    private String uuid;
    // enum for content
}
