package pet_hosting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class jdbc {
    public static Connection Getconnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(" JDBC Driver not found.");
            throw new SQLException("Driver load failed");
        }

        return DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/pet_hosting",
                "root",
                "bhavishya@22");
    }
}

