package com.github.ec25779.digitalid.cli;

import com.github.ec25779.digitalid.model.*;
import com.github.ec25779.digitalid.portal.*;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.UUID;
import java.util.function.Function;

public class Cli {

    private final Scanner scanner;
    private final PrintWriter out;
    private final CentralAuthorityPortal central;
    private final TaxAuthorityPortal tax;
    private final DrivingLicenceAuthorityPortal dvla;
    private final BankPortal bank;
    private final HealthServicePortal health;

    public Cli(@NotNull Scanner scanner, @NotNull PrintWriter out, @NotNull CentralAuthorityPortal central,
               @NotNull TaxAuthorityPortal tax, @NotNull DrivingLicenceAuthorityPortal dvla, @NotNull BankPortal bank,
               @NotNull HealthServicePortal health) {
        this.scanner = scanner;
        this.out = out;
        this.central = central;
        this.tax = tax;
        this.dvla = dvla;
        this.bank = bank;
        this.health = health;
    }

    public void run() {
        out.println("Digital ID System CLI");
        while (true) {
            out.println();
            out.println("Select portal:");
            out.println("  1) central-authority");
            out.println("  2) tax-authority");
            out.println("  3) driving-licence-authority");
            out.println("  4) bank");
            out.println("  5) health-service");
            out.println("  0) exit");
            String choice = prompt("> ");
            switch (choice) {
                case "1" -> centralLoop();
                case "2" -> taxLoop();
                case "3" -> dvlaLoop();
                case "4" -> bankLoop();
                case "5" -> healthLoop();
                case "0", "exit", "quit" -> {
                    out.println("Goodbye.");
                    return;
                }
                default -> out.println("Unknown portal.");
            }
        }
    }

    private void centralLoop() {
        String help = "commands: create, lookup, update-name, update-address, suspend, reinstate, revoke, back";
        out.println("[central-authority] " + help);
        while (true) {
            String cmd = prompt("central> ");
            try {
                switch (cmd) {
                    case "" -> {
                    }
                    case "help" -> out.println(help);
                    case "back" -> {
                        return;
                    }
                    case "create" -> {
                        LocalDate dob = promptDateOfBirth();
                        String placeOfBirth = promptPlaceOfBirth();
                        BiologicalSex sex = promptBiologicalSex();
                        String fullName = promptFullName();
                        String address = promptAddress();
                        DigitalId id = central.createIdentity(dob, placeOfBirth, sex, fullName, address);
                        out.println("Created: " + id);
                    }
                    case "lookup" -> {
                        UUID id = promptDigitalId();
                        out.println(central.lookupIdentity(id));
                    }
                    case "update-name" -> {
                        UUID id = promptDigitalId();
                        String name = prompt("New full name: ");
                        out.println(central.updateIdentityFullName(id, name));
                    }
                    case "update-address" -> {
                        UUID id = promptDigitalId();
                        String address = prompt("New address: ");
                        out.println(central.updateIdentityAddress(id, address));
                    }
                    case "suspend" -> out.println(central.suspendIdentity(promptDigitalId()));
                    case "reinstate" -> out.println(central.reinstateIdentity(promptDigitalId()));
                    case "revoke" -> out.println(central.revokeIdentity(promptDigitalId()));
                    default -> out.println("Unknown. " + help);
                }
            } catch (Exception e) {
                reportError(e);
            }
        }
    }

    private void taxLoop() {
        String help = "commands: verify-current, verify-year, back";
        out.println("[tax-authority] " + help);
        while (true) {
            String cmd = prompt("tax> ");
            try {
                switch (cmd) {
                    case "" -> {
                    }
                    case "help" -> out.println(help);
                    case "back" -> {
                        return;
                    }
                    case "verify-current" -> out.println(
                        "Verified: " + tax.verifyIdentityForCurrentTaxYear(promptDigitalId()));
                    case "verify-year" -> {
                        UUID id = promptDigitalId();
                        int year = Integer.parseInt(prompt("Tax year start year (e.g. 2025): "));
                        out.println("Verified: " + tax.verifyIdentityForTaxYear(id, year));
                    }
                    default -> out.println("Unknown. " + help);
                }
            } catch (Exception e) {
                reportError(e);
            }
        }
    }

