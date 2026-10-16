package kz.olzhas.config;

import kz.olzhas.component.StorageInitializer;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@ComponentScan("kz.olzhas")
@PropertySource("classpath:application.properties")
public class AppConfig {
    @Bean
    public Map<Long, Trainee> traineeStorage() {
        return new LinkedHashMap<>();
    }

    @Bean
    public Map<Long, Trainer> trainerStorage() {
        return new LinkedHashMap<>();
    }

    @Bean
    public Map<Long, Training> trainingStorage() {
        return new LinkedHashMap<>();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholders() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public static StorageInitializer storageInitializer() {
        return new StorageInitializer();
    }
}
