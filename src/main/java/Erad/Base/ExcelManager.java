package Erad.Base;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import Erad.config.DefineConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.ITestResult;

    public class ExcelManager {
        private static List<Map<String, String>> excelRow = null;

        public ExcelManager() {
        }

        public static synchronized List<Map<String, String>> getControllerRowsList() {
            return excelRow;
        }

        public static String getCellValue(Cell cell) {
            if (cell == null) {
                return "";
            } else {
                CellType cellType = cell.getCellType();
                switch (cellType) {
                    case STRING:
                        return cell.getStringCellValue();
                    case NUMERIC:
                        return String.valueOf(cell.getNumericCellValue());
                    case BOOLEAN:
                        return String.valueOf(cell.getBooleanCellValue());
                    case FORMULA:
                        return String.valueOf(cell.getCellFormula());
                    default:
                        return "";
                }
            }
        }

        private static synchronized List<Map<String, String>> getExcelRow() {
//            LOGGER.info("TIME : getExcelRow Started.");
            List<Map<String, String>> data = new LinkedList();

            try {
                FileInputStream fis = new FileInputStream(new File(DefineConstants.ExcelPath).getAbsolutePath());

                try {
                    Workbook workbook = new XSSFWorkbook(fis);

                    try {
                        Sheet sheet = workbook.getSheet("Controller");
                        Row headerRow = sheet.getRow(0);
                        int numColumns = headerRow.getLastCellNum();

                        for(int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); ++rowIndex) {
                            Row currentRow = sheet.getRow(rowIndex);
                            Map<String, String> rowData = new HashMap();

                            for(int columnIndex = 0; columnIndex < numColumns; ++columnIndex) {
                                Cell currentCell = currentRow.getCell(columnIndex, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                                String header = headerRow.getCell(columnIndex).getStringCellValue();
                                String cellValue = getCellValue(currentCell);
                                rowData.put(header, cellValue);
                            }

                            data.add(rowData);
                        }
                    } catch (Throwable var15) {
                        try {
                            workbook.close();
                        } catch (Throwable var14) {
                            var15.addSuppressed(var14);
                        }

                        throw var15;
                    }

                    workbook.close();
                } catch (Throwable var16) {
                    try {
                        fis.close();
                    } catch (Throwable var13) {
                        var16.addSuppressed(var13);
                    }

                    throw var16;
                }

                fis.close();
            } catch (IOException var17) {
//                LOGGER.error(var17);
                throw new RuntimeException(var17);
            }

//            LOGGER.info("TIME : getExcelRow Completed.");
            return data;
        }

        public static int getRowIndex(String reference, Sheet sheet, int columnNumber) {
            Iterator<Row> rows = sheet.rowIterator();

            XSSFRow row;
            do {
                if (!rows.hasNext()) {
                    String msg = "No Such Reference Found. Reference -> " + reference;
                    throw new RuntimeException(msg);
                }

                row = (XSSFRow)rows.next();
            } while(!row.getCell(columnNumber).toString().trim().equals(reference.trim()));

            return row.getRowNum();
        }

        public static synchronized List<Map<String, String>> getExcelRowsAsListOfMap(String excelWorkbookName, String excelSheetName, String testMethodName) {
//            LOGGER.info("TIME : getExcelRowsAsListOfMap Started.");
            List<Map<String, String>> data = new LinkedList();

            try {
                FileInputStream fis = new FileInputStream(excelWorkbookName);

                try {
                    Workbook workbook = new XSSFWorkbook(fis);

                    try {
                        Sheet sheet = workbook.getSheet(excelSheetName);
                        Row headerRow = sheet.getRow(0);
                        int numColumns = headerRow.getLastCellNum();
                        int rowIndex = getRowIndex(testMethodName, sheet, 1);
                        Row currentRow = sheet.getRow(rowIndex);
                        Map<String, String> rowData = new HashMap();
                        int columnIndex = 0;

                        while(true) {
                            if (columnIndex >= numColumns) {
                                data.add(rowData);
                                break;
                            }

                            Cell currentCell = currentRow.getCell(columnIndex, MissingCellPolicy.CREATE_NULL_AS_BLANK);
                            String header = headerRow.getCell(columnIndex).getStringCellValue();
                            String cellValue = getCellValue(currentCell);
                            rowData.put(header, cellValue);
                            ++columnIndex;
                        }
                    } catch (Throwable var18) {
                        try {
                            workbook.close();
                        } catch (Throwable var17) {
                            var18.addSuppressed(var17);
                        }

                        throw var18;
                    }

                    workbook.close();
                } catch (Throwable var19) {
                    try {
                        fis.close();
                    } catch (Throwable var16) {
                        var19.addSuppressed(var16);
                    }

                    throw var19;
                }

                fis.close();
            } catch (IOException var20) {
//                LOGGER.error(var20);
                throw new RuntimeException(var20);
            }

//            LOGGER.info("TIME : getExcelRowsAsListOfMap Completed.");
            return data;
        }

        protected static synchronized void writeTestStatusToExcel(ITestResult result) {
            try {
                FileInputStream fileInputStream = new FileInputStream(new File(DefineConstants.ExcelPath).getAbsolutePath());
                Workbook workbook = new XSSFWorkbook(fileInputStream);
                Sheet sheet = workbook.getSheet("Controller");
                Map<String, Integer> headersMap = new LinkedHashMap();

                int columnIndex;
                for(columnIndex = 0; columnIndex < sheet.getRow(0).getPhysicalNumberOfCells(); ++columnIndex) {
                    headersMap.put(sheet.getRow(0).getCell(columnIndex).getStringCellValue(), columnIndex);
                }

                columnIndex = 1;

                while(true) {
                    if (columnIndex >= sheet.getPhysicalNumberOfRows()) {
                        fileInputStream.close();
                        FileOutputStream fileOutputStream = new FileOutputStream(new File(DefineConstants.ExcelPath).getAbsolutePath());
                        workbook.write(fileOutputStream);
                        fileOutputStream.close();
                        break;
                    }

                    if (sheet.getRow(columnIndex).getCell((Integer)headersMap.get("TestMethodName")) != null && StringUtils.isNotBlank(sheet.getRow(columnIndex).getCell((Integer)headersMap.get("TestMethodName")).getStringCellValue())) {
                        String testMethodName = sheet.getRow(columnIndex).getCell((Integer)headersMap.get("TestMethodName")).getStringCellValue();
                        String executeFlag = sheet.getRow(columnIndex).getCell((Integer)headersMap.get("Execute")).getStringCellValue();
                        if (executeFlag.equalsIgnoreCase("yes") && result.getMethod().getMethodName().equals(testMethodName)) {
                            String status = result.getStatus() == 2 ? "Failed" : "Passed";
                            sheet.getRow(columnIndex).getCell((Integer)headersMap.get("Status")).setCellValue(status);
                        }
                    }

                    ++columnIndex;
                }
            } catch (Exception var9) {
//                LOGGER.error(var9);
                throw new RuntimeException(var9);
            }

           // LOGGER.debug("Successfully wrote back test result to TestRunner sheet");
        }

        public static synchronized void writeToExcelColumn(Map<String, String> data, String sheetName, String columnName, String columnValueToSet) {
            writeToExcelColumn(new File(DefineConstants.ExcelPath).getAbsolutePath(), sheetName, (String)data.get("TestMethodName"), columnName, columnValueToSet);
        }

        public static synchronized void writeToExcelColumn(String workBookPath, String sheetName, String testMethodName, String columnName, String columnValueToSet) {
            try {
                FileInputStream fileInputStream = new FileInputStream(workBookPath);
                Workbook workbook = new XSSFWorkbook(fileInputStream);
                Sheet sheet = workbook.getSheet(sheetName);
                Map<String, Integer> headersMap = new LinkedHashMap();
                int columnIndex = 0;

                while(true) {
                    if (columnIndex >= sheet.getRow(0).getPhysicalNumberOfCells()) {
                        for(columnIndex = 1; columnIndex < sheet.getPhysicalNumberOfRows(); ++columnIndex) {
                            String runTimePKey = sheet.getRow(columnIndex).getCell((Integer)headersMap.get("TestMethodName")).getStringCellValue();
                            if (runTimePKey.equals(testMethodName)) {
                                sheet.getRow(columnIndex).getCell((Integer)headersMap.get(columnName)).setCellValue(columnValueToSet);
                            }
                        }

                        fileInputStream.close();
                        FileOutputStream fileOutputStream = new FileOutputStream(workBookPath);
                        workbook.write(fileOutputStream);
                        fileOutputStream.close();
                        break;
                    }

                    headersMap.put(sheet.getRow(0).getCell(columnIndex).getStringCellValue(), columnIndex);
                    ++columnIndex;
                }
            } catch (Exception var11) {
               // LOGGER.error(var11);
                throw new RuntimeException(var11);
            }

          //  LOGGER.debug("Successfully wrote to excel");
        }

        public static synchronized void writeToExcelColumn(String workBookPath, String sheetName, String testMethodName, String columnName, String columnValueToSet, String P_key) {
            try {
                FileInputStream fileInputStream = new FileInputStream(workBookPath);
                Workbook workbook = new XSSFWorkbook(fileInputStream);
                Sheet sheet = workbook.getSheet(sheetName);
                Map<String, Integer> headersMap = new LinkedHashMap();
                int columnIndex = 0;

                label33:
                while(true) {
                    if (columnIndex >= sheet.getRow(0).getPhysicalNumberOfCells()) {
                        columnIndex = 1;

                        while(true) {
                            if (columnIndex < sheet.getPhysicalNumberOfRows()) {
                                String runTimePKey = sheet.getRow(columnIndex).getCell((Integer)headersMap.get("TestMethodName")).getStringCellValue();
                                String runP_key = getCellValue(sheet.getRow(columnIndex).getCell((Integer)headersMap.get("P_Key")));
                                if (!runTimePKey.equals(testMethodName) || !runP_key.equals(P_key)) {
                                    ++columnIndex;
                                    continue;
                                }

                                sheet.getRow(columnIndex).getCell((Integer)headersMap.get(columnName)).setCellValue(columnValueToSet);
                            }

                            fileInputStream.close();
                            FileOutputStream fileOutputStream = new FileOutputStream(workBookPath);
                            workbook.write(fileOutputStream);
                            fileOutputStream.close();
                            break label33;
                        }
                    }

                    headersMap.put(sheet.getRow(0).getCell(columnIndex).getStringCellValue(), columnIndex);
                    ++columnIndex;
                }
            } catch (Exception var13) {
             //   LOGGER.error(var13);
                throw new RuntimeException(var13);
            }

            //LOGGER.debug("Successfully wrote to excel");
        }

        protected static Map<String, String> getControllerRowMapByTestMethodName(String testMethodName) {
            return (Map)((List)excelRow.stream().filter((map) -> {
                return ((String)map.get("TestMethodName")).equals(testMethodName);
            }).collect(Collectors.toList())).get(0);
        }

        public static Sheet getSheet(Path excelPath, String sheetName) throws Exception {
            FileInputStream fileInputStream = new FileInputStream(excelPath.toFile());
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            return workbook.getSheet(sheetName);
        }

        public static int getRowIndex(String reference, Path excelPath, String sheetName, int rowNumber) throws Exception {
            Iterator<Row> rows = getSheet(excelPath, sheetName).rowIterator();

            XSSFRow row;
            do {
                if (!rows.hasNext()) {
                //    LOGGER.error("No Such Reference Found. Reference -> " + reference);
                    throw new Exception("No Such Reference Found. Reference -> " + reference);
                }

                row = (XSSFRow)rows.next();
            } while(!row.getCell(rowNumber).toString().trim().equals(reference.trim()));

            return row.getRowNum();
        }

        public static Map<String, String> getRowValue(int rowNumber, Path excelPath, String sheetName) throws Exception {
            Map<String, String> rowValue = new HashMap();
            Sheet sheet = getSheet(excelPath, sheetName);
            Iterator<Cell> keyCells = sheet.getRow(0).cellIterator();
            XSSFRow valueRow = (XSSFRow)sheet.getRow(rowNumber);
            int i = 0;

            while(keyCells.hasNext()) {
                String key = ((Cell)keyCells.next()).toString().trim();

                String value;
                try {
                    value = getCellValue(valueRow.getCell(i)).trim();
                } catch (NoSuchElementException var11) {
                    value = "";
                }

                ++i;
                rowValue.put(key, value);
            }

            return rowValue;
        }

        public static Map<String, String> getRowValue(int rowNumber, int headerRowNumber, Path excelPath, String sheetName) throws Exception {
            Map<String, String> rowValue = new HashMap();
            Sheet sheet = getSheet(excelPath, sheetName);
            Iterator<Cell> keyCells = sheet.getRow(headerRowNumber).cellIterator();
            XSSFRow valueRow = (XSSFRow)sheet.getRow(rowNumber);
            int i = 0;

            while(keyCells.hasNext()) {
                String key = ((Cell)keyCells.next()).toString().trim();

                String value;
                try {
                    value = getCellValue(valueRow.getCell(i)).trim();
                } catch (NoSuchElementException var12) {
                    value = "";
                }

                ++i;
                rowValue.put(key, value);
            }

            return rowValue;
        }

        public static Map<String, String> getRowValue(String reference, Path excelPath, String sheetName) throws Exception {
            return getRowValue(getRowIndex(reference, excelPath, sheetName, 0), excelPath, sheetName);
        }

        private static String getMobileSettingsReference() throws Exception {
            FileInputStream fileInputStream = new FileInputStream(new File(DefineConstants.ExcelPath).getAbsolutePath());
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheet("Settings");
            return sheet.getRow(2).getCell(1).toString();
        }

        private static String getAPISettingsReference() throws Exception {
            FileInputStream fileInputStream = new FileInputStream(new File(DefineConstants.ExcelPath).getAbsolutePath());
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheet("Settings");
            return sheet.getRow(3).getCell(1).toString();
        }

        private static String getWebSettingsReference() throws Exception {
            FileInputStream fileInputStream = new FileInputStream(new File(DefineConstants.ExcelPath).getAbsolutePath());
            Workbook workbook = new XSSFWorkbook(fileInputStream);
            Sheet sheet = workbook.getSheet("Settings");
            return sheet.getRow(1).getCell(1).toString();
        }

        /*private static Map<String, String> getWebSettingsDetailsAsMap() throws Exception {
            return getRowValue(getRowIndex(getMobileSettingsReference(), new File(DefineConstants.ExcelPath), "Settings", 0), getRowIndex("WebConfiguration", Constants.RUN_MANAGER_WORKBOOK, "Settings", 0), Constants.RUN_MANAGER_WORKBOOK, "Settings");
        }

        public static Map<String, String> getMobileSettingsDetailsAsMap() throws Exception {
            return getRowValue(getRowIndex(getMobileSettingsReference(), Constants.RUN_MANAGER_WORKBOOK, "Settings", 0), getRowIndex("MobileConfiguration", Constants.RUN_MANAGER_WORKBOOK, "Settings", 0), Constants.RUN_MANAGER_WORKBOOK, "Settings");
        }

        public static Map<String, String> getMobileSettingsDetailsAsMap(String reference) throws Exception {
            return getRowValue(getRowIndex(reference, Constants.RUN_MANAGER_WORKBOOK, "Settings", 0), getRowIndex("MobileConfiguration", Constants.RUN_MANAGER_WORKBOOK, "Settings", 0), Constants.RUN_MANAGER_WORKBOOK, "Settings");
        }

        public static Map<String, String> getAPISettingsDetailsAsMap() throws Exception {
            return getRowValue(getRowIndex(getAPISettingsReference(), Constants.RUN_MANAGER_WORKBOOK, "Settings", 0), getRowIndex("APIConfiguration", Constants.RUN_MANAGER_WORKBOOK, "Settings", 0), Constants.RUN_MANAGER_WORKBOOK, "Settings");
        }

        public static Map<String, String> getAPISettingsDetailsAsMap(String reference) throws Exception {
            return getRowValue(getRowIndex(reference, Constants.RUN_MANAGER_WORKBOOK, "Settings", 0), getRowIndex("MobileConfiguration", Constants.RUN_MANAGER_WORKBOOK, "Settings", 0), Constants.RUN_MANAGER_WORKBOOK, "Settings");
        }
*/
        public static List<String> getExcelColumnAsList(String excelPath, String sheetName, int columnNumber) {
            List<String> columnValues = new ArrayList();

            try {
                FileInputStream fileInputStream = new FileInputStream(excelPath);
                Workbook workbook = new XSSFWorkbook(fileInputStream);
                Sheet sheet = workbook.getSheet(sheetName);
                Iterator var7 = sheet.iterator();

                while(var7.hasNext()) {
                    Row row = (Row)var7.next();
                    Cell cell = row.getCell(columnNumber);
                    cell.setCellType(CellType.STRING);
                    columnValues.add(getCellValue(cell));
                }

                return columnValues;
            } catch (Exception var10) {
              //  LOGGER.error(var10);
                throw new RuntimeException(var10);
            }
        }

        public static synchronized void writeToExcelCell(String workBookPath, String sheetName, int rowNumber, int columnNumber, String columnValueToSet) {
            try {
                FileInputStream fileInputStream = new FileInputStream(workBookPath);
                Workbook workbook = new XSSFWorkbook(fileInputStream);
                Sheet sheet = workbook.getSheet(sheetName);
                sheet.getRow(rowNumber).getCell(columnNumber).setCellValue(columnValueToSet);
                fileInputStream.close();
                FileOutputStream fileOutputStream = new FileOutputStream(workBookPath);
                workbook.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception var9) {
               // LOGGER.error(var9);
                throw new RuntimeException(var9);
            }

           // LOGGER.debug("Successfully wrote to excel");
        }

        static {
            try {
                excelRow = getExcelRow();
            } catch (Exception var1) {
                var1.printStackTrace();
            }

        }
    }

