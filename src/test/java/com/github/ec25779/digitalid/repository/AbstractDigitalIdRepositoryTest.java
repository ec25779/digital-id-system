package com.github.ec25779.digitalid.repository;

import com.github.ec25779.digitalid.model.BiologicalSex;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.DigitalIdStatus;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractDigitalIdRepositoryTest {

    private static final Instant TEST_CREATED_AT = Instant.parse("2000-01-01T00:00:00Z");
    private static final LocalDate TEST_DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);

    protected abstract @NotNull DigitalIdRepository createRepository();

    protected DigitalIdRepository repository;

    @BeforeEach
    public void setUp() {
        repository = createRepository();
    }

    @Test
    public void testNewDigitalIdSavesAndFindsSingle() {
        UUID id = UUID.randomUUID();
        DigitalId digitalId = new DigitalId(id, TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.ACTIVE);

        repository.save(digitalId);

        Optional<DigitalId> result = repository.find(id);
        assertTrue(result.isPresent());
        assertEquals(digitalId, result.get());
    }

    @Test
    public void testNewDigitalIdSavesAndFindsMany() {
        List<DigitalId> digitalIds = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            digitalIds.add(createNewId());
        }

        for (DigitalId digitalId : digitalIds) {
            repository.save(digitalId);
        }

        for (DigitalId digitalId : digitalIds) {
            Optional<DigitalId> result = repository.find(digitalId.getId());
            assertTrue(result.isPresent());
            assertEquals(digitalId, result.get());
        }
    }

    @Test
    public void testMissingDigitalIdReturnsEmpty() {
        Optional<DigitalId> result = repository.find(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    public void testExistingDigitalIdUpdates() {
        DigitalId digitalId = createNewId();
        repository.save(digitalId);

        digitalId.setFullName("Jane Doe");
        repository.save(digitalId);

        Optional<DigitalId> result = repository.find(digitalId.getId());
        assertTrue(result.isPresent());
        assertEquals("Jane Doe", result.get().getFullName());
    }

    @AfterEach
    public void tearDown() {
        repository = null;
    }

    protected static DigitalId createNewId() {
        return new DigitalId(UUID.randomUUID(), TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE,
            "John Doe", "123 Main St", DigitalIdStatus.ACTIVE);
    }

}
