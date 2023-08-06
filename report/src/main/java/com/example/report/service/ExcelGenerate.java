package com.example.report.service;

import java.io.File;

public interface ExcelGenerate {

    File generateEtaxMonthlyReport(String compCode, String language, int year, int month, String destinationPath);

}
