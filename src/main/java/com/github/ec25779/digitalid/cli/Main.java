package com.github.ec25779.digitalid.cli;

import com.github.ec25779.digitalid.bootstrap.SystemBootstrap;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        SystemBootstrap bootstrap = new SystemBootstrap(new File("data"));
        bootstrap.setup();

        try (Scanner scanner = new Scanner(System.in)) {
            PrintWriter out = new PrintWriter(System.out, true);
            new Cli(scanner, out,
                bootstrap.createCentralAuthorityPortal(),
                bootstrap.createTaxAuthorityPortal(),
                bootstrap.createDrivingLicenceAuthorityPortal(),
                bootstrap.createBankPortal(),
                bootstrap.createHealthServicePortal()
            ).run();
        }
    }

}
