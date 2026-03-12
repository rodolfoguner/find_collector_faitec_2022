package br.fai.findcollectors.entities;

import br.fai.findcollectors.enums.GarbageType;
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
    private String cep;
    private String address;
    private String district;
    private String number;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "collector_id")
    private Person collector;

    @ManyToOne
    @JoinColumn(name = "recycler_id")
    private Person recycler;
}