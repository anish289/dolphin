package com.topbloc.codechallenge.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static spark.Spark.options;

class DatabaseManagerTest {
    @BeforeEach
    public void setUp() {
        DatabaseManager.connect();
        options("/*",
                (req, res) -> {
                    res.header("Access-Control-Allow-Headers", "content-type");
                    res.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
                    return "OK";
                });
        DatabaseManager.resetDatabase();
    }

    @Test
    void getItemsOutOfStock() {
        assertEquals("[]", DatabaseManager.getItemsOutOfStock().toString());
    }

    @Test
    void getItemsOverstocked() {
        assertEquals("[]", DatabaseManager.getItemsOverstocked().toString());
    }

    @Test
    void getItemsLowOnStock() {
        assertEquals("[{\"name\":\"Good & Plenty\",\"id\":2,\"stock\":4,\"capacity\":20},{\"name\":\"Twix\"," +
                "\"id\":9,\"stock\":17,\"capacity\":70},{\"name\":\"Starburst\",\"id\":13,\"stock\":8,\"capacity\":45},"
                + "{\"name\":\"Butterfinger\",\"id\":14,\"stock\":10,\"capacity\":60},{\"name\":\"Sour " + "Patch Kids"
                + "\",\"id\":17,\"stock\":14,\"capacity\":60}]", DatabaseManager.getItemsLowOnStock().toString());
    }
    @Test
    void getItem() {
        assertEquals("[{\"name\":\"Good & Plenty\",\"id\":2,\"stock\":4,\"capacity\":20}]",
                DatabaseManager.getItem(2).toString());
    }
    @Test
    void getItemsFromDistributor() {
        assertEquals("[{\"item\":10,\"cost\":0.47,\"name\":\"Snickers\",\"distributor\":3},{\"item\":11," +
                "\"cost\":0.84," + "\"name\":\"M&Ms\",\"distributor\":3},{\"item\":12,\"cost\":0.15,\"name\":\"Skittles" +
                "\",\"distributor" + "\":3},{\"item\":13,\"cost\":0.07,\"name\":\"Starburst\",\"distributor\":3}," +
                "{\"item\":14,\"cost\":0.97," + "\"name\":\"Butterfinger\",\"distributor\":3},{\"item\":15,\"cost" +
                "\":0.39,\"name\":\"Peach " + "Rings\",\"distributor\":3},{\"item\":16,\"cost\":0.91,\"name\":\"" +
                "Gummy Bears\",\"distributor\":3}," + "{\"item\":17,\"cost\":0.85,\"name\":\"Sour " +
                "Patch Kids\",\"distributor\":3}]", DatabaseManager.getItemsFromDistributor(3).toString());
    }

    @Test
    void getDistributorsFromItem() {
        assertEquals("[{\"item\":2,\"cost\":0.46,\"name\":\"Candy Corp\",\"distributor\":1},{\"item\":2,\"cost" +
                "\":0.18,\"name\":\"The Sweet " + "Suite\",\"distributor\":2}]",
                DatabaseManager.getDistributorsFromItem(2).toString());
    }

    @Test
    void addItem() {
        DatabaseManager.addItem(18, "New Candy");
        assertEquals("[{\"name\":\"Licorice\",\"id\":1},{\"name\":\"Good & Plenty\",\"id\":2},{\"name\":\"Smarties\"," +
                "\"id\":3},{\"name\":\"Tootsie " + "Rolls\",\"id\":4},{\"name\":\"Necco Wafers\",\"id\":5},{\"name\":" +
                "\"Wax Cola Bottles\",\"id\":6},{\"name\":\"Circus " + "Peanuts\",\"id\":7},{\"name\":\"Candy " +
                "Corn\",\"id\":8},{\"name\":\"Twix\",\"id\":9},{\"name\":\"Snickers\",\"id\":10},{\"name\":\"M&Ms\"," +
                "\"id\":11},{\"name\":\"Skittles\",\"id\":12},{\"name\":\"Starburst\",\"id\":13},{\"name\":\"Butterfinger" +
                "\",\"id\":14},{\"name\":\"Peach " + "Rings\",\"id\":15},{\"name\":\"Gummy Bears\",\"id\":16}," +
                "{\"name\":\"Sour Patch Kids\",\"id\":17},{\"name\":\"New Candy\",\"id\":18}]",
                DatabaseManager.getItemsTest().toString());
    }

