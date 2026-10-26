package kz.olzhas.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;
import kz.olzhas.model.TrainingType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class JpaDaoTest {
    @Configuration
    @ComponentScan("kz.olzhas.dao.impl")
    static class TestConfig {
        @Bean
        DataSource dataSource() {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(
                    "jdbc:h2:mem:gym;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS gym_crm");

            return new HikariDataSource(config);
        }

        @Bean
        LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
            Properties properties = new Properties();
            properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
            properties.setProperty("hibernate.default_schema", "gym_crm");
            LocalContainerEntityManagerFactoryBean factory =
                    new LocalContainerEntityManagerFactoryBean();
            factory.setDataSource(dataSource);
            factory.setPackagesToScan("kz.olzhas.model");
            factory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            factory.setJpaProperties(properties);

            return factory;
        }

        @Bean
        PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
            return new JpaTransactionManager(entityManagerFactory);
        }
    }

    private AnnotationConfigApplicationContext context;
    private TransactionTemplate tx;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingDao trainingDao;
    private TrainingTypeDao trainingTypeDao;
    private UserDao userDao;
    private EntityManager entityManager;

    @Before
    public void setUp() {
        context = new AnnotationConfigApplicationContext(TestConfig.class);
        tx = new TransactionTemplate(context.getBean(PlatformTransactionManager.class));
        traineeDao = context.getBean(TraineeDao.class);
        trainerDao = context.getBean(TrainerDao.class);
        trainingDao = context.getBean(TrainingDao.class);
        trainingTypeDao = context.getBean(TrainingTypeDao.class);
        userDao = context.getBean(UserDao.class);
        entityManager = SharedEntityManagerCreator.createSharedEntityManager(
                context.getBean(EntityManagerFactory.class));

        tx.executeWithoutResult(status -> {
            TrainingType yoga = persist(new TrainingType(null, "YOGA"));
            TrainingType lifting = persist(new TrainingType(null, "LIFTING"));
            Trainee john = traineeDao.create(trainee("John", "Smith"));
            Trainer jane = trainerDao.create(trainer("Jane", "Doe", yoga));
            Trainer mike = trainerDao.create(trainer("Mike", "Ross", lifting));
            john.addTrainer(jane);
            trainingDao.create(new Training(null, john, jane, "Morning yoga", yoga,
                    LocalDate.of(2026, 1, 10), 60));
            trainingDao.create(new Training(null, john, mike, "Heavy day", lifting,
                    LocalDate.of(2026, 2, 10), 90));
        });
    }

    @After
    public void tearDown() {
        context.close();
    }

    @Test
    public void findsProfilesByUsername() {
        tx.executeWithoutResult(status -> {
            assertEquals("John", traineeDao.findByUsername("John.Smith").orElseThrow().getFirstName());
            assertEquals("YOGA", trainerDao.findByUsername("Jane.Doe").orElseThrow()
                    .getSpecialization().getName());
            assertTrue(traineeDao.findByUsername("Nobody").isEmpty());
            assertTrue(trainerDao.findByUsername("Nobody").isEmpty());
        });
    }

    @Test
    public void userDaoChecksUsernames() {
        tx.executeWithoutResult(status -> {
            assertTrue(userDao.existsByUsername("John.Smith"));
            assertTrue(userDao.existsByUsername("Jane.Doe"));
            assertFalse(userDao.existsByUsername("Nobody"));
        });
    }

    @Test
    public void trainingTypeIsFoundIgnoringCase() {
        tx.executeWithoutResult(status -> {
            assertEquals("YOGA", trainingTypeDao.findByName("yoga").orElseThrow().getName());
            assertTrue(trainingTypeDao.findByName("JUDO").isEmpty());
        });
    }

    @Test
    public void updateSavesChanges() {
        tx.executeWithoutResult(status -> {
            Trainee john = traineeDao.findByUsername("John.Smith").orElseThrow();
            john.toggleActive();
            john.changePassword("newPass123");
            traineeDao.update(john);
        });

        tx.executeWithoutResult(status -> {
            Trainee john = traineeDao.findByUsername("John.Smith").orElseThrow();
            assertFalse(john.isActive());
            assertEquals("newPass123", john.getPassword());
        });
    }

    @Test
    public void findsTraineeTrainingsByCriteria() {
        tx.executeWithoutResult(status -> {
            assertEquals(2, traineeTrainings(null, null, null, null).size());
            assertEquals(1, traineeTrainings(LocalDate.of(2026, 2, 1), null, null, null).size());
            assertEquals(1, traineeTrainings(null, LocalDate.of(2026, 1, 31), null, null).size());
            assertEquals("Morning yoga",
                    traineeTrainings(null, null, "Jane", null).get(0).getTrainingName());
            assertEquals("Heavy day",
                    traineeTrainings(null, null, null, "LIFTING").get(0).getTrainingName());
            assertTrue(traineeTrainings(null, null, "Jane", "LIFTING").isEmpty());
        });
    }

    @Test
    public void findsTrainerTrainingsByCriteria() {
        tx.executeWithoutResult(status -> {
            assertEquals(1, trainingDao.findTrainerTrainings("Jane.Doe", null, null, null).size());
            assertEquals(1, trainingDao.findTrainerTrainings("Jane.Doe", null, null, "John").size());
            assertTrue(trainingDao.findTrainerTrainings("Jane.Doe", null, null, "Nobody").isEmpty());
            assertTrue(trainingDao.findTrainerTrainings(
                    "Jane.Doe", LocalDate.of(2026, 3, 1), null, null).isEmpty());
        });
    }

    @Test
    public void findsTrainersNotAssignedToTrainee() {
        tx.executeWithoutResult(status -> {
            List<Trainer> trainers = trainerDao.findNotAssignedToTrainee("John.Smith");
            assertEquals(1, trainers.size());
            assertEquals("Mike.Ross", trainers.get(0).getUsername());
        });
    }

    @Test
    public void deletingTraineeRemovesTrainingsAndUser() {
        tx.executeWithoutResult(status ->
                traineeDao.delete(traineeDao.findByUsername("John.Smith").orElseThrow()));

        tx.executeWithoutResult(status -> {
            assertTrue(traineeDao.findByUsername("John.Smith").isEmpty());
            assertFalse(userDao.existsByUsername("John.Smith"));
            assertTrue(trainingDao.findTrainerTrainings("Jane.Doe", null, null, null).isEmpty());
            assertTrue(trainerDao.findByUsername("Jane.Doe").isPresent());
            assertEquals(2, trainerDao.findNotAssignedToTrainee("John.Smith").size());
        });
    }

    private List<Training> traineeTrainings(LocalDate from, LocalDate to, String trainerName,
            String type) {
        return trainingDao.findTraineeTrainings("John.Smith", from, to, trainerName, type);
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);

        return entity;
    }

    private Trainee trainee(String first, String last) {
        Trainee trainee = new Trainee(first, last, true, LocalDate.of(1995, 4, 15), "Street");
        trainee.assignCredentials(first + "." + last, "secret12345");

        return trainee;
    }

    private Trainer trainer(String first, String last, TrainingType type) {
        Trainer trainer = new Trainer(first, last, true, type);
        trainer.assignCredentials(first + "." + last, "secret12345");

        return trainer;
    }
}
