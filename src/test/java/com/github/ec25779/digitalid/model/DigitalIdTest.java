package com.github.ec25779.digitalid.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DigitalIdTest {

    private static final Instant TEST_CREATED_AT = Instant.parse("2000-01-01T00:00:00Z");
    private static final LocalDate TEST_DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);

    @Test
    public void testCreateDigitalId() {
        UUID id = UUID.randomUUID();
        DigitalId digitalId = new DigitalId(id, TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.ACTIVE);
        assertEquals(id, digitalId.getId());
        assertEquals(TEST_CREATED_AT, digitalId.getCreatedAt());
        assertEquals(TEST_DATE_OF_BIRTH, digitalId.getDateOfBirth());
        assertEquals("London", digitalId.getPlaceOfBirth());
        assertEquals(BiologicalSex.MALE, digitalId.getBiologicalSex());
        assertEquals("John Doe", digitalId.getFullName());
        assertEquals("123 Main St", digitalId.getAddress());
        assertEquals(DigitalIdStatus.ACTIVE, digitalId.getStatus());
    }

    @Test
    public void testSetFullNameActiveDigitalId() {
        UUID id = UUID.randomUUID();
        DigitalId digitalId = new DigitalId(id, TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.ACTIVE);
        digitalId.setFullName("Jane Doe");
        assertEquals("Jane Doe", digitalId.getFullName());
    }

    @Test
    public void testSetFullNameSuspendedDigitalId() {
        UUID id = UUID.randomUUID();
        DigitalId digitalId = new DigitalId(id, TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.SUSPENDED);
        digitalId.setFullName("Jane Doe");
        assertEquals("Jane Doe", digitalId.getFullName());
    }

    @Test
    public void testSetFullNameRevokedDigitalId() {
        UUID id = UUID.randomUUID();
        DigitalId digitalId = new DigitalId(id, TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.REVOKED);
        assertThrows(InvalidStateTransitionException.class, () -> digitalId.setFullName("Jane Doe"));
        assertEquals("John Doe", digitalId.getFullName());
    }

    @Test
    public void testSetAddressActiveDigitalId() {
        UUID id = UUID.randomUUID();
        DigitalId digitalId = new DigitalId(id, TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.ACTIVE);
        digitalId.setAddress("456 Elm St");
        assertEquals("456 Elm St", digitalId.getAddress());
    }

    @Test
    public void testSetAddressSuspendedDigitalId() {
        UUID id = UUID.randomUUID();
        DigitalId digitalId = new DigitalId(id, TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.SUSPENDED);
        digitalId.setAddress("456 Elm St");
        assertEquals("456 Elm St", digitalId.getAddress());
    }

    @Test
    public void testSetAddressRevokedDigitalId() {
        UUID id = UUID.randomUUID();
        DigitalId digitalId = new DigitalId(id, TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.REVOKED);
        assertThrows(InvalidStateTransitionException.class, () -> digitalId.setAddress("456 Elm St"));
        assertEquals("123 Main St", digitalId.getAddress());
    }

    @Test
    public void testSuspendActiveDigitalId() {
        UUID id = UUID.randomUUID();
        DigitalId digitalId = new DigitalId(id, TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.ACTIVE);
        
        digitalId.suspend();
        assertEquals(DigitalIdStatus.SUSPENDED, digitalId.getStatus());
        assertThrows(InvalidStateTransitionException.class, digitalId::suspend);
    }

    @Test
    public void testActivateSuspendedDigitalId() {
        UUID id = UUID.randomUUID();
        DigitalId digitalId = new DigitalId(id, TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.SUSPENDED);

        digitalId.activate();
        assertEquals(DigitalIdStatus.ACTIVE, digitalId.getStatus());
    }

    @Test
    public void testRevokeActiveDigitalId() {
        UUID id = UUID.randomUUID();
        DigitalId digitalId = new DigitalId(id, TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.ACTIVE);
        
        digitalId.revoke();
        assertEquals(DigitalIdStatus.REVOKED, digitalId.getStatus());
        assertThrows(InvalidStateTransitionException.class, digitalId::revoke);
        assertThrows(InvalidStateTransitionException.class, digitalId::activate);
        assertThrows(InvalidStateTransitionException.class, digitalId::suspend);
    }

    @Test
    public void testRevokeSuspendedDigitalId() {
        UUID id = UUID.randomUUID();
        DigitalId digitalId = new DigitalId(id, TEST_CREATED_AT, TEST_DATE_OF_BIRTH, "London", BiologicalSex.MALE, "John Doe", "123 Main St", DigitalIdStatus.SUSPENDED);

        digitalId.revoke();
        assertEquals(DigitalIdStatus.REVOKED, digitalId.getStatus());
        assertThrows(InvalidStateTransitionException.class, digitalId::revoke);
        assertThrows(InvalidStateTransitionException.class, digitalId::activate);
        assertThrows(InvalidStateTransitionException.class, digitalId::suspend);
    }

}
