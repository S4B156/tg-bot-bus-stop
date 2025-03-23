package com.example.javaDemoPopular_bot.entite;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tg_user")
@NoArgsConstructor
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(name = "user_id")
    Long userId;

    @ManyToMany
    @JoinTable(
            name = "user_favorite_bus_stops",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "stop_id")
    )
    List<BusStop> busStops = new ArrayList<>();

    public void addBusStop(BusStop busStop) {
        this.busStops.add(busStop);
    }
    public void removeBusStop(BusStop busStop){
        this.busStops.remove(busStop);
    }

    public User(Long userId) {
        this.userId = userId;
    }
}
