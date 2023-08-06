package com.example.report.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BUSINESS_TRANSACTION_DETAIL")
public class BusinessTransactionDetail {
    @Id
    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "ACTIVE_STATUS")
    private String activeStatus;

    @Column(name = "DOCUMENT_NO")
    private String documentNo;

    @Column(name = "AMOUNT")
    private double amount;

    @Column(name = "ISSUE_DATETIME")
    private String issueDateTime;

    @Column(name = "JSON")
    private String json;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getIssueDateTime() {
        return issueDateTime;
    }

    public void setIssueDateTime(String issueDatetime) {
        this.issueDateTime = issueDatetime;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

}
