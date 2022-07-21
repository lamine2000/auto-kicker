package dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class Connection {
    private static final String connectionString = "mongodb://discordbot:pass@localhost:27017/?retryWrites=true&w=majority";

    public static MongoDatabase getDiscordBotsDB() throws Exception {
        MongoDatabase discordBotsDB;

        try{
            MongoClient mongoClient = MongoClients.create(connectionString);
            discordBotsDB = mongoClient.getDatabase("discordBots");
            return discordBotsDB;
        }catch (Exception e){
            throw new Exception("Connection to database failed");
        }
    }

    public static MongoCollection<Document> getCollectionFromDB(String collectionName) throws Exception {
        MongoDatabase db = getDiscordBotsDB();
        return db.getCollection(collectionName);
    }
}
