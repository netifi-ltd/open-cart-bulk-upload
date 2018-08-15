package co.uk.netifi.ocbu;

import co.uk.netifi.ocbu.models.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 13/08/2018.
 */
public class Main {

    /** Destination MySql OpenCart database ( assumed tables are prefixed with oc_ ) */
    private static final String USERNAME = "xxx";
    private static final String PASSWORD = "xxx";
    private static final String SERVERNAME = "localhost";
    private static final int PORTNUMBER = 3306;
    private static final String DBNAME= "/xxx?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    /** Destination and Target for images (nwhich will then be manually copied to server */
    private static final String FROMDIR = "C:\\DEV\\EverythingDoors\\Suppliers\\Dale\\All_Product_Images";
    private static final String TODIR = "C:\\DEV\\EverythingDoors\\Suppliers\\Dale\\EDoors_Product_Images";

    public static void main (String... args) throws SQLException {
        CategoryManager categoryManager = new CategoryManager(USERNAME, PASSWORD, SERVERNAME, PORTNUMBER, DBNAME);
        BulkProductUpload bulkProductUpload = new BulkProductUpload(USERNAME, PASSWORD, SERVERNAME, PORTNUMBER, DBNAME);

        Map<String, Integer> categoryStringIdMap = categoryManager.getCategoryStringIdMap();

        ProductMaker productMaker = new ProductMaker(categoryStringIdMap);
        List<Product> products = productMaker.getProducts();

        bulkProductUpload.insertProducts(products);

        ImageExtraction imageExtraction = new ImageExtraction();
        imageExtraction.copy(products, FROMDIR, TODIR);

    }
}
