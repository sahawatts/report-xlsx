package com.example.report.dto;

public class TransactionDetailDTO {
    
    String transactionId;
    String companyCode;
    String branchCode;
    String json;
    String issueDateTime;
    
    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    public String getCompanyCode() {
        return companyCode;
    }
    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
    public String getBranchCode() {
        return branchCode;
    }
    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
    public String getJson() {
        return json;
    }
    public void setJson(String json) {
        this.json = json;
    }
    public String getIssueDateTime() {
        return issueDateTime;
    }
    public void setIssueDateTime(String issueDateTime) {
        this.issueDateTime = issueDateTime;
    }

    
}
