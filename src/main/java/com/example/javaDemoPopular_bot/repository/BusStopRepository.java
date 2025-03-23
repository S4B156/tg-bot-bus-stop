package com.example.javaDemoPopular_bot.repository;

import com.example.javaDemoPopular_bot.entite.BusStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusStopRepository extends JpaRepository<BusStop, Long> {
    @Query(value = "SELECT c.* FROM bus_stops c " +
            "ORDER BY ST_Distance(" +
            "c.location, " +
            "ST_GeomFromText(CONCAT('POINT(', ?1, ' ', ?2, ')'), 4326)\\:\\:geography" +
            ") LIMIT 1",
            nativeQuery = true)
    Optional<BusStop> findNearestStop(double longitude, double latitude);
    BusStop findByStopId(Long stopId);
}
