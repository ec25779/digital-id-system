package com.github.ec25779.digitalid.central;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.DigitalIdStatus;
import com.github.ec25779.digitalid.model.IdentityNotFoundException;
import com.github.ec25779.digitalid.repository.DigitalIdRepository;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class CoreIdentityManager implements DigitalIdentityManager {

    private final DigitalIdRepository repository;

    public CoreIdentityManager(@NotNull DigitalIdRepository repository) {
        this.repository = repository;
    }

    @Override
    public @NotNull DigitalId createIdentity(@NotNull OrganizationId caller, @NotNull CreateIdentityCommand command) {
        DigitalId digitalId = new DigitalId(UUID.randomUUID(), Instant.now(), command.dateOfBirth(),
            command.placeOfBirth(), command.biologicalSex(), command.fullName(), command.address(), DigitalIdStatus.ACTIVE);
        repository.save(digitalId);

        return digitalId;
    }

    @Override
    public @NotNull DigitalId updateIdentity(@NotNull OrganizationId caller, @NotNull UpdateIdentityCommand command) {
        Optional<DigitalId> result = repository.find(command.id());
        if (result.isEmpty()) {
            throw new IdentityNotFoundException(command.id());
        }

        DigitalId digitalId = result.get();
        switch (command) {
            case UpdateIdentityFullNameCommand(UUID _, String fullName) -> digitalId.setFullName(fullName);
            case UpdateIdentityAddressCommand(UUID _, String address) -> digitalId.setAddress(address);
        }

        repository.save(digitalId);
        return digitalId;
    }

}
