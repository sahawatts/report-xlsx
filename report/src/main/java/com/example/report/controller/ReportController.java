package com.example.report.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.report.dto.ApiResponse;
import com.example.report.service.ExcelGenerate;

@RestController
@RequestMapping("report")
public class ReportController {

    @Autowired
    ExcelGenerate excelGenerate;

    @RequestMapping(value = "/monthly-vat-excel", method = RequestMethod.GET)
    public ResponseEntity<ApiResponse> etaxVatReport(@RequestParam("month") int month,
            @RequestParam("year") int year,
            @RequestParam("companyCode") String companyCode,
            @RequestParam("lang") String language) {

        String reportPath = "D:" + File.separator
                + String.format("VAT_REPORT_%s_Y%d_M%02d.xlsx", companyCode.toUpperCase(), year, month);
        File report = null;
        ApiResponse response = new ApiResponse();
        HttpHeaders headers = new HttpHeaders();

        try {

            report = excelGenerate.generateEtaxMonthlyReport(companyCode, language, year, month, reportPath);

            if (report == null) {

                response.setResponseCode(HttpStatus.NO_CONTENT.value());
                response.setResponseMessage("No transaction found in the selected month.");

            } else {

                // to do
                // send to file deletion service

                headers.setContentDispositionFormData("attachment", report.getName());
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

                response.setFileAsResource(report);
                response.setResponseCode(HttpStatus.OK.value());
                response.setResponseMessage("Report generated success.");

                return ResponseEntity.ok().headers(headers).body(response);

            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setResponseMessage("An unexpected error occurred on the server : " + e + ".");
        }

        return ResponseEntity.status(HttpStatus.valueOf(response.getResponseCode())).body(response);

    }
}
