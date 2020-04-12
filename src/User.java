import java.io.*;
import java.util.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class User {

  private String firstname;
  private String lastname;
  private String userID;
  private String username;
  private String password;
  private String birthday;
  private String lastlogin;
  private boolean isAdmin;

  public User(String first, String last, String user, String pass, String birthday, boolean isAdmin, String userID) {
    this.firstname = first;
    this.lastname = last;
    this.username = user;
    this.password = pass;
    this.birthday = birthday;
    this.lastlogin = "";
    this.isAdmin = isAdmin;
    this.userID = userID;
  }

  /* ---------- Getters --------------- */
  public String getFirstName() {
    return this.firstname;
  }

  public String getLastName() {
    return this.lastname;
  }

  public String getUserName() {
    return this.username;
  }

  public String getPasswordHash() {
    return this.password;
  }

  /* ---------- Setters --------------- */
  public void setFirstName(String name) {
    this.firstname = name;
  }

  public void setLastName(String name) {
    this.lastname = name;
  }

  public void setUserName(String name) {
    this.username = name;
  }

  public void setLastLogin(String time) {
    this.lastlogin = time;
  }

  @Override
  public String toString() {
    String stringToPrint = "username: " + this.username + "\n" +
                           "first name: " + this.firstname + "\n" +
                           "last name: " + this.lastname + "\n" +
                           "date of birth: " + this.birthday + "\n" +
                           "last login: " + this.lastlogin + "\n";
    return stringToPrint;
  }



}
