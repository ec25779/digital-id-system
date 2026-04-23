package com.github.ec25779.digitalid.repository;

import com.github.ec25779.digitalid.model.DigitalId;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class VolatileDigitalIdRepository implements DigitalIdRepository {

    private final Map<UUID, DigitalId> store;

    public VolatileDigitalIdRepository() {
        this.store = new HashMap<>();
    }

    @Override
    public @NotNull Optional<DigitalId> find(@NotNull UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void save(@NotNull DigitalId digitalId) {
        store.put(digitalId.getId(), digitalId);
    }

}
