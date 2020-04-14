import java.sql.*;

public class Program {
  public static void main(String[] args) {
    Server server = new Server();
    try {
      server.parseCSVFile("../resources/generated_sample.csv");
      server.executeProgram();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
