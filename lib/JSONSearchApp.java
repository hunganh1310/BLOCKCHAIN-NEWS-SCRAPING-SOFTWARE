import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class JSONSearchApp {
    private static final String DATABASE_NAME = "json_search_db";
    private static final String COLLECTION_NAME = "json_data";

    public static void main(String[] args) {
        // Kết nối tới MongoDB
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);

        // Tạo collection nếu chưa tồn tại
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);
        if (collection.countDocuments() == 0) {
            initializeCollection(collection);
        }

        // Tìm kiếm dữ liệu trong MongoDB
        searchInMongoDB(collection, "search_keyword");

        // Đóng kết nối tới MongoDB
        mongoClient.close();
    }

    // Khởi tạo collection từ tệp JSON
    private static void initializeCollection(MongoCollection<Document> collection) {
        try {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(new FileReader("data.json"));

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                Document document = Document.parse(jsonObject.toJSONString());
                collection.insertOne(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tìm kiếm trong MongoDB
    private static void searchInMongoDB(MongoCollection<Document> collection, String keyword) {
        Document query = new Document();
        query.append("$text", new Document("$search", keyword));

        for (Document doc : collection.find(query)) {
            System.out.println(doc);
        }
    }
}
