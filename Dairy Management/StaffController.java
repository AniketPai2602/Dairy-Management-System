/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MilkManagement;

import DB.DBConnection;
import DB.DisplayDatabase;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Wipro
 */
public class StaffController implements Initializable {
  DisplayDatabase displayStaffData = new DisplayDatabase();
   
    @FXML
    private TextField mId;
    @FXML
    private TextField mName;
    @FXML
    private TextField mContact;
    @FXML
    private TextField mAddress;
    @FXML
    private Button addBtn;
    @FXML
    private ComboBox<String> mGender;
    @FXML
    private TableView<?> tableView;

    /**
     * Initializes the controller class.
     */ 
        String id="";
        String name="";
        String gender="";
        String address="";
        String contact="";
        LocalDate date;
    
    ObservableList<String> gList = FXCollections.observableArrayList();
    @FXML
    private DatePicker sDate;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
     gList.add("M");
     gList.add("F");
     gList.add("O");
     mGender.setItems(gList);
     
      displayStaffData.buildData(tableView, "Select * from staffTable Order By(Id) desc;");
    }    

    @FXML
    private void addStaff(ActionEvent event) {
       
        id = mId.getText();
        name = mName.getText();
        contact = mContact.getText();
        address = mAddress.getText();
        gender = mGender.getValue();
        date = sDate.getValue();
       
        if(date==null ){
        sDate.requestFocus();
        return;
        }
         
    if(id==null || id.isEmpty()){
        mId.requestFocus();
        return;
        }
        
     if(name==null || name.isEmpty()){
        mName.requestFocus();
        return;
        }
          if(contact==null || contact.isEmpty()){
        mContact.requestFocus();
        return;
        }
         if(address==null || address.isEmpty()){
        mAddress.requestFocus();
        return;
         }
   
          Connection c;
         try{
            c = DBConnection.connect();
            
            String query = "insert into staffTable (date,Name,gender,Contact,Address) values ('"+date+"','"+name+"','"+gender+"','"+contact+"','"+address+"');";
            c.createStatement().execute(query);
     
            c.close();
            mId.clear();
            mContact.clear();
            mAddress.clear(); 
            mName.clear();
            
           
           
            displayStaffData.buildData(tableView, "Select * from staffTable Order By(Id) desc;");
            
            
            
        } catch (SQLException ex) {
            Logger.getLogger(StaffController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         
         
    } 
       
    }
    

