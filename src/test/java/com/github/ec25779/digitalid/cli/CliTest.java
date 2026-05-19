package com.github.ec25779.digitalid.cli;

import com.github.ec25779.digitalid.model.BiologicalSex;
import com.github.ec25779.digitalid.model.DigitalId;
import com.github.ec25779.digitalid.model.DigitalIdStatus;
import com.github.ec25779.digitalid.model.IdentityNotFoundException;
import com.github.ec25779.digitalid.portal.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CliTest {

    private static final UUID TEST_ID = UUID.randomUUID();

    @Mock CentralAuthorityPortal centralAuthorityPortal;
    @Mock TaxAuthorityPortal taxAuthorityPortal;
    @Mock DrivingLicenceAuthorityPortal dvlaPortal;
    @Mock BankPortal bankPortal;
    @Mock HealthServicePortal healthPortal;

    private StringWriter writer;

    @BeforeEach
    void setUp() {
        writer = new StringWriter();
    }

    private String run(String... lines) {
        String script = String.join("\n", lines) + "\n";
        Scanner scanner = new Scanner(new StringReader(script));
        PrintWriter out = new PrintWriter(writer, true);

        Cli cli = new Cli(scanner, out, centralAuthorityPortal, taxAuthorityPortal, dvlaPortal, bankPortal,
            healthPortal);
        cli.run();

        return writer.toString();
    }

    @Test
    void testExitImmediatelyDoesNothing() {
        String output = run("0");
        assertThat(output).contains("Digital ID System CLI", "Goodbye.");
        verifyNoInteractions(centralAuthorityPortal, taxAuthorityPortal, dvlaPortal, bankPortal, healthPortal);
    }

    @Test
    void testRejectsUnknownPortal() {
        String output = run("INVALID_PORTAL", "0");
        assertThat(output).contains("Unknown portal.");
    }

    @Test
    void testRejectsUnknownSubcommand() {
        String output = run("4", "INVALID_SUBCOMMAND", "back", "0");
        assertThat(output).contains("Unknown.", "verify-exists, verify-loan");
        verifyNoInteractions(bankPortal);
    }

    @Test
    void testRejectsInvalidId() {
        DigitalId identity = new DigitalId(TEST_ID, Instant.parse("2026-01-01T00:00:00Z"), LocalDate.of(1990, 1, 1),
            "London", BiologicalSex.MALE, "John Doe", "1 Main St", DigitalIdStatus.SUSPENDED);
        when(centralAuthorityPortal.suspendIdentity(TEST_ID)).thenReturn(identity);

        String output = run("1", "suspend", "INVALID_UUID", TEST_ID.toString(), "back", "0");

        assertThat(output).contains("Invalid input, try again.");
        verify(centralAuthorityPortal).suspendIdentity(TEST_ID);
    }

    @Test
    void testCentralAuthorityCreate() {
        DigitalId identity = new DigitalId(TEST_ID, Instant.parse("2026-01-01T00:00:00Z"), LocalDate.of(1990, 1, 1),
            "London", BiologicalSex.MALE, "John Doe", "1 Main St", DigitalIdStatus.ACTIVE);
        when(centralAuthorityPortal.createIdentity(any(), any(), any(), any(), any())).thenReturn(identity);

        String output = run("1", "create", "1990-01-01", "London", "male", "John Doe", "1 Main St", "back", "0");

        verify(centralAuthorityPortal).createIdentity(LocalDate.of(1990, 1, 1), "London", BiologicalSex.MALE,
            "John Doe", "1 Main St");
        assertThat(output).contains("Created:", TEST_ID.toString());
    }

    @Test
    void testCentralAuthorityIdentityNotFound() {
        when(centralAuthorityPortal.lookupIdentity(TEST_ID)).thenThrow(new IdentityNotFoundException(TEST_ID));

        String output = run("1", "lookup", TEST_ID.toString(), "back", "0");

        verify(centralAuthorityPortal).lookupIdentity(TEST_ID);
        assertThat(output).contains("Rejected:");
    }

    @Test
    void testTaxPortalVerifiesTaxIdentity() {
        when(taxAuthorityPortal.verifyIdentityForTaxYear(TEST_ID, 2025)).thenReturn(true);

        String output = run("2", "verify-year", TEST_ID.toString(), "2025", "back", "0");

        verify(taxAuthorityPortal).verifyIdentityForTaxYear(TEST_ID, 2025);
        assertThat(output).contains("Verified: true");
    }

    @Test
    void testDvlaPortalChecksLicenseEligibility() {
        when(dvlaPortal.checkLicenceEligibility(TEST_ID, LicenceType.FULL)).thenReturn(LicenceEligibility.UNDERAGE);

        String output = run("3", "check-full", TEST_ID.toString(), "back", "0");

        verify(dvlaPortal).checkLicenceEligibility(TEST_ID, LicenceType.FULL);
        assertThat(output).contains("Full: UNDERAGE");
    }

    @Test
    void testBankPortalVerifiesLoanEligibility() {
        when(bankPortal.verifyIdentityEligibleForLoan(TEST_ID)).thenReturn(false);

        String output = run("4", "verify-loan", TEST_ID.toString(), "back", "0");

        verify(bankPortal).verifyIdentityEligibleForLoan(TEST_ID);
        assertThat(output).contains("Eligible for loan: false");
    }

    @Test
    void testHealthPortalVerifiesElderlyEligibility() {
        when(healthPortal.verifyIdentityEligibleForElderlyServices(TEST_ID)).thenReturn(true);

        String output = run("5", "verify-elderly", TEST_ID.toString(), "back", "0");

        verify(healthPortal).verifyIdentityEligibleForElderlyServices(TEST_ID);
        assertThat(output).contains("Eligible for elderly services: true");
    }

}
