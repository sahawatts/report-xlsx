package com.example.report.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;

public class CompanyProfilePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "COMPANY_CODE")
    private String companyCode;

    @Basic(optional = false)
    @Column(name = "BRANCH_CODE")
    private String branchCode;

    @Column(name = "FIELD_NAME")
    private String fieldName;

    @Column(name = "LANG")
    private String lang;

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

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
