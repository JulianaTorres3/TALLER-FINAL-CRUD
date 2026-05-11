package com.zoo.zoo_fantastic.repository;

import com.zoo.zoo_fantastic.model.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    @Query("SELECT COUNT(c) FROM Creature c WHERE c.zone.id = :zoneId")
    Long countCreaturesByZoneId(@Param("zoneId") Long zoneId);
}
