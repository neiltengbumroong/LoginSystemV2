import java.util.regex.*;

public class Validator {
  final String USER_VALIDATOR = "(.{4,13})";
  final String PASS_VALIDATOR = "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,20})";
  final String BDAY_VALIDATOR = "\\d{2}/\\d{2}/\\d{4}";
  final String NAME_VALIDATOR = "[a-zA-Z_]+";

  // first and last names can only contain letters
  public boolean validateName(String string) {
    Pattern pattern = Pattern.compile(NAME_VALIDATOR);
    Matcher matcher = pattern.matcher(string);
    return matcher.matches();
  }

  // usernames must be between 4 and 13 characters
  public boolean validateUsername(String string) {
    Pattern pattern = Pattern.compile(USER_VALIDATOR);
    Matcher matcher = pattern.matcher(string);
    return matcher.matches();
  }

  // passwords must contain a digit, lower/uppercase letter, and between 6-20 letters
  public boolean validatePassword(String string) {
    Pattern pattern = Pattern.compile(PASS_VALIDATOR);
    Matcher matcher = pattern.matcher(string);
    return matcher.matches();
  }

  // birthday must be in mm/dd/yyyy format
  public boolean validateBirthday(String string) {
    Pattern pattern = Pattern.compile(BDAY_VALIDATOR);
    Matcher matcher = pattern.matcher(string);
    return matcher.matches();
  }
}
