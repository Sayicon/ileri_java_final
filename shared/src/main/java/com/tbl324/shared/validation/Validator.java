package com.tbl324.shared.validation;

@FunctionalInterface
public interface Validator<T> {

    ValidationResult validate(T input);
}
