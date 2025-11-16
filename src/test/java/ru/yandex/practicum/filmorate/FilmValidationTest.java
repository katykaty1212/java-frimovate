package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
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
        film.setDuration(Duration.ofMinutes(120));
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
    public void releaseDateValidationTest() {

        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации для даты 1895-12-27");

        boolean hasReleaseDateError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Дата релиза должна быть после 28 декабря 1895 года"));

        assertTrue(hasReleaseDateError, "Должна быть ошибка валидации даты релиза");


        film.setReleaseDate(LocalDate.of(2001, 1, 1));

        violations = validator.validate(film);

        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации для даты 2001-01-01");
    }

    @Test
    public void durationPositiveTest() {

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации для положительной продолжительности");

        film.setDuration(Duration.ofMinutes(-10));

        violations = validator.validate(film);

        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации для отрицательной продолжительности");

        boolean hasDurationError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Продолжительность фильма должна быть положительной"));

        assertTrue(hasDurationError, "Должна быть ошибка валидации продолжительности");
    }
}