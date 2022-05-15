package me.jtx.flopac.database.impl;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import me.jtx.flopac.FlopAC;
import me.jtx.flopac.database.api.DatabaseInterface;
import me.jtx.flopac.database.api.InputData;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MongoManager implements DatabaseInterface {

    private MongoDatabase mongoDatabase;
    private MongoClient mongoClient;
    private List<String> databaseCollections;
    private MongoCollection<Document> logsCollection;

    @Override
    public void initManager() {
        if (FlopAC.getInstance().getConfigValues().isLogs()) {
            FlopAC.getInstance().getDatabaseManager().getExecutorService().execute(() -> {
                try {
                    FlopAC.getInstance().getLogger().info("Connecting to MongoDB...");
                    this.mongoClient = new MongoClient(new MongoClientURI(FlopAC.getInstance()
                            .getConfigValues().getMongoDBURI()));
                    this.mongoDatabase = this.mongoClient.getDatabase("FlopAC");
                    this.databaseCollections = this.mongoDatabase.listCollectionNames().into(new ArrayList<>());
                    this.createCollection("Logs");
                    this.logsCollection = this.mongoDatabase.getCollection("Logs");
                    this.createIndexes();
                    FlopAC.getInstance().getLogger().info("Connected to MongoDB!");
                } catch (Exception e) {
                    FlopAC.getInstance().getLogger().warning("Unable to connect to MongoDB." +
                            " (" + e.getMessage() + ")");
                }
            });
        }
    }

    @Override
    public void shutdown() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
        }
    }

    @Override
    public List<InputData> getLogs(String playerName) {
        List<Document> logs = new ArrayList<>();
        List<Bson> bsonArrayList = new ArrayList<>();

        bsonArrayList.add(Aggregates.match(Filters.eq("name", playerName)));
        AggregateIterable<Document> aggregateIterable = this.logsCollection
                .aggregate(bsonArrayList).allowDiskUse(true);
        aggregateIterable.forEach((Consumer<Document>) logs::add);

        List<InputData> inputData = new ArrayList<>();

        logs.forEach(document -> {
            String uuid = document.getString("uuid");
            String check = document.getString("check");
            String type = document.getString("type");
            int violation = document.getInteger("violation");
            inputData.add(new InputData(
                    uuid,
                    playerName,
                    check,
                    type,
                    violation,
                    false)
            );
        });

        return inputData;
    }

    @Override
    public boolean isSetup() {
        return this.mongoClient != null && this.mongoDatabase != null && this.logsCollection != null
                && FlopAC.getInstance().getConfigValues().isLogs();
    }

    @Override
    public void addViolation(InputData inputData) {
        String uuid = inputData.getUUID();
        String playerName = inputData.getPlayerName();
        String checkName = inputData.getCheckName();
        String checkType = inputData.getCheckType();
        int violation = inputData.getViolation();

        this.logsCollection.insertOne(
                new Document("uuid", uuid)
                .append("name", playerName)
                .append("check", checkName)
                .append("type", checkType)
                .append("violation", violation)
        );
    }


    void createCollection(String s) {
        if (!this.databaseCollections.contains(s)) {
            FlopAC.getInstance().getLogger().info("Could not find " + s + " creating it...");
            this.mongoDatabase.createCollection(s);
        }
    }

    void createIndexes() {
        if (this.logsCollection != null) {
            this.logsCollection.createIndex(Indexes.ascending("uuid"));
            this.logsCollection.createIndex(Indexes.ascending("name"));
            this.logsCollection.createIndex(Indexes.ascending("check"));
            this.logsCollection.createIndex(Indexes.ascending("type"));
            this.logsCollection.createIndex(Indexes.ascending("violation"));
        }
    }
}
