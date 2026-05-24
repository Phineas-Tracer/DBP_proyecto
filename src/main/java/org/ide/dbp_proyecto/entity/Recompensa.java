package org.ide.dbp_proyecto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Recompensa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String imagenBadgeUrl;

    private int puntosXp;

    @ManyToMany(mappedBy = "recompensas")
    private List<User> usuarios = new ArrayList<>();

    @OneToOne
    @JoinColumn(name="desafio_id")
    private Desafio desafio;
}
