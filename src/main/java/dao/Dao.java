/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.util.List;

/**
 *
 * @author Victor Okonkwo
 * @param <Model>
 */
public interface Dao <Model>{
    Model getById(int id);
    
    List<Model> getAll();
    
    public void save(Model m);
    
    public void update(Model m, String...params);
    
    public void delete(Model m);
    
    /**Connection con;    
      *ResultSet rs;    
      * PreparedStatement stmt;    
      * public Employee[] findAll() throws SQLException {   
      *     Employee[] employees;       
      *     String SQL_QUERY= “Select * from Employee”;        
      *     con=ResourceManager.getConnection();        
      *     stmt = con.prepareStatement(SQL_QUERY);        
      *     rs = stmt.executeQuery();       
      *     while(rs.next) {           
      *         //get columns and store in array       
      *     }        
      *     return employees;   
      * }   
      * public Employee findByPK(EmployeePK) throws SQL Exception {       //Implementation code   }    
      * public Employee[] findbyemployeename(String EmployeeName) throws SQLException{       //Implementation code    }    
      * public boolean insert(Employee employee) throws SQLException{        //Implementation code    }    
      * public boolean update(Employee employee) throws SQLException{   
      * //Implementation code    }    
      * public boolean delete(Employee employee) throws SQLException{        //Implementation code     }}
     
     */
}
