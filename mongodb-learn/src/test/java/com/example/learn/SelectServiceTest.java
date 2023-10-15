package com.example.learn;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertManyResult;
import org.bson.Document;
import org.junit.*;

import java.util.Map;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static java.util.Arrays.asList;

public class SelectServiceTest {

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
                Document.parse("{ item: 'journal', qty: 25, size: { h: 14, w: 21, uom: 'cm' }, status: 'A', tags: ['blank', 'red'], dim_cm: [ 14, 21 ], instock: [ { warehouse: 'A', qty: 5 }, { warehouse: 'C', qty: 15 } ] }"),
                Document.parse("{ item: 'notebook', qty: 50, size: { h: 8.5, w: 11, uom: 'in' }, status: 'A', tags: ['red', 'blank'], dim_cm: [ 14, 21 ], instock: [ { warehouse: 'C', qty: 5 } ] }"),
                Document.parse("{ item: 'paper', qty: 100, size: { h: 8.5, w: 11, uom: 'in' }, status: 'D', tags: ['red', 'blank', 'plain'], dim_cm: [ 14, 21 ], instock: [ { warehouse: 'A', qty: 60 }, { warehouse: 'B', qty: 15 } ] }"),
                Document.parse("{ item: 'planner', qty: 75, size: { h: 22.85, w: 30, uom: 'cm' }, status: 'D', tags: ['blank', 'red'], dim_cm: [ 22.85, 30 ], instock: [ { warehouse: 'A', qty: 40 }, { warehouse: 'B', qty: 5 } ] }"),
                Document.parse("{ item: 'postcard', qty: 45, size: { h: 10, w: 15.25, uom: 'cm' }, status: 'A', tags: ['blue'], dim_cm: [ 10, 15.25 ], instock: [ { warehouse: 'B', qty: 15 }, { warehouse: 'C', qty: 35 } ] }")
        ));
        System.out.println(result);
        System.out.println("插入完成");
    }

    @Test
    public void find() {
        findIterable = collection.find(new Document());
        printDocument(findIterable);

        findIterable = collection.find(eq("status", "D"));
        printDocument(findIterable);

        findIterable = collection.find(in("status", "A", "D"));
        printDocument(findIterable);

        // 复合查询 compound
        findIterable = collection.find(and(eq("status", "A"), lt("qty", 30)));
        printDocument(findIterable);

        findIterable = collection.find(or(eq("status", "A"), lt("qty", 30)));
        printDocument(findIterable);

        // 同一个字段多个条件查询
        // 10 > qty >= 20
        findIterable = collection.find(Filters.elemMatch("instock", Document.parse("{ qty: { $gt: 10, $lte: 20 } }")));
        printDocument(findIterable);

        // 返回指定字段
        findIterable = collection.find(eq("status", "A")).projection(include("item", "status"));
        printDocument(findIterable);
        // 不返回 _id
        findIterable = collection.find(eq("status", "A")).projection(fields(include("item", "status"), excludeId()));
        printDocument(findIterable);
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

    @Test
    public void embedded() {
        findIterable = collection.find(eq("size.uom", "in"));
        printDocument(findIterable);

        findIterable = collection.find(lt("size.h", 15));
        printDocument(findIterable);

        // 整个 document 进行查询
        // 包括字段顺序也要匹配
        findIterable = collection.find(eq("size", Document.parse("{ h: 14, w: 21, uom: 'cm' }")));
        printDocument(findIterable);
    }

    @Test
    public void array() {
        // eq 第二个参数为 list: 两个值完全匹配，包括顺序
        findIterable = collection.find(eq("tags", asList("red", "blank")));
        printDocument(findIterable);

        // all 包含两个值就可以
        findIterable = collection.find(all("tags", asList("red", "blank")));
        printDocument(findIterable);

        // 包含其一个就可以
        findIterable = collection.find(eq("tags", "red"));
        printDocument(findIterable);

        findIterable = collection.find(size("tags", 3));
        printDocument(findIterable);
    }

    @After
    public void after() {
        collection.drop();
        System.out.println("after 清除数据");
    }

    @AfterClass
    public static void close() {
        mongoClient.close();
        System.out.println("close 关闭连接");
    }
}
