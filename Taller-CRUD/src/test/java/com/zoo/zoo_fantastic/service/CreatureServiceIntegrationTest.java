package com.zoo.zoo_fantastic.service;

import com.zoo.zoo_fantastic.model.Creature;
import com.zoo.zoo_fantastic.repository.CreatureRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CreatureServiceIntegrationTest {

    @Autowired
    private CreatureService creatureService;

    @Autowired
    private CreatureRepository creatureRepository;

    @Test
    void testCreateCreature_ShouldPersistInDatabase() {
        Creature creature = new Creature();
        creature.setName("Unicornio");
        creature.setSize(15.0);
        creature.setDangerLevel(5);
        creature.setHealthStatus("healthy");

        creatureService.createCreature(creature);
        Optional<Creature> foundCreature = creatureRepository.findById(creature.getId());

        assertTrue(foundCreature.isPresent());
        assertEquals("Unicornio", foundCreature.get().getName());
    }

    @Test
    void testUpdateCreature_ShouldPersistChangesInDatabase() {
        // First create
        Creature creature = new Creature();
        creature.setName("Dragón");
        creature.setSize(20.0);
        creature.setDangerLevel(10);
        creature.setHealthStatus("healthy");
        Creature savedCreature = creatureService.createCreature(creature);

        // Then update
        Creature updatedCreature = new Creature();
        updatedCreature.setName("Dragón Anciano");
        updatedCreature.setSize(25.0);
        updatedCreature.setDangerLevel(10);
        updatedCreature.setHealthStatus("wounded");
        
        creatureService.updateCreature(savedCreature.getId(), updatedCreature);
        
        Optional<Creature> foundCreature = creatureRepository.findById(savedCreature.getId());
        assertTrue(foundCreature.isPresent());
        assertEquals("Dragón Anciano", foundCreature.get().getName());
        assertEquals(25.0, foundCreature.get().getSize());
        assertEquals("wounded", foundCreature.get().getHealthStatus());
    }

    @Test
    void testDeleteCreature_ShouldRemoveFromDatabase() {
        // First create
        Creature creature = new Creature();
        creature.setName("Troll");
        creature.setSize(12.0);
        creature.setDangerLevel(8);
        creature.setHealthStatus("healthy");
        Creature savedCreature = creatureService.createCreature(creature);

        // Then delete
        creatureService.deleteCreature(savedCreature.getId());

        Optional<Creature> foundCreature = creatureRepository.findById(savedCreature.getId());
        assertFalse(foundCreature.isPresent());
    }
}
