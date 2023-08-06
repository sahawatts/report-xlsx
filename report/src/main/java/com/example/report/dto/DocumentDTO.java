package com.example.report.dto;

import java.util.List;

public class DocumentDTO {

    private String documentNo;
    private String issueDateTime;

    private String customerName;
    private String customerTaxID;
    private String customerBranch;

    private double taxBasisTotalAmount = 0;
    private double taxTotalAmount = 0;
    private double grandTotalAmount = 0;

    private List<ItemDTO> items;

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public String getIssueDateTime() {
        return issueDateTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerTaxID() {
        return customerTaxID;
    }

    public void setCustomerTaxID(String customerTaxID) {
        this.customerTaxID = customerTaxID;
    }

    public String getCustomerBranch() {
        return customerBranch;
    }

    public void setCustomerBranch(String customerBranch) {
        this.customerBranch = customerBranch;
    }

    public void setIssueDateTime(String issueDateTime) {
        this.issueDateTime = issueDateTime;
    }

    public double getTaxBasisTotalAmount() {
        return taxBasisTotalAmount;
    }

    public void setTaxBasisTotalAmount(double taxBasisTotalAmount) {
        this.taxBasisTotalAmount = taxBasisTotalAmount;
    }

    public double getTaxTotalAmount() {
        return taxTotalAmount;
    }

    public void setTaxTotalAmount(double taxTotalAmount) {
        this.taxTotalAmount = taxTotalAmount;
    }

    public double getGrandTotalAmount() {
        return grandTotalAmount;
    }

    public void setGrandTotalAmount(double grandTotalAmount) {
        this.grandTotalAmount = grandTotalAmount;
    }

    public List<ItemDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemDTO> items) {
        this.items = items;
    }

}
