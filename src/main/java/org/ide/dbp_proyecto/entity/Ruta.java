package org.ide.dbp_proyecto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Double latitudCentro;
    private Double longitudCentro;

    @ManyToMany
    @JoinTable(
            name = "ruta_poi",
            joinColumns = @JoinColumn(name = "ruta_id"),
            inverseJoinColumns = @JoinColumn(name = "poi_id")
    )
    private List<PuntoInteres> puntosDeInteres = new ArrayList<>();
}
