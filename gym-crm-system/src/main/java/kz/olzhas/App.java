package kz.olzhas;

import kz.olzhas.config.AppConfig;
import kz.olzhas.service.GymService;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.logging.Logger;

public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class)) {
            GymService gym = context.getBean(GymService.class);
            LOGGER.info(
                    () ->
                            "Available profiles: "
                                    + gym.listTrainees().size()
                                    + " trainees, "
                                    + gym.listTrainers().size()
                                    + " trainers; "
                                    + gym.listTrainings().size()
                                    + " trainings");
        }
    }
}
