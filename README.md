# Digital ID System

[![Build and Test](https://github.com/ec25779/digital-id-system/actions/workflows/build.yml/badge.svg)](https://github.com/ec25779/digital-id-system/actions/workflows/build.yml)

Digital ID System for IOT452U coursework. A central authority manages Digital IDs which are consumed by organizations such as DVLA, tax authority, bank, and health service. Each organization has a dedicated portal to verify or look up Digital IDs, which can be accessed using the CLI.

## Requirements

- Java 23+
- Maven 3.9+

## Running

Build and run all tests:

```
mvn clean verify
```

Launch the interactive console application:

```
mvn -q compile exec:java -Dexec.mainClass=com.github.ec25779.digitalid.cli.Main
```

In the interactive CLI, you can pick a portal and issue commands within that portal. Use `help` to see what commands are available.

Identities and audit log are saved under the data/ directory, persisted between runs of the CLI. There is initial sample data committed to the repository, so you can query Digital IDs by their UUID defined in there, or create a new ID with the central authority portal.

## System structure

The `DigitalId` class is the main model for a digital identity in the system. `DigitalIdRepository` persists these identities.

Identities are accessed and modified through three service interfaces: `ManagementService` for creating and updating identities, `LookupService` for retrieving identities, and `VerificationService` for verifying identity attributes. Each service is implemented as a core class (e.g. `ManagementServiceImpl`) wrapped by decorators that add auditing and authorization.

The authorizing decorators check permissions based on the caller organization and the action being performed, using `OrganizationPermissionRegistry` to determine if the action is allowed. 

The auditing decorators record actions in an `AuditLog`.

There is a simple `Cli` application which constructs the necessary services and portals, and allows users to interact with the system through an interactive shell. Each portal is associated with a specific organization and provides access to the services according to that organization's permissions.

## Testing and CI

Testing uses JUnit, Mockito and AssertJ. Tests live alongside each package and cover the main functionality. GitHub Actions runs `mvn -B clean verify` on every push and pull request to the main branch - see`.github/workflows/build.yml`.

A JaCoCo coverage report is produced in CI and attached as a comment to PRs.
