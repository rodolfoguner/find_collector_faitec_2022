package br.fai.findcollectors.entities;

import br.fai.findcollectors.enums.GarbageType;
import br.fai.findcollectors.valueobject.Address;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "collects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Collect extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateAndTime;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "collect_garbage_types",
            joinColumns = @JoinColumn(name = "collect_id")
    )
    @Column(name = "garbage_type")
    private List<GarbageType> garbageType;
    private boolean accept;
    private boolean collected;
    private boolean recurrent;
    
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

    @ManyToOne
    @JoinColumn(name = "collector_id")
    private Person collector;

    @ManyToOne
    @JoinColumn(name = "recycler_id")
    private Person recycler;
}