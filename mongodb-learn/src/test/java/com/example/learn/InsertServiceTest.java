package com.example.learn;

import com.mongodb.client.*;
import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class InsertServiceTest {

    private static MongoClient mongoClient;
    private static MongoCollection<Document> collection;

    @BeforeClass
    public static void getDocument() {
        String uri = "mongodb://localhost:27017/";
        mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("school");
        collection = database.getCollection("students");
    }

    @Test
    public void insertOne() {
        Document canvas = new Document("item", "canvas")
                .append("qty", 100)
                .append("tags", singletonList("cotton"));

        Document size = new Document("h", 28)
                .append("w", 35.5)
                .append("uom", "cm");
        canvas.put("size", size);
        collection.insertOne(canvas);

        printDocument();
    }

    private static void printDocument() {
        FindIterable<Document> findIterable = collection.find();
        for (Document document : findIterable) {
            for (Map.Entry<String, Object> entry : document.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                System.out.println(key + " = " + value);
            }
        }
    }

    @Test
    public void insertMany(){
        Document journal = new Document("item", "journal")
                .append("qty", 25)
                .append("tags", asList("blank", "red"));

        Document journalSize = new Document("h", 14)
                .append("w", 21)
                .append("uom", "cm");
        journal.put("size", journalSize);

        Document mat = new Document("item", "mat")
                .append("qty", 85)
                .append("tags", singletonList("gray"));

        Document matSize = new Document("h", 27.9)
                .append("w", 35.5)
                .append("uom", "cm");
        mat.put("size", matSize);

        Document mousePad = new Document("item", "mousePad")
                .append("qty", 25)
                .append("tags", asList("gel", "blue"));

        Document mousePadSize = new Document("h", 19)
                .append("w", 22.85)
                .append("uom", "cm");
        mousePad.put("size", mousePadSize);

        collection.insertMany(asList(journal, mat, mousePad));
        printDocument();
    }

    @After
    public void after() {
        collection.drop();
    }

    @AfterClass
    public static void close() {
        mongoClient.close();
    }
}
