package com.karam.invoice;

public class ProducTable {

    private int slNumber;
    private String descriptionOfGoods;
    private int HSNCode;
    private double qty;
    private double rate;

    private int stateCode;
    private  int invoiceNum;
    private double taxableAmount;
    private double cgst;
    private double sgst;

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    private double grandTotal;


    public ProducTable(int slNumber, int invoiceNum, String descriptionOfGoods, int HSNCode, double qty, double rate, double taxableAmount, double cgst, double sgst, double grandTotal) {
        this.slNumber= slNumber;
        this.descriptionOfGoods = descriptionOfGoods;
        this.HSNCode = HSNCode;
        this.qty = qty;
        this.rate = rate;

        this.invoiceNum = invoiceNum;
        this.taxableAmount = taxableAmount;
        this.cgst = cgst;
        this.sgst = sgst;
    }




    public int getSlNumber() {
        return slNumber;
    }

    public void setSlNumber(int slNumber) {
        this.slNumber = slNumber;
    }

    public double getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(double taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public double getCgst() {
        return cgst;
    }

    public void setCgst(double cgst) {
        this.cgst = cgst;
    }

    public double getSgst() {
        return sgst;
    }

    public void setSgst(double sgst) {
        this.sgst = sgst;
    }





    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public int getInvoiceNum() {
        return invoiceNum;
    }

    public void setInvoiceNum(int invoiceNum) {
        this.invoiceNum = invoiceNum;
    }
//

    public String getDescriptionOfGoods() {
        return descriptionOfGoods;
    }

    public void setDescriptionOfGoods(String descriptionOfGoods) {
        this.descriptionOfGoods = descriptionOfGoods;
    }

    public int getHSNCode() {
        return HSNCode;
    }

    public void setHSNCode(int HSNCode) {
        this.HSNCode = HSNCode;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

}
