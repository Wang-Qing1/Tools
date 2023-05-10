package org.tools.poi;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ExcelTools {
    public static void main(String[] args) {
        readExcelByPOI();
    }

    private static void readExcelByPOI() {
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream("E:\\text.xlsx"))){
            // 获取工作簿
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i < sheet.getLastRowNum(); i++) {
                // 获取行
                Row row = sheet.getRow(i);

                // 获取单元格
                Cell cell = row.getCell(0);

                System.out.println("UUID:" + cell.getStringCellValue());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
