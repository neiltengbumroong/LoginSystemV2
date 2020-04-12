import java.io.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

import java.util.regex.*;

import java.sql.*;

public class Server {
  HashMap<String, User> users; //hashmap listed by username and User object
  HashSet<String> usernames; // hashset containing lowercase users
  HashSet<String> userIDs; // hashset containing userIDs
  boolean write = false;
  static Validator validator;

  User currentUser;

  Connection connection;
  Statement createStmt;
  PreparedStatement insertStmt;
  PreparedStatement deleteStmt;
  PreparedStatement searchStmt;
  PreparedStatement updateStmt;


  // standard constructor for server
  public Server() {
    users = new HashMap<String, User>();
    usernames = new HashSet<String>();
    userIDs = new HashSet<String>();
    validator = new Validator();
    currentUser = null;

    // establish database connection with SQLite
    try {
      // main driver plus create table if it doesn't exist
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:Users.db");

      createStmt = connection.createStatement();
      //createStmt.executeUpdate("DROP TABLE IF EXISTS Users");
      String sql = "CREATE TABLE IF NOT EXISTS Users " +
                    "(UserID INT PRIMARY KEY NOT NULL, " +
                    "Type TEXT, " +
                    "First TEXT," +
                    "Last TEXT, " +
                    "Username TEXT NOT NULL, " +
                    "Password TEXT, " +
                    "Date of Birth TEXT, " +
                    "LastLogin TEXT)";
      createStmt.executeUpdate(sql);


      // create PreparedStatement objects for future use
      insertStmt = connection.prepareStatement("INSERT INTO USERS VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
      deleteStmt = connection.prepareStatement("DELETE FROM USERS WHERE Username=?");
      searchStmt = connection.prepareStatement("SELECT * FROM USERS WHERE Username=? AND Password=?");
      updateStmt = connection.prepareStatement("UPDATE USERS SET LastLogin=? WHERE Username=?");


    }
    catch ( Exception e ) {
     System.err.println( e.getClass().getName() + ": " + e.getMessage() );
     System.exit(0);
    }
  }

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
    return Integer.toString(curr);
  }

