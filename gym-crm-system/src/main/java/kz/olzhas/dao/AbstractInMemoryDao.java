package kz.olzhas.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractInMemoryDao<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String entityName;
    private final Function<T, Long> idGetter;
    private final BiConsumer<T, Long> idSetter;
    private Map<Long, T> storage;
    private long lastId;

    protected AbstractInMemoryDao(
            String entityName, Function<T, Long> idGetter, BiConsumer<T, Long> idSetter) {
        this.entityName = entityName;
        this.idGetter = idGetter;
        this.idSetter = idSetter;
    }

    protected void setStorage(Map<Long, T> storage) {
        this.storage = storage;
    }

    public T create(T entity) {
        long id = generateId();
        idSetter.accept(entity, id);
        storage.put(id, entity);
        logger.debug("Stored {} with ID: {}", entityName, id);
        return entity;
    }

    protected long generateId() {
        for (Long id : storage.keySet()) {
            if (id > lastId) {
                lastId = id;
            }
        }
        return ++lastId;
    }

    public Optional<T> findById(long id) {
        logger.debug("Looking up {} with ID: {}", entityName, id);
        return Optional.ofNullable(storage.get(id));
    }

    public List<T> findAll() {
        logger.debug("Retrieving all {} records; count: {}", entityName, storage.size());
        return new ArrayList<>(storage.values());
    }

    protected boolean updateEntity(
            T entity, Function<String, ? extends RuntimeException> exceptionFactory) {
        Long id = idGetter.apply(entity);
        if (!storage.containsKey(id)) {
            logger.warn("Cannot update {}: ID {} does not exist", entityName, id);
            throw exceptionFactory.apply(entityName + " with ID " + id + " does not exist.");
        }
        storage.put(id, entity);
        logger.debug("Updated stored {} with ID: {}", entityName, id);
        return true;
    }

    protected void deleteEntity(
            long id, Function<String, ? extends RuntimeException> exceptionFactory) {
        if (storage.remove(id) == null) {
            logger.warn("Cannot delete {}: ID {} does not exist", entityName, id);
            throw exceptionFactory.apply(entityName + " with ID " + id + " does not exist.");
        }
        logger.debug("Removed {} with ID: {}", entityName, id);
    }
}
