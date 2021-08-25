/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import dao.ConnectionDao;
import dao.UserDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.UserModel;

/**
 *
 * @author Victor Okonkwo
 */
public class UserService implements UserDao {

    private static List<UserModel> users;
    private static ResultSet user;

    public UserService() {
    }

    @Override
    public void save(UserModel model) {
        Connection conn = ConnectionDao.createConnection();
        String sql = "INSERT INTO users(_id, firstName, lastName, email, password)"
                + " VALUES(?,?,?,?,?)";
        try {
            model.setID((UUID.randomUUID().toString()));
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, model.getID());
            statement.setString(2, model.getFirstName());
            statement.setString(3, model.getLastName());
            statement.setString(4, model.getEmail());
            statement.setString(5, model.getPassword());
            statement.execute();
            
            System.out.println(model.getID());
            users.add(model);
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean update(UserModel m, String... params) {
        ConnectionDao.createConnection();
        return true;
    }

    @Override
    public void delete(UserModel m) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UserModel getById(int id) {
        Connection conn = ConnectionDao.createConnection();
        users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE ID = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.execute();
            statement.setInt(1, id);
            user = statement.executeQuery();
            while (user.next()) {
                return users.get(0);
            }
        } catch (SQLException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "SQL Exception", ex);
        }
        return null;
    }

    @Override
    public List<UserModel> getAll() {
        return null;
    }

    @Override
    public UserModel getByEmail(String email, String password) {
        Connection conn = ConnectionDao.createConnection();
        UserModel model = new UserModel();
        String sql = password.equalsIgnoreCase(" ") ? "SELECT * FROM users WHERE email = ?"
                : "SELECT * FROM users WHERE email = ? AND password = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            users = new ArrayList<>();
            statement.setString(1, email);
            if (!password.equalsIgnoreCase(" ")) {
                statement.setString(2, password);
            }
            user = statement.executeQuery();
            // returns true if result of Query(sql) > 0
            while (user.next()) {
                model.setEmail(email);
                model.setFirstName(user.getString("name"));
                model.setID((String) user.getObject("_id"));
                users.add(model);
                return users.get(0);
            }
        } catch (SQLException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "SQL Exception", ex);
        }
        return null;
    }

}
