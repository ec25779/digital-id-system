package com.github.ec25779.digitalid.auth;

import com.github.ec25779.digitalid.model.UnauthorizedOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class OrganizationPermissionRegistry {

    private final Map<OrganizationId, Set<Permission>> organizationPermissions;

    private OrganizationPermissionRegistry(Map<OrganizationId, Set<Permission>> organizationPermissions) {
        this.organizationPermissions = organizationPermissions;
    }

    public boolean hasPermission(@NotNull OrganizationId org, @NotNull Permission permission) {
        return organizationPermissions.getOrDefault(org, Set.of()).contains(permission);
    }

    public void require(@NotNull OrganizationId org, @NotNull Permission permission) {
        if (!hasPermission(org, permission)) {
            throw new UnauthorizedOperationException("Organization " + org + " is not authorized to execute " + permission);
        }
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Map<OrganizationId, Set<Permission>> organizationPermissions;

        private Builder() {
            this.organizationPermissions = new HashMap<>();
        }

        public @NotNull Builder grant(@NotNull OrganizationId org, @NotNull Permission ...permissions) {
            organizationPermissions.computeIfAbsent(org, _ -> createPermissionSet()).addAll(Set.of(permissions));
            return this;
        }

        public @NotNull Builder grant(@NotNull String org, @NotNull Permission ...permissions) {
            return grant(new OrganizationId(org), permissions);
        }

        public @NotNull OrganizationPermissionRegistry build() {
            return new OrganizationPermissionRegistry(Map.copyOf(organizationPermissions));
        }

        private static @NotNull Set<Permission> createPermissionSet() {
            return Collections.newSetFromMap(new EnumMap<>(Permission.class));
        }

    }

}
