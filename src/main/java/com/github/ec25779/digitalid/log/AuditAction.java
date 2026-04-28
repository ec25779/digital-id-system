package com.github.ec25779.digitalid.log;

import com.github.ec25779.digitalid.model.BiologicalSex;
import com.github.ec25779.digitalid.model.DigitalIdStatus;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

public sealed interface AuditAction permits AuditAction.CreateIdentityAction, AuditAction.UpdateIdentityFullNameAction,
        AuditAction.UpdateIdentityAddressAction, AuditAction.UpdateIdentityStatusAction {

     record CreateIdentityAction(@NotNull LocalDate dateOfBirth, @NotNull String placeOfBirth,
                                 @NotNull BiologicalSex biologicalSex, @NotNull String fullName,
                                 @NotNull String address, @NotNull DigitalIdStatus status) implements AuditAction {
     }

     record UpdateIdentityFullNameAction(@NotNull String fullName) implements AuditAction {
     }

     record UpdateIdentityAddressAction(@NotNull String address) implements AuditAction {
     }

     record UpdateIdentityStatusAction(@NotNull DigitalIdStatus status) implements AuditAction {
     }

}
