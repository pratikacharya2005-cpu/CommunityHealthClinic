package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/** Centralised input validation rules (Single Responsibility). */
public final class Validator {

    private static final Pattern ID_PATTERN = Pattern.compile("^[A-Za-z0-9-]{3,12}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z .'-]{2,60}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9 ]{8,15}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[A-Za-z]{2,}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private Validator() { }

    public static void requireText(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty())
            throw new ValidationException(fieldName + " is required and cannot be empty.");
    }

    public static void validateId(String id, String fieldName) throws ValidationException {
        requireText(id, fieldName);
        if (!ID_PATTERN.matcher(id.trim()).matches())
            throw new ValidationException(fieldName
                    + " must be 3-12 characters using letters, digits or hyphens (e.g. P001).");
    }

    public static void validateName(String name, String fieldName) throws ValidationException {
        requireText(name, fieldName);
        if (!NAME_PATTERN.matcher(name.trim()).matches())
            throw new ValidationException(fieldName
                    + " may only contain letters, spaces, apostrophes and hyphens.");
    }

    public static void validatePhone(String phone) throws ValidationException {
        requireText(phone, "Phone number");
        if (!PHONE_PATTERN.matcher(phone.trim()).matches())
            throw new ValidationException(
                    "Phone number must contain 8-15 digits and may start with '+'.");
    }

    public static void validateEmail(String email) throws ValidationException {
        requireText(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches())
            throw new ValidationException(
                    "Email must be in a valid format (e.g. name@example.com).");
    }

    public static void validateGender(String gender) throws ValidationException {
        requireText(gender, "Gender");
        if (!"Male".equals(gender) && !"Female".equals(gender) && !"Other".equals(gender))
            throw new ValidationException("Gender must be Male, Female or Other.");
    }

    public static void validateDate(String date, String fieldName) throws ValidationException {
        requireText(date, fieldName);
        try {
            LocalDate.parse(date.trim(), DATE_FORMAT);
        } catch (DateTimeParseException ex) {
            throw new ValidationException(fieldName
                    + " must be a valid date in yyyy-MM-dd format.");
        }
    }

    public static void validatePastDate(String date) throws ValidationException {
        validateDate(date, "Date of birth");
        if (LocalDate.parse(date.trim(), DATE_FORMAT).isAfter(LocalDate.now()))
            throw new ValidationException("Date of birth cannot be in the future.");
    }

    public static void validateTime(String time) throws ValidationException {
        requireText(time, "Time");
        if (!TIME_PATTERN.matcher(time.trim()).matches())
            throw new ValidationException(
                    "Time must be in 24-hour HH:mm format (e.g. 09:30).");
    }

    public static double validateFee(String fee) throws ValidationException {
        requireText(fee, "Consultation fee");
        try {
            double value = Double.parseDouble(fee.trim());
            if (value < 0)
                throw new ValidationException("Consultation fee cannot be negative.");
            return value;
        } catch (NumberFormatException ex) {
            throw new ValidationException(
                    "Consultation fee must be a numeric value (e.g. 75.00).");
        }
    }
}