package com.example.report.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BUSINESS_TRANSACTION")
public class BusinessTransaction {
    @Id
    @Column(name = "TRANSACTION_ID")
    private String transactionId;

    @Column(name = "COMPANY_CODE")
    private String companyCode;

    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "TRANSACTION_DATE")
    private String transactionDate;

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

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

}
