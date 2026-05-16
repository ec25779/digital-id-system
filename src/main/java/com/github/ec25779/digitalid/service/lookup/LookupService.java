package com.github.ec25779.digitalid.service.lookup;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.IdentityNotFoundException;
import com.github.ec25779.digitalid.service.lookup.command.LookupIdentityCommand;
import org.jetbrains.annotations.NotNull;

public interface LookupService {

    @NotNull DigitalId lookupIdentity(@NotNull OrganizationId caller, @NotNull LookupIdentityCommand command)
        throws IdentityNotFoundException;

}
