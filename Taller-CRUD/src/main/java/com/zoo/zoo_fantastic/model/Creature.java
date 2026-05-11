package com.zoo.zoo_fantastic.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Creature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String species;
    private double size;
    private int dangerLevel;
    private String healthStatus;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;
}