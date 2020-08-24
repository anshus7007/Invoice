package com.karam.invoice;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Controller implements Initializable {



    @FXML
    private TextField invoiceNum;
    @FXML
    private TextField txtName;
    @FXML
    private TextField address;
    @FXML
    private TextField shipAddress;
    @FXML
    private TextField district;
    @FXML
    private TextField gstinNum;
    @FXML
    private TextField stateCode;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label error_invoiceNum;
    @FXML
    private Label error_name;
    @FXML
    private Label error_address;
    @FXML
    private Label error_shipAddress;
    @FXML
    private Label error_district;
    @FXML
    private Label error_gstin;
    @FXML
    private Label error_stateCode;
    @FXML
    private TextField txt_search;
    @FXML
    private Button btn_manage;

//    private ProductController controller;
    int slNumber =0;
    int sl_num=0;
    private Connection con = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private ObservableList<TableData> data;
    @FXML
    private TableColumn<TableData, Integer> colSLNum;
    @FXML
    private TableColumn<TableData, Integer> colInvoiceNumber;
    @FXML
    private TableColumn<TableData, String> colName;
    @FXML
    private TableColumn<TableData, String> colAddress;
    @FXML
    private TableColumn<TableData, String> colShipAddress;
    @FXML
    private TableColumn<TableData, String> colDistrict;
    @FXML
    private TableColumn<TableData, Integer> colGSTIN;
    @FXML
    private TableColumn<TableData, Integer> colStateCode;
    @FXML
    private TableColumn<TableData, String> colDate;

    @FXML
    private TableView<TableData> taxTable;


    @FXML
    private void addUser(ActionEvent event) throws SQLException, NullPointerException {

            boolean isGoodsEmpty = TextFieldValidation.isTextFieldNotEmpty(txtName, error_name, "Name required");
            boolean isHSNCodeEmpty = TextFieldValidation.isTextFieldNotEmpty(address, error_address, "Address required");
            boolean isQuantityEmpty = TextFieldValidation.isTextFieldNotEmpty(shipAddress, error_shipAddress, "Ship Address required");
            boolean isRateEmpty = TextFieldValidation.isTextFieldNotEmpty(district, error_district, "District required");
            boolean isgstinEmpty = TextFieldValidation.isTextFieldNotEmpty(gstinNum, error_gstin, "GSTIN  required");
            boolean isStateCodeEmpty = TextFieldValidation.isTextFieldNotEmpty(stateCode, error_stateCode, "Code required");
            boolean isInvoiceNumEmpty = TextFieldValidation.isTextFieldNotEmpty(invoiceNum, error_invoiceNum, "Invoice No. required");
            if (isGoodsEmpty && isHSNCodeEmpty && isQuantityEmpty && isRateEmpty && isgstinEmpty && isStateCodeEmpty && isInvoiceNumEmpty) {
                String sql = "INSERT INTO `user_data`(`SL. No.`, `Invoice Number`,`Name`, `Address`, `Ship Address`, `District`, `GSTIN No.`, `State Code`, `Date` )" +
                        "VALUES(?,?,?,?,?,?,?,?,?);";
                int invoice_num = Integer.parseInt(invoiceNum.getText());
                String name = txtName.getText();
                String Useraddress = address.getText();
                String ship_address = shipAddress.getText();
                String UserDistrict = district.getText();
                int gstin_num = Integer.parseInt(gstinNum.getText());
                int state_code = Integer.parseInt(stateCode.getText());
                String date = String.valueOf(Date.valueOf(datePicker.getValue()));
                try {

                    slNumber=sl_num;
                    pst = con.prepareStatement(sql);
                    pst.setInt(1,++slNumber);
                    pst.setInt(2, invoice_num);
                    pst.setString(3, name);
                    pst.setString(4, Useraddress);
                    pst.setString(5, ship_address);
                    pst.setString(6, UserDistrict);
                    pst.setInt(7, gstin_num);
                    pst.setInt(8, state_code);
                    pst.setString(9, date);
                    if(!checkInvoiceNumber(invoice_num)){
                        int i = pst.executeUpdate();
                        if (i == 1)
                        {
                            setCellTable();
                            loadDataFromDatabase();
                            AlertDialog.display("Info", "Data inserted Successfully");
                             sl_num= slNumber;
                            clearTextField();
                        }
//                        Insert Failed Alert
                    }else{
                        AlertDialog.display("Error", "Invoice Number already exists");
                    }

                } catch (SQLException e) {

                    throw e;

                } finally {
                    pst.close();
                }
            }


    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            con = ConnectionModel.dbConnect();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        data = FXCollections.observableArrayList();
        setCellTable();
        loadDataFromDatabase();
        setCellValueFromTableToTextField();
        searchUser();
        try {
            rowClicked();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void setCellTable() {
        colSLNum.setCellValueFactory(new PropertyValueFactory<>("slNumber"));
        colInvoiceNumber.setCellValueFactory(new PropertyValueFactory<>("invoiceNum"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colShipAddress.setCellValueFactory(new PropertyValueFactory<>("shipAddress"));
        colDistrict.setCellValueFactory(new PropertyValueFactory<>("district"));
        colGSTIN.setCellValueFactory(new PropertyValueFactory<>("gstinNum"));
        colStateCode.setCellValueFactory(new PropertyValueFactory<>("stateCode"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

    }

    private void loadDataFromDatabase() {
    data.clear();
        try {
            pst = con.prepareStatement("SELECT * FROM  `user_data`");
            rs = pst.executeQuery();

            while (rs.next()) {
                data.add(new TableData(rs.getInt(1),rs.getInt(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getInt(7), rs.getInt(8)
                        , rs.getString(9)));
            }
        } catch (SQLException e) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);
        }
//        TableData newObj = new TableData(Integer.parseInt(invoiceNum.getText()));
//        if(taxTable.getItems().contains(String.valueOf(newObj))){
//            AlertDialog.display("Error", "Invoice Number already exists");
//
//        }
//        else
        taxTable.setItems(data);
    }

    private void setCellValueFromTableToTextField() {
        taxTable.setOnMouseClicked(e ->
        {
            TableData t1 = taxTable.getItems().get(taxTable.getSelectionModel().getSelectedIndex());
            txtName.setText(t1.getName());
            address.setText(t1.getAddress());
            shipAddress.setText(t1.getShipAddress());
            district.setText(t1.getDistrict());
            gstinNum.setText(String.valueOf(Integer.valueOf(t1.getGstinNum())));
            stateCode.setText(String.valueOf(Integer.valueOf(t1.getStateCode())));
            invoiceNum.setText(String.valueOf(Integer.valueOf(t1.getInvoiceNum())));
//            datePicker.setValue(t1.getDate());


        });
    }

    @FXML
    private void updateUser(ActionEvent event) throws SQLException, ClassNotFoundException {
        boolean isGoodsEmpty = TextFieldValidation.isTextFieldNotEmpty(txtName, error_name, "Name required");
        boolean isHSNCodeEmpty = TextFieldValidation.isTextFieldNotEmpty(address, error_address, "Address required");
        boolean isQuantityEmpty = TextFieldValidation.isTextFieldNotEmpty(shipAddress, error_shipAddress, "Ship Address required");
        boolean isRateEmpty = TextFieldValidation.isTextFieldNotEmpty(district, error_district, "District required");
        boolean isgstinEmpty = TextFieldValidation.isTextFieldNotEmpty(gstinNum, error_gstin, "GSTIN  required");
        boolean isStateCodeEmpty = TextFieldValidation.isTextFieldNotEmpty(stateCode, error_stateCode, "Code required");
        boolean isInvoiceNumEmpty = TextFieldValidation.isTextFieldNotEmpty(invoiceNum, error_invoiceNum, "Invoice No. required");

        if (isGoodsEmpty && isHSNCodeEmpty && isQuantityEmpty && isRateEmpty && isgstinEmpty && isStateCodeEmpty && isInvoiceNumEmpty)  {
            String sql = "UPDATE `user_data` SET  `Name` = ?, `Address` = ?, `Ship Address` = ?, `District` = ?, `GSTIN No.` = ?" +
                    ", `State Code` = ? WHERE `Invoice Number` = ?;";
    try
    {

            int invoice_num = Integer.parseInt(invoiceNum.getText());
            String name = txtName.getText();
            String Useraddress = address.getText();
            String ship_address = shipAddress.getText();
            String UserDistrict = district.getText();
            int gstin_num = Integer.parseInt(gstinNum.getText());
            int state_code = Integer.parseInt(stateCode.getText());

//            String date = String.valueOf(Date.valueOf(datePicker.getValue()));



                pst = con.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, Useraddress);
                pst.setString(3, ship_address);
                pst.setString(4, UserDistrict);
                pst.setInt(5, gstin_num);
                pst.setInt(6, state_code);

        pst.setInt(7, invoice_num);



        int i = pst.executeUpdate();
        if (i == 1) {
            System.out.println("Dta inserted");
            loadDataFromDatabase();
            setCellTable();
            AlertDialog.display("Info", "Data updated Successfully");
            clearTextField();


        }



            } catch (SQLException e) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);
//            } finally {
//                pst.close();
//            }
            }


        }
    }
    @FXML
    private void deleteUser(ActionEvent event)
    {
        String sql = "DELETE  FROM `user_data` WHERE `Invoice Number` = ?";
        try{
            pst = con.prepareStatement(sql);

            pst.setString(1, invoiceNum.getText());
            int i = pst.executeUpdate();
            if (i == 1) {
                AlertDialog.display("Info", "Data deleted Successfully");
                loadDataFromDatabase();
                clearTextField();


            }

        } catch (SQLException e) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    private void clearTextField()
    {
        invoiceNum.clear();
        txtName.clear();
        address.clear();
        shipAddress.clear();
        gstinNum.clear();
        stateCode.clear();
        district.clear();
    }

    private void searchUser()
    {
        txt_search.setOnKeyReleased(e -> {
            if(txt_search.getText().equals(""))
            {
                loadDataFromDatabase();
            }else{
                data.clear();
                String sql = "SELECT * FROM `user_data` WHERE `Invoice Number` LIKE '%"+txt_search.getText()+"%'"
                        + " UNION SELECT * FROM `user_data` WHERE `Name` LIKE '%"+txt_search.getText()+"%'"
                        + " UNION SELECT * FROM `user_data` WHERE  `District` LIKE '%"+txt_search.getText()+"%'"
                        + " UNION SELECT * FROM `user_data` WHERE `GSTIN No.` LIKE '%"+txt_search.getText()+"%'"
                        + " UNION SELECT * FROM `user_data` WHERE `State Code` LIKE '%"+txt_search.getText()+"%'"
                        + " UNION SELECT * FROM `user_data` WHERE `Date` LIKE '%"+txt_search.getText()+"%'";
                try{
                    pst = con.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        data.add(new TableData(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
                                rs.getString(5), rs.getString(6), rs.getInt(7), rs.getInt(8)
                                , rs.getString(9)));
                    }
                    taxTable.setItems(data);
                }catch (SQLException ex)
                {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
//    int count =0;
////    @FXML
////    private void manageOrder(ActionEvent event) throws IOException {
////
//////        taxTable.setOnMouseClicked(e ->
//////        {
////////
//////                Parent root = null;
//////                try {
//////                    root = FXMLLoader.load(getClass().getResource("product.fxml"));
//////                } catch (IOException exc) {
//////                    exc.printStackTrace();
//////                }
//////                Stage primaryStage = new Stage();
//////                primaryStage.setScene(new Scene(root));
//////                primaryStage.show();
//////
//////            });
//////
////
////        count++;
////        FXMLLoader loader = new FXMLLoader(getClass().getResource("product.fxml"));
////        try {
////            // Load the another FXML file
////            Parent newParent = loader.load();
//////             controller = loader.getController();
////            // Set the String property
////            // If you want to use data from the current selection: newValue contains the currently selected Person
////            // TODO: Get value from DB
//////            controller.textToDisplay.set(newValue.getName());
////
////            // newParent contains the root of your other FXML file, do anything that you want to do with it (e.g. add to the current node graph)
////            // Now I just simply open it in a new window
////            Stage newStage = new Stage();
////            Scene newScene = new Scene(newParent);
////            newStage.setScene(newScene);
////            newStage.show();
////
////        } catch (IOException ex) {
////            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
////        }
////
////
////    }
        private boolean checkInvoiceNumber(int invoiceNum) throws SQLException {
            boolean invoiceNumExists = false;
             rs = pst.executeQuery("SELECT * FROM `user_data` WHERE `Invoice Number`= '"+invoiceNum+"'");
            int id;
            if (rs.next()){
                id = rs.getInt(2);
                if(id==(invoiceNum)){
                    invoiceNumExists = true;
                }
            }
            return invoiceNumExists;
        }

        private void rowClicked() throws IOException
        {
            taxTable.setRowFactory(tv -> {
                TableRow<TableData> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if ((event.getClickCount() == 2 && (! row.isEmpty())))  {
                        TableData rowData = row.getItem();
                        System.out.println("Double click on: "+rowData.getName());
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("product.fxml"));
                        Parent root = null;
                        try {
                            root = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Scene scene = new Scene(root);
                    ProductController controller = loader.getController();
                            controller.initData(String.valueOf(rowData.getInvoiceNum()),rowData.getDate());
                                Stage primaryStage =(Stage) ((Node)event.getSource()).getScene().getWindow();
                                primaryStage.setScene(scene);

                        Alert alert = new Alert( Alert.AlertType.INFORMATION);
                        alert.setTitle("Go Back");
                        alert.setContentText("Clear everything from the cart");
                        ButtonType yesBtn = new ButtonType("Yes,clear");

                        ButtonType noBtn = new ButtonType("No");
                        alert.getButtonTypes().setAll(yesBtn, noBtn);
                        Platform.runLater(() -> {
                            Optional<ButtonType> btnClicked = alert.showAndWait();
                            {
                                if(btnClicked.isPresent()&& btnClicked.get()== yesBtn)
                                {
                                    String sql = "DELETE FROM `tax_invoice`;";
                                    try{
                                        pst =con.prepareStatement(sql);
                                            pst.executeUpdate();
                                        } catch (SQLException e) {
                                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);
                                    }
                                }
                                else
                                {

                                }
                            }
                        });
                         primaryStage.setTitle("Product List Details");
                        primaryStage.setScene(new Scene(root));
                        primaryStage.show();

                    }
                });
                return row ;
            });
        }
}







