package com.tbl324.shared.validation;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    private final Validator<String> notBlankValidator = input -> {
        if (input == null || input.isBlank()) {
            return ValidationResult.invalid(Map.of("value", List.of("boş olamaz")));
        }
        return ValidationResult.valid();
    };

    @Test
    void validInputReturnsValid() {
        ValidationResult result = notBlankValidator.validate("hello");

        assertTrue(result.isValid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void blankInputReturnsInvalid() {
        ValidationResult result = notBlankValidator.validate("  ");

        assertFalse(result.isValid());
        assertTrue(result.errors().containsKey("value"));
    }

    @Test
    void nullInputReturnsInvalid() {
        ValidationResult result = notBlankValidator.validate(null);

        assertFalse(result.isValid());
    }

    @Test
    void validResultHasEmptyErrors() {
        ValidationResult result = ValidationResult.valid();

        assertTrue(result.isValid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void invalidResultHasErrors() {
        ValidationResult result = ValidationResult.invalid(Map.of("field", List.of("hata mesajı")));

        assertFalse(result.isValid());
        assertEquals(1, result.errors().size());
    }
}
