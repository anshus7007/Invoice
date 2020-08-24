package com.karam.invoice;

public class TableData {

    private int slNumber;
    private String name;
    private String address;
    private String shipAddress;
    private String district;
    private int gstinNum;
    private int stateCode;
    private  int invoiceNum;
    private String date;




    public TableData(int slNumber, int invoiceNum, String name, String address, String shipAddress, String district, int gstinNum, int stateCode, String date) {
       this.slNumber= slNumber;
        this.name = name;

        this.address = address;
        this.shipAddress = shipAddress;
        this.district = district;
        this.gstinNum = gstinNum;
        this.stateCode = stateCode;
        this.invoiceNum = invoiceNum;
        this.date = date;

    }
//
//    public TableData(int invoiceNumText) {
//        this.invoiceNum = invoiceNumText;
//    }

    public int getSlNumber() {
        return slNumber;
    }

    public void setSlNumber(int slNumber) {
        this.slNumber = slNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getGstinNum() {
        return gstinNum;
    }

    public void setGstinNum(int gstinNum) {
        this.gstinNum = gstinNum;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
