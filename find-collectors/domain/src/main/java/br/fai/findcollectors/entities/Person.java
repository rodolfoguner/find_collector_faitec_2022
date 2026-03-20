package br.fai.findcollectors.entities;

import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.enums.PersonType;
import br.fai.findcollectors.valueobject.Address;
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

    @Enumerated(EnumType.STRING)
    private PersonType personType;

    @ManyToOne
    @JoinColumn(name = "godfather_id")
    private Person godfather;

    @ElementCollection(targetClass = GarbageType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "person_garbage_type",
            joinColumns = @JoinColumn(name = "person_id")
    )
    @Column(name = "garbage_type")
    private List<GarbageType> garbageType;
    private String description;
    private boolean collectPoint;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "cep", column = @Column(name = "cep")),
        @AttributeOverride(name = "street", column = @Column(name = "street")),
        @AttributeOverride(name = "district", column = @Column(name = "district")),
        @AttributeOverride(name = "number", column = @Column(name = "number")),
        @AttributeOverride(name = "city", column = @Column(name = "city")),
        @AttributeOverride(name = "state", column = @Column(name = "state")),
    })
    private Address address;
}