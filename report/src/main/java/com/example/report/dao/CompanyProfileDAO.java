package com.example.report.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.report.model.CompanyProfile;
import com.example.report.model.CompanyProfilePK;

public interface CompanyProfileDAO extends JpaRepository<CompanyProfile, CompanyProfilePK> {

        @Query("SELECT r FROM CompanyProfile r " +
                        "WHERE r.companyProfilePK.companyCode = :companyCode " +
                        "AND r.companyProfilePK.branchCode = :branchCode " +
                        "AND r.companyProfilePK.lang = :language")
        List<CompanyProfile> findCompanyInfo(@Param("companyCode") String companyCode,
                        @Param("branchCode") String branchCode, @Param("language") String language);

        @Query("SELECT r.fieldValue FROM CompanyProfile r " +
                        "WHERE r.companyProfilePK.companyCode = :companyCode " +
                        "AND r.companyProfilePK.fieldName = 'companyName' " +
                        "AND r.companyProfilePK.lang = 'TH'")
        String findCompanyNameTH(@Param("companyCode") String companyCode);
}
