package com.github.ec25779.digitalid.repository;

import com.github.ec25779.digitalid.model.DigitalId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JsonDigitalIdRepository implements DigitalIdRepository {

    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Instant.class, new InstantAdapter())
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .setPrettyPrinting()
        .create();

    private final File file;

    private VolatileDigitalIdRepository loadedRepository;

    public JsonDigitalIdRepository(@NotNull File file) {
        this.file = file;
    }

    @Override
    public @NotNull Optional<DigitalId> find(@NotNull UUID id) {
        if (loadedRepository == null) {
            loadAll();
        }

        return loadedRepository.find(id);
    }

    private void loadAll() {
        if (!file.exists()) {
            loadedRepository = new VolatileDigitalIdRepository();
            return;
        }

        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            DigitalId[] digitalIds = GSON.fromJson(reader, DigitalId[].class);
            loadedRepository = new VolatileDigitalIdRepository(digitalIds);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void save(@NotNull DigitalId digitalId) {
        if (loadedRepository == null) {
            loadAll();
        }

        loadedRepository.save(digitalId);
        saveAll();
    }

    private void saveAll() {
        List<DigitalId> digitalIds = loadedRepository.store.values().stream().toList();
        try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            GSON.toJson(digitalIds, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static class InstantAdapter extends TypeAdapter<Instant> {

        @Override
        public void write(JsonWriter out, Instant value) throws IOException {
            out.value(value.toString());
        }

        @Override
        public Instant read(JsonReader in) throws IOException {
            return Instant.parse(in.nextString());
        }

    }

    private static class LocalDateAdapter extends TypeAdapter<LocalDate> {

        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            out.value(value.toString());
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            return LocalDate.parse(in.nextString());
        }

    }

}
