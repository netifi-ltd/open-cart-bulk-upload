package co.uk.netifi.ocbu;

import co.uk.netifi.ocbu.models.Category;
import co.uk.netifi.ocbu.models.Product;

import java.sql.*;
import java.util.List;
import java.util.Properties;

public class BulkProductUpload {

    private String userName;
    private String password;
    private String serverName ;
    private int portNumber;
    private String dbName;


    public BulkProductUpload(String userName, String password, String serverName, int portNumber, String dbName) {
        this.userName = userName;
        this.password = password;
        this.serverName = serverName;
        this.portNumber = portNumber;
        this.dbName = dbName;
    }

    public void insertProducts(List<Product> products) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            insertProducts(conn, products);
            conn.commit();
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    private Connection getConnection() throws SQLException {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", this.userName);
        connectionProps.put("password", this.password);
        conn = DriverManager.getConnection("jdbc:mysql" + "://" + this.serverName + ":" + this.portNumber + dbName, connectionProps);
        System.out.println("Connected to database");
        return conn;
    }

    private void insertProducts(Connection conn, List<Product> products) throws SQLException {
        PreparedStatement insertProductStmt = null;

        String productInsertString = getProductInsertString();

        try {
            insertProductStmt = conn.prepareStatement(productInsertString, Statement.RETURN_GENERATED_KEYS);

            for (Product product : products) {
                int index = 0;

                insertProductStmt.setString(++index, product.getModel());
                insertProductStmt.setString(++index, product.getSku());
                insertProductStmt.setString(++index, product.getImageLocation() + "/" + product.getImageName() + "." + product.getImageExt());
                insertProductStmt.setDouble(++index, product.getPrice());
                insertProductStmt.setDate(++index, new Date(new java.util.Date().getTime()));
                insertProductStmt.setDate(++index, new Date(new java.util.Date().getTime()));
                insertProductStmt.setDate(++index, new Date(new java.util.Date().getTime()));

                insertProductStmt.executeUpdate();
                try (ResultSet generatedKeys = insertProductStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setProductId(generatedKeys.getInt(1));
                        insertProductDescription(conn, product);
                        insertProductImage(conn, product);
                        insertProductToLayout(conn, product);
                        insertProductToStore(conn, product);
                        insertCategories(conn, product, product.getCategories());
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    conn.rollback();
                } catch (SQLException excep) {
                    excep.printStackTrace();
                }
            }
        } finally {
            if (insertProductStmt != null) {
                insertProductStmt.close();
            }
        }
    }

