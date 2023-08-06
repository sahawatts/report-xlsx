package com.example.report.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.report.model.ReportFlag;

public interface ReportFlagDAO extends JpaRepository<ReportFlag, String> {

}
