package org.ide.dbp_proyecto.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ide.dbp_proyecto.enums.TipoDesafio;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Desafio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TipoDesafio type;

    private int value;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User usuario;
}
