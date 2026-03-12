package br.fai.findcollectors.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "states")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class State extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uf;
    private String stateName;
}