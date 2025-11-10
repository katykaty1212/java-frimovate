package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmValidationTest {
    private Validator validator;
    private Film film;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
    }

    @Test
    public void nameAnnotationNotBlankTest() {
        film.setName("   ");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);//  validator проверяет все аннотации и возвращает Set нарушений

        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации");

        boolean hasBlankError = violations.stream().
                anyMatch(v -> v.getMessage().contains("не может быть пустым"));

        assertTrue(hasBlankError, "Должна быть ошибка пустого поля");
    }

    @Test
    public void descriptionSizeTest() {
        film.setDescription("F".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации");

        film.setDescription("F".repeat(200));

        violations = validator.validate(film);

        assertTrue(violations.isEmpty(), "Не должны быть ошибки валидации");

    }

    @Test
    public void durationPositiveTest() {
        film.setDuration(-10);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);


        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации");

        boolean hasPositiveError = violations.stream()
                .anyMatch(v -> v.getMessage()
                        .contains("Продолжительность фильма должна быть положительным числом"));

        assertTrue(hasPositiveError, "Должна быть ошибка положительного числа");
    }
}