package com.topbloc.codechallenge;

import com.topbloc.codechallenge.db.DatabaseManager;
import java.sql.SQLException;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        DatabaseManager.connect();
        // Don't change this - required for GET and POST requests with the header 'content-type'
        options("/*",
                (req, res) -> {
                    res.header("Access-Control-Allow-Headers", "content-type");
                    res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
                    return "OK";
                });

        // Don't change - if required you can reset your database by hitting this endpoint at localhost:4567/reset
        get("/reset", (req, res) -> {
            DatabaseManager.resetDatabase();
            return "OK";
        });

        // GET inventory routes
        get("/items", (req, res) -> DatabaseManager.getItems());

        // test route that retrieves all items, including ones that are just added and not in inventory
        get("/items-all", (req, res) -> DatabaseManager.getItemsTest());

        get("/version", (req, res) -> "TopBloc Code Challenge v1.0");
        get("/items-out-of-stock", (req, res) -> DatabaseManager.getItemsOutOfStock());
        get("/items-overstocked", (req, res) -> DatabaseManager.getItemsOverstocked());
        get("/items-low-stock", (req, res) -> DatabaseManager.getItemsLowOnStock());
        get("/item/:id", (req, res) -> DatabaseManager.getItem(Integer.parseInt(req.params(":id"))));
        get("/inventory", (req, res) -> DatabaseManager.getInventory());

        // GET distributor routes
        get("/distributors", (req, res) -> DatabaseManager.getDistributors());

        // test route to retrieve distributor prices table for testing
        get("/distributor-prices", (req, res) -> DatabaseManager.getDistributorPrices());

        get("/items/:dist-id", (req, res) -> DatabaseManager.getItemsFromDistributor
                (Integer.parseInt(req.params(":dist-id"))));
        get("/distributors/:item-id", (req, res) -> DatabaseManager.getDistributorsFromItem
                (Integer.parseInt(req.params(":item-id"))));
        get("/min-cost", (req, res) -> DatabaseManager.getMinCost(Integer.parseInt(req.queryParams("item-id")),
                Integer.parseInt(req.queryParams("quantity"))));

        //POST routes
        post("/item", "application/json", (req, res) -> {
            DatabaseManager.addItem(Integer.parseInt(req.queryParams("id")), req.queryParams("name"));
            return "OK";
        });
        post("/item-to-inventory", "application/json", (req, res) -> {
            DatabaseManager.addItemToInventory(Integer.parseInt(req.queryParams("item-id")),
                    Integer.parseInt(req.queryParams("stock")), Integer.parseInt(req.queryParams("capacity")));
            return "OK";
        });
        post("/distributor", "application/json", (req, res) -> {
            DatabaseManager.addDistributor(Integer.parseInt(req.queryParams("id")), (req.queryParams("name")));
            return "OK";
        });
        post("/item-to-distributor", "application/json", (req, res) -> {
            DatabaseManager.addItemToDistributor(Integer.parseInt(req.queryParams("dist-id")),
                    Integer.parseInt(req.queryParams("item-id")), Double.parseDouble((req.queryParams("cost"))));
            return "OK";
        });

        //PUT routes
        put("/item-stock","application/json", (req, res) -> {
            DatabaseManager.modifyItemStock(Integer.parseInt(req.queryParams("id")),
                    Integer.parseInt(req.queryParams("stock")));
            return "OK";
        });
        put("/item-capacity","application/json", (req, res) -> {
            DatabaseManager.modifyItemCapacity(Integer.parseInt(req.queryParams("id")),
                    Integer.parseInt(req.queryParams("capacity")));
            return "OK";
        });
        put("/distributor-price","application/json", (req, res) -> {
            DatabaseManager.modifyDistributorPrice(Integer.parseInt(req.queryParams("dist-id")),
                    Integer.parseInt(req.queryParams("item-id")), Double.parseDouble(req.queryParams("cost")));
            return "OK";
        });

        // DELETE routes
        delete("/item-from-inventory","application/json", (req, res) -> {
            DatabaseManager.removeItemFromInventory(Integer.parseInt(req.queryParams("id")));
            return "OK";
        });
        delete("/distributor","application/json", (req, res) -> {
            DatabaseManager.removeDistributor(Integer.parseInt(req.queryParams("id")));
            return "OK";
        });

        exception(InternalServerException.class, (e, req, res) -> {
            res.status(500);
            // since sql exceptions are due to constraint violations, add message when these occur
            res.body("ERROR: ID or name in query parameters is already taken, try again!");
        });
    }
}