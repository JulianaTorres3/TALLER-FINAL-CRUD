package com.zoo.zoo_fantastic.service;

import com.zoo.zoo_fantastic.model.Creature;
import com.zoo.zoo_fantastic.repository.CreatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreatureServiceTest {

    @Mock
    private CreatureRepository creatureRepository;

    @InjectMocks
    private CreatureService creatureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCreature_ShouldReturnSavedCreature() {
        Creature creature = new Creature();
        creature.setName("Fénix");
        creature.setSize(10.0);
        creature.setDangerLevel(5);

        when(creatureRepository.save(any(Creature.class))).thenReturn(creature);

        Creature savedCreature = creatureService.createCreature(creature);

        assertNotNull(savedCreature);
        assertEquals("Fénix", savedCreature.getName());
    }

    @Test
    void testCreateCreature_InvalidSize_ShouldThrowException() {
        Creature creature = new Creature();
        creature.setSize(-1.0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creatureService.createCreature(creature);
        });

        assertEquals("Size cannot be negative", exception.getMessage());
    }

    @Test
    void testCreateCreature_InvalidDangerLevel_ShouldThrowException() {
        Creature creature = new Creature();
        creature.setSize(10.0);
        creature.setDangerLevel(11);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            creatureService.createCreature(creature);
        });

        assertEquals("Danger level must be between 1 and 10", exception.getMessage());
    }

    @Test
    void testGetCreatureById_Success() {
        Creature creature = new Creature();
        creature.setId(1L);
        when(creatureRepository.findById(1L)).thenReturn(Optional.of(creature));

        Creature found = creatureService.getCreatureById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    void testGetCreatureById_NotFound_ShouldThrowException() {
        when(creatureRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            creatureService.getCreatureById(1L);
        });

        assertEquals("Creature not found", exception.getMessage());
    }

    @Test
    void testUpdateCreature_Success() {
        Creature existingCreature = new Creature();
        existingCreature.setId(1L);
        existingCreature.setName("Fénix");

        Creature updatedCreature = new Creature();
        updatedCreature.setName("Fénix Renacido");
        updatedCreature.setDangerLevel(5);
        updatedCreature.setSize(10.0);

        when(creatureRepository.findById(1L)).thenReturn(Optional.of(existingCreature));
        when(creatureRepository.save(any(Creature.class))).thenReturn(existingCreature);

        Creature result = creatureService.updateCreature(1L, updatedCreature);

        assertEquals("Fénix Renacido", result.getName());
    }

    @Test
    void testDeleteCreature_Success() {
        Creature creature = new Creature();
        creature.setId(1L);
        creature.setHealthStatus("healthy");

        when(creatureRepository.findById(1L)).thenReturn(Optional.of(creature));

        creatureService.deleteCreature(1L);

        verify(creatureRepository, times(1)).delete(creature);
    }

    @Test
    void testDeleteCreature_CriticalHealth_ShouldThrowException() {
        Creature creature = new Creature();
        creature.setId(1L);
        creature.setHealthStatus("critical");

        when(creatureRepository.findById(1L)).thenReturn(Optional.of(creature));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            creatureService.deleteCreature(1L);
        });

        assertEquals("Cannot delete a creature in critical health", exception.getMessage());
    }
}
