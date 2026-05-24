package org.ide.dbp_proyecto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Planificacion {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String notas;

    private LocalDateTime fecha;

    private Boolean disponibilidad;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User usuario;

    @ManyToMany
    @JoinTable(name="planificacion_rutas",
    joinColumns = @JoinColumn(name = "planificacion_id"),
    inverseJoinColumns = @JoinColumn(name="ruta_id"))
    private List<Ruta> rutas = new ArrayList<>();

    @ManyToMany
    @JoinTable(name="planificacion_pois",
    joinColumns = @JoinColumn(name = "planificacion_id"),
    inverseJoinColumns = @JoinColumn(name = "poi_id"))
    private List<PuntoInteres> puntosInteres = new ArrayList<>();

}
