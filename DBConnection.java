import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to your database
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/registrationdb",
                    "root",
                    ""
            );

            return con;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