  // method to return the current time (used for logging last login)
  public static String getTime() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    String currTime = dtf.format(now);
    return currTime;
  }

  public boolean checkValidCredentials(String user, String pass) {
    String passAttempt = "";
    try {
      passAttempt = computeHash(pass);
    }
    catch (NoSuchAlgorithmException n) {
      n.printStackTrace();
    }

    ResultSet rs;
    try {
      searchStmt.setString(1, user);
      searchStmt.setString(2, passAttempt);
      rs = searchStmt.executeQuery();
      if (rs.next()) {
        return true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;

  }


  public boolean createUser(String first, String last, String username, String password, String birthday, boolean isAdmin) {
    try {
      password = computeHash(password);
    }
    catch (NoSuchAlgorithmException n) {
      n.printStackTrace();
    }

    String userID = computeUserID(username);

    // create and add user to hashmap with appropriate elements
    User newUser = new User(first, last, username, password, birthday, isAdmin, userID);
    newUser.setLastLogin(getTime());

    // attempt to add user to database
    try {
      insertStmt.setString(1, userID);
      if (isAdmin) {
        insertStmt.setString(2, "admin");
      } else {
        insertStmt.setString(2, "user");
      }
      insertStmt.setString(3, first);
      insertStmt.setString(4, last);
      insertStmt.setString(5, username);
      insertStmt.setString(6, password);
      insertStmt.setString(7, birthday);
      insertStmt.setString(8, null);
      insertStmt.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("User added successfully!");
    this.users.put(newUser.getUserName(), newUser);
    this.usernames.add(username.toLowerCase());
    this.userIDs.add(userID);
    return true;
  }


  public boolean deleteUser(String username) {
    if (users.containsKey(username)) {
      users.remove(username);
      if (usernames.contains(username.toLowerCase())) {
        usernames.remove(username.toLowerCase());
        this.write = true;
        return true;
      }
    }
    return false;
  }

  public void createUserPath() {
    Scanner scanner = new Scanner(System.in);
    // handle first name
    System.out.print("Please enter your first name: ");
    String first = "";
    while (true) {
      first = scanner.next();
      if (validator.validateName(first)) {
        break;
      }
      System.out.println("Invalid name!");
      System.out.print("Please enter your first name: ");
    }

    // handle last name
    System.out.print("Please enter your last name: ");
    String last = "";
    while (true) {
      last = scanner.next();
      if (validator.validateName(last)) {
        break;
      }
      System.out.println("Invalid name!");
      System.out.print("Please enter your last name: ");
    }

    // handle username
    System.out.print("Please choose your username: ");
    String username = "";
    while (true) {
      username = scanner.next();
      String tempChecker = username.toLowerCase();
      if (this.usernames.contains(tempChecker) || (this.users.containsKey(username)))  {
        System.out.println("Username already taken! Please choose another");
        System.out.print("Please choose your username: ");
        continue;
      }
      else if (validator.validateUsername(username)) {
        break;
      }
      else {
        System.out.println("Username must be between 6 and 13 characters");
      }
    }

    // handle password
    System.out.print("Please choose a password: ");
    String password = "";
    while (true) {
      password = scanner.next();
      if (validator.validatePassword(password)) {
        break;
      }
      System.out.println("Password must contain at least one uppercase, one lowercase, one digit, and be between 6 and 20 characters");
      System.out.print("Please choose a password: ");
    }

    // handle birthday
    System.out.print("Please enter your birthday (mm-dd-yyyy): ");
    String birthday = "";
    while (true) {
      birthday = scanner.next();
      if (validator.validateBirthday(birthday)) {
        break;
      }
      System.out.println("Invalid birthday! Birthday must be in mm-dd-yyyy format");
      System.out.print("Please enter your birthday (mm-dd-yyyy): ");
    }

    // create the final user
    if (this.createUser(first, last, username, password, birthday, false)) {
      System.out.println("User created succesfully!");
    }
  }


  public void loginPath() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Username: ");
    String username = scanner.next();
    System.out.print("Password: ");
    String password = scanner.next();

    if (this.checkValidCredentials(username, password)) {
      System.out.println("Login successful!");
      try {
        updateStmt.setString(1, getTime());
        updateStmt.setString(2, username);
        updateStmt.executeUpdate();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    else {
      System.out.println("Login unsuccessful! Username and password combination could not be found");
    }

  }

  public static void printUsage() {
    System.out.println("c - create account. Terminal will prompt for user information, such as creating username and password.");
    System.out.println("l - login. Terminal will prompt for user and password, and will verify matching user to password hash.");
    System.out.println("d - delete account. Terminal will prompt for username and password to delete.");
    System.out.println("What else would you like to do?" + "\n" +
                        "c - create account" + "\n" +
                        "l - login" + "\n" +
                        "d - delete account" + "\n" +
                        "e - exit");
  }





  public void executeProgram() {
    System.out.println("Welcome! What would you like to do?" + "\n" +
                        "c - create account" + "\n" +
                        "l - login" + "\n" +
                        "d - delete account" + "\n" +
                        "e - exit");

    Scanner scanner = new Scanner(System.in);
    String input = scanner.next();

    // keep looping until e or exit is typed
    while (!input.equals("e") && !input.equals("exit")) {
      this.write = true;
      // handles user creation
      if (input.equals("c")) {
        this.createUserPath();
      }
      // handles user login
      else if (input.equals("l")) {
        this.loginPath();
      }
      // handles user login
      else if (input.equals("d")) {
        //TODO
      }
      // handle help flag
      else if (input.equals("h")) {
        printUsage();

      }
      // print prompts again
      System.out.println("What else would you like to do?" + "\n" +
                          "c - create account" + "\n" +
                          "l - login" + "\n" +
                          "d - delete account" + "\n" +
                          "e - exit");

      input = scanner.next();
    }
    try {
      createStmt.close();
      insertStmt.close();
      deleteStmt.close();
      searchStmt.close();
      updateStmt.close();
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
