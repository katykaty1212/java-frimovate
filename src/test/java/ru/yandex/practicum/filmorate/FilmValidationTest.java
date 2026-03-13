package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmValidationTest {
    private Validator validator;
    private Film validFilm;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        validFilm = new Film();
        validFilm.setName("Valid Film");
        validFilm.setDescription("Valid description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(Duration.ofMinutes(120));

        MPA mpa = new MPA();
        mpa.setId(1);
        validFilm.setMpa(mpa);
    }

    @Test
    public void shouldPassForValidFilm() {
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertTrue(violations.isEmpty(), "Валидный фильм не должен содержать ошибок");
    }

    @Test
    public void nameAnnotationNotBlankTest() {
        validFilm.setName("   ");
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации для пустого имени");

        boolean hasBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("не может быть пустым"));
        assertTrue(hasBlankError, "Должна быть ошибка 'не может быть пустым'");
    }

    @Test
    public void descriptionSizeTest() {
        validFilm.setDescription("F".repeat(201));
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации для описания длиннее 200 символов");

        validFilm.setDescription("F".repeat(200));
        violations = validator.validate(validFilm);
        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации для описания в 200 символов");
    }

    @Test
    public void releaseDateValidationTest() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации для даты 1895-12-27");

        validFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        violations = validator.validate(validFilm);
        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации для даты 2001-01-01");
    }

    @Test
    public void durationPositiveTest() {
        Set<ConstraintViolation<Film>> violations = validator.validate(validFilm);
        assertTrue(violations.isEmpty(), "Не должно быть ошибок валидации для положительной продолжительности");

        validFilm.setDuration(Duration.ofMinutes(-10));
        violations = validator.validate(validFilm);
        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации для отрицательной продолжительности");

        boolean hasDurationError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Продолжительность фильма должна быть положительной"));
        assertTrue(hasDurationError, "Должна быть ошибка валидации продолжительности");
    }
}