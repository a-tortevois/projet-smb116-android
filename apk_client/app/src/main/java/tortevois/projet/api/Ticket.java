package tortevois.projet.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Ticket {
    private int id;
    private int idEquipment;
    private String subject;
    private int status;
    private Date dateStatus;

    private final SimpleDateFormat formatter = new SimpleDateFormat(ConfigAPI.DATE_FORMAT);

    public static final String FIELD_ID = "id_ticket";
    public static final String FIELD_ID_EQUIPMENT = "id_equipment";
    public static final String FIELD_SUBJECT = "subject";
    public static final String FIELD_STATUS = "status";
    public static final String FIELD_DATE_STATUS = "date_status";

    public static int STATUS_CLOSE = 0;
    public static int STATUS_OPEN = 1;

    public Ticket() {
    }

    public Ticket(JSONObject jsonObject) {
        try {
            if (jsonObject.length() == 5) {
                this.id = jsonObject.getInt(FIELD_ID);
            }
            this.idEquipment = jsonObject.getInt(FIELD_ID_EQUIPMENT);
            this.subject = jsonObject.getString(FIELD_SUBJECT);
            this.status = jsonObject.getInt(FIELD_STATUS);
            this.dateStatus = formatter.parse(jsonObject.getString(FIELD_DATE_STATUS));
        } catch (Exception e) { // JSONException || ParseException
            // TODO
            // e.getMessage();
        }
    }

    public Ticket(int idEquipment, String subject, int status, Date dateStatus) {
        this.idEquipment = idEquipment;
        this.subject = subject;
        this.status = status;
        this.dateStatus = dateStatus;
    }

    public Ticket(int id, int idEquipment, String subject, int status, Date dateStatus) {
        this.id = id;
        this.idEquipment = idEquipment;
        this.subject = subject;
        this.status = status;
        this.dateStatus = dateStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(int idEquipment) {
        this.idEquipment = idEquipment;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getDateStatus() {
        return dateStatus;
    }

    public void setDateStatus(Date dateStatus) {
        this.dateStatus = dateStatus;
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        try {
            if( getId() != 0 ) {
                jsonObject.accumulate(FIELD_ID, getId());
            }
            jsonObject.accumulate(FIELD_ID_EQUIPMENT, getIdEquipment());
            jsonObject.accumulate(FIELD_SUBJECT, getSubject());
            jsonObject.accumulate(FIELD_STATUS, getStatus());
            jsonObject.accumulate(FIELD_DATE_STATUS, formatter.format(getDateStatus()));
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
        str += FIELD_ID_EQUIPMENT + ": " + getIdEquipment() + "\n";
        str += FIELD_SUBJECT + ": " + getSubject() + "\n";
        str += FIELD_STATUS + ": " + getStatus() + "\n";
        str += FIELD_DATE_STATUS + ": " + formatter.format(getDateStatus()) + "\n";
        return str;
    }
}
