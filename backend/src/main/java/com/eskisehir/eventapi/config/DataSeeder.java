package com.eskisehir.eventapi.config;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.repository.PoiRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final PoiRepository poiRepository;

    public DataSeeder(PoiRepository poiRepository) {
        this.poiRepository = poiRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only seed if database is empty
        if (poiRepository.count() > 0) {
            log.info("Database already contains {} POIs, skipping seed.", poiRepository.count());
            return;
        }

        log.info("Seeding database with Eskişehir POIs...");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            InputStream inputStream = new ClassPathResource("data/pois.json").getInputStream();
            List<Poi> pois = mapper.readValue(inputStream, new TypeReference<List<Poi>>() {});

            poiRepository.saveAll(pois);
            log.info("Successfully seeded {} POIs into the database.", pois.size());
        } catch (Exception e) {
            log.error("Failed to seed database: {}", e.getMessage());
            throw e;
        }
    }
}
