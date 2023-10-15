package com.example.learn;

import com.mongodb.client.*;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.junit.*;

import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.*;
import static java.util.Arrays.asList;

public class UpdateServiceTest {

    private static MongoClient mongoClient;
    private static MongoCollection<Document> collection;
    private static FindIterable<Document> findIterable;

    @BeforeClass
    public static void getDocument() {
        String uri = "mongodb://localhost:27017/";
        mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("test");
        collection = database.getCollection("inventory");
        System.out.println("创建数据库连接");
    }

    @Before
    public void before() {
        System.out.println("before: 插入数据");
        InsertManyResult result = collection.insertMany(asList(
                Document.parse("{ item: 'canvas', qty: 100, size: { h: 28, w: 35.5, uom: 'cm' }, status: 'A' }"),
                Document.parse("{ item: 'journal', qty: 25, size: { h: 14, w: 21, uom: 'cm' }, status: 'A' }"),
                Document.parse("{ item: 'mat', qty: 85, size: { h: 27.9, w: 35.5, uom: 'cm' }, status: 'A' }"),
                Document.parse("{ item: 'mousepad', qty: 25, size: { h: 19, w: 22.85, uom: 'cm' }, status: 'P' }"),
                Document.parse("{ item: 'notebook', qty: 50, size: { h: 8.5, w: 11, uom: 'in' }, status: 'P' }"),
                Document.parse("{ item: 'paper', qty: 100, size: { h: 8.5, w: 11, uom: 'in' }, status: 'D' }"),
                Document.parse("{ item: 'planner', qty: 75, size: { h: 22.85, w: 30, uom: 'cm' }, status: 'D' }"),
                Document.parse("{ item: 'postcard', qty: 45, size: { h: 10, w: 15.25, uom: 'cm' }, status: 'A' }"),
                Document.parse("{ item: 'sketchbook', qty: 80, size: { h: 14, w: 21, uom: 'cm' }, status: 'A' }"),
                Document.parse("{ item: 'sketch pad', qty: 95, size: { h: 22.85, w: 30.5, uom: 'cm' }, status: 'A' }")
        ));
        System.out.println(result);
        System.out.println("插入完成");
    }

    @Test
    public void update() {
        // 更新一条
        collection.updateOne(eq("item", "paper"),
                combine(set("size.uom", "cm"), set("status", "P"), currentDate("lastModified")));

        // 替换整个 document
        collection.replaceOne(eq("item", "paper"),
                Document.parse("{ item: 'paper', instock: [ { warehouse: 'A', qty: 60 }, { warehouse: 'B', qty: 40 } ] }"));
    }

    private static void printDocument(FindIterable<Document> documents) {
        System.out.println("---- 开始输出 ----");
        for (Document document : documents) {
            for (Map.Entry<String, Object> entry : document.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                System.out.println(key + " = " + value);
            }
        }
        System.out.println("---- 输出完成 ----");
    }

    @After
    public void after() {
        collection.deleteMany(new Document());
        System.out.println("after 清除数据");
    }

    @AfterClass
    public static void close() {
        mongoClient.close();
        System.out.println("close 关闭连接");
    }
}
