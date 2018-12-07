package pl.edu.wat.gadugadu.common;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import javax.json.*;

public class JsonParser {
    private int contentType;
    // TODO zamienic clientId na inta po implementacji bazy danych
    private String clientId;
    private String date;
    private String content;
    private JsonReaderFactory readerFactory;

    public JsonParser() {
        readerFactory = Json.createReaderFactory(Collections.emptyMap());
    }

// TODO parsowanie zrobione byle jak, trzeba sprawdzic czy da sie lepiej
    public void parsePayload(String payload){
        contentType=1;
        JsonReader jsonReader = readerFactory.createReader(new ByteArrayInputStream(payload.getBytes()));
        JsonObject jsonObject = jsonReader.readObject();
        clientId=jsonObject.get("clientId").toString().replace("\"","");
        date=jsonObject.get("date").toString().replace("\"","");
        content=jsonObject.get("content").toString().replace("\"","");
    }

    public String makePayload(int contentType, String clientId, String content){
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date date = new Date();
        JsonBuilderFactory builderFactory = Json.createBuilderFactory(Collections.emptyMap());
        JsonObject publicationDateObject = builderFactory.createObjectBuilder()
                .add("type", contentType)
                .add("clientId", clientId)
                .add("date", dateFormat.format(date))
                .add("content", content).build();
        return publicationDateObject.toString();
    }

    public int getContentType() {
        return contentType;
    }

    public String getClientId() {
        return clientId;
    }

    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}
