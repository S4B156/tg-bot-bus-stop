package com.example.javaDemoPopular_bot.service;

import com.example.javaDemoPopular_bot.entite.BusStop;
import com.example.javaDemoPopular_bot.entite.BusStopDoc;
import com.example.javaDemoPopular_bot.repository.BusStopElasticsearchRepository;
import com.example.javaDemoPopular_bot.repository.BusStopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BusStopService{
    @Autowired
    private BusStopRepository busStopRepository;
    @Autowired
    private BusStopElasticsearchRepository busStopElasticsearchRepository;

    public Page<BusStopDoc> search(String query, Pageable pageable){
        return busStopElasticsearchRepository.searchByName(query, pageable);
    }
    @Cacheable(value = "busStops", key = "#id")
    public BusStopDoc findInElasticSearch(String stopId){
        return busStopElasticsearchRepository.findByStopId(Long.parseLong(stopId));
    }

    @Cacheable(value = "busStops", key = "#id")
    public BusStop find(Long stopId){
        return busStopRepository.findByStopId(stopId);
    }

    public Optional<BusStop> findNearestStop(double longitude, double latitude){
        return busStopRepository.findNearestStop(longitude, latitude);
    }
}
