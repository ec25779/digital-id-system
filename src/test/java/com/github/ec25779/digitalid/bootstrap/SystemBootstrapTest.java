package com.github.ec25779.digitalid.bootstrap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class SystemBootstrapTest {

    @TempDir private Path tempDir;

    private SystemBootstrap bootstrap;

    @BeforeEach
    void setUp() {
        bootstrap = new SystemBootstrap(tempDir.toFile());
        bootstrap.setup();
    }

    @Test
    void testSetupCreatesDataDirectoryIfMissing() {
        File missing = tempDir.resolve("data").toFile();
        new SystemBootstrap(missing).setup();
        assertThat(missing).exists().isDirectory();
    }

    @Test
    void testCreatesCentralAuthorityPortal() {
        assertThat(bootstrap.createCentralAuthorityPortal()).isNotNull();
    }

    @Test
    void testCreatesTaxAuthorityPortal() {
        assertThat(bootstrap.createTaxAuthorityPortal()).isNotNull();
    }

    @Test
    void testCreatesDrivingLicenceAuthorityPortal() {
        assertThat(bootstrap.createDrivingLicenceAuthorityPortal()).isNotNull();
    }

    @Test
    void testCreatesBankPortal() {
        assertThat(bootstrap.createBankPortal()).isNotNull();
    }

    @Test
    void testCreatesHealthServicePortal() {
        assertThat(bootstrap.createHealthServicePortal()).isNotNull();
    }

}
