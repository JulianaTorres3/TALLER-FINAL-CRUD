package com.zoo.zoo_fantastic.service;

import com.zoo.zoo_fantastic.model.Creature;
import com.zoo.zoo_fantastic.repository.CreatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CreatureService {

    private final CreatureRepository creatureRepository;

    @Autowired
    public CreatureService(CreatureRepository creatureRepository) {
        this.creatureRepository = creatureRepository;
    }

    public Creature createCreature(Creature creature) {
        if (creature.getSize() < 0) {
            throw new IllegalArgumentException("Size cannot be negative");
        }
        if (creature.getDangerLevel() < 1 || creature.getDangerLevel() > 10) {
            throw new IllegalArgumentException("Danger level must be between 1 and 10");
        }
        return creatureRepository.save(creature);
    }

    public List<Creature> getAllCreatures() {
        return creatureRepository.findAll();
    }

    public Creature getCreatureById(Long id) {
        return creatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Creature not found"));
    }

    public Creature updateCreature(Long id, Creature updatedCreature) {
        Creature creature = getCreatureById(id);
        creature.setName(updatedCreature.getName());
        creature.setSpecies(updatedCreature.getSpecies());
        creature.setSize(updatedCreature.getSize());
        creature.setDangerLevel(updatedCreature.getDangerLevel());
        creature.setHealthStatus(updatedCreature.getHealthStatus());
        return creatureRepository.save(creature);
    }

    public void deleteCreature(Long id) {
        Creature creature = getCreatureById(id);
        if ("critical".equals(creature.getHealthStatus())) {
            throw new IllegalStateException("Cannot delete a creature in critical health");
        }
        creatureRepository.delete(creature);
    }
}