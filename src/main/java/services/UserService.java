/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import dao.UserDao;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import models.UserModel;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Victor Okonkwo
 */
public class UserService implements UserDao {

    UserModel usermodel;
    private static final EntityManagerFactory entityManager = Persistence.createEntityManagerFactory("persistentUnit");
    private static final EntityManager entity = entityManager.createEntityManager();
    static final String SALT = BCrypt.gensalt(12);

    public UserService() {
    }

    @Override
    public boolean save(UserModel model) {
        Query query = entity.createNativeQuery("INSERT INTO users(_id, firstName, lastName, email, password) "
                + "VALUES(?,?,?,?,?)");
        entity.getTransaction().begin();
        query.setParameter("_id", model.getID());
        query.setParameter("firstName", model.getFirstName());
        query.setParameter("lastName", model.getLastName());
        query.setParameter("email", model.getEmail());
        query.setParameter("password", BCrypt.hashpw(model.getPassword(), SALT));
        entity.persist(model);
        entity.getTransaction().commit();
        return true;
    }

    @Override
    public boolean update(UserModel m, String... params) {
        return true;
    }

    @Override
    public void delete(UserModel m) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UserModel findById(Object _id) {
       return entity.find(UserModel.class, _id);
    }

    @Override
    public List<UserModel> findAll() {
        Query query = entity.createNativeQuery("SELECT * FROM users", UserModel.class);

        List<UserModel> users = (List<UserModel>) query.getResultList();
        Iterator iterator = users.iterator();

        while (iterator.hasNext()) {
            usermodel = (UserModel) iterator.next();
            return users;
        }
        return null;
    }

    @Override
    public UserModel findByEmail(String email, Optional<String> password) {
        
        // 1b) create Query(sql) 
        Query query = entity.createQuery("SELECT u FROM UserModel u WHERE u.email=:email", UserModel.class);
        // 2a) Execute Query and assign (Returns a ResultSet)
        List<UserModel> user = (List<UserModel>) query.setParameter("email", email).getResultList();
        Iterator iterator = user.iterator();
        // 2b) Check if password is present in the parameter list 
        if (password.isPresent()) {
            /**
             * If password is present then compare plain password to the stored
             * password in the database. *FOR CHECKING IF USER EXIST BEFORE
             * CREATING AND ACCOUNT. TO AVOID DUPLICATE FIELDS IN THE DATABASE
             * AND LOGGIN IN A USER*
             */
            while (iterator.hasNext()) {
                usermodel = (UserModel) iterator.next();
                if (BCrypt.checkpw(password.get(), usermodel.getPassword())) {
                    usermodel.setEmail(email);
                    usermodel.setFirstName(usermodel.getFirstName());
                    usermodel.setLastName(usermodel.getLastName());
                    return usermodel;
                }
            }
            return null;
        }
        // returns true if result of Query(sql) > 0
        /* ONLY RUNS FOR SIGN-UP */
        while (iterator.hasNext()) {
            usermodel = (UserModel) iterator.next();
            usermodel.setEmail(email);
            return usermodel;
        }
        return null;
    }

}
