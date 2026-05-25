package org.ide.dbp_proyecto.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.ide.dbp_proyecto.ruta.Dificultad;
import org.ide.dbp_proyecto.ruta.TipoPaisaje;
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

    @Enumerated(EnumType.STRING)
    private Dificultad dificultad;

    @Enumerated(EnumType.STRING)
    private TipoPaisaje tipoPaisaje;

    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<Coordenada> trazadoGeografico = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "ruta_poi",
            joinColumns = @JoinColumn(name = "ruta_id"),
            inverseJoinColumns = @JoinColumn(name = "poi_id")
    )
    private List<PuntoInteres> puntosDeInteres = new ArrayList<>();
}