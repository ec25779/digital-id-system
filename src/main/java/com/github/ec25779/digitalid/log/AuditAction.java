package com.github.ec25779.digitalid.log;

import com.github.ec25779.digitalid.model.BiologicalSex;
import com.github.ec25779.digitalid.model.DigitalIdStatus;
import com.github.ec25779.digitalid.service.verification.VerificationScope;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDate;

public sealed interface AuditAction
    permits AuditAction.CreateIdentityAction, AuditAction.LookupIdentityAction, AuditAction.UpdateIdentityAddressAction,
            AuditAction.UpdateIdentityFullNameAction, AuditAction.UpdateIdentityStatusAction,
            AuditAction.VerifyIdentityAction, AuditAction.VerifyIdentityBetweenAction {

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

     record LookupIdentityAction() implements AuditAction {
     }

     record VerifyIdentityAction(@NotNull VerificationScope scope) implements AuditAction {
     }

     record VerifyIdentityBetweenAction(@NotNull VerificationScope scope,
                                        @NotNull Instant from, @NotNull Instant to) implements AuditAction {
     }

}
