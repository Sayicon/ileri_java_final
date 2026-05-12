package com.tbl324.shared.validation;

import java.util.List;
import java.util.Map;

public record ValidationResult(boolean isValid, Map<String, List<String>> errors) {

    public static ValidationResult valid() {
        return new ValidationResult(true, Map.of());
    }

    public static ValidationResult invalid(Map<String, List<String>> errors) {
        return new ValidationResult(false, errors);
    }
}
