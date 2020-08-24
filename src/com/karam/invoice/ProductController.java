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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductController implements Initializable {

//    int slNumber =0;

    @FXML
    public Label labelInvoiceNumber;
    @FXML
    private TextField tfDescription;
    @FXML
    private TextField tfHSNCode;
    @FXML
    private TextField tfQuantity;
    @FXML
    private TextField tfRate;
    @FXML
    private Label error_goods;
    @FXML
    private Label error_HSNCode;
    @FXML
    private Label error_quantity;
    @FXML
    private Label error_rates;
    @FXML
    private TextField txt_searchProduct;
    @FXML
    private Label dateLabel;

    @FXML
    private Label grandTotal;
    private Connection con = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private ObservableList<ProducTable> data;

    @FXML
    private TableColumn<TableData, Integer> colSLNumber;
    @FXML
    private TableColumn<TableData, String> colDescription;
    @FXML
    private TableColumn<TableData, Integer> colHSNCode;
    @FXML
    private TableColumn<TableData, Double> colQTY;
    @FXML
    private TableColumn<TableData, Double> colRate;
    @FXML
    private TableColumn<TableData, Double> colTaxableAmount;
    @FXML
    private TableColumn<TableData, Double> colCGST;
    @FXML
    private TableColumn<TableData, Double> colSGST;

    @FXML
    private TableView<ProducTable> productTable;
    String date;


    static double  totalSum =0.0;

    @FXML
    private void addProduct(ActionEvent event) throws SQLException, NullPointerException {
//        TextFieldValidation validation = new TextFieldValidation();

        boolean isGoodsEmpty = TextFieldValidation.isTextFieldNotEmpty(tfDescription, error_goods, "Description required");
            boolean isHSNCodeEmpty = TextFieldValidation.isTextFieldNotEmpty(tfHSNCode, error_HSNCode, "HSN code required");
            boolean isQuantityEmpty = TextFieldValidation.isTextFieldNotEmpty(tfQuantity, error_quantity, "Qty required");
            boolean isRateEmpty = TextFieldValidation.isTextFieldNotEmpty(tfRate, error_rates, "Rate required");
            if (isGoodsEmpty && isHSNCodeEmpty && isQuantityEmpty && isRateEmpty) {
            String sql = "INSERT INTO `tax_invoice`( `Invoice Number`,`Description of goods`, `HSN Code`, `QTY/litres`, `Rate/pcs`, `Taxable Amount`, `CGST`, `SGST`, `Grand Total` )" +
                    "VALUES(?,?,?,?,?,?,?,?,?);";
            int invoice_num = Integer.parseInt(labelInvoiceNumber.getText());
            String description_of_goods = tfDescription.getText();
            int hsn_code = Integer.parseInt(tfHSNCode.getText());
            double qty = Double.parseDouble(tfQuantity.getText());
            double rate = Double.parseDouble(tfRate.getText());

            double taxable_amount = taxableAmount(qty, rate);
            double cgst_amount = cgst(qty, rate);
            double sgst_amount = sgst(qty, rate);
            double total_sum = totalSum(qty,rate);

            try {
                pst = con.prepareStatement(sql);
//                pst.setInt(1,slNumber++);

                pst.setInt(1, invoice_num);

                pst.setString(2, description_of_goods);
                pst.setInt(3, hsn_code);
                pst.setDouble(4, qty);
                pst.setDouble(5, rate);
                pst.setDouble(6, taxable_amount);
                pst.setDouble(7, cgst_amount);
                pst.setDouble(8, sgst_amount);
                pst.setDouble(9, total_sum);


                int i = pst.executeUpdate();
                if (i == 1) {
                    System.out.println("Dta inserted");

                    setCellTable();
                    loadDataFromDatabase();
                    AlertDialog.display("Info", "Data inserted Successfully");

                    clearTextField();


                }


            } catch (SQLException e) {
                throw e;
            } finally {
                pst.close();
            }
        }


    }

    public  static double taxableAmount(double qty, double rate)
    {
        double amount =  qty * rate;
        double taxAmount = amount + (0.18 * amount);
        return Math.round((taxAmount*100)/100);
    }
    public  static double cgst(double qty, double rate)
    {
        double amount = taxableAmount(qty,rate);
        double cgstTax = amount + (0.09 * amount);
        return Math.round((cgstTax*100)/100);
    }
    public  static double sgst(double qty, double rate)
    {
        double amount = cgst(qty,rate);
        double sgstTax = amount + (0.09 * amount);
        return Math.round((sgstTax*100)/100);
    }
    public  static double totalSum(double qty, double rate)
    {
        double amount = sgst(qty,rate);
        totalSum = totalSum + amount;
        return Math.round((totalSum*100)/100);
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
        try {
            loadDataFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setCellValueFromTableToTextField();
        searchUser();



    }

    private void setCellTable() {
        colSLNumber.setCellValueFactory(new PropertyValueFactory<>("slNumber"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("descriptionOfGoods"));
        colHSNCode.setCellValueFactory(new PropertyValueFactory<>("HSNCode"));
        colQTY.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colRate.setCellValueFactory(new PropertyValueFactory<>("rate"));

        colTaxableAmount.setCellValueFactory(new PropertyValueFactory<>("taxableAmount"));
        colCGST.setCellValueFactory(new PropertyValueFactory<>("cgst"));
        colSGST.setCellValueFactory(new PropertyValueFactory<>("sgst"));

    }

    private void loadDataFromDatabase() throws SQLException {
        data.clear();
        try {
            pst = con.prepareStatement("SELECT * FROM  `tax_invoice`");
            rs = pst.executeQuery();

            while (rs.next()) {
                data.add(new ProducTable(rs.getInt(1),rs.getInt(2), rs.getString(3), rs.getInt(4),
                        rs.getDouble(5), rs.getDouble(6), rs.getDouble(7), rs.getDouble(8)
                        , rs.getDouble(9),rs.getDouble(10)));
                grandTotal.setText(String.valueOf(Math.round((rs.getDouble(10)*100)/100)));

            }
        } catch (SQLException e) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);
        }

        productTable.setItems(data);
    }

    private void setCellValueFromTableToTextField() {
        productTable.setOnMouseClicked(e ->
        {
            ProducTable p1 = productTable.getItems().get(productTable.getSelectionModel().getSelectedIndex());
            labelInvoiceNumber.setText(String.valueOf(Integer.valueOf(p1.getInvoiceNum())));

            tfDescription.setText(p1.getDescriptionOfGoods());
            tfHSNCode.setText(String.valueOf(Integer.valueOf(p1.getHSNCode())));
            tfQuantity.setText(String.valueOf(Double.valueOf(p1.getQty())));
            tfRate.setText(String.valueOf(Double.valueOf(p1.getRate())));


        });
    }

    @FXML
    private void updateProduct(ActionEvent event) throws SQLException, ClassNotFoundException {
        boolean isGoodsEmpty = TextFieldValidation.isTextFieldNotEmpty(tfDescription, error_goods, "Description required");
        boolean isHSNCodeEmpty = TextFieldValidation.isTextFieldNotEmpty(tfHSNCode, error_HSNCode, "HSN code required");
        boolean isQuantityEmpty = TextFieldValidation.isTextFieldNotEmpty(tfQuantity, error_quantity, "Qty required");
        boolean isRateEmpty = TextFieldValidation.isTextFieldNotEmpty(tfRate, error_rates, "Rate required");
        if (isGoodsEmpty && isHSNCodeEmpty && isQuantityEmpty && isRateEmpty) {
            String sql = "UPDATE `tax_invoice` SET  `Description of Goods` = ?,  `HSN Code` = ?, `QTY/litres` = ?, `Rate/pcs` = ? WHERE `Invoice Number` = ?;";
            try
            {
                int invoice_num = Integer.parseInt(labelInvoiceNumber.getText());
//            int sl_num = Integer.parseInt(slNumber.getText());
                String description_of_goods = tfDescription.getText();
                int hsn_code = Integer.parseInt(tfHSNCode.getText());
                double qty = Double.parseDouble(tfQuantity.getText());
                double rate = Double.parseDouble(tfRate.getText());






                pst = con.prepareStatement(sql);
//                pst.setInt(1,++slNumber);


                pst.setString(1, description_of_goods);
                pst.setInt(2, hsn_code);
                pst.setDouble(3, qty);
                pst.setDouble(4, rate);
                pst.setInt(5, invoice_num);









                int i = pst.executeUpdate();
                if (i == 1) {

                    setCellTable();
                    loadDataFromDatabase();
                    AlertDialog.display("Info", "Data updated Successfully");

                    clearTextField();

                }
                else {
                    AlertDialog.display("Info", "Data not updated Successfully");

                }

            } catch (SQLException e) {
                Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);
//            } finally {
//                pst.close();
//            }
            }


        }
    }
