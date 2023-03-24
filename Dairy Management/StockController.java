/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MilkManagement;

import DB.DisplayDatabase;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Wipro
 */
public class StockController implements Initializable {

    private TextField sName;
    @FXML
    private TableView<?> sTableView;

    /**
     * Initializes the controller class.
     */ String name="";
     DisplayDatabase displayStockData = new DisplayDatabase();
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
         displayStockData.buildData(sTableView, "Select * from InventoryTable;");
    }    

   

    
}
