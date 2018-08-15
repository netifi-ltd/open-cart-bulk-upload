package co.uk.netifi.ocbu;

import co.uk.netifi.ocbu.models.Category;
import co.uk.netifi.ocbu.models.Product;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ProductMaker {

    private static final String FILE_NAME = "C:\\DEV\\EverythingDoors\\Suppliers\\Dale\\ProductDetails.xlsx";

    private Map<String, Integer> colIndexMap;
    private Map<String, Integer> categoryPathIdMap;

    {
        colIndexMap = new LinkedHashMap<>();
        colIndexMap.put("TempId", -1);
        colIndexMap.put("Name", -1);
        colIndexMap.put("Sku", -1);
        colIndexMap.put("Model", -1);
        colIndexMap.put("ImageLocation", -1);
        colIndexMap.put("ImageName", -1);
        colIndexMap.put("ImageExt", -1);
        colIndexMap.put("Price", -1);
    }

    public ProductMaker(Map<String, Integer> categoryPathIdMap) {
        this.categoryPathIdMap = categoryPathIdMap;
    }

    public List<Product> getProducts() {
        ArrayList<Product> products = new ArrayList<>();

        try {
            FileInputStream excelFile = new FileInputStream(new File(FILE_NAME));
            Workbook workbook = new XSSFWorkbook(excelFile);

            Sheet categorySheet = workbook.getSheet("product_to_category");
            Map<Integer, List<String>> categoryPaths = getCategoryPaths(categorySheet);

            Sheet productSheet = workbook.getSheet("product");
            setUpColumnNameToColumnIndex(productSheet.getRow(0));

            int rowIndex = 1;
            Row row = productSheet.getRow(rowIndex);
            do {
                Product product = createProduct(row);
                List<String> catPaths = categoryPaths.get(product.getProductId());
                if (catPaths != null) {
                    for (String catPath : catPaths) {
                        Integer categoryPrimaryKey = categoryPathIdMap.get(catPath);
                        Category c = new Category();
                        c.setCategoryId(categoryPrimaryKey);
                        product.getCategories().add(c);
                    }
                }
                products.add(product);
                rowIndex++;
                row = productSheet.getRow(rowIndex);
            } while (row.getCell(0) != null && row.getCell(0).getCellTypeEnum() != CellType.BLANK);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    private Map<Integer, List<String>> getCategoryPaths(Sheet categorySheet) {
        Map<Integer, List<String>> tempId2CatPath = new HashMap<>();

        int rowIndex = 1;
        Row row = categorySheet.getRow(rowIndex);
        do {

            int tempId = Double.valueOf(row.getCell(0).getNumericCellValue()).intValue();
            String path = row.getCell(1).getStringCellValue();
            if (tempId2CatPath.containsKey(tempId) == false) {
                tempId2CatPath.put(tempId, new ArrayList<>());
            }
            tempId2CatPath.get(tempId).add(path);

            rowIndex++;
            row = categorySheet.getRow(rowIndex);
        } while (row.getCell(0) != null && row.getCell(0).getCellTypeEnum() != CellType.BLANK );

        return tempId2CatPath;
    }

    private Product createProduct(Row row) {
        Cell tempIdCell = row.getCell(colIndexMap.get("TempId"));
        int tempId = Double.valueOf(tempIdCell.getNumericCellValue()).intValue();

        String name = row.getCell(colIndexMap.get("Name")).getStringCellValue();
        String sku = row.getCell(colIndexMap.get("Sku")).getStringCellValue();
        String model = row.getCell(colIndexMap.get("Model")).getStringCellValue();
        String imageLocation = row.getCell(colIndexMap.get("ImageLocation")).getStringCellValue();
        String imageName = row.getCell(colIndexMap.get("ImageName")).getStringCellValue();
        String imageExt = row.getCell(colIndexMap.get("ImageExt")).getStringCellValue();

        double price = row.getCell(colIndexMap.get("Price")).getNumericCellValue();

        Product product = new Product(tempId, name, model, sku, imageLocation, imageName, imageExt, price);
        return product;
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
