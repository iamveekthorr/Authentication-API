/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Victor Okonkwo
 * @param <Model>
 */
public interface Dao <Model>{
    Optional<Model> getById(long id);
    
    List<Model> getAll();
    
    public void save(Model m);
    
    public void update(Model m, String...params);
    
    public void delete(Model m);
}
