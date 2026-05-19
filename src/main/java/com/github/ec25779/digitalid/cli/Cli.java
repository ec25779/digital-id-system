package com.github.ec25779.digitalid.cli;

import com.github.ec25779.digitalid.model.*;
import com.github.ec25779.digitalid.portal.*;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Cli {

    private final PrintWriter out;
    private final PortalShell shell;
    private final PromptHelper promptHelper;
    private final CentralAuthorityPortal central;
    private final TaxAuthorityPortal tax;
    private final DrivingLicenceAuthorityPortal dvla;
    private final BankPortal bank;
    private final HealthServicePortal health;

    public Cli(@NotNull Scanner scanner, @NotNull PrintWriter out, @NotNull CentralAuthorityPortal central,
               @NotNull TaxAuthorityPortal tax, @NotNull DrivingLicenceAuthorityPortal dvla, @NotNull BankPortal bank,
               @NotNull HealthServicePortal health) {
        this.out = out;
        this.shell = new PortalShell(scanner, out);
        this.promptHelper = new PromptHelper(shell, out);
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
            String choice = shell.readLine("> ");
            switch (choice) {
                case "1" -> shell.run("central-authority", "central", centralCommands());
                case "2" -> shell.run("tax-authority", "tax", taxCommands());
                case "3" -> shell.run("driving-licence-authority", "dvla", dvlaCommands());
                case "4" -> shell.run("bank", "bank", bankCommands());
                case "5" -> shell.run("health-service", "health", healthCommands());
                case "0", "exit", "quit" -> {
                    out.println("Goodbye.");
                    return;
                }
                default -> out.println("Unknown portal.");
            }
        }
    }

    private @NotNull List<PortalCommand> centralCommands() {
        return List.of(
            new PortalCommand("create", "Create a new identity", () -> {
                LocalDate dob = promptHelper.promptDateOfBirth();
                String placeOfBirth = promptHelper.promptPlaceOfBirth();
                BiologicalSex sex = promptHelper.promptBiologicalSex();
                String fullName = promptHelper.promptFullName();
                String address = promptHelper.promptAddress();
                DigitalId id = central.createIdentity(dob, placeOfBirth, sex, fullName, address);
                out.println("Created: " + id);
            }),
            new PortalCommand("lookup", "Look up an identity",
                () -> out.println(central.lookupIdentity(promptHelper.promptDigitalId()))),
            new PortalCommand("update-name", "Update an identity's full name", () -> {
                UUID id = promptHelper.promptDigitalId();
                String name = promptHelper.prompt("New full name: ");
                out.println(central.updateIdentityFullName(id, name));
            }),
            new PortalCommand("update-address", "Update an identity's address", () -> {
                UUID id = promptHelper.promptDigitalId();
                String address = promptHelper.prompt("New address: ");
                out.println(central.updateIdentityAddress(id, address));
            }),
            new PortalCommand("suspend", "Suspend an identity",
                () -> out.println(central.suspendIdentity(promptHelper.promptDigitalId()))),
            new PortalCommand("reinstate", "Reinstate a suspended identity",
                () -> out.println(central.reinstateIdentity(promptHelper.promptDigitalId()))),
            new PortalCommand("revoke", "Permanently revoke an identity",
                () -> out.println(central.revokeIdentity(promptHelper.promptDigitalId())))
        );
    }

    private @NotNull List<PortalCommand> taxCommands() {
        return List.of(
            new PortalCommand("verify-current", "Verify identity for the current tax year",
                () -> out.println("Verified: " + tax.verifyIdentityForCurrentTaxYear(promptHelper.promptDigitalId()))),
            new PortalCommand("verify-year", "Verify identity for a specific tax year", () -> {
                UUID id = promptHelper.promptDigitalId();
                int year = Integer.parseInt(promptHelper.prompt("Tax year start year (e.g. 2025): "));
                out.println("Verified: " + tax.verifyIdentityForTaxYear(id, year));
            })
        );
    }

    private @NotNull List<PortalCommand> dvlaCommands() {
        return List.of(
            new PortalCommand("check-provisional", "Check eligibility for a provisional licence",
                () -> out.println("Provisional: " +
                    dvla.checkLicenceEligibility(promptHelper.promptDigitalId(), LicenceType.PROVISIONAL))),
            new PortalCommand("check-full", "Check eligibility for a full licence",
                () -> out.println("Full: " +
                    dvla.checkLicenceEligibility(promptHelper.promptDigitalId(), LicenceType.FULL)))
        );
    }

    private @NotNull List<PortalCommand> bankCommands() {
        return List.of(
            new PortalCommand("verify-exists", "Verify that an identity is currently valid",
                () -> out.println("Valid: " + bank.verifyIdentityExists(promptHelper.promptDigitalId()))),
            new PortalCommand("verify-loan", "Verify loan eligibility",
                () -> out.println("Eligible for loan: " + bank.verifyIdentityEligibleForLoan(promptHelper.promptDigitalId())))
        );
    }

    private @NotNull List<PortalCommand> healthCommands() {
        return List.of(
            new PortalCommand("verify", "Verify that an identity is currently valid",
                () -> out.println("Valid: " + health.verifyIdentity(promptHelper.promptDigitalId()))),
            new PortalCommand("verify-elderly", "Check eligibility for elderly services",
                () -> out.println("Eligible for elderly services: " +
                    health.verifyIdentityEligibleForElderlyServices(promptHelper.promptDigitalId())))
        );
    }

}
