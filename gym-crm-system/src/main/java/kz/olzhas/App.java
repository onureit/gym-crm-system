package kz.olzhas;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import kz.olzhas.config.AppConfig;
import kz.olzhas.service.GymService;

public class App {
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymService gymService = context.getBean(GymService.class);
            System.out.println(gymService.getWelcomeMessage());
        }
    }
}
