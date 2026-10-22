package kz.olzhas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import kz.olzhas.component.StorageInitializer;
import kz.olzhas.model.Trainee;
import kz.olzhas.model.Trainer;
import kz.olzhas.model.Training;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class StorageInitializerTest {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    private StorageInitializer initializer(String location) {
        StorageInitializer initializer = new StorageInitializer();
        initializer.setDataLocation(location);
        initializer.setResourceLoader(new DefaultResourceLoader());
        return initializer;
    }

    private Properties seed() throws IOException {
        Properties seed = new Properties();
        try (Reader reader =
                new InputStreamReader(
                        getClass().getResourceAsStream("/test-seed-data.properties"),
                        StandardCharsets.UTF_8)) {
            seed.load(reader);
        }
        return seed;
    }

    private String file(Properties properties) throws IOException {
        File file = folder.newFile();
        try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            properties.store(writer, "Test seed");
        }
        return file.toURI().toString();
    }

    @Test
    public void postProcessorInitializesOnlyStorageBeans() {
        StorageInitializer initializer = initializer("classpath:test-seed-data.properties");
        Object unrelated = new Object();
        assertSame(unrelated, initializer.postProcessAfterInitialization(unrelated, "other"));
        assertSame(unrelated, initializer.postProcessAfterInitialization(unrelated, "traineeStorage"));
        Map<Long, Trainee> trainees = new LinkedHashMap<>();
        Map<Long, Trainer> trainers = new LinkedHashMap<>();
        Map<Long, Training> trainings = new LinkedHashMap<>();
        Map<Long, Object> otherMap = new LinkedHashMap<>();
        assertSame(otherMap, initializer.postProcessAfterInitialization(otherMap, "otherMap"));
        assertEquals(0, otherMap.size());
        assertSame(trainees, initializer.postProcessBeforeInitialization(trainees, "traineeStorage"));
        assertEquals(0, trainees.size());
        assertSame(trainees, initializer.postProcessAfterInitialization(trainees, "traineeStorage"));
        assertSame(trainers, initializer.postProcessAfterInitialization(trainers, "trainerStorage"));
        assertSame(trainings, initializer.postProcessAfterInitialization(trainings, "trainingStorage"));
        assertEquals("Test.Member", trainees.get(41L).getUsername());
        assertEquals("Test.Coach", trainers.get(71L).getUsername());
        assertEquals(java.time.Duration.ofMinutes(35), trainings.get(91L).getTrainingDuration());
        assertEquals(1, trainees.size());
        assertEquals(1, trainers.size());
        assertEquals(1, trainings.size());
    }

    @Test
    public void externalFileLoadsUtf8AndOptionalFields() throws IOException {
        Properties properties = seed();
        properties.setProperty("trainee.1.firstName", "Әлия");
        properties.remove("trainee.1.dateOfBirth");
        properties.remove("trainee.1.address");
        Map<Long, Trainee> storage = new LinkedHashMap<>();
        initializer(file(properties)).postProcessAfterInitialization(storage, "traineeStorage");
        assertEquals("Әлия", storage.get(41L).getFirstName());
        assertNull(storage.get(41L).getDateOfBirth());
        assertNull(storage.get(41L).getAddress());
    }

    @Test
    public void seedFileIsReadOnceForAllMaps() throws IOException {
        String location = file(seed());
        StorageInitializer initializer = initializer(location);
        Map<Long, Training> trainings = new LinkedHashMap<>();
        initializer.postProcessAfterInitialization(trainings, "trainingStorage");
        Files.delete(java.nio.file.Path.of(java.net.URI.create(location)));

        Map<Long, Trainer> trainers = new LinkedHashMap<>();
        Map<Long, Trainee> trainees = new LinkedHashMap<>();
        initializer.postProcessAfterInitialization(trainers, "trainerStorage");
        initializer.postProcessAfterInitialization(trainees, "traineeStorage");
        assertEquals(1, trainings.size());
        assertEquals(1, trainers.size());
        assertEquals(1, trainees.size());
    }

    @Test
    public void missingFileFailsWhenAStorageMapIsInitialized() {
        StorageInitializer initializer = initializer("classpath:missing-test-seed.properties");
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> initializer.postProcessAfterInitialization(new LinkedHashMap<>(), "traineeStorage"));
        org.junit.Assert.assertTrue(exception.getCause() instanceof IOException);
    }
}
