package org.ide.dbp_proyecto.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Coordenada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitud;
    private Double longitud;
    private Integer orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id")
    private Ruta ruta;
}
