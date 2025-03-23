package com.example.javaDemoPopular_bot.repository;

import com.example.javaDemoPopular_bot.entite.BusStop;
import com.example.javaDemoPopular_bot.entite.BusStopDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusStopElasticsearchRepository extends ElasticsearchRepository<BusStopDoc, String> {
    @Query("{\"match_phrase_prefix\": {\"name\": \"?0\"}}")
    Page<BusStopDoc> searchByName(String query, Pageable pageable);
    BusStopDoc findByStopId(long stopId);
}
