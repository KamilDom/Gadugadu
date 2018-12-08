package pl.edu.wat.gadugadu.common;

public class Payload {
    private int contentType;
    private int clientId;
    private String date;
    private String content;
    private ClientStatus status;

    public Payload(int contentType, int clientId, String date, String content, ClientStatus status) {
        this.contentType = contentType;
        this.clientId = clientId;
        this.date = date;
        this.content = content;
        this.status = status;
    }

}