    @Test
    void addItemToInventory() {
        DatabaseManager.addItemToInventory(18, 20,50);
        assertEquals("[{\"item\":1,\"id\":1,\"stock\":22,\"capacity\":25},{\"item\":2,\"id\":2,\"stock\":4," +
                "\"capacity\":20},{\"item\":3,\"id\":3,\"stock\":15,\"capacity\":25},{\"item\":4,\"id\":4,\"stock" +
                "\":30," + "\"capacity\":50},{\"item\":5,\"id\":5,\"stock\":14,\"capacity\":15},{\"item\":6," +
                "\"id\":6,\"stock\":8,\"capacity\":10},{\"item\":7,\"id\":7,\"stock\":10,\"capacity\":10}," +
                "{\"item\":8,\"id\":8,\"stock\":30,\"capacity\":40},{\"item\":9,\"id\":9,\"stock\":17,\"capacity" +
                "\":70},{\"item\":10,\"id\":10,\"stock\":43,\"capacity\":65},{\"item\":11,\"id\":11,\"stock\":32," +
                "\"capacity\":55},{\"item\":12,\"id\":12,\"stock\":25,\"capacity\":45},{\"item\":13,\"id\":13," +
                "\"stock\":8,\"capacity\":45},{\"item\":14,\"id\":14,\"stock\":10,\"capacity\":60},{\"item\":15," +
                "\"id\":15,\"stock\":20,\"capacity\":30},{\"item\":16,\"id\":16,\"stock\":15,\"capacity\":35}," +
                "{\"item\":17,\"id\":17,\"stock\":14,\"capacity\":60},{\"item\":18,\"id\":18,\"stock\":20," +
                "\"capacity\":50}]", DatabaseManager.getInventory().toString());
    }

    @Test
    void modifyItemStock() {
        DatabaseManager.modifyItemStock(17, 60);
        assertEquals("[{\"item\":1,\"id\":1,\"stock\":22,\"capacity\":25},{\"item\":2,\"id\":2,\"stock\":4," +
                "\"capacity\":20},{\"item\":3,\"id\":3,\"stock\":15,\"capacity\":25},{\"item\":4,\"id\":4,\"stock" +
                "\":30," + "\"capacity\":50},{\"item\":5,\"id\":5,\"stock\":14,\"capacity\":15},{\"item\":6," +
                "\"id\":6,\"stock\":8,\"capacity\":10},{\"item\":7,\"id\":7,\"stock\":10,\"capacity\":10}," +
                "{\"item\":8,\"id\":8,\"stock\":30,\"capacity\":40},{\"item\":9,\"id\":9,\"stock\":17,\"capacity" +
                "\":70},{\"item\":10,\"id\":10,\"stock\":43,\"capacity\":65},{\"item\":11,\"id\":11,\"stock\":32," +
                "\"capacity\":55},{\"item\":12,\"id\":12,\"stock\":25,\"capacity\":45},{\"item\":13,\"id\":13," +
                "\"stock\":8,\"capacity\":45},{\"item\":14,\"id\":14,\"stock\":10,\"capacity\":60},{\"item\":15," +
                "\"id\":15,\"stock\":20,\"capacity\":30},{\"item\":16,\"id\":16,\"stock\":15,\"capacity\":35}," +
                "{\"item\":17,\"id\":17,\"stock\":60,\"capacity\":60}]", DatabaseManager.getInventory().toString());
    }

