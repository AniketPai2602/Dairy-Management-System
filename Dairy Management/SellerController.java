/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MilkManagement;

import DB.DBConnection;
import DB.DeleteDatabase;
import DB.DisplayDatabase;
import DB.QueryDatabase;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Wipro
 */
public class SellerController implements Initializable {
   DisplayDatabase displayMilkData = new DisplayDatabase();
    @FXML
    private TextField sId;
    @FXML
    private TextField sContact;
    @FXML
    private TextField sAddress;
    @FXML
    private TextField sName;
    @FXML
    private TextField sQty;
    @FXML
    private TextField sRate;
    @FXML
    private ComboBox<String> sMilkType;
    @FXML
    private Label tAmount;
    @FXML
    private DatePicker sDate;
    @FXML
    private Button submitBtn;
    @FXML
    private TableView<?> sTablevView;

    /**
     * Initializes the controller class.
     */ String sid="";
        String name="";
        String contact="";
        String address="";
        String qty="";
        String rate="";
        String milktype="";
        double amount= 0;
        LocalDate date;
        
    ObservableList<String> milkTypeList = FXCollections.observableArrayList();
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
       
     milkTypeList.add("Cow");
     milkTypeList.add("Buffalo");
      milkTypeList.add("Goat");
     sMilkType.setItems(milkTypeList);
     displayMilkData.buildData(sTablevView, "Select * from purchaseTable Order By(Id) desc;");
     
    }    

    @FXML
    private void submit(ActionEvent event) {
         name = sName.getText();
        contact = sContact.getText();
        address = sAddress.getText();
        qty = sQty.getText();
        rate = sRate.getText();
       
        date = sDate.getValue();
        sid = sId.getText();
        milktype= sMilkType.getValue();
        
       if(milktype==null || milktype.isEmpty()){
        sMilkType.requestFocus();
        return;
        }
         
     if(name==null || name.isEmpty()){
        sName.requestFocus();
        return;
        }
          if(contact==null || contact.isEmpty()){
        sContact.requestFocus();
        return;
        }
         if(address==null || address.isEmpty()){
        sAddress.requestFocus();
        return;
         }
   
        
         
         if(qty==null || qty.isEmpty()){
        sQty.requestFocus();
        return;
        }
         
         if(rate==null || rate.isEmpty()){
        sRate.requestFocus();
        return;
        }
         
         
         
         if(sid==null || sid.isEmpty()){
        sId.requestFocus();
        return;
        }
         calcAmount();
         
         Connection c;
         try{
            c = DBConnection.connect();
            
            String query = "insert into PurchaseTable (date,StaffId,SellerName,Contact,Address,Milktype,QTY,Rate,TotalAmount) values ('"+date+"','"+sid+"','"+name+"','"+contact+"','"+address+"','"+milktype+"','"+qty+"','"+rate+"','"+amount+"');";
            PreparedStatement ps;
            ps = c.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS);
            ps.execute();
              ResultSet rs = ps.getGeneratedKeys();
              rs.next();
              int pId = Integer.parseInt(rs.getString(1));
              
            
            
             rs = QueryDatabase.query("Select MilkType from inventorytable where MilkType='"+milktype+"';");
            if(rs==null){
                query = "insert into InventoryTable (MilkType,Qty) values ('"+milktype+"','"+qty+"');";
                c.createStatement().execute(query);
            }else{
                if(!rs.next()){
                     query = "insert into InventoryTable (MilkType,Qty) values ('"+milktype+"','"+qty+"');";
                    c.createStatement().execute(query);
                }else{
                 query = "Update InventoryTable set Qty=Qty+"+qty+" where MilkType='"+milktype+"';";
                c.createStatement().execute(query);
                }
             
            } 
            
             query = "insert into TransactionTable (Date,EName,Amount,Type,Description) values ('"+date+"','"+sid+"','"+amount+"','Credit','"+pId+"');";
                c.createStatement().execute(query);
          
           
                
              query = "insert into sellerTable (Name,Contact,Address) values ('"+name+"','"+contact+"','"+address+"');";
                c.createStatement().execute(query);

                c.close();
          
            
        } catch (SQLException ex) {
            Logger.getLogger(SaleController.class.getName()).log(Level.SEVERE, null, ex);
        }
         
         
           
            sName.clear();
            sContact.clear();
            sAddress.clear(); 
            sQty.clear();
            sRate.clear();
            tAmount.setText("0.0");
            sId.clear();
            amount =0;
           
            displayMilkData.buildData(sTablevView, "Select * from purchaseTable Order By(Id) desc;");
            
    }
     
    @FXML
    private void calcAmount() {
        qty = sQty.getText();
        rate = sRate.getText();
          
         if(qty==null || qty.isEmpty()){
        sQty.requestFocus();
        return;
        }
         
         if(rate==null || rate.isEmpty()){
        sRate.requestFocus();
        return;
        }
        
        amount = Double.parseDouble(qty)*Double.parseDouble(rate); 
         tAmount.setText(String.format("%.2f", amount));
         
    }

    @FXML
    private void mDeleteRecord(ActionEvent event) {
        
         int index = sTablevView.getSelectionModel().getSelectedIndex();
        ObservableList<ObservableList> data = displayMilkData.getData();
        ObservableList<String> row = data.get(index);
        DeleteDatabase.deleteRecord(Integer.parseInt(row.get(0)), "PurchaseTable");
        displayMilkData.buildData(sTablevView, "Select * from purchaseTable Order By(Id) desc;");

         Connection c;
         try{
            c = DBConnection.connect();
               String query = "Update InventoryTable set Qty=Qty-"+Integer.parseInt(row.get(7))+" where MilkType='"+row.get(6)+"';";
                c.createStatement().execute(query);
               c.close();
        
    }   catch (SQLException ex) {
            Logger.getLogger(SellerController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
}
