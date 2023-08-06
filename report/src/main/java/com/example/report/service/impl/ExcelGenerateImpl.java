package com.example.report.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.report.dao.BusinessTransactionDAO;
import com.example.report.dao.CompanyProfileDAO;
import com.example.report.dto.DocumentDTO;
import com.example.report.dto.TransactionDetailDTO;
import com.example.report.model.CompanyProfile;
import com.example.report.model.CompanyProfilePK;
import com.example.report.service.ExcelGenerate;
import com.example.report.util.DateUtil;
import com.google.gson.Gson;

@Service
public class ExcelGenerateImpl implements ExcelGenerate {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BusinessTransactionDAO businessTransactionDAO;

    @Autowired
    CompanyProfileDAO companyProfileDAO;

    final int COLUMN_DATE = 0;
    final int COLUMN_DOCUMENT_NO = 1;
    final int COLUMN_CUSTOMER_NAME = 2;
    final int COLUMN_CUSTOMER_TAXID = 3;
    final int COLUMN_CUSTOMER_BRANCH = 4;
    final int COLUMN_TAX_BASIS_AMOUNT = 5;
    final int COLUMN_TAX_TOTAL_AMOUNT = 6;
    final int COLUMN_GRAND_TOTAL_AMOUNT = 7;

    public final String FILE_TYPE_EXCEL = ".xlsx";

    CellStyle csAllborder = null;
    CellStyle csTextCenteredAndBorder = null;
    CellStyle csTextRightNumericFormatAndBorder = null;

    FormulaEvaluator evaluator = null;

    /**
     * Genereted .xlsx report contains all etax transactions occurred in the
     * specific month and year
     * 
     * @param compCode
     * @param year            as int value between 1900 and 2100
     * @param month           as int value between 1 and 12
     * @param destinationPath of the report file must be ends with .xlsx format
     * @return File of generated report
     */
    public File generateEtaxMonthlyReport(String compCode, String language, int year, int month,
            String destinationPath) {

        logger.info("Generating ETAX VAT report Y{} M{} for compCode: {}", year, month, compCode);

        // query trx for monthly report
        List<TransactionDetailDTO> monthlyReportTransactions = businessTransactionDAO
                .getMonthlyActiveTransaction(compCode, year, month);
        logger.info("{} transactions found for {} Y{} M{}", monthlyReportTransactions.size(), compCode, year, month);

        if (monthlyReportTransactions.size() == 0) {
            return null;
        }
        // prepare report info
        String monthString = "";
        String yearString = "";
        if (language.equalsIgnoreCase("TH")) {
            monthString = DateUtil.getMonthNameInThai(month);
            yearString = String.valueOf(year + 543);
        } else if (language.equalsIgnoreCase("EN")) {
            monthString = DateUtil.getMonthNameInEnglish(month);
            yearString = String.valueOf(year);
        }

        // filter each branchCode
        Map<String, List<TransactionDetailDTO>> branchCodeMap = new HashMap<String, List<TransactionDetailDTO>>();

        for (TransactionDetailDTO transaction : monthlyReportTransactions) {
            if (!branchCodeMap.containsKey(transaction.getBranchCode())) {
                branchCodeMap.put(transaction.getBranchCode(), new ArrayList<TransactionDetailDTO>());
            }
            branchCodeMap.get(transaction.getBranchCode()).add(transaction);
        }

        // generating report, one xlsx file, 1 sheet per branchCode
        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            POIXMLProperties xmlProps = wb.getProperties();
            POIXMLProperties.CoreProperties coreProps = xmlProps.getCoreProperties();
            coreProps.setCreator("JIB");

            Gson gson = new Gson();
            evaluator = wb.getCreationHelper().createFormulaEvaluator();
            setCellStyles(wb);

            Set<String> branchCodeSet = branchCodeMap.keySet();

            List<CompanyProfile> listCompanyField = null;

            for (String branchCode : branchCodeSet) {

                listCompanyField = companyProfileDAO.findCompanyInfo(compCode, branchCode, language);

                List<TransactionDetailDTO> transactions = branchCodeMap.get(branchCode);

                List<DocumentDTO> transactionList = transactions.stream()
                        .map(transaction -> gson.fromJson(transaction.getJson(), DocumentDTO.class))
                        .collect(Collectors.toList());

                logger.info("{} VAT transactions found to processed for branchCode: {}", transactionList.size(),
                        branchCode);

                String companyName = listCompanyField.stream()
                        .filter(field -> field.getCompanyProfilePK().getFieldName().equals("companyName"))
                        .map(field -> field.getFieldValue())
                        .findFirst().get();
                String taxId = listCompanyField.stream()
                        .filter(field -> field.getCompanyProfilePK().getFieldName().equals("taxID"))
                        .map(field -> field.getFieldValue())
                        .findFirst().get();

                logger.info("CompanyName: {}", companyName);
                // logger.info("CompanyAddress: {}", companyAddress);
                logger.info("TaxId: {}", taxId);

                genererateExcelSheetMonthlyReport(wb, transactionList, language, yearString, monthString, companyName,
                        companyName, taxId, branchCode);

            }

            // Write the Excel file
            File reportFile = new File(destinationPath);
            FileOutputStream fileOut = null;
            fileOut = new FileOutputStream(reportFile);
            wb.write(fileOut);
            fileOut.close();

            logger.info("Generated report successfully at {}", destinationPath);

            return reportFile;

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

        return null;
    }

