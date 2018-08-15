package co.uk.netifi.ocbu;

import co.uk.netifi.ocbu.models.Category;

import java.sql.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class CategoryManager {

    private String userName;
    private String password;
    private String serverName ;
    private int portNumber;
    private String dbName;

    public CategoryManager(String userName, String password, String serverName, int portNumber, String dbName) {
        this.userName = userName;
        this.password = password;
        this.serverName = serverName;
        this.portNumber = portNumber;
        this.dbName = dbName;
    }

    public Map<String, Integer> getCategoryStringIdMap() throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            List<Category> categories = getCategoryStringIdMap(conn);
            return getCategoryAndPaths(categories);
        } finally {
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

    private List<Category> getCategoryStringIdMap(Connection conn) throws SQLException {
        Statement stmt = null;
        String query =
                "select c.category_id, c.parent_id, cd.name " +
                        "from oc_category c " +
                        "join oc_category_description cd on c.category_id = cd.category_id " +
                        "where cd.language_id = 1 " +
                        "order by c.category_id, c.parent_id";

        List cats = new ArrayList<Category>();

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            Category c = null;
            while (rs.next()) {
                c =  new Category();
                c.setCategoryId(rs.getInt("category_id"));
                c.setParentId(rs.getInt("parent_id"));
                c.setName(rs.getString("name"));
                cats.add(c);
            }
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (stmt != null) { stmt.close(); }
        }

        return cats;
    }

    private Map<String, Integer> getCategoryAndPaths(List<Category> nodes) {
        Map<Integer, Category> mapTmp = new HashMap<>();

        for (Category current : nodes) {
            mapTmp.put(current.getCategoryId(), current);
        }

        for (Category current : nodes) {
            Integer parentId = current.getParentId();
            if (!parentId.equals(0)) {
                Category parent = mapTmp.get(parentId);
                if (parent != null) {
                    current.setParent(parent);
                    parent.addChild(current);
                    mapTmp.put(parentId, parent);
                    mapTmp.put(current.getCategoryId(), current);
                }
            }
        }

        List<Category> topL = nodes.stream().filter(c -> c.getParentId() == 0).collect(toList());
        Map<String, Integer> catPaths = new LinkedHashMap<>();
        for (Category c : topL) {
            mapCategoryPathsToId(c, "", catPaths);
        }
        return catPaths;
    }

    private void mapCategoryPathsToId(Category c, String prepend, Map<String, Integer> catPaths) {
        prepend += "/" + c.getName().trim().replace("&amp;", "&");
        System.out.println(prepend);
        catPaths.put(prepend, c.getCategoryId());

        if (c.getChildren() == null || c.getChildren().size() == 0) {
            return;
        }
        for (Category child : c.getChildren()) {
            mapCategoryPathsToId(child, prepend, catPaths);
        }
    }

}