    @Test
    void modifyItemCapacity() {
        DatabaseManager.modifyItemCapacity(17, 80);
        assertEquals("[{\"item\":1,\"id\":1,\"stock\":22,\"capacity\":25},{\"item\":2,\"id\":2,\"stock\":4," +
                "\"capacity\":20},{\"item\":3,\"id\":3,\"stock\":15,\"capacity\":25},{\"item\":4,\"id\":4,\"stock" +
                "\":30," + "\"capacity\":50},{\"item\":5,\"id\":5,\"stock\":14,\"capacity\":15},{\"item\":6," +
                "\"id\":6,\"stock\":8,\"capacity\":10},{\"item\":7,\"id\":7,\"stock\":10,\"capacity\":10}," +
                "{\"item\":8,\"id\":8,\"stock\":30,\"capacity\":40},{\"item\":9,\"id\":9,\"stock\":17,\"capacity" +
                "\":70},{\"item\":10,\"id\":10,\"stock\":43,\"capacity\":65},{\"item\":11,\"id\":11,\"stock\":32," +
                "\"capacity\":55},{\"item\":12,\"id\":12,\"stock\":25,\"capacity\":45},{\"item\":13,\"id\":13," +
                "\"stock\":8,\"capacity\":45},{\"item\":14,\"id\":14,\"stock\":10,\"capacity\":60},{\"item\":15," +
                "\"id\":15,\"stock\":20,\"capacity\":30},{\"item\":16,\"id\":16,\"stock\":15,\"capacity\":35}," +
                "{\"item\":17,\"id\":17,\"stock\":14,\"capacity\":80}]", DatabaseManager.getInventory().toString());
    }

    @Test
    void addDistributor() {
        DatabaseManager.addDistributor(4, "Cavity Corporation");
        assertEquals("[{\"name\":\"Candy Corp\",\"id\":1},{\"name\":\"The Sweet Suite\",\"id\":2},{\"name\":" +
                "\"Dentists Hate Us\",\"id\":3},{\"name\":\"Cavity " + "Corporation\",\"id\":4}]",
                DatabaseManager.getDistributors().toString());
    }

    @Test
    void addItemToDistributor() {
        DatabaseManager.addItemToDistributor(2, 2, 0.60);
        assertEquals("[{\"item\":1,\"cost\":0.81,\"id\":1,\"distributor\":1},{\"item\":2,\"cost\":0.46,\"id" +
                "\":2,\"distributor\":1},{\"item\":3,\"cost\":0.89,\"id\":3,\"distributor\":1},{\"item\":4,\"cost" +
                "\":0.45,\"id\":4,\"distributor\":1},{\"item\":2,\"cost\":0.18,\"id\":5,\"distributor\":2},{\"item" +
                "\":3,\"cost\":0.54,\"id\":6,\"distributor\":2},{\"item\":4,\"cost\":0.67,\"id\":7,\"distributor" +
                "\":2},{\"item\":5,\"cost\":0.25,\"id\":8,\"distributor\":2},{\"item\":6,\"cost\":0.35,\"id\":9," +
                "\"distributor\":2},{\"item\":7,\"cost\":0.23,\"id\":10,\"distributor\":2},{\"item\":8,\"cost\":0.41," +
                "\"id\":11,\"distributor\":2},{\"item\":9,\"cost\":0.54,\"id\":12,\"distributor\":2},{\"item\":10," +
                "\"cost\":0.25,\"id\":13,\"distributor\":2},{\"item\":11,\"cost\":0.52,\"id\":14,\"distributor\":2}" +
                ",{\"item\":12,\"cost\":0.07,\"id\":15,\"distributor\":2},{\"item\":13,\"cost\":0.77,\"id\":16," +
                "\"distributor\":2},{\"item\":14,\"cost\":0.93,\"id\":17,\"distributor\":2},{\"item\":15,\"cost" +
                "\":0.11,\"id\":18,\"distributor\":2},{\"item\":16,\"cost\":0.42,\"id\":19,\"distributor\":2},{\"item" +
                "\":10,\"cost\":0.47,\"id\":20,\"distributor\":3},{\"item\":11,\"cost\":0.84,\"id\":21,\"distributor" +
                "\":3},{\"item\":12,\"cost\":0.15,\"id\":22,\"distributor\":3},{\"item\":13,\"cost\":0.07,\"id\":23," +
                "\"distributor\":3},{\"item\":14,\"cost\":0.97,\"id\":24,\"distributor\":3},{\"item\":15,\"cost" +
                "\":0.39," + "\"id\":25,\"distributor\":3},{\"item\":16,\"cost\":0.91,\"id\":26,\"distributor\":3}," +
                "{\"item\":17," + "\"cost\":0.85,\"id\":27,\"distributor\":3},{\"item\":2,\"cost\":0.6,\"id\":28," +
                "\"distributor\":2}]", DatabaseManager.getDistributorPrices().toString());
    }

