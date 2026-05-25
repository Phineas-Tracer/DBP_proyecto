package org.ide.dbp_proyecto.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.ide.dbp_proyecto.Ruta.Dificultad;
import org.ide.dbp_proyecto.Ruta.TipoPaisaje;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotNull
    private Double latitudCentro;

    @NotNull
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