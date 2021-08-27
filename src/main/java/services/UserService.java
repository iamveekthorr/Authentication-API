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
import java.util.Optional;
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
            model.setID((UUID.randomUUID().toString()));
            statement.setString(1, model.getID());
            statement.setString(2, model.getFirstName());
            statement.setString(3, model.getLastName());
            statement.setString(4, model.getEmail());
            statement.setString(5, BCrypt.hashpw(model.getPassword(), SALT));
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
    public UserModel getByEmail(String email, Optional<String> password) {
        // 1a) Create connection to database
        Connection conn = ConnectionDao.createConnection();
        UserModel model = new UserModel();
        users = new ArrayList<>();

        // 1b) create Query(sql) 
        String sql = "SELECT * FROM users WHERE email = ?";

        try {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, email);
            // 2a) Execute Query and assign (Returns a ResultSet)
            user = statement.executeQuery();

            // 2b) Check if password is present in the parameter list 
            /**
             * If password is present then compare plain password to the stored
             * password in the database. * ONLY RUNS FOR CHECKING IF USER EXIST
             * BEFORE CREATING AND ACCOUNT. TO AVOID DUPLICATE FIELDS IN THE
             * DATABASE AND LOGGIN IN A USER*
             */
            if (password.isPresent()) {
                // Runs as long as ResultSet is > 0
                while (user.next()) {
                    // 2c) Compare plain user password with the stored password in the database
                    if (BCrypt.checkpw(password.get(), user.getString("password"))) {
                        model.setEmail(email);
                        model.setFirstName(user.getString("firstName"));
                        model.setLastName(user.getString("lastName"));
                        model.setID((String) user.getObject("_id"));
                        users.add(model);
                        // returns the user found 
                        return users.get(0);
                    }
                }
                return null;
            }
            // returns true if result of Query(sql) > 0
            /* ONLY RUNS FOR SIGN-UP */
            while (user.next()) {
                model.setEmail(email);
                users.add(model);
                return users.get(0);
            }
        } catch (SQLException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "SQL Exception", ex);
        }
        return null;
    }

}
