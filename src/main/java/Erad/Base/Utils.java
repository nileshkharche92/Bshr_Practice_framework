package Erad.Base;

import Erad.config.DefineConstants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {


//    public void getData() throws IOException {
//        FileInputStream f=new FileInputStream(new File(DefineConstants.ExcelPath).getAbsolutePath());
//        Sheet sheet=WorkbookFactory.create(f).getSheet("LogIn");
//       int cellNumbers= sheet.getRow(0).getLastCellNum();
//        System.out.println(cellNumbers);

    public static List<Map<String, String>> readTestData(String filePath, String sheetName) throws IOException {
        List<Map<String, String>> testDataList = new ArrayList<>();

        FileInputStream fis = new FileInputStream(filePath);

        //Workbook workbook = WorkbookFactory.create(fis);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetName);

        // Assuming the first row contains column headers
        Row headerRow = sheet.getRow(0);
        int columnCount = headerRow.getLastCellNum();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            Map<String, String> rowData = new HashMap<>();

            for (int j = 0; j < columnCount; j++) {
                Cell headerCell = headerRow.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                String columnName = headerCell.getStringCellValue().trim();
                String cellValue = cell.getStringCellValue().trim();

                rowData.put(columnName, cellValue);
            }

            testDataList.add(rowData);
        }

        workbook.close();
        fis.close();

        return testDataList;
    }

    public static void main(String[] args) {
        try {
            List<Map<String, String>> testData = readTestData(new File(DefineConstants.ExcelPath).getAbsolutePath(), "LogIn");

            // Example usage
            for (Map<String, String> data : testData) {
                System.out.println("Test data:");
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());

                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
