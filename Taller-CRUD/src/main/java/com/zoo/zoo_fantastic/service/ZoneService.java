package com.zoo.zoo_fantastic.service;

import com.zoo.zoo_fantastic.model.Zone;
import com.zoo.zoo_fantastic.repository.ZoneRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @Autowired
    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    public Zone createZone(Zone zone) {
        return zoneRepository.save(zone);
    }

    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zone not found"));
    }

    public Zone updateZone(Long id, Zone updatedZone) {
        Zone zone = getZoneById(id);
        zone.setName(updatedZone.getName());
        zone.setDescription(updatedZone.getDescription());
        zone.setCapacity(updatedZone.getCapacity());
        return zoneRepository.save(zone);
    }
    /*
    public void deleteZone(Long id) {
        Zone zone = getZoneById(id);
        if (zone.getCreatures() != null && !zone.getCreatures().isEmpty()) {
            throw new IllegalStateException("Cannot delete a zone that has creatures");
        }
        zoneRepository.delete(zone);
    }
    */
    @Transactional
    public void deleteZone(Long id) {
        Long creatureCount = zoneRepository.countCreaturesByZoneId(id);

        if (creatureCount > 0) {
            throw new IllegalStateException("Cannot delete zone with creatures. Remove all creatures from zone first.");
        }

        zoneRepository.deleteById(id);
    }
}