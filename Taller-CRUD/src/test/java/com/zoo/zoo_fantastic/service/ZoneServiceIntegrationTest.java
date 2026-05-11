package com.zoo.zoo_fantastic.service;

import com.zoo.zoo_fantastic.model.Creature;
import com.zoo.zoo_fantastic.model.Zone;
import com.zoo.zoo_fantastic.repository.CreatureRepository;
import com.zoo.zoo_fantastic.repository.ZoneRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ZoneServiceIntegrationTest {

    @Autowired
    private ZoneService zoneService;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private CreatureRepository creatureRepository;

    @Test
    void testCreateZone_ShouldPersistInDatabase() {
        Zone zone = new Zone();
        zone.setName("Zona de Prueba");
        zone.setDescription("Descripción de prueba");
        zone.setCapacity(10);

        Zone saved = zoneService.createZone(zone);

        assertNotNull(saved.getId());

        Optional<Zone> found = zoneRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Zona de Prueba", found.get().getName());
        assertEquals(10, found.get().getCapacity());
    }

    @Test
    void testDeleteZone_EmptyZone_ShouldRemoveFromDatabase() {
        Zone zone = new Zone();
        zone.setName("Zona a Eliminar");
        zone.setDescription("Test");
        zone.setCapacity(5);
        Zone saved = zoneRepository.save(zone);

        zoneService.deleteZone(saved.getId());

        Optional<Zone> found = zoneRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testDeleteZone_WithCreatures_ShouldNotDelete() {
        // Crear y guardar la zona
        Zone zone = new Zone();
        zone.setName("Zona Ocupada");
        zone.setDescription("Test");
        zone.setCapacity(5);
        Zone savedZone = zoneRepository.save(zone);

        // Crear y guardar la criatura asociada a la zona
        Creature creature = new Creature();
        creature.setName("Dragón");
        creature.setSize(5.0);
        creature.setDangerLevel(8);
        creature.setHealthStatus("healthy");
        creature.setZone(savedZone);
        creatureRepository.save(creature);

        // Forzar la sincronización con la base de datos
        creatureRepository.flush();

        // Ahora intentamos eliminar la zona (debería lanzar excepción)
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> zoneService.deleteZone(savedZone.getId())
        );

        // Verificar el mensaje de la excepción
        assertEquals("Cannot delete zone with creatures. Remove all creatures from zone first.",
                exception.getMessage());

        // Verificar que la zona aún existe en la base de datos
        Optional<Zone> found = zoneRepository.findById(savedZone.getId());
        assertTrue(found.isPresent());

        // Verificar que la criatura sigue existiendo
        Optional<Creature> foundCreature = creatureRepository.findById(creature.getId());
        assertTrue(foundCreature.isPresent());
    }
    @Test
    void testUpdateZone_ShouldUpdateFields() {
        Zone zone = new Zone();
        zone.setName("Nombre Original");
        zone.setDescription("Descripción Original");
        zone.setCapacity(5);
        Zone saved = zoneRepository.save(zone);

        Zone updatedDetails = new Zone();
        updatedDetails.setName("Nombre Actualizado");
        updatedDetails.setDescription("Descripción Actualizada");
        updatedDetails.setCapacity(10);

        Zone updated = zoneService.updateZone(saved.getId(), updatedDetails);

        assertEquals("Nombre Actualizado", updated.getName());
        assertEquals("Descripción Actualizada", updated.getDescription());
        assertEquals(10, updated.getCapacity());
    }

    @Test
    void testGetZoneById_ShouldReturnZone() {
        Zone zone = new Zone();
        zone.setName("Zona Test");
        zone.setDescription("Descripción");
        zone.setCapacity(8);
        Zone saved = zoneRepository.save(zone);

        Zone found = zoneService.getZoneById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Zona Test", found.getName());
    }

    @Test
    void testGetAllZones_ShouldReturnAllZones() {
        Zone zone1 = new Zone();
        zone1.setName("Zona 1");
        zone1.setCapacity(10);

        Zone zone2 = new Zone();
        zone2.setName("Zona 2");
        zone2.setCapacity(20);

        zoneRepository.save(zone1);
        zoneRepository.save(zone2);

        var zones = zoneService.getAllZones();

        assertTrue(zones.size() >= 2);
    }
}