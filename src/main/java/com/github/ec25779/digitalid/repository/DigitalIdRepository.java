package com.github.ec25779.digitalid.repository;

import com.github.ec25779.digitalid.model.DigitalId;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public interface DigitalIdRepository {

    @NotNull Optional<DigitalId> find(@NotNull UUID id);

    void save(@NotNull DigitalId digitalId);

}