    private void insertProductDescription(Connection conn, Product product) throws SQLException {
        PreparedStatement insertProductDesriptionStmt = null;

        String productDescriptionInsertString = getProductDescriptionInsertString();

        try {
            insertProductDesriptionStmt = conn.prepareStatement(productDescriptionInsertString);

            int index = 0;
            insertProductDesriptionStmt.setInt(++index, product.getProductId());
            insertProductDesriptionStmt.setString(++index, product.getName());
            insertProductDesriptionStmt.setString(++index, product.getName());
            insertProductDesriptionStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    conn.rollback();
                } catch (SQLException excep) {
                    excep.printStackTrace();
                }
            }
        } finally {
            if (insertProductDesriptionStmt != null) {
                insertProductDesriptionStmt.close();
            }
        }
    }

    private void insertProductImage(Connection conn, Product product) throws SQLException {
        PreparedStatement insertStmt = null;

        String insertString =
                "INSERT INTO oc_product_image (product_id, image, sort_order) " +
                        "VALUES (?, ?, 0);";

        try {
            insertStmt = conn.prepareStatement(insertString);
            int index = 0;
            insertStmt.setInt(++index, product.getProductId());
            insertStmt.setString(++index, product.getImageLocation() + "/" + product.getImageName() + "." + product.getImageExt());
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    conn.rollback();
                } catch (SQLException excep) {
                    excep.printStackTrace();
                }
            }
        } finally {
            if (insertStmt != null) {
                insertStmt.close();
            }
        }
    }

    private void insertProductToLayout(Connection conn, Product product) throws SQLException {
        PreparedStatement insertStmt = null;

        String insertString =
                "INSERT INTO oc_product_to_layout(product_id, store_id, layout_id) " +
                "VALUES (?, 0, 0);";

        try {
            insertStmt = conn.prepareStatement(insertString);
            int index = 0;
            insertStmt.setInt(++index, product.getProductId());
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    conn.rollback();
                } catch (SQLException excep) {
                    excep.printStackTrace();
                }
            }
        } finally {
            if (insertStmt != null) {
                insertStmt.close();
            }
        }
    }

    private void insertProductToStore(Connection conn, Product product) throws SQLException {
        PreparedStatement insertStmt = null;

        String insertString =
                "INSERT INTO oc_product_to_store(product_id, store_id) " +
                "VALUES (?, 0)";
        try {
            insertStmt = conn.prepareStatement(insertString);
            int index = 0;
            insertStmt.setInt(++index, product.getProductId());
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    conn.rollback();
                } catch (SQLException excep) {
                    excep.printStackTrace();
                }
            }
        } finally {
            if (insertStmt != null) {
                insertStmt.close();
            }
        }
    }

    private void insertCategories(Connection conn, Product product, List<Category> categories) throws SQLException {
        PreparedStatement insertCategoryStmt = null;

        String categoryInsertString = getCategoryInsertString();

        try {
            insertCategoryStmt = conn.prepareStatement(categoryInsertString);

            for (Category category: categories) {
                int index = 0;

                insertCategoryStmt.setInt(++index, product.getProductId());
                insertCategoryStmt.setInt(++index, category.getCategoryId());
                insertCategoryStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    conn.rollback();
                } catch (SQLException excep) {
                    excep.printStackTrace();
                }
            }
        } finally {
            if (insertCategoryStmt != null) {
                insertCategoryStmt.close();
            }
        }
    }

    private String getProductInsertString() {
        String productInsertString =
                "INSERT INTO oc_product(" +
                "  model" +
                "  ,sku" +
                "  ,upc" +
                "  ,ean" +
                "  ,jan" +
                "  ,isbn" +
                "  ,mpn" +
                "  ,location" +
                "  ,quantity" +
                "  ,stock_status_id" +
                "  ,image" +
                "  ,manufacturer_id" +
                "  ,shipping" +
                "  ,price" +
                "  ,points" +
                "  ,tax_class_id" +
                "  ,date_available" +
                "  ,weight" +
                "  ,weight_class_id" +
                "  ,length" +
                "  ,width" +
                "  ,height" +
                "  ,length_class_id" +
                "  ,subtract" +
                "  ,minimum" +
                "  ,sort_order" +
                "  ,status" +
                "  ,viewed" +
                "  ,date_added" +
                "  ,date_modified" +
                ") VALUES (" +
                "  ?" +
                "  ,?" +
                "  ,''" +
                "  ,''" +
                "  ,''" +
                "  ,''" +
                "  ,''" +
                "  ,''" +
                "  ,1" +
                "  ,7" +
                "  ,?" +
                "  ,11" +
                "  ,1" +
                "  ,?" +
                "  ,0" +
                "  ,9" +
                "  ,?" +
                "  ,0" +
                "  ,1" +
                "  ,0" +
                "  ,0" +
                "  ,0" +
                "  ,2" +
                "  ,0" +
                "  ,1" +
                "  ,0" +
                "  ,1" +
                "  ,0" +
                "  ,?" +
                "  ,?" +
                ")";

        return productInsertString;
    }

    private String getProductDescriptionInsertString() {
        String productDescriptionInsertString =
                "INSERT INTO oc_product_description(" +
                        "   product_id" +
                        "  ,language_id" +
                        "  ,name" +
                        "  ,description" +
                        "  ,tag" +
                        "  ,meta_title" +
                        "  ,meta_description" +
                        "  ,meta_keyword" +
                        ") VALUES (" +
                        "   ?" +
                        "  ,1" +
                        "  ,?" +
                        "  ,''" +
                        "  ,''" +
                        "  ,?" +
                        "  ,''" +
                        "  ,''" +
                        ")";
        return productDescriptionInsertString;
    }

    private String getCategoryInsertString() {
        String categoryInsertString =
                "INSERT INTO oc_product_to_category(" +
                "   product_id " +
                "  ,category_id " +
                ") VALUES (" +
                "   ?" +
                "  ,?" +
                ")";
        return categoryInsertString;
    }
}
