package com.github.ec25779.digitalid.central;

import com.github.ec25779.digitalid.model.BiologicalSex;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public record CreateIdentityCommand(@NotNull LocalDate dateOfBirth, @NotNull String placeOfBirth,
                                    @NotNull BiologicalSex biologicalSex, @NotNull String fullName,
                                    @NotNull String address) {
}