    private void genererateExcelSheetMonthlyReport(Workbook wb, List<DocumentDTO> transactionList, String language,
            String year, String month, String companyName, String companyAddress, String taxId, String branchCode) {

        try {
            String branchString = "";
            if (language.equalsIgnoreCase("TH")) {
                branchString = "สาขา ";
                branchString += branchCode.equals("00000") ? "สำนักงานใหญ่" : branchCode;
            } else if (language.equalsIgnoreCase("EN")) {
                branchString = "Branch ";
                branchString += branchCode.equals("00000") ? "Head quarter" : branchCode;
            }

            Sheet sheet = wb.createSheet(branchString);
            logger.info("Create new sheet for branchCode: {}", branchCode);

            // Set Column Widths
            sheet.setColumnWidth(0, 18 * 256);
            sheet.setColumnWidth(1, 28 * 256);
            sheet.setColumnWidth(2, 28 * 256);
            sheet.setColumnWidth(3, 25 * 256);
            sheet.setColumnWidth(4, 24 * 256);
            sheet.setColumnWidth(5, 13 * 256);
            sheet.setColumnWidth(6, 13 * 256);
            sheet.setColumnWidth(7, 13 * 256);

            insertEtaxReportTemplate(sheet, language, year, month, companyName, companyAddress, taxId,
                    branchString);

            insertReportTransaction(sheet, language, transactionList);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void insertEtaxReportTemplate(Sheet sheet, String language, String year, String month,
            String companyName, String companyAddress, String taxId, String branchCode) {

        // The company template start from row 0 to row 8
        // Header for company info
        if (language.equalsIgnoreCase("TH")) {
            sheet.createRow(0).createCell(3).setCellValue("รายงานภาษีขาย (e-Tax Invoice)");
            sheet.createRow(1).createCell(0).setCellValue("เดือนภาษี");
            sheet.getRow(1).createCell(1).setCellValue(month);
            sheet.getRow(1).createCell(2).setCellValue("ปีภาษี");
            sheet.getRow(1).createCell(3).setCellValue(year);
            sheet.createRow(2).createCell(0).setCellValue("ชื่อผู้ประกอบการ");
            sheet.getRow(2).createCell(1).setCellValue(companyName);
            sheet.getRow(2).createCell(4).setCellValue("เลขประจำตัวผู้เสียภาษีอากร");
            sheet.getRow(2).createCell(5).setCellValue(taxId);
            sheet.createRow(3).createCell(0).setCellValue("ชื่อสถานประกอบการ");
            sheet.getRow(3).createCell(1).setCellValue(companyAddress);
            sheet.getRow(3).createCell(4).setCellValue("สาขา");
            sheet.getRow(3).createCell(5).setCellValue(branchCode);
        } else if (language.equalsIgnoreCase("EN")) {
            sheet.createRow(0).createCell(3).setCellValue("VAT report (e-Tax Invoice)");
            sheet.createRow(1).createCell(0).setCellValue("Month");
            sheet.getRow(1).createCell(1).setCellValue(month);
            sheet.getRow(1).createCell(2).setCellValue("Tax year");
            sheet.getRow(1).createCell(3).setCellValue(year);
            sheet.createRow(2).createCell(0).setCellValue("Taxable person");
            sheet.getRow(2).createCell(1).setCellValue(companyName);
            sheet.getRow(2).createCell(4).setCellValue("Tax ID");
            sheet.getRow(2).createCell(5).setCellValue(taxId);
            sheet.createRow(3).createCell(0).setCellValue("Juristic name");
            sheet.getRow(3).createCell(1).setCellValue(companyAddress);
            sheet.getRow(3).createCell(4).setCellValue("Branch");
            sheet.getRow(3).createCell(5).setCellValue(branchCode);
        }

        // Merge cells (A8 with B8, C8 with C9, D8 with D9, E8 with E9, F8 with F9, G8
        // with G9,
        // H8 with H9)
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(7, 8, 2, 2));
        sheet.addMergedRegion(new CellRangeAddress(7, 8, 3, 3));
        sheet.addMergedRegion(new CellRangeAddress(7, 8, 4, 4));
        sheet.addMergedRegion(new CellRangeAddress(7, 8, 5, 5));
        sheet.addMergedRegion(new CellRangeAddress(7, 8, 6, 6));
        sheet.addMergedRegion(new CellRangeAddress(7, 8, 7, 7));

        // Set values to the merged cells
        Row row7 = sheet.createRow(7);
        Row row8 = sheet.createRow(8);

        for (int i = 0; i < 8; i++) {
            Cell cellRow7 = row7.createCell(i);
            Cell cellRow8 = row8.createCell(i);

            cellRow7.setCellStyle(csTextCenteredAndBorder);

            cellRow8.setCellStyle(csTextCenteredAndBorder);

        }

        Cell headerCell1 = row7.getCell(COLUMN_DATE);
        Cell headerCell2 = row8.getCell(COLUMN_DATE);
        Cell headerCell3 = row8.getCell(COLUMN_DOCUMENT_NO);
        Cell headerCell4 = row7.getCell(COLUMN_CUSTOMER_NAME);
        Cell headerCell5 = row7.getCell(COLUMN_CUSTOMER_TAXID);
        Cell headerCell6 = row7.getCell(COLUMN_CUSTOMER_BRANCH);
        Cell headerCell7 = row7.getCell(COLUMN_TAX_BASIS_AMOUNT);
        Cell headerCell8 = row7.getCell(COLUMN_TAX_TOTAL_AMOUNT);
        Cell headerCell9 = row7.getCell(COLUMN_GRAND_TOTAL_AMOUNT);

        if (language.equalsIgnoreCase("TH")) {
            headerCell1.setCellValue("ใบกำกับภาษี");
            headerCell2.setCellValue("วัน เดือน ปี");
            headerCell3.setCellValue("เลขที่");
            headerCell4.setCellValue("ชื่อผู้ซื้อสินค้า/ผู้รับบริการ");
            headerCell5.setCellValue("เลขประจำตัวผู้เสียภาษีอากรของผู้ซื้อสินค้า/ผู้รับบริการ");
            headerCell6.setCellValue("สถานประกอบการ");
            headerCell7.setCellValue("มูลค่าสินค้าหรือบริการ");
            headerCell8.setCellValue("จำนวนเงินภาษีมูลค่าเพิ่ม");
            headerCell9.setCellValue("มูลค่ารวม");
        } else if (language.equalsIgnoreCase("EN")) {
            headerCell1.setCellValue("Document");
            headerCell2.setCellValue("Issue Date");
            headerCell3.setCellValue("Document No.");
            headerCell4.setCellValue("Customer name");
            headerCell5.setCellValue("Customer tax ID");
            headerCell6.setCellValue("Customer branch");
            headerCell7.setCellValue("Tax basis total amount");
            headerCell8.setCellValue("Tax total amount");
            headerCell9.setCellValue("Grand total amount");
        }

    }

    private void insertReportTransaction(Sheet sheet, String language, List<DocumentDTO> transactions) {

        int rowIndex = sheet.getLastRowNum() + 1;
        Row row = null;
        Cell c = null;
        CellValue cellValue = null;

        logger.info("Start insert {} transactions to sheet {}", transactions.size(), sheet.getSheetName());

        for (DocumentDTO document : transactions) {

            row = sheet.createRow(rowIndex);

            // convert issue date in format
            String issueDate = document.getIssueDateTime();
            try {

                SimpleDateFormat formatLongDate = DateUtil.getDateFormat(issueDate);
                SimpleDateFormat ddMMyyy = null;
                if (language.equalsIgnoreCase("TH")) {
                    ddMMyyy = new SimpleDateFormat(DateUtil.SHORT_DATE_FORMAT_REPORT, new Locale("th", "TH"));
                } else {
                    ddMMyyy = new SimpleDateFormat(DateUtil.SHORT_DATE_FORMAT_REPORT);
                }

                Date issueDateLongFormat = formatLongDate.parse(issueDate);
                issueDate = ddMMyyy.format(issueDateLongFormat);

            } catch (ParseException e) {
                // do nothing, just set issue date as old format
            }

            c = row.createCell(COLUMN_DATE);
            c.setCellValue(issueDate);
            c.setCellStyle(csAllborder);

            c = row.createCell(COLUMN_DOCUMENT_NO);
            c.setCellValue(document.getDocumentNo());
            c.setCellStyle(csAllborder);

            c = row.createCell(COLUMN_CUSTOMER_NAME);
            c.setCellValue(document.getCustomerName());
            c.setCellStyle(csAllborder);

            c = row.createCell(COLUMN_CUSTOMER_TAXID);
            c.setCellValue(document.getCustomerTaxID());
            c.setCellStyle(csAllborder);

            c = row.createCell(COLUMN_CUSTOMER_BRANCH);
            c.setCellValue(document.getCustomerBranch());
            c.setCellStyle(csAllborder);

            c = row.createCell(COLUMN_TAX_BASIS_AMOUNT);
            c.setCellValue(document.getTaxBasisTotalAmount());
            c.setCellStyle(csTextRightNumericFormatAndBorder);

            c = row.createCell(COLUMN_TAX_TOTAL_AMOUNT);
            c.setCellValue(document.getTaxTotalAmount());
            c.setCellStyle(csTextRightNumericFormatAndBorder);

            c = row.createCell(COLUMN_GRAND_TOTAL_AMOUNT);
            c.setCellValue(document.getGrandTotalAmount());
            c.setCellStyle(csTextRightNumericFormatAndBorder);

            rowIndex++;

        }
        logger.info("Inserted transactions to sheet completed");

        // set sum formula
        row = sheet.createRow(rowIndex);

        c = row.createCell(COLUMN_CUSTOMER_BRANCH);
        if (language.equalsIgnoreCase("TH")) {
            c.setCellValue("รวม");
        } else if (language.equalsIgnoreCase("EN")) {
            c.setCellValue("Total");
        }

        c = row.createCell(COLUMN_TAX_BASIS_AMOUNT);
        c.setCellFormula("SUM(F10:F" + (rowIndex) + ")");
        cellValue = evaluator.evaluate(c);
        c.setCellValue(cellValue.getNumberValue());
        c.setCellStyle(csTextRightNumericFormatAndBorder);

        c = row.createCell(COLUMN_TAX_TOTAL_AMOUNT);
        c.setCellFormula("SUM(G10:G" + (rowIndex) + ")");
        cellValue = evaluator.evaluate(c);
        c.setCellValue(cellValue.getNumberValue());
        c.setCellStyle(csTextRightNumericFormatAndBorder);

        c = row.createCell(COLUMN_GRAND_TOTAL_AMOUNT);
        c.setCellFormula("SUM(H10:H" + (rowIndex) + ")");
        cellValue = evaluator.evaluate(c);
        c.setCellValue(cellValue.getNumberValue());
        c.setCellStyle(csTextRightNumericFormatAndBorder);

        logger.info("Inserted sum formula to sheet completed");

    }

    private void setCellStyles(Workbook wb) {
        csAllborder = wb.createCellStyle();
        csAllborder.setBorderTop(BorderStyle.THIN);
        csAllborder.setBorderBottom(BorderStyle.THIN);
        csAllborder.setBorderLeft(BorderStyle.THIN);
        csAllborder.setBorderRight(BorderStyle.THIN);

        csTextCenteredAndBorder = wb.createCellStyle();
        csTextCenteredAndBorder.cloneStyleFrom(csAllborder);
        csTextCenteredAndBorder.setAlignment(HorizontalAlignment.CENTER);
        csTextCenteredAndBorder.setVerticalAlignment(VerticalAlignment.CENTER);
        csTextCenteredAndBorder.setWrapText(true);

        csTextRightNumericFormatAndBorder = wb.createCellStyle();
        csTextRightNumericFormatAndBorder.cloneStyleFrom(csAllborder);
        csTextRightNumericFormatAndBorder.setAlignment(HorizontalAlignment.RIGHT);
        csTextRightNumericFormatAndBorder.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
    }
}
