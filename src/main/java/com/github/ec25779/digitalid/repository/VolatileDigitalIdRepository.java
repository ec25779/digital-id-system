package com.github.ec25779.digitalid.repository;

import com.github.ec25779.digitalid.model.DigitalId;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VolatileDigitalIdRepository implements DigitalIdRepository {

    final Map<UUID, DigitalId> store;

    public VolatileDigitalIdRepository() {
        this.store = new HashMap<>();
    }

    VolatileDigitalIdRepository(DigitalId ...ids) {
        this.store = createMap(ids);
    }

    private static Map<UUID, @NotNull DigitalId> createMap(DigitalId[] ids) {
        return Stream.of(ids).collect(Collectors.toMap(DigitalId::getId, Function.identity()));
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