    @Test
    void modifyDistributorPrice() {
        DatabaseManager.modifyDistributorPrice(3, 17, 0.95);
        assertEquals("[{\"item\":1,\"cost\":0.81,\"id\":1,\"distributor\":1},{\"item\":2,\"cost\":0.46,\"id" +
                "\":2,\"distributor\":1},{\"item\":3,\"cost\":0.89,\"id\":3,\"distributor\":1},{\"item\":4,\"cost" +
                "\":0.45,\"id\":4,\"distributor\":1},{\"item\":2,\"cost\":0.18,\"id\":5,\"distributor\":2},{\"item" +
                "\":3,\"cost\":0.54,\"id\":6,\"distributor\":2},{\"item\":4,\"cost\":0.67,\"id\":7,\"distributor" +
                "\":2},{\"item\":5,\"cost\":0.25,\"id\":8,\"distributor\":2},{\"item\":6,\"cost\":0.35,\"id\":9," +
                "\"distributor\":2},{\"item\":7,\"cost\":0.23,\"id\":10,\"distributor\":2},{\"item\":8,\"cost\":0.41," +
                "\"id\":11,\"distributor\":2},{\"item\":9,\"cost\":0.54,\"id\":12,\"distributor\":2},{\"item\":10," +
                "\"cost\":0.25,\"id\":13,\"distributor\":2},{\"item\":11,\"cost\":0.52,\"id\":14,\"distributor\":2}" +
                ",{\"item\":12,\"cost\":0.07,\"id\":15,\"distributor\":2},{\"item\":13,\"cost\":0.77,\"id\":16," +
                "\"distributor\":2},{\"item\":14,\"cost\":0.93,\"id\":17,\"distributor\":2},{\"item\":15,\"cost" +
                "\":0.11,\"id\":18,\"distributor\":2},{\"item\":16,\"cost\":0.42,\"id\":19,\"distributor\":2},{\"item" +
                "\":10,\"cost\":0.47,\"id\":20,\"distributor\":3},{\"item\":11,\"cost\":0.84,\"id\":21,\"distributor" +
                "\":3},{\"item\":12,\"cost\":0.15,\"id\":22,\"distributor\":3},{\"item\":13,\"cost\":0.07,\"id\":23," +
                "\"distributor\":3},{\"item\":14,\"cost\":0.97,\"id\":24,\"distributor\":3},{\"item\":15,\"cost\":0.39,"
                + "\"id\":25,\"distributor\":3},{\"item\":16,\"cost\":0.91,\"id\":26,\"distributor\":3},{\"item\":17," +
                "\"cost\":0.95,\"id\":27,\"distributor\":3}]", DatabaseManager.getDistributorPrices().toString());
    }

    @Test
    void getMinCost() {
        DatabaseManager.getMinCost(2, 4);
        assertEquals("[{\"((MIN(cost)) * 4)\":0.72}]",
                DatabaseManager.getMinCost(2,4).toString());
    }

