/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import services.AuthenticationService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
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

    protected static Connection DATABASE_CONNECTION;

    private List<UserModel> users = null;

    public UserDao() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            DATABASE_CONNECTION = DriverManager.getConnection(AuthenticationService.properties.getProperty("DATABASE_URL"),
                    AuthenticationService.properties.getProperty("DATABASE_USER"),
                    AuthenticationService.properties.getProperty("DATABASE_PASSWORD"));
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "SQL Exception", ex);
        }
    }

    @Override
    public UserModel getById(int id) {
        return users.get(id);
    }

    @Override
    public List<UserModel> getAll() {
        return users;
    }

    @Override
    public void save(UserModel model) {
        users.add(model);
    }

    @Override
    public void update(UserModel model, String... params) {
        users.get((int) model.getId()).setUserName(model.getUserName());
    }

    @Override
    @SuppressWarnings("element-type-mismatch")
    public void delete(UserModel model) {
        users.remove(model.getId());
    }

}
