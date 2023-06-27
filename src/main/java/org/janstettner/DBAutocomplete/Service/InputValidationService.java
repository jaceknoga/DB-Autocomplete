package org.janstettner.DBAutocomplete.Service;

import org.janstettner.DBAutocomplete.DTO.ValidationError;

import java.util.Optional;

public class InputValidationService {
    private InputValidationService() {
    }

    public static Optional<ValidationError> validate(String input) {
        // check for any digit
        if (input.matches(".*\\d.*")) {
            return Optional.of(new ValidationError(
                    "001",
                    "Numeric characters are not allowed, please provide only alphabetical inputs."));
        }
        // check the length of the input with trimmed whitespace
        if (input.trim().length() < 3) {
            return Optional.of(new ValidationError(
                    "002",
                    "Input for autocomplete is too short, please provide at least three characters."));
        }
        // check if input contains only non-alphabetic characters
        if (input.matches("[^a-zA-Z]+")) {
            return Optional.of(new ValidationError(
                    "003",
                    "The input must consist of only alphabetic characters."));
        }
        return Optional.empty();
    }
}
