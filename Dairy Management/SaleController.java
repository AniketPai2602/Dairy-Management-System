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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
 *
 * @author Wipro
 */
public class SaleController implements Initializable {
   DisplayDatabase displayMilkData = new DisplayDatabase();
    @FXML
    private TextField sId;
  
    @FXML
    private TextField sName;
    @FXML
    private TextField sQty;
    @FXML
    private TextField sRate;
    @FXML
    private ComboBox<String> sMilkType;
    @FXML
    private DatePicker sDate;
    @FXML
    private Button submitBtn;
    @FXML
    private Label tAmount;
    @FXML
    private TableView<?> cTablevView;

   /**
     * Initializes the controller class.
     */ 
    String sid="";
        String name="";
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
     sMilkType.setItems(milkTypeList);
     displayMilkData.buildData(cTablevView, "Select * from salesTable Order By(Id) desc;");
     
    }    

    @FXML
    private void submit(ActionEvent event) throws SQLException {
         name = sName.getText();
      
        qty = sQty.getText();
        rate = sRate.getText();
       
        date = sDate.getValue();
        sid = sId.getText();
        milktype= sMilkType.getValue();
         Connection c;
       if(milktype==null || milktype.isEmpty()){
        sMilkType.requestFocus();
        return;
        }
         
     if(name==null || name.isEmpty()){
        sName.requestFocus();
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
         
      
         try{
            c = DBConnection.connect();
             String query = "insert into SalesTable (date,StaffId,dealerName,Milktype,QTY,Rate,TotalAmount) values ('"+date+"','"+sid+"','"+name+"','"+milktype+"','"+qty+"','"+rate+"','"+amount+"');";
            if(update == true){
           
              query = "Update salestable set Date='"+date+"',StaffId='"+sid+"',DealerName='"+name+"',"
                   + "MilkType='"+milktype+"',Qty='"+qty+"',Rate='"+rate+"',TotalAmount='"+amount+"' Where Id='"+id+"';";
                 
          c.createStatement().execute(query);
              
         }
           
            PreparedStatement ps;
            ps = c.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS);
            ps.execute();
              ResultSet rs = ps.getGeneratedKeys();
              rs.next();
              int sId = Integer.parseInt(rs.getString(1));
              
            
            
             rs = QueryDatabase.query("Select MilkType from inventorytable where MilkType='"+milktype+"';");
            if(rs==null){
                query = "insert into InventoryTable (MilkType,Qty) values ('"+milktype+"','"+0+"');";
                c.createStatement().execute(query);
            }else{
                if(!rs.next()){
                     query = "insert into InventoryTable (MilkType,Qty) values ('"+milktype+"','"+0+"');";
                    c.createStatement().execute(query);
                }else{
                 query = "Update InventoryTable set Qty=Qty-"+qty+" where MilkType='"+milktype+"';";
                c.createStatement().execute(query);
                }
             
            } 
            
             query = "insert into TransactionTable (Date,EName,Amount,Type,Description) values ('"+date+"','"+sid+"','"+amount+"','Debit','"+sId+"');";
                c.createStatement().execute(query);
          
           
                
//              query = "insert into sellerTable (Name,Contact,Address) values ('"+name+"','"+contact+"','"+address+"');";
//                c.createStatement().execute(query);

                c.close();
          
            
        } catch (SQLException ex) {
            Logger.getLogger(SaleController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
         
         
         
           
            sName.clear();
           
            sQty.clear();
            sRate.clear();
            tAmount.setText("0.0");
            sId.clear();
            amount =0;
            update=false;
        submitBtn.setText("Add");
            displayMilkData.buildData(cTablevView, "Select * from SalesTable Order By(Id) desc;");
            
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
        
         int index = cTablevView.getSelectionModel().getSelectedIndex();
        ObservableList<ObservableList> data = displayMilkData.getData();
        ObservableList<String> row = data.get(index);
        DeleteDatabase.deleteRecord(Integer.parseInt(row.get(0)), "SalesTable");
        displayMilkData.buildData(cTablevView, "Select * from SalesTable Order By(Id) desc;");

         Connection c;
         try{
            c = DBConnection.connect();
               String query = "Update InventoryTable set Qty=Qty+"+Integer.parseInt(row.get(5))+" where MilkType='"+row.get(4)+"';";
                c.createStatement().execute(query);
               c.close();
        
    }   catch (SQLException ex) {
            Logger.getLogger(SellerController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
 int id;
boolean update = false;
    String oldQty="0";
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @FXML
    private void UpdateRecord(ActionEvent event) {
        int index = cTablevView.getSelectionModel().getFocusedIndex();
      ObservableList<ObservableList> data = displayMilkData.getData();
      ObservableList<String> itemData = data.get(index);
      
      oldQty = itemData.get(7);
      id = Integer.parseInt(itemData.get(0));
        String[] sdate = itemData.get(1).split(" ");  // get only date from DateTime
               sDate.setValue(LocalDate.parse(sdate[0],format));
     
       sMilkType.setValue(itemData.get(4));
      sName.setText(itemData.get(3));
        sQty.setText(itemData.get(5));
        sRate.setText(itemData.get(6));
        tAmount.setText(oldQty);
        sId.setText(itemData.get(2));
       
        update=true;
        submitBtn.setText("Update");
    }
    
}
