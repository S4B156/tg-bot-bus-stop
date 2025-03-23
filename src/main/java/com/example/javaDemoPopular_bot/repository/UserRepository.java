package com.example.javaDemoPopular_bot.repository;

import com.example.javaDemoPopular_bot.entite.BusStop;
import com.example.javaDemoPopular_bot.entite.User;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserId(long userId);
    @Query("SELECT u.busStops FROM User u WHERE u.userId = :userId")
    List<BusStop> findBusStopsByUserId(Long userId);
    Optional<User> findByUserId(long userId);

    @Query("SELECT CASE WHEN COUNT(bs) > 0 THEN TRUE ELSE FALSE END " +
            "FROM User u JOIN u.busStops bs " +
            "WHERE u.userId = :userId AND bs.stopId = :stopId")
    boolean existsBusStopByUserId(@Param("userId") Long userId, @Param("stopId") Long stopId);
}
