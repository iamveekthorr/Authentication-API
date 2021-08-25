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
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Victor Okonkwo
 */
public class UserService implements UserDao {

    private static List<UserModel> users;
    private static ResultSet user;
    static final String SALT = BCrypt.gensalt(12);

    public UserService() {
    }

    @Override
    public boolean save(UserModel model) {
        Connection conn = ConnectionDao.createConnection();
        String sql = "INSERT INTO users(_id, firstName, lastName, email, password) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            System.out.println(statement);
            model.setID((UUID.randomUUID().toString()));
            System.out.println(model.getID());
            statement.setString(1, model.getID());
            statement.setString(2, model.getFirstName());
            statement.setString(3, model.getLastName());
            statement.setString(4, model.getEmail());
            statement.setString(5, BCrypt.hashpw(model.getPassword(),SALT));
            statement.execute();
            users.add(model);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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
                model.setFirstName(user.getString("email"));
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
