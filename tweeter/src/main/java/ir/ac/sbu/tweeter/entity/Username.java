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
@Table(name = "TW_USERNAME")
@AllArgsConstructor
public class Username {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String value;

    @OneToOne
    @JoinColumn
    private User owner;

}
