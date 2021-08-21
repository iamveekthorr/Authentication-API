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
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.UserModel;

/**
 *
 * @author Victor Okonkwo
 */
public class UserService implements UserDao{
    
    List<UserModel> users;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public UserModel getById(int id) {
        Connection conn = ConnectionDao.createConnection();
        String sql = "SELECT * FROM users WHERE ID = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.execute();
            return users.get(id);
        } catch (SQLException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "SQL Exception", ex);
        }
        return users.get(id);
    }

    @Override
    public List<UserModel> getAll() {
        return null;
    }

    @Override
    public String getByEmail(String email) {
        Connection conn = ConnectionDao.createConnection();
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.execute();
            return users.get(0).getEmail(email);
        } catch (SQLException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "SQL Exception", ex);
        }
        return users.get(0).getEmail(email);
    }
    
    
}
