package com.example.javaDemoPopular_bot.entite;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "stops_index_v2")
@NoArgsConstructor
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusStopDoc {
    @Id
    String id;

    @Field(type = FieldType.Text, analyzer = "russian_analyzer")
    String name;

    @Field(type = FieldType.Long)
    Long stopId;
}