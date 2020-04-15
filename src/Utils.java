import java.io.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

public class Utils {

  
  // compute a hash for the password when the user is created
  public static String computeHash(String pass) throws NoSuchAlgorithmException {
    MessageDigest md = null;
    String hex = "";
    md = MessageDigest.getInstance("SHA-256");
    // Change this to UTF-16 if needed
    md.update(pass.getBytes(StandardCharsets.UTF_8));
    byte[] digest = md.digest();
    hex = String.format("%064x", new BigInteger(1, digest));

    return hex;
  }

  // compute a userID by using the username to generate a hash
  public static String computeUserID(String val) {
    int p = 1000003;
		int primePower = 1;
		int key = 0;
		for (int i = 0; i < val.length(); i++) {
			key += val.charAt(i) * primePower;
			key %= 999999;
			primePower = (primePower * p) % 999999;
		}
		int curr = Math.abs(key);
    String temp = Integer.toString(curr);
    // append arbitrary digits to userID to fill 6 characters
    Random rand = new Random();
    while (temp.length() < 6) {
      temp = temp + Integer.toString((rand.nextInt(9)));
    }
    return temp;
  }

  // method to return the current time (used for logging last login)
  public static String getTime() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String currTime = dtf.format(now);
    return currTime;
  }
}
