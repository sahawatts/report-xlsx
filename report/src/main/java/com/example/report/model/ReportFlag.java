package com.example.report.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Table for checking report flag to scheduling process report for each company. One type of report per one flag column.
 * <ul>
 * <li>Flag 0 when the report are not available for the company</li>
 * <li>Flag 1 when the report are available for the company to process</li>
 * </ul>
 */
@Entity
@Table(name = "REPORT_FLAG")
public class ReportFlag implements Serializable {

    @Id
    @Column(name = "COMPANY_CODE")
    private String companyCode;

    @Column(name = "COMPANY_EMAIL")
    private String companyEmail;

    @Column(name = "FLAG_VAT_REPORT")
    private int flagVatReport;

    // Added more column, if more report added
    // @Column(name = "FLAG_MONTHLY_SALE_REPORT")
    // private int flagMonthlySaleReport;

    // @Column(name = "FLAG_CSV_REPORT")
    // private int flagCsvReport;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String departmentEmail) {
        this.companyEmail = departmentEmail;
    }

    public int getFlagVatReport() {
        return flagVatReport;
    }

    public void setFlagVatReport(int flagVatReport) {
        this.flagVatReport = flagVatReport;
    }

    
 
}
