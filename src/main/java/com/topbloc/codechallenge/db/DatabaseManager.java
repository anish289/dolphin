package com.topbloc.codechallenge.db;

import com.topbloc.codechallenge.InternalServerException;
import  org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatabaseManager {
    private static final String jdbcPrefix = "jdbc:sqlite:";
    private static final String dbName = "challenge.db";
    private static String connectionString;
    private static Connection conn;

    static {
        File dbFile = new File(dbName);
        connectionString = jdbcPrefix + dbFile.getAbsolutePath();
    }

    public static void connect() {
        try {
            Connection connection = DriverManager.getConnection(connectionString);
            System.out.println("Connection to SQLite has been established.");
            conn = connection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    // Schema function to reset the database if needed - do not change
    public static void resetDatabase() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        File dbFile = new File(dbName);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        connectionString = jdbcPrefix + dbFile.getAbsolutePath();
        connect();
        applySchema();
        seedDatabase();
    }

    // Schema function to reset the database if needed - do not change
    private static void applySchema() {
        String itemsSql = "CREATE TABLE IF NOT EXISTS items (\n"
                + "id integer PRIMARY KEY,\n"
                + "name text NOT NULL UNIQUE\n"
                + ");";
        String inventorySql = "CREATE TABLE IF NOT EXISTS inventory (\n"
                + "id integer PRIMARY KEY,\n"
                + "item integer NOT NULL UNIQUE references items(id) ON DELETE CASCADE,\n"
                + "stock integer NOT NULL,\n"
                + "capacity integer NOT NULL\n"
                + ");";
        String distributorSql = "CREATE TABLE IF NOT EXISTS distributors (\n"
                + "id integer PRIMARY KEY,\n"
                + "name text NOT NULL UNIQUE\n"
                + ");";
        String distributorPricesSql = "CREATE TABLE IF NOT EXISTS distributor_prices (\n"
                + "id integer PRIMARY KEY,\n"
                + "distributor integer NOT NULL references distributors(id) ON DELETE CASCADE,\n"
                + "item integer NOT NULL references items(id) ON DELETE CASCADE,\n"
                + "cost float NOT NULL\n" +
                ");";

        try {
            System.out.println("Applying schema");
            conn.createStatement().execute(itemsSql);
            conn.createStatement().execute(inventorySql);
            conn.createStatement().execute(distributorSql);
            conn.createStatement().execute(distributorPricesSql);
            System.out.println("Schema applied");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Schema function to reset the database if needed - do not change
    private static void seedDatabase() {
        String itemsSql = "INSERT INTO items (id, name) VALUES (1, 'Licorice'), (2, 'Good & Plenty'),\n"
            + "(3, 'Smarties'), (4, 'Tootsie Rolls'), (5, 'Necco Wafers'), (6, 'Wax Cola Bottles'), (7, 'Circus Peanuts'), (8, 'Candy Corn'),\n"
            + "(9, 'Twix'), (10, 'Snickers'), (11, 'M&Ms'), (12, 'Skittles'), (13, 'Starburst'), (14, 'Butterfinger'), (15, 'Peach Rings'), (16, 'Gummy Bears'), (17, 'Sour Patch Kids')";
        String inventorySql = "INSERT INTO inventory (item, stock, capacity) VALUES\n"
                + "(1, 22, 25), (2, 4, 20), (3, 15, 25), (4, 30, 50), (5, 14, 15), (6, 8, 10), (7, 10, 10), (8, 30, 40), (9, 17, 70), (10, 43, 65),\n" +
                "(11, 32, 55), (12, 25, 45), (13, 8, 45), (14, 10, 60), (15, 20, 30), (16, 15, 35), (17, 14, 60)";
        String distributorSql = "INSERT INTO distributors (id, name) VALUES (1, 'Candy Corp'), (2, 'The Sweet Suite'), (3, 'Dentists Hate Us')";
        String distributorPricesSql = "INSERT INTO distributor_prices (distributor, item, cost) VALUES \n" +
                "(1, 1, 0.81), (1, 2, 0.46), (1, 3, 0.89), (1, 4, 0.45), (2, 2, 0.18), (2, 3, 0.54), (2, 4, 0.67), (2, 5, 0.25), (2, 6, 0.35), (2, 7, 0.23), (2, 8, 0.41), (2, 9, 0.54),\n" +
                "(2, 10, 0.25), (2, 11, 0.52), (2, 12, 0.07), (2, 13, 0.77), (2, 14, 0.93), (2, 15, 0.11), (2, 16, 0.42), (3, 10, 0.47), (3, 11, 0.84), (3, 12, 0.15), (3, 13, 0.07), (3, 14, 0.97),\n" +
                "(3, 15, 0.39), (3, 16, 0.91), (3, 17, 0.85)";

        try {
            System.out.println("Seeding database");
            conn.createStatement().execute(itemsSql);
            conn.createStatement().execute(inventorySql);
            conn.createStatement().execute(distributorSql);
            conn.createStatement().execute(distributorPricesSql);
            System.out.println("Database seeded");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Helper methods to convert ResultSet to JSON - change if desired, but should not be required
    private static JSONArray convertResultSetToJson(ResultSet rs) throws SQLException{
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<String> colNames = IntStream.range(0, columns)
                .mapToObj(i -> {
                    try {
                        return md.getColumnName(i + 1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList());

        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            jsonArray.add(convertRowToJson(rs, colNames));
        }
        return jsonArray;
    }

    private static JSONObject convertRowToJson(ResultSet rs, List<String> colNames) throws SQLException {
        JSONObject obj = new JSONObject();
        for (String colName : colNames) {
            obj.put(colName, rs.getObject(colName));
        }
        return obj;
    }

    // Controller functions - add your routes here. getItems is provided as an example

    /**
     * Retrieves all items by name and id, edited to also retrieve item stock and capacity
     * @return JSONArray of all items
     */
    //retrieves all items by name and id, and edited to retrieve item stock and capacity
    public static JSONArray getItems() {
        String sql = "SELECT items.name, items.id, inventory.stock, inventory.capacity " +
                "FROM items, inventory " +
                "ON items.id=inventory.item";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static JSONArray getInventory() {
        String sql = "SELECT * FROM inventory";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * @return all items currently out of stock
     */
    public static JSONArray getItemsOutOfStock() {
        String sql = "SELECT items.name, items.id, inventory.stock, inventory.capacity " +
                "FROM items, inventory " +
                "ON items.id=inventory.item " +
                "WHERE inventory.stock == 0";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     *
     * @return all items currently overstocked
     */
    public static JSONArray getItemsOverstocked() {
        String sql = "SELECT items.name, items.id, inventory.stock, inventory.capacity " +
                "FROM items, inventory " +
                "ON items.id=inventory.item " +
                "WHERE inventory.stock > inventory.capacity";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * @return all items currently low on stock (<35% of capacity)
     */
    public static JSONArray getItemsLowOnStock() {
        String sql = "SELECT items.name, items.id, inventory.stock, inventory.capacity " +
                "FROM items, inventory " +
                "ON items.id=inventory.item " +
                "WHERE inventory.stock < .35 * inventory.capacity";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * @param id
     * @return item details given an id
     */
    public static JSONArray getItem(int id) {
        String sql = "SELECT items.name, items.id, inventory.stock, inventory.capacity " +
                "FROM items, inventory " +
                "ON items.id=inventory.item " +
                "WHERE items.id == " + id;
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // test method to retrieve all items, including ones that are just added and not in inventory
    public static JSONArray getItemsTest() {
        String sql = "SELECT * " +
                "FROM items";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


    /**
     * @return all distributors including name and id
     */
    public static JSONArray getDistributors() {
        String sql = "SELECT distributors.id, distributors.name " +
                "FROM distributors";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // test method to retrieve distributor prices table for testing
    public static JSONArray getDistributorPrices() {
        String sql = "SELECT * FROM distributor_prices";
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     *
     * @param id
     * @return all items from a distributor given distributor's id
     */
    public static JSONArray getItemsFromDistributor(int id) {
        String sql = "SELECT distributor_prices.distributor, distributor_prices.item, distributor_prices.cost, items.name " +
                "FROM distributor_prices, items " +
                "ON distributor_prices.item=items.id " +
                "WHERE distributor_prices.distributor == " + id;
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     *
     * @param id
     * @return all distributors of an item given item id
     */
    public static JSONArray getDistributorsFromItem(int id) {
        String sql = "SELECT distributors.name, distributor_prices.distributor, distributor_prices.cost, distributor_prices.item " +
                "FROM distributors, distributor_prices " +
                "ON distributor_prices.distributor=distributors.id " +
                "WHERE distributor_prices.item == " + id;
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * adds item to database given id and name
     * @param id
     * @param name
     */
    public static void addItem(int id, String name) {

        String sql = "INSERT INTO items(id, name) VALUES(?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new InternalServerException();
        }
    }

    /**
     * adds item to inventory in database given item id, stock, and capacity
     * @param item
     * @param stock
     * @param capacity
     */
    public static void addItemToInventory(int item, int stock, int capacity) {

        String sql = "INSERT INTO inventory (item, stock, capacity) VALUES(?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, item);
            ps.setInt(2, stock);
            ps.setInt(3, capacity);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * change the stock of an item given the item id and amount to change to
     * @param item
     * @param stock
     */
    public static void modifyItemStock(int item, int stock) {

        String sql = "UPDATE inventory SET stock = ? WHERE item = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, stock);
            ps.setInt(2, item);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * change the capacity of an item given the item id and amount to change to
     * @param item
     * @param capacity
     */
    public static void modifyItemCapacity(int item, int capacity) {

        String sql = "UPDATE inventory SET capacity = ? WHERE item = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, capacity);
            ps.setInt(2, item);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * add a distributor given distributor id and name
     * @param id
     * @param name
     */
    public static void addDistributor(int id, String name) {

        String sql = "INSERT INTO distributors (id, name) VALUES(?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * add item to a distributor's catalog given distributor id, item id, and item cost
     * @param distributor
     * @param item
     * @param cost
     */
    public static void addItemToDistributor(int distributor, int item, double cost) {

        String sql = "INSERT INTO distributor_prices (distributor, item, cost) VALUES(?,?,?) ";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, distributor);
            ps.setInt(2, item);
            ps.setDouble(3, cost);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * change the cost of a distributor's item given distributor id, item id, and amount to change cost to
     * @param distributor
     * @param item
     * @param cost
     */
    public static void modifyDistributorPrice(int distributor, int item, double cost) {

        String sql = "UPDATE distributor_prices SET cost = ? WHERE item = ? AND distributor = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, cost);
            ps.setInt(2, item);
            ps.setInt(3, distributor);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * @param item
     * @param quantity
     * @return minimum cost of item from all distributor's given item id, quantity
     */
    public static JSONArray getMinCost(int item, int quantity) {
        String sql =  "SELECT ((MIN(cost)) * " + quantity + ") FROM distributor_prices " +
                "WHERE item == " + item;
        try {
            ResultSet set = conn.createStatement().executeQuery(sql);
            return convertResultSetToJson(set);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * removes an item from the inventory
     * @param item
     */
    public static void removeItemFromInventory(int item) {
        String sql = "DELETE FROM inventory WHERE item = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, item);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new InternalServerException();
        }
    }

    /**
     * removes a distributor from the database
     * @param id
     */
    public static void removeDistributor(int id) {
        String sql = "DELETE FROM distributors WHERE id = ?";
        String sql2 = "DELETE FROM distributor_prices WHERE distributor = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            // remove data for distributor from the prices table also since data is no longer needed
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, id);
            ps2.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new InternalServerException();
        }
    }
}