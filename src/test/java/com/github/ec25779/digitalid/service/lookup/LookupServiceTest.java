package com.github.ec25779.digitalid.service.lookup;

import com.github.ec25779.digitalid.auth.OrganizationId;
import com.github.ec25779.digitalid.model.BiologicalSex;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.DigitalIdStatus;
import com.github.ec25779.digitalid.model.IdentityNotFoundException;
import com.github.ec25779.digitalid.repository.DigitalIdRepository;
import com.github.ec25779.digitalid.repository.VolatileDigitalIdRepository;
import com.github.ec25779.digitalid.service.lookup.command.LookupIdentityCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class LookupServiceTest {

    private static final OrganizationId CALLER = new OrganizationId("test");

    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);
    private static final Instant CREATED_AT = Instant.parse("2024-01-01T00:00:00Z");

    private DigitalIdRepository repository;
    private LookupService service;

    @BeforeEach
    void setUp() {
        repository = new VolatileDigitalIdRepository();
        service = new LookupServiceImpl(repository);
    }

    @Test
    void testLookupMissingId() {
        UUID id = UUID.randomUUID();
        assertThrows(IdentityNotFoundException.class, () -> service.lookupIdentity(CALLER, new LookupIdentityCommand(id)));
    }

    @Test
    void testLookupExistingId() {
        DigitalId digitalId = new DigitalId(UUID.randomUUID(), CREATED_AT, DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.ACTIVE);
        repository.save(digitalId);

        DigitalId result = service.lookupIdentity(CALLER, new LookupIdentityCommand(digitalId.getId()));
        assertEquals(result, digitalId);
    }

}
