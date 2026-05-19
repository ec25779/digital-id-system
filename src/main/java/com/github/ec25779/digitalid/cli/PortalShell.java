package com.github.ec25779.digitalid.cli;

import com.github.ec25779.digitalid.model.IdentityNotFoundException;
import com.github.ec25779.digitalid.model.InvalidStateTransitionException;
import com.github.ec25779.digitalid.model.UnauthorizedOperationException;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PortalShell {

    private static final List<Class<? extends Exception>> REJECTED_EXCEPTIONS = List.of(
        UnauthorizedOperationException.class,
        IdentityNotFoundException.class,
        InvalidStateTransitionException.class
    );

    private final Scanner scanner;
    private final PrintWriter out;

    public PortalShell(@NotNull Scanner scanner, @NotNull PrintWriter out) {
        this.scanner = scanner;
        this.out = out;
    }

    public void run(@NotNull String label, @NotNull String prompt, @NotNull List<PortalCommand> commands) {
        out.println("[" + label + "] " + formatSummary(commands));
        while (true) {
            String command = readLine(prompt + "> ");
            switch (command) {
                case "" -> {
                }
                case "help" -> printHelp(commands);
                case "back" -> {
                    return;
                }
                default -> dispatch(command, commands);
            }
        }
    }

    private void dispatch(@NotNull String command, @NotNull List<PortalCommand> commands) {
        for (PortalCommand cmd : commands) {
            if (cmd.name().equals(command)) {
                try {
                    cmd.action().run();
                } catch (Exception e) {
                    if (REJECTED_EXCEPTIONS.contains(e.getClass())) {
                        out.println("Rejected: " + e.getMessage());
                    } else {
                        out.println("Error: " + e.getMessage());
                    }
                }
                return;
            }
        }

        out.println("Unknown. " + formatSummary(commands));
    }

    private @NotNull String formatSummary(@NotNull List<PortalCommand> commands) {
        String names = commands.stream().map(PortalCommand::name).collect(Collectors.joining(", "));
        return "commands: " + names + ", help, back";
    }

    private void printHelp(@NotNull List<PortalCommand> commands) {
        out.println("commands:");
        for (PortalCommand c : commands) {
            out.println("  " + c.name() + " — " + c.description());
        }
        out.println("  back — return to portal selection");
    }

    public @NotNull String readLine(@NotNull String label) {
        out.print(label);
        out.flush();

        if (!scanner.hasNextLine()) {
            return "back";
        }

        return scanner.nextLine().trim();
    }

}
