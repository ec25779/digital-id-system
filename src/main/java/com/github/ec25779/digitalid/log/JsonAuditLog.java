package com.github.ec25779.digitalid.log;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.util.json.AuditActionAdapter;
import com.github.ec25779.digitalid.util.json.InstantAdapter;
import com.github.ec25779.digitalid.util.json.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JsonAuditLog implements AuditLog {

    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Instant.class, new InstantAdapter())
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .registerTypeAdapter(AuditAction.class, new AuditActionAdapter())
        .setPrettyPrinting()
        .create();

    private final File file;
    private final Clock clock;

    private VolatileAuditLog loadedLog;

    public JsonAuditLog(@NotNull File file, @NotNull Clock clock) {
        this.file = file;
        this.clock = clock;
    }

    public JsonAuditLog(@NotNull File file) {
        this(file, Clock.systemUTC());
    }

    private void ensureLoaded() {
        if (loadedLog != null) {
            return;
        }

        loadedLog = new VolatileAuditLog(clock);

        if (!file.exists()) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            AuditEvent[] events = GSON.fromJson(reader, AuditEvent[].class);
            if (events != null) {
                for (AuditEvent event : events) {
                    loadedLog.record(event);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void saveAll() {
        List<AuditEvent> events = loadedLog.getAllEvents();
        try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            GSON.toJson(events, writer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void record(@NotNull AuditEvent event) {
        ensureLoaded();
        loadedLog.record(event);
        saveAll();
    }

    @Override
    public @NotNull AuditEvent record(@NotNull UUID identityId, @NotNull OrganizationId caller,
                                      @NotNull AuditAction action) {
        ensureLoaded();
        AuditEvent event = loadedLog.record(identityId, caller, action);
        saveAll();
        return event;
    }

    @Override
    public @NotNull Optional<AuditEvent> getEvent(@NotNull UUID eventId) {
        ensureLoaded();
        return loadedLog.getEvent(eventId);
    }

    @Override
    public @NotNull List<AuditEvent> getEvents(@NotNull UUID identityId) {
        ensureLoaded();
        return loadedLog.getEvents(identityId);
    }

    @Override
    public @NotNull List<AuditEvent> getEventsBetween(@NotNull UUID identityId, @NotNull Instant from, @NotNull Instant to) {
        ensureLoaded();
        return loadedLog.getEventsBetween(identityId, from, to);
    }

}
