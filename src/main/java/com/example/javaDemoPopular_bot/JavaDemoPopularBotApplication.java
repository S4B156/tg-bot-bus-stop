package com.example.javaDemoPopular_bot;

import com.example.javaDemoPopular_bot.entite.BusStop;
import com.example.javaDemoPopular_bot.entite.BusStopDoc;
import com.example.javaDemoPopular_bot.repository.BusStopElasticsearchRepository;
import com.example.javaDemoPopular_bot.repository.BusStopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@SpringBootApplication
@EnableElasticsearchRepositories
public class JavaDemoPopularBotApplication {
	@Autowired
	BusStopRepository busStopRepository;
	@Autowired
	BusStopElasticsearchRepository busStopElasticsearchRepository;

	public static void main(String[] args) throws TelegramApiException {
		SpringApplication.run(JavaDemoPopularBotApplication.class, args);
	}
	@Bean
	public CommandLineRunner commandLineRunner(){
		return (args) -> {
			System.out.println("In CommandLineRunner");
			busStopElasticsearchRepository.deleteAll();
			List<BusStop> busStops = busStopRepository.findAll();
			busStopElasticsearchRepository.saveAll(
					busStops.stream().map(
							busStop -> new BusStopDoc(
									Long.toString(busStop.getId()),
									busStop.getName(),
									busStop.getStopId()
							)
					).toList()
			);
		};
	}
}
