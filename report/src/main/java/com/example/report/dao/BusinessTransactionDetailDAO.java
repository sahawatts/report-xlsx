package com.example.report.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.report.model.BusinessTransactionDetail;

public interface BusinessTransactionDetailDAO extends JpaRepository<BusinessTransactionDetail, String>{
    
}