//    @FXML
//    private void deleteProduct(ActionEvent event)
//    {
//        String sql = "DELETE  FROM `tax_invoice` WHERE `Invoice Number` = ?";
//        try{
//            pst = con.prepareStatement(sql);
//
//            pst.setString(1, tfInvoiceNumber.getText());
//            int i = pst.executeUpdate();
//            if (i == 1) {
//                AlertDialog.display("Info", "Data deleted Successfully");
//                loadDataFromDatabase();
//                clearTextField();
//
//
//            }
//
//
//        } catch (SQLException e) {
//            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);
//        }
//    }
    private void clearTextField()
    {

        tfDescription.clear();
        tfHSNCode.clear();
        tfRate.clear();
        tfQuantity.clear();


    }
    @FXML
    private void deleteProduct(ActionEvent event) throws SQLException
    {
        String sql = "DELETE FROM `tax_invoice` WHERE `SL. No.` = ?";
        try{
            pst =con.prepareStatement(sql);
            pst.setInt(1, rs.getInt(1));
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

    @FXML
    private void clearProduct(ActionEvent event)
    {
        String sql = "DELETE FROM `tax_invoice`;";
        try{
            pst =con.prepareStatement(sql);
            int i = pst.executeUpdate();
            if (i == 1) {
                AlertDialog.display("Info", "Data cleared Successfully");
                loadDataFromDatabase();
                clearTextField();


            }


        } catch (SQLException e) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void searchUser()
    {
        txt_searchProduct.setOnKeyReleased(e -> {
            if(txt_searchProduct.getText().equals(""))
            {
                try {
                    loadDataFromDatabase();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }else{
                data.clear();
                String sql = "SELECT * FROM `tax_invoice` WHERE `Description of Goods` LIKE '%"+txt_searchProduct.getText()+"%'"
                        + " UNION SELECT * FROM `tax_invoice` WHERE `HSN Code` LIKE '%"+txt_searchProduct.getText()+"%'"
                        + " UNION SELECT * FROM `tax_invoice` WHERE  `QTY/litres` LIKE '%"+txt_searchProduct.getText()+"%'"
                        + " UNION SELECT * FROM `tax_invoice` WHERE `Rate/pcs` LIKE '%"+txt_searchProduct.getText()+"%'"
                        + " UNION SELECT * FROM `tax_invoice` WHERE `Invoice Number` LIKE '%"+txt_searchProduct.getText()+"%'";


                try{
                    pst = con.prepareStatement(sql);
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        data.add(new ProducTable(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4),
                                rs.getDouble(5), rs.getDouble(6), rs.getDouble(7), rs.getDouble(8)
                                , rs.getDouble(9),rs.getDouble(10)));
                    }
                    productTable.setItems(data);
                }catch (SQLException ex)
                {
                    Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);

                }
            }
        });
    }
//    @FXML
//    private void handleInvoiceScan(KeyEvent event) throws SQLException
//    {
//        pst = con.prepareStatement("SELECT * FROM `user_data` WHERE `Invoice Number` = ?");
//        pst.setString(1,tfInvoiceNumber.getText());
//        rs = pst.executeQuery();
//        if (rs.next())
//        {
//            date = rs.getString(9);
//            System.out.println(date);
//            dateLabel.setText(date);
//            tfDescription.requestFocus();
//            tfHSNCode.requestFocus();
//            tfQuantity.requestFocus();
//            tfRate.requestFocus();
//
//        }
//        rs.close();
//    }
    public void initData(String invoice_num, String date)
    {
        labelInvoiceNumber.setText(invoice_num);
        dateLabel.setText(date);

    }
    @FXML
    private void backPrev(ActionEvent event)
    {
        Alert alert = new Alert( Alert.AlertType.INFORMATION);
        alert.setTitle("Go Back");
        alert.setContentText("Going back will remove everything from product Cart." +
                "\nAre you sure you want to go back?");
        ButtonType yesBtn = new ButtonType("Clear, Go back");
        ButtonType save = new ButtonType("Save and Go back");

        ButtonType noBtn = new ButtonType("No");
        alert.getButtonTypes().setAll(yesBtn,save, noBtn);
        Platform.runLater(() -> {
            Optional<ButtonType> btnClicked = alert.showAndWait();
            {
                if(btnClicked.isPresent()&& btnClicked.get()== yesBtn)
                {
                    String sql = "DELETE FROM `tax_invoice`;";
                    try{
                        pst =con.prepareStatement(sql);
                        int i = pst.executeUpdate();



                    } catch (SQLException e) {
                        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);
                    }
                   translate(event);
           }
                else if (btnClicked.isPresent()&& btnClicked.get()== save)

                {
                    translate(event);

                }
            }
        });


    }
    private void translate(ActionEvent event)
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("sample.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);

        Stage primaryStage =(Stage) ((Node)event.getSource()).getScene().getWindow();
        primaryStage.setScene(scene);

        primaryStage.setTitle("Invoice Generator");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
