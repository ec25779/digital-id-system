package com.github.ec25779.digitalid.cli;

import com.github.ec25779.digitalid.model.BiologicalSex;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import java.util.function.Function;

public class PromptHelper {

    private final PortalShell shell;
    private final PrintWriter out;

    public PromptHelper(@NotNull PortalShell shell, @NotNull PrintWriter out) {
        this.shell = shell;
        this.out = out;
    }

    public @NotNull String prompt(@NotNull String label) {
        return shell.readLine(label);
    }

    public <T> T prompt(@NotNull String label, @NotNull Function<String, T> parser) {
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

    public UUID promptUuid(@NotNull String label) {
        return prompt(label, input -> {
            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID format.");
            }
        });
    }

    public LocalDate promptDate(@NotNull String label) {
        return prompt(label, input -> {
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format, expected YYYY-MM-DD.");
            }
        });
    }

    public BiologicalSex promptSex(@NotNull String label) {
        return prompt(label, input -> {
            try {
                return BiologicalSex.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid sex, expected male/female/other.");
            }
        });
    }

    public UUID promptDigitalId() {
        return promptUuid("Enter Digital ID (UUID): ");
    }

    public @NotNull String promptAddress() {
        return prompt("Enter address: ");
    }

    public @NotNull String promptFullName() {
        return prompt("Enter full name: ");
    }

    public LocalDate promptDateOfBirth() {
        return promptDate("Enter date of birth (YYYY-MM-DD): ");
    }

    public @NotNull String promptPlaceOfBirth() {
        return prompt("Enter place of birth: ");
    }

    public BiologicalSex promptBiologicalSex() {
        return promptSex("Enter biological sex: ");
    }

}
