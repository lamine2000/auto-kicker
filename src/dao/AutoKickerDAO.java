package dao;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class AutoKickerDAO {
    private MongoCollection<Document> collection;
    private static final long delayBeforeKick = 7L;

    public AutoKickerDAO(String collectionName) throws Exception {
        this.collection = Connection.getCollectionFromDB(collectionName);
    }

    public void addNewMember(String userId, String guildId){
        System.out.println("inserted");
        Document doc = new Document("_id", new ObjectId());
        LocalDateTime timeToKick = LocalDateTime.now().plusDays(delayBeforeKick);
        doc
                .append("userId", userId)
                .append("guildId", guildId)
                .append("dateTimeToKick", timeToKick.toString());

        this.collection.insertOne(doc);
        System.out.println("inserted");
    }

    //to be called to remove a member from the list of members to kick
    public void removeMember(String userId, String guildId){
        Bson filter =
                and(
                        eq("userId", userId),
                        eq("guildId", guildId));
        this.collection.deleteOne(filter);
    }
}
