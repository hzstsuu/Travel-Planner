package com.travelplanner.repository;

import com.travelplanner.model.BaseEntity;
import com.travelplanner.util.IdGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class FileRepository<T extends BaseEntity> implements Repository<T> {
    private final Path filePath;

    public FileRepository(String filePath) {
        this.filePath = Path.of(filePath);
    }

    protected abstract String serialize(T entity);

    protected abstract T deserialize(String line);

    @Override
    public List<T> findAll() {
        List<T> entities = new ArrayList<>();

        try {
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }

            List<String> lines = Files.readAllLines(filePath);

            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    entities.add(deserialize(line));
                }
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Error reading file: " + filePath + ". " + e.getMessage());
        }

        return entities;
    }

    @Override
    public Optional<T> findById(int id) {
        return findAll()
                .stream()
                .filter(entity -> entity.getId() == id)
                .findFirst();
    }

    @Override
    public T save(T entity) {
        List<T> entities = findAll();
        int newId = IdGenerator.generateNextId(entities);
        entity.setId(newId);

        try {
            Files.writeString(
                    filePath,
                    serialize(entity) + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.out.println("Error saving record: " + e.getMessage());
        }

        return entity;
    }

    @Override
    public boolean update(T entity) {
        List<T> entities = findAll();
        boolean updated = false;

        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i).getId() == entity.getId()) {
                entities.set(i, entity);
                updated = true;
                break;
            }
        }

        if (updated) {
            writeAll(entities);
        }

        return updated;
    }

    @Override
    public boolean delete(int id) {
        List<T> entities = findAll();
        boolean removed = entities.removeIf(entity -> entity.getId() == id);

        if (removed) {
            writeAll(entities);
        }

        return removed;
    }

    protected void writeAll(List<T> entities) {
        List<String> lines = new ArrayList<>();

        for (T entity : entities) {
            lines.add(serialize(entity));
        }

        try {
            Files.write(filePath, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    protected String escape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\", "\\\\")
                .replace("|", "\\p")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    protected String unescape(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\\r", "\r")
                .replace("\\n", "\n")
                .replace("\\p", "|")
                .replace("\\\\", "\\");
    }

    protected String[] splitLine(String line) {
        return line.split("(?<!\\\\)\\|", -1);
    }
}
