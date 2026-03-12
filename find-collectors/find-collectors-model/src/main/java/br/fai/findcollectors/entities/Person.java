package br.fai.findcollectors.entities;

import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "persons",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String name;
    private String telephone;
    private String cep;
    private String address;
    private String district;
    private String number;

    @Enumerated(EnumType.STRING)
    private PersonType personType;

    @ManyToOne
    @JoinColumn(name = "godfather_id")
    private Person godfather;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "person_garbage_type",
            joinColumns = @JoinColumn(name = "person_id")
    )
    @Column(name = "garbage_type")
    private List<GarbageType> garbageType;
    private String description;
    private boolean collectPoint;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;


}