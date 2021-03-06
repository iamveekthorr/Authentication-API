/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import java.util.Optional;
import models.UserModel;

/**
 *
 * @author Victor Okonkwo
 */
public interface UserDao{
    UserModel getById(Object id);
    
    List<UserModel> getAll();
    
    public boolean save(UserModel m);
    
    public boolean update(UserModel m, String...params);
    
    public void delete(UserModel m);
    
    public UserModel getByEmail(String email, Optional<String> password);
}
