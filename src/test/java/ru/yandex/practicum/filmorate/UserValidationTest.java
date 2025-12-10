package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserValidationTest {
    private Validator validator;
    private User user;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        user = new User();
        user.setEmail("test@mail.ru");
        user.setLogin("validlogin");
        user.setName("Valid Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    public void emailAnnotationNotBlankTest() {
        user.setEmail("   ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации");

        boolean hasBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("не может быть пустой"));

        assertTrue(hasBlankError, "Должна быть ошибка пустой строки");
    }

    @Test
    public void emailAnnotationInvalidFormatTest() {
        user.setEmail("invalid-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации");

        boolean hasEmailError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("формату электронного адреса"));

        assertTrue(hasEmailError, "Должна быть ошибка формата email");
    }

    @Test
    public void loginAnnotationNotBlankTest() {
        user.setLogin("   ");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации");

        boolean hasBlankError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("не может быть пустой"));

        assertTrue(hasBlankError, "Должна быть ошибка пустой строки");
    }

    @Test
    public void loginAnnotationNoSpacesTest() {
        user.setLogin("login with spaces");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации");

        boolean hasSpaceError = violations.stream()
                .anyMatch(v -> v.getMessage().contains("не должен содержать пробелов"));

        assertTrue(hasSpaceError, "Должна быть ошибка пробелов в логине");
    }

    @Test
    public void birthdayAnnotationPastOrPresentTest() {
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty(), "Должны быть ошибки валидации");
    }
}