    private void dvlaLoop() {
        String help = "commands: check-provisional, check-full, back";
        out.println("[driving-licence-authority] " + help);
        while (true) {
            String cmd = prompt("dvla> ");
            try {
                switch (cmd) {
                    case "" -> {
                    }
                    case "help" -> out.println(help);
                    case "back" -> {
                        return;
                    }
                    case "check-provisional" -> {
                        LicenceEligibility result = dvla.checkLicenceEligibility(promptDigitalId(),
                            LicenceType.PROVISIONAL);
                        out.println("Provisional: " + result);
                    }
                    case "check-full" -> {
                        LicenceEligibility result = dvla.checkLicenceEligibility(promptDigitalId(), LicenceType.FULL);
                        out.println("Full: " + result);
                    }
                    default -> out.println("Unknown. " + help);
                }
            } catch (Exception e) {
                reportError(e);
            }
        }
    }

    private void bankLoop() {
        String help = "commands: verify-exists, verify-loan, back";
        out.println("[bank] " + help);
        while (true) {
            String cmd = prompt("bank> ");
            try {
                switch (cmd) {
                    case "" -> {
                    }
                    case "help" -> out.println(help);
                    case "back" -> {
                        return;
                    }
                    case "verify-exists" -> out.println(
                        "Valid: " + bank.verifyIdentityExists(promptDigitalId()));
                    case "verify-loan" -> out.println(
                        "Eligible for loan: " + bank.verifyIdentityEligibleForLoan(promptDigitalId()));
                    default -> out.println("Unknown. " + help);
                }
            } catch (Exception e) {
                reportError(e);
            }
        }
    }

    private void healthLoop() {
        String help = "commands: verify, verify-elderly, back";
        out.println("[health-service] " + help);
        while (true) {
            String cmd = prompt("health> ");
            try {
                switch (cmd) {
                    case "" -> {
                    }
                    case "help" -> out.println(help);
                    case "back" -> {
                        return;
                    }
                    case "verify" -> out.println("Valid: " + health.verifyIdentity(promptDigitalId()));
                    case "verify-elderly" -> out.println("Eligible for elderly services: " +
                        health.verifyIdentityEligibleForElderlyServices(promptDigitalId()));
                    default -> out.println("Unknown. " + help);
                }
            } catch (Exception e) {
                reportError(e);
            }
        }
    }

    private void reportError(Exception e) {
        if (e instanceof UnauthorizedOperationException || e instanceof IdentityNotFoundException ||
            e instanceof InvalidStateTransitionException) {
            out.println("Rejected: " + e.getMessage());
        } else {
            out.println("Error: " + e.getMessage());
        }
    }

    private String prompt(@NotNull String label) {
        out.print(label);
        out.flush();
        if (!scanner.hasNextLine()) {
            return "exit";
        }

        return scanner.nextLine().trim();
    }

    private <T> T prompt(@NotNull String label, @NotNull Function<String, T> parser) {
        while (true) {
            String input = prompt(label);
            try {
                return parser.apply(input);
            } catch (Exception e) {
                out.println("Invalid input, try again.");
                if (e instanceof IllegalArgumentException) {
                    out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    private UUID promptUuid(@NotNull String label) {
        return prompt(label, input -> {
            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID format.");
            }
        });
    }

    private LocalDate promptDate(@NotNull String label) {
        return prompt(label, input -> {
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format, expected YYYY-MM-DD.");
            }
        });
    }

    private BiologicalSex promptSex(@NotNull String label) {
        return prompt(label, input -> {
            try {
                return BiologicalSex.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid sex, expected male/female/other.");
            }
        });
    }

    private UUID promptDigitalId() {
        return promptUuid("Enter Digital ID (UUID): ");
    }

    private @NotNull String promptAddress() {
        return prompt("Enter address: ");
    }

    private @NotNull String promptFullName() {
        return prompt("Enter full name: ");
    }

    private LocalDate promptDateOfBirth() {
        return promptDate("Enter date of birth (YYYY-MM-DD): ");
    }

    private @NotNull String promptPlaceOfBirth() {
        return prompt("Enter place of birth: ");
    }

    private BiologicalSex promptBiologicalSex() {
        return promptSex("Enter biological sex: ");
    }

}
