package com.github.ec25779.digitalid.service.lookup;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.IdentityNotFoundException;
import com.github.ec25779.digitalid.repository.DigitalIdRepository;
import com.github.ec25779.digitalid.service.lookup.command.LookupIdentityCommand;
import org.jetbrains.annotations.NotNull;

public class LookupServiceImpl implements LookupService {

    private final DigitalIdRepository repository;

    public LookupServiceImpl(@NotNull DigitalIdRepository repository) {
        this.repository = repository;
    }

    @Override
    public @NotNull DigitalId lookupIdentity(@NotNull OrganizationId caller, @NotNull LookupIdentityCommand command) {
        return repository.find(command.id()).orElseThrow(() -> new IdentityNotFoundException(command.id()));
    }

}
