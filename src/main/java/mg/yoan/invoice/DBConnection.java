package mg.yoan.invoice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public Connection getConnection() {
        try {
//            String jdbcURl = System.getenv("JDBC_URl"); //
//            String user = System.getenv("JDBC_"); //mini_dish_db_manager
//            String password = System.getenv("123456"); //123456
            return DriverManager.getConnection("jdbc:postgresql://193.180.213.193:1552/postgres", "yotechadmin", "YoTech25@");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
