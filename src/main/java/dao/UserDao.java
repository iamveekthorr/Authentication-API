/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import controllers.AuthenticationContoller;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.UserModel;

/**
 *
 * @author Victor Okonkwo
 */
public class UserDao implements Dao<UserModel> {

    static Connection DATABASE_CONNECTION;

    public UserDao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DATABASE_CONNECTION = DriverManager.getConnection(
                    AuthenticationContoller.properties.getProperty("DATABASE_URL"),
                    AuthenticationContoller.properties.getProperty("DATABASE_USER"),
                    AuthenticationContoller.properties.getProperty("DATABASE_PASSWORD"));
            System.out.println("Connected" + DATABASE_CONNECTION);
            System.out.println(AuthenticationContoller.properties.getProperty("DATABASE_URL") +
                    AuthenticationContoller.properties.getProperty("DATABASE_USER") +
                    AuthenticationContoller.properties.getProperty("DATABASE_PASSWORD"));
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "SQL Exception", ex);
        }
    }

    @Override
    public Optional<UserModel> getById(long id) {
        return null;
    }

    @Override
    public List<UserModel> getAll() {

        return null;
    }

    @Override
    public void save(UserModel model) {

    }

    @Override
    public void update(UserModel model, String... params) {

    }

    @Override
    public void delete(UserModel model) {

    }

}
