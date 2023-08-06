package com.example.report.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.DynamicUpdate;

/**
 * A Table to stored client's information e.g. for company's address, will
 * stored in
 * <table>
 * <tr>
 * <th>COMPANY_CODE</th>
 * <th>BRANCH_CODE</th>
 * <th>FIELD_NAME</th>
 * <th>VALUE</th>
 * <th>LANG</th>
 * </tr>
 * <tr>
 * <th>COMPANY_CODE1</th>
 * <th>00000</th>
 * <th>companyName</th>
 * <th>Lorem ipsum limited</th>
 * <th>TH</th>
 * </tr>
 * <tr>
 * <th>COMPANY_CODE1</th>
 * <th>00000</th>
 * <th>taxID</th>
 * <th>1234567890123</th>
 * <th>TH</th>
 * </tr>
 * </table>
 */
@Entity
@DynamicUpdate
@Table(name = "COMPANY_PROFILE")
public class CompanyProfile {

    @EmbeddedId
    protected CompanyProfilePK companyProfilePK;
    @Column(name = "FIELD_VALUE")
    private String fieldValue;

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String value) {
        this.fieldValue = value;
    }

    public CompanyProfilePK getCompanyProfilePK() {
        return companyProfilePK;
    }

    public void setCompanyProfilePK(CompanyProfilePK companyProfilePK) {
        this.companyProfilePK = companyProfilePK;
    }

}
