/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import services.AuthenticationService;
import services.UserService;

/**
 *
 * @author Victor Okonkwo
 */
public class ConnectionDao extends DaoFactory {

    protected static Connection DATABASE_CONNECTION;

    public static Connection createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DATABASE_CONNECTION = DriverManager.getConnection(AuthenticationService.properties.getProperty("DATABASE_URL")
                    .concat(AuthenticationService.properties.getProperty("TIME_ZONE")),
                    AuthenticationService.properties.getProperty("DATABASE_USER"),
                    AuthenticationService.properties.getProperty("DATABASE_PASSWORD"));
            return DATABASE_CONNECTION;
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "SQL Exception", ex);
        }
        if (DATABASE_CONNECTION == null) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "SQL Exception", "Shutdown.....");
            return null;
        }
        return null;
    }

    @Override
    public UserDao getUserDAO() {
        return new UserService();
    }

}
