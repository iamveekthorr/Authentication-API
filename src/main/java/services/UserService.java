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
import java.util.logging.Level;
import java.util.logging.Logger;
import models.UserModel;

/**
 *
 * @author Victor Okonkwo
 */
public class UserService implements UserDao{
    
    private static List<UserModel> users;
    private static ResultSet user;
    public UserService() {
    }
    @Override
    public void save(UserModel m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        UserModel model = new UserModel();
        users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE ID = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.execute();
            statement.setInt(1, id);
            user = statement.executeQuery();
            while(user.next()){
                model.setID(user.getInt("_id"));
                users.add(model);
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
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            users = new ArrayList<>();
            statement.setString(1, email);
            statement.setString(2, password);
            user = statement.executeQuery();
            while(user.next()){
                model.setEmail(email);
                model.setUserName(user.getString("name"));
                model.setID(user.getInt("_id"));
                users.add(model);
                return users.get(0);
            }
        } catch (SQLException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "SQL Exception", ex);
        }
        return null;
    }
    
    
}
