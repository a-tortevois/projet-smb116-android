package tortevois.projet.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private int id;
    private int idTicket;
    private int idCustomer;
    private Date dateMessage;
    private String content;

    private final SimpleDateFormat formatter = new SimpleDateFormat(ConfigAPI.DATE_FORMAT);

    public static final String FIELD_ID = "id_message";
    public static final String FIELD_ID_TICKET = "id_ticket";
    public static final String FIELD_ID_CUSTOMER = "id_customer";
    public static final String FIELD_DATE_MESSSAGE = "date_message";
    public static final String FIELD_CONTENT = "content";

    public Message() {
    }

    public Message(JSONObject jsonObject) {
        try {
            if (jsonObject.length() == 5) {
                this.id = jsonObject.getInt(FIELD_ID);
            }
            this.idTicket = jsonObject.getInt(FIELD_ID_TICKET);
            this.idCustomer = Integer.parseInt(jsonObject.getString(FIELD_ID_CUSTOMER));
            this.dateMessage = formatter.parse(jsonObject.getString(FIELD_DATE_MESSSAGE));
            this.content = jsonObject.getString(FIELD_CONTENT);
        } catch (Exception e) { // JSONException || ParseException
            // TODO
            // e.getMessage();
        }
    }

    public Message(int idTicket, int idCustomer, Date dateMessage, String content) {
        this.idTicket = idTicket;
        this.idCustomer = idCustomer;
        this.dateMessage = dateMessage;
        this.content = content;
    }

    public Message(int id, int idTicket, int idCustomer, Date dateMessage, String content) {
        this.id = id;
        this.idTicket = idTicket;
        this.idCustomer = idCustomer;
        this.dateMessage = dateMessage;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public Date getDateMessage() {
        return dateMessage;
    }

    public void setDateMessage(Date dateMessage) {
        this.dateMessage = dateMessage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        try {
            if( getId() != 0 ) {
                jsonObject.accumulate(FIELD_ID, getId());
            }
            jsonObject.accumulate(FIELD_ID_TICKET, getIdTicket());
            jsonObject.accumulate(FIELD_ID_CUSTOMER, getIdCustomer());
            jsonObject.accumulate(FIELD_DATE_MESSSAGE, formatter.format(getDateMessage()));
            jsonObject.accumulate(FIELD_CONTENT, getContent());
        } catch (JSONException e) {
            // TODO
            // e.getMessage();
        }
        return jsonObject.toString();
    }

    @Override
    public String toString() {
        String str = "";
        str += FIELD_ID + ": " + getId() + "\n";
        str += FIELD_ID_TICKET + ": " + getIdTicket() + "\n";
        str += FIELD_ID_CUSTOMER + ": " + getIdCustomer() + "\n";
        str += FIELD_DATE_MESSSAGE + ": " + formatter.format(getDateMessage()) + "\n";
        str += FIELD_CONTENT + ": " + getContent() + "\n";
        return str;
    }
}
