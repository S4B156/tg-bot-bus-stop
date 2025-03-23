package com.example.javaDemoPopular_bot.service;

import com.example.javaDemoPopular_bot.entite.BusStop;
import com.example.javaDemoPopular_bot.entite.User;
import com.example.javaDemoPopular_bot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    BusStopService busStopService;

    @Transactional
    public void addNewFavoriteBusStop(long userId, long stopId){
        User user = userRepository.findByUserId(userId).orElseThrow();
        BusStop busStop = busStopService.find(stopId);
        user.addBusStop(busStop);
        userRepository.save(user);
    }

    @Transactional
    public void removeFavoriteBusStop(long userId, long stopId){
        User user = userRepository.findByUserId(userId).orElseThrow();
        BusStop busStop = busStopService.find(stopId);
        user.removeBusStop(busStop);
        userRepository.save(user);
    }

    @Transactional
    public boolean containsInBusStops(long userId, long stopId){
        return userRepository.existsBusStopByUserId(userId, stopId);
    }

    public boolean existsByUserId(long userId){
        return userRepository.existsByUserId(userId);
    }

    @Cacheable(value = "users", key = "#id")
    public List<BusStop> findBusStopsByUserId(long userId){
        return userRepository.findBusStopsByUserId(userId);
    }
    public void saveNewUser(User user){
        userRepository.save(user);
    }
}
