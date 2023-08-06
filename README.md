# Report-xlsx
Sometimes I need to generate a monthly report for any transaction occurred in the previous month to the customer, here is the example code where we can query the transaction in the selected month from the database, then summarize its dynamically to an excel file (.xlsx).

## Requirements
- JDK installed in your computer atleast version 1.8
- Apache POI-OOXML version 4.1.2

## Classes highlight
- banner.txt, nothing, just showing how much I love Liverpool FC 🐓
- ReportController, an API receiving company code, year, month, and language as parameters then return an .xlsx report file for the selected params.
- ExcelGenerate, a class for generating .xlsx report by set company info as a header, then add transactions and summarize its with excel formula.
- MonthlyReportGenerateService, monthly batch scheduling for generated report and send email to the customer.
  
## Xlsx report format
The report will have a header contains company info, transaction, and sum in this format
![image](https://github.com/sahawatts/report-xlsx/assets/88260319/14daebef-b29d-41ea-a7f2-13af28a53f95)