    @Test
    void removeItemFromInventory() {
        DatabaseManager.removeItemFromInventory(1);
        assertEquals("[{\"item\":2,\"id\":2,\"stock\":4,\"capacity\":20},{\"item\":3,\"id\":3,\"stock\":15," +
                "\"capacity\":25},{\"item\":4,\"id\":4,\"stock\":30,\"capacity\":50},{\"item\":5,\"id\":5,\"stock" +
                "\":14,\"capacity\":15},{\"item\":6,\"id\":6,\"stock\":8,\"capacity\":10},{\"item\":7,\"id\":7,\"stock" +
                "\":10,\"capacity\":10},{\"item\":8,\"id\":8,\"stock\":30,\"capacity\":40},{\"item\":9,\"id\":9,\"stock" +
                "\":17,\"capacity\":70},{\"item\":10,\"id\":10,\"stock\":43,\"capacity\":65},{\"item\":11,\"id\":11," +
                "\"stock\":32,\"capacity\":55},{\"item\":12,\"id\":12,\"stock\":25,\"capacity\":45},{\"item\":13,\"id" +
                "\":13,\"stock\":8,\"capacity\":45},{\"item\":14,\"id\":14,\"stock\":10,\"capacity\":60},{\"item" +
                "\":15,\"id\":15,\"stock\":20,\"capacity\":30},{\"item\":16,\"id\":16,\"stock\":15,\"capacity\":35},{" +
                "\"item\":17,\"id\":17,\"stock\":14,\"capacity\":60}]", DatabaseManager.getInventory().toString());

    }

    @Test
    void removeDistributor() {
        DatabaseManager.removeDistributor(1);
        assertEquals("[{\"name\":\"The Sweet Suite\",\"id\":2},{\"name\":\"Dentists Hate Us\",\"id\":3}]",
                DatabaseManager.getDistributors().toString());
        assertEquals("[{\"item\":2,\"cost\":0.18,\"id\":5,\"distributor\":2},{\"item\":3,\"cost\":0.54," +
                "\"id\":6,\"distributor\":2},{\"item\":4,\"cost\":0.67,\"id\":7,\"distributor\":2},{\"item\":5," +
                "\"cost\":0.25,\"id\":8,\"distributor\":2},{\"item\":6,\"cost\":0.35,\"id\":9,\"distributor\":2}," +
                "{\"item\":7,\"cost\":0.23,\"id\":10,\"distributor\":2},{\"item\":8,\"cost\":0.41,\"id\":11," +
                "\"distributor\":2},{\"item\":9,\"cost\":0.54,\"id\":12,\"distributor\":2},{\"item\":10,\"cost" +
                "\":0.25,\"id\":13,\"distributor\":2},{\"item\":11,\"cost\":0.52,\"id\":14,\"distributor\":2}," +
                "{\"item\":12,\"cost\":0.07,\"id\":15,\"distributor\":2},{\"item\":13,\"cost\":0.77,\"id\":16," +
                "\"distributor\":2},{\"item\":14,\"cost\":0.93,\"id\":17,\"distributor\":2},{\"item\":15,\"cost" +
                "\":0.11,\"id\":18,\"distributor\":2},{\"item\":16,\"cost\":0.42,\"id\":19,\"distributor\":2},{" +
                "\"item\":10,\"cost\":0.47,\"id\":20,\"distributor\":3},{\"item\":11,\"cost\":0.84,\"id\":21," +
                "\"distributor\":3},{\"item\":12,\"cost\":0.15,\"id\":22,\"distributor\":3},{\"item\":13,\"cost" +
                "\":0.07,\"id\":23,\"distributor\":3},{\"item\":14,\"cost\":0.97,\"id\":24,\"distributor\":3},{" +
                "\"item\":15,\"cost\":0.39,\"id\":25,\"distributor\":3},{\"item\":16,\"cost\":0.91,\"id\":26," +
                "\"distributor\":3},{\"item\":17,\"cost\":0.85,\"id\":27,\"distributor\":3}]",
                DatabaseManager.getDistributorPrices().toString());
    }
}