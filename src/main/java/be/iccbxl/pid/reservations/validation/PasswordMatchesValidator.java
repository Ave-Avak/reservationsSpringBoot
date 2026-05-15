package be.iccbxl.pid.reservations.validation;

import be.iccbxl.pid.reservations.dto.UserRegistrationDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (!(obj instanceof UserRegistrationDto dto)) {
            return true; // pas notre type, on laisse passer
        }

        String password = dto.getPassword();
        String passwordConfirm = dto.getPasswordConfirm();

        // Cas où l'un des deux est null : c'est @NotBlank qui s'en chargera
        if (password == null || passwordConfirm == null) {
            return true;
        }

        boolean matches = password.equals(passwordConfirm);

        // Si non, on attache le message d'erreur au champ "passwordConfirm"
        if (!matches) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("passwordConfirm")
                .addConstraintViolation();
        }

        return matches;
    }
}