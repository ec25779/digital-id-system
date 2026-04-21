package com.github.ec25779.digitalid.model;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class DigitalId {

    private final @NotNull UUID id;
    private final @NotNull Instant createdAt;

    private final @NotNull LocalDate dateOfBirth;
    private final @NotNull String placeOfBirth;
    private final @NotNull BiologicalSex biologicalSex;

    private @NotNull String fullName;
    private @NotNull String address;
    private @NotNull DigitalIdStatus status;

    public DigitalId(@NotNull UUID id, @NotNull Instant createdAt, @NotNull LocalDate dateOfBirth,
                     @NotNull String placeOfBirth, @NotNull BiologicalSex biologicalSex, @NotNull String fullName,
                     @NotNull String address, @NotNull DigitalIdStatus status) {
        this.id = id;
        this.createdAt = createdAt;
        this.dateOfBirth = dateOfBirth;
        this.placeOfBirth = placeOfBirth;
        this.biologicalSex = biologicalSex;
        this.fullName = fullName;
        this.address = address;
        this.status = status;
    }

    private void checkMutable() {
        if (status == DigitalIdStatus.REVOKED) {
            throw new InvalidStateTransitionException("Digital ID is revoked");
        }
    }

    public @NotNull UUID getId() {
        return id;
    }

    public @NotNull Instant getCreatedAt() {
        return createdAt;
    }

    public @NotNull LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public @NotNull String getPlaceOfBirth() {
        return placeOfBirth;
    }

    public @NotNull BiologicalSex getBiologicalSex() {
        return biologicalSex;
    }

    public @NotNull String getFullName() {
        return fullName;
    }

    public DigitalId setFullName(@NotNull String fullName) {
        checkMutable();
        this.fullName = fullName;
        return this;
    }

    public @NotNull String getAddress() {
        return address;
    }

    public DigitalId setAddress(@NotNull String address) {
        checkMutable();
        this.address = address;
        return this;
    }

    public @NotNull DigitalIdStatus getStatus() {
        return status;
    }

    public void suspend() {
        if (status == DigitalIdStatus.REVOKED) {
            throw new InvalidStateTransitionException("Digital ID is revoked");
        }

        if (status == DigitalIdStatus.SUSPENDED) {
            throw new InvalidStateTransitionException("Digital ID is already suspended");
        }

        this.status = DigitalIdStatus.SUSPENDED;
    }

    public void activate() {
        if (status == DigitalIdStatus.REVOKED) {
            throw new InvalidStateTransitionException("Digital ID is revoked");
        }

        if (status == DigitalIdStatus.ACTIVE) {
            throw new InvalidStateTransitionException("Digital ID is already active");
        }

        this.status = DigitalIdStatus.ACTIVE;
    }

    public void revoke() {
        if (status == DigitalIdStatus.REVOKED) {
            throw new InvalidStateTransitionException("Digital ID is already revoked");
        }

        this.status = DigitalIdStatus.REVOKED;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof DigitalId digitalId)) {
            return false;
        }
        return Objects.equals(getId(), digitalId.getId()) && Objects.equals(getCreatedAt(), digitalId.getCreatedAt()) &&
            Objects.equals(getDateOfBirth(), digitalId.getDateOfBirth()) && Objects.equals(getPlaceOfBirth(),
            digitalId.getPlaceOfBirth()) && Objects.equals(getBiologicalSex(), digitalId.getBiologicalSex()) &&
            Objects.equals(getFullName(), digitalId.getFullName()) && Objects.equals(getAddress(),
            digitalId.getAddress()) && getStatus() == digitalId.getStatus();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getCreatedAt(), getDateOfBirth(), getPlaceOfBirth(), getBiologicalSex(),
            getFullName(), getAddress(), getStatus());
    }

    @Override
    public String toString() {
        return "DigitalId{" + "id='" + id + '\'' + ", createdAt=" + createdAt + ", dateOfBirth=" + dateOfBirth +
            ", placeOfBirth='" + placeOfBirth + '\'' + ", biologicalSex='" + biologicalSex + '\'' + ", fullName='" +
            fullName + '\'' + ", address='" + address + '\'' + ", status=" + status + '}';
    }

}
