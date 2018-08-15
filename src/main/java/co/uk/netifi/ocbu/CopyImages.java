package co.uk.netifi.ocbu;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class CopyImages {

    private Map<String, Integer> colIndexMap;
    {
        colIndexMap = new LinkedHashMap<>();
        colIndexMap.put("ImageName", -1);
        colIndexMap.put("ImageExt", -1);
    }

    public void copyImages(String productExcelFile, String allImagesFolder, String eDoorsImagesFolder) {
        try {
            FileInputStream excelFile = new FileInputStream(new File(productExcelFile));
            Workbook workbook = new XSSFWorkbook(excelFile);

            Sheet productSheet = workbook.getSheet("product");
            setUpColumnNameToColumnIndex(productSheet.getRow(0));

            int rowIndex = 1;
            Row row = productSheet.getRow(rowIndex);
            do {


                rowIndex++;
                row = productSheet.getRow(rowIndex);
            } while (row.getCell(0) != null && row.getCell(0).getCellTypeEnum() != CellType.BLANK);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setUpColumnNameToColumnIndex(Row row) {
        Iterator<Cell> iterator = row.iterator();
        int colIndex = 0;
        while (iterator.hasNext()) {
            Cell nextCell = iterator.next();
            if (colIndexMap.containsKey(nextCell.getStringCellValue())) {
                colIndexMap.put(nextCell.getStringCellValue(), colIndex);
            }
            colIndex++;
        }

        System.out.println(colIndexMap);
    }
}
