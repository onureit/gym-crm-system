package kz.olzhas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import kz.olzhas.component.InMemoryStorage;
import kz.olzhas.component.StorageInitializer;
import kz.olzhas.config.AppConfig;
import kz.olzhas.service.GymService;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;
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

    private InMemoryStorage storage() {
        InMemoryStorage storage = new InMemoryStorage();
        storage.setTrainees(new LinkedHashMap<>());
        storage.setTrainers(new LinkedHashMap<>());
        storage.setTrainings(new LinkedHashMap<>());
        return storage;
    }

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
                        getClass().getResourceAsStream("/seed-data.properties"),
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
        StorageInitializer initializer = initializer("classpath:seed-data.properties");
        Object unrelated = new Object();
        assertSame(unrelated, initializer.postProcessBeforeInitialization(unrelated, "other"));
        InMemoryStorage storage = storage();
        assertSame(storage, initializer.postProcessBeforeInitialization(storage, "storage"));
        assertEquals(1, storage.getTrainees().size());
        assertEquals(1, storage.getTrainers().size());
        assertEquals(1, storage.getTrainings().size());
    }

    @Test
    public void externalFileLoadsUtf8AndOptionalFields() throws IOException {
        Properties properties = seed();
        properties.setProperty("trainee.1.firstName", "Әлия");
        properties.remove("trainee.1.dateOfBirth");
        properties.remove("trainee.1.address");
        InMemoryStorage storage = storage();
        initializer(file(properties)).initialize(storage);
        assertEquals("Әлия", storage.getTrainees().get(1L).getFirstName());
        assertNull(storage.getTrainees().get(1L).getDateOfBirth());
        assertNull(storage.getTrainees().get(1L).getAddress());
    }

    @Test
    public void configuredExternalPathIsUsedBySpring() throws IOException {
        Properties properties = seed();
        properties.setProperty("trainee.1.address", "External address");
        try (AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext()) {
            context.getEnvironment()
                    .getPropertySources()
                    .addFirst(
                            new MapPropertySource(
                                    "test", Map.of("gym.storage.data.path", file(properties))));
            context.register(AppConfig.class);
            context.refresh();
            assertEquals(
                    "External address",
                    context.getBean(GymService.class).selectTrainee(1).getAddress());
        }
    }

    @Test
    public void missingFileFailsStartup() {
        try (AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext()) {
            context.getEnvironment()
                    .getPropertySources()
                    .addFirst(
                            new MapPropertySource(
                                    "test",
                                    Map.of(
                                            "gym.storage.data.path",
                                            "classpath:missing-seed.properties")));
            context.register(AppConfig.class);
            try {
                context.refresh();
                fail("Missing file must prevent startup");
            } catch (org.springframework.beans.BeansException expected) {
                assertNotNull(expected.getCause());
            }
        }
    }
}
