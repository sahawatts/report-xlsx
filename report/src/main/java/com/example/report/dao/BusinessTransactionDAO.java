package com.example.report.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.report.dto.TransactionDetailDTO;
import com.example.report.model.BusinessTransaction;

public interface BusinessTransactionDAO extends JpaRepository<BusinessTransaction, String> {

       @Query("SELECT bt.transactionId, bt.companyCode, bt.branchCode, btd.issueDateTime, btd.json " +
                     "FROM BusinessTransaction bt " +
                     "LEFT JOIN BusinessTransactionDetail btd ON bt.transactionId = btd.transactionId " +
                     "WHERE bt.companyCode = :companyCode " +
                     "AND YEAR(btd.issueDateTime) = :year " +
                     "AND MONTH(btd.issueDateTime) = :month " +
                     "AND btd.json != '' " +
                     "AND btd.activeStatus = 'A' " +
                     "ORDER BY btd.issueDateTime ASC")
       List<TransactionDetailDTO> getMonthlyActiveTransaction(@Param("companyCode") String companyCode,
                     @Param("year") int year,
                     @Param("month") int month);
}
