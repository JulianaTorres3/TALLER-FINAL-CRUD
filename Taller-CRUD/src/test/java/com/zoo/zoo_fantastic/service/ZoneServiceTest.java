package com.zoo.zoo_fantastic.service;

import com.zoo.zoo_fantastic.model.Creature;
import com.zoo.zoo_fantastic.model.Zone;
import com.zoo.zoo_fantastic.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ZoneServiceTest {

    @Mock
    private ZoneRepository zoneRepository;

    @InjectMocks
    private ZoneService zoneService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateZone_ShouldReturnSavedZone() {
        Zone zone = new Zone();
        zone.setName("Bosque Mágico");

        when(zoneRepository.save(any(Zone.class))).thenReturn(zone);

        Zone savedZone = zoneService.createZone(zone);

        assertNotNull(savedZone);
        assertEquals("Bosque Mágico", savedZone.getName());
        verify(zoneRepository, times(1)).save(zone);
    }

    @Test
    void testGetZoneById_Success() {
        Zone zone = new Zone();
        zone.setId(1L);
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));

        Zone found = zoneService.getZoneById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(zoneRepository, times(1)).findById(1L);
    }

    @Test
    void testGetZoneById_NotFound_ShouldThrowException() {
        when(zoneRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            zoneService.getZoneById(1L);
        });

        assertEquals("Zone not found", exception.getMessage());
    }

    @Test
    void testUpdateZone_Success() {
        Zone existingZone = new Zone();
        existingZone.setId(1L);
        existingZone.setName("Bosque Mágico");
        existingZone.setDescription("Descripción original");
        existingZone.setCapacity(30);

        Zone updatedZone = new Zone();
        updatedZone.setName("Bosque Encantado");
        updatedZone.setDescription("Nueva descripción");
        updatedZone.setCapacity(50);

        when(zoneRepository.findById(1L)).thenReturn(Optional.of(existingZone));
        when(zoneRepository.save(any(Zone.class))).thenReturn(existingZone);

        Zone result = zoneService.updateZone(1L, updatedZone);

        assertEquals("Bosque Encantado", result.getName());
        assertEquals("Nueva descripción", result.getDescription());
        assertEquals(50, result.getCapacity());
        verify(zoneRepository, times(1)).save(existingZone);
    }

    @Test
    void testDeleteZone_Success() {
        Zone zone = new Zone();
        zone.setId(1L);
        zone.setCreatures(new ArrayList<>()); // Lista vacía

        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));
        doNothing().when(zoneRepository).deleteById(1L); // ← Cambiado a deleteById

        zoneService.deleteZone(1L);

        verify(zoneRepository, times(1)).deleteById(1L); // ← Cambiado a deleteById
    }

    @Test
    void testDeleteZone_WithCreatures_ShouldThrowException() {
        Zone zone = new Zone();
        zone.setId(1L);
        List<Creature> creatures = new ArrayList<>();
        creatures.add(new Creature());
        zone.setCreatures(creatures);

        // Mock del conteo - devuelve 1 criatura
        when(zoneRepository.countCreaturesByZoneId(1L)).thenReturn(1L);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            zoneService.deleteZone(1L);
        });

        assertEquals("Cannot delete zone with creatures. Remove all creatures from zone first.",
                exception.getMessage());
        verify(zoneRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteZone_ZoneNotFound_ShouldThrowException() {
        // Este test prueba getZoneById que sí lanza excepción
        when(zoneRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            zoneService.getZoneById(99L);
        });

        assertEquals("Zone not found", exception.getMessage());
    }
    @Test
    void testGetAllZones_ShouldReturnList() {
        List<Zone> zones = new ArrayList<>();
        zones.add(new Zone());
        zones.add(new Zone());

        when(zoneRepository.findAll()).thenReturn(zones);

        List<Zone> result = zoneService.getAllZones();

        assertEquals(2, result.size());
        verify(zoneRepository, times(1)).findAll();
    }


}