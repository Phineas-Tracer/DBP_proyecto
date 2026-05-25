package org.ide.dbp_proyecto.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class PuntoInteres {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String descripcion;

    @NotNull
    private Double latitud;

    @NotNull
    private Double longitud;

    @NotBlank
    private String urlImagen;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToMany(mappedBy = "puntosDeInteres")
    @JsonIgnore
    private List<Ruta> rutas = new ArrayList<>();

    @OneToMany(mappedBy = "puntoDeInteres")
    private List<LugarColeccionado> colecciones;

    //Google Places
    @Column(unique = true)
    private String googlePlaceId;

    private String direccion;

}
