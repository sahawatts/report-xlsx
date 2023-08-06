package com.example.report.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.mail.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.report.dao.CompanyProfileDAO;
import com.example.report.dao.ReportFlagDAO;
import com.example.report.model.ReportFlag;
import com.example.report.util.DateUtil;
import com.example.report.util.ResourceUtil;

@Service
public class MonthlyReportGenerateService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CompanyProfileDAO companyProfileDAO;

    @Autowired
    ReportFlagDAO reportFlagDAO;

    @Autowired
    ExcelGenerate excelGenerate;

    @Autowired
    MailService mailService;

    @Scheduled(cron = "${scheduled.monthly.tenth}") // every 10th day of the month at 7AM
    public void generateMonthlyReportScheduleding() {

        LocalDate localDate = LocalDate.now();
        int currentMonth = localDate.getMonthValue();
        int currentYear = localDate.getYear();
        int monthToGenerate = currentMonth - 1;

        List<ReportFlag> companyFlag = reportFlagDAO.findAll();
        companyFlag = companyFlag.stream().filter(comp -> comp.getFlagVatReport() == 1).collect(Collectors.toList());

        logger.info("========== START BATCH ETAX VAT MONTHLY REPORT Y{} M{} SCHEDULING ==========", currentYear,
                monthToGenerate);

        try {
            processVatReport(companyFlag, currentYear, monthToGenerate);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error("ERROR IN BATCH SCHEDULE : ", e);
        }

        logger.info("========== BATCH ETAX VAT MONTHLY REPORT Y{} M{} COMPLETED ==========", currentYear,
                monthToGenerate);

    }

    public void processVatReport(List<ReportFlag> companyFlag, int year, int month) throws Exception {

        boolean validateReportDate = validateReportDate(year, month);

        if (validateReportDate == false) {
            throw new Exception("INVALID_DATE");
        }

        companyFlag = companyFlag.stream().filter(flag -> flag.getFlagVatReport() == 1).collect(Collectors.toList());

        List<File> reportFiles = new ArrayList<>();

        // generate report, one report per compCode
        for (ReportFlag company : companyFlag) {

            String companyCode = company.getCompanyCode();
            String companyNameTH = companyProfileDAO.findCompanyNameTH(companyCode);

            Session session = mailService.connectDefaultSMTP();

            String reportName = getVatReportFileName(companyCode, year, month);
            String reportPath = "D:" + File.separator + reportName;
            File reportFile = excelGenerate.generateEtaxMonthlyReport(companyCode, "TH", year, month, reportPath);

            try {

                String recipientEmail = company.getCompanyEmail();

                if (recipientEmail.isEmpty()) {
                    throw new Exception("No email to send report");
                }

                if (session == null) {
                    throw new Exception("Can not connect to SMTP server");
                }

                String mailSubject = String.format("EXCEL MONTHLY REPORT Y%d M%02d", year, month);

                String monthThai = DateUtil.getMonthNameInThai(month);
                String yearThai = String.valueOf(year + 543);

                String subject = "รายงานภาษีขาย ประจำเดือน " + monthThai + " " + yearThai;
                String reportNameTH = "รายงานภาษีขาย (e-Tax Invoice) ประจำเดือน " + monthThai + " พ.ศ." + yearThai;
                String mailBody = "";
                if (reportFile != null) {
                    mailBody = ResourceUtil.readTemplateFromResource(
                            "templates" + File.separator + "email" + File.separator + "excel_vat_report_attach.txt");
                } else {
                    mailBody = ResourceUtil.readTemplateFromResource(
                            "templates" + File.separator + "email" + File.separator + "excel_vat_report_no_attach.txt");
                }
                mailBody = mailBody.replace("{subject}", subject)
                        .replace("{recipeint}", companyNameTH)
                        .replace("{attachment}", reportNameTH);

                String senderEmail = "batch-service@jib-test.co.th";
                String senderName = "Batch Service";

                mailService.send(session, senderEmail, senderName, recipientEmail,
                        mailSubject,
                        mailBody,
                        reportFile);

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                logger.error("ERROR OCCURED : {}", e.getMessage());
            }

            if (reportFile.exists()) {
                reportFiles.add(reportFile);
            }

        }

        reportFiles.forEach(file -> {
            try {
                Files.delete(file.toPath());
                logger.info("Deleted report file: {}", file.getAbsolutePath());
            } catch (IOException e) {
                logger.error("Failed to delete report file: {}", file.getAbsolutePath());
                e.printStackTrace();
            }
        });

    }

    public boolean validateReportDate(int year, int month) {
        LocalDate localDate = LocalDate.now();
        int currentMonth = localDate.getMonthValue();
        int currentYear = localDate.getYear();
        if (year < currentYear || year > currentYear || month < 1 || month > 12) {
            return false;
        }
        if (year == currentYear && month >= currentMonth) {
            return false;
        }
        return true;
    }

    private String getVatReportFileName(String compCode, int year, int month) {
        return String.format("VAT_REPORT_%s_Y%d_M%02d.xlsx", compCode.toUpperCase(), year, month);
    }

}