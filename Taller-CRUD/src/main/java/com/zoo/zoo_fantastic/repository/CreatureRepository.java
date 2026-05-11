package com.zoo.zoo_fantastic.repository;

import com.zoo.zoo_fantastic.model.Creature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatureRepository extends JpaRepository<Creature, Long> {
}