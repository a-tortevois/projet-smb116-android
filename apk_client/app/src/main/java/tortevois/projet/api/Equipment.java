package tortevois.projet.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Equipment { //  implements Parcelable
    private int id;
    private int idCustomer;
    private String serialNumber;
    private String comment;
    private double lat;
    private double lng;

    public static final String FIELD_ID = "id_equipment";
    public static final String FIELD_ID_CUSTOMER = "id_customer";
    public static final String FIELD_SERIAL_NUMBER = "serial_number";
    public static final String FIELD_COMMENT = "comment";
    public static final String FIELD_LAT = "lat";
    public static final String FIELD_LNG = "lng";

    public Equipment() {
    }

    public Equipment(JSONObject jsonObject) {
        try {
            if (jsonObject.length() == 6) {
                this.id = jsonObject.getInt(FIELD_ID);
            }
            this.idCustomer = jsonObject.getInt(FIELD_ID_CUSTOMER);
            this.serialNumber = jsonObject.getString(FIELD_SERIAL_NUMBER);
            this.comment = jsonObject.getString(FIELD_COMMENT);
            this.lat = Double.parseDouble(jsonObject.getString(FIELD_LAT));
            this.lng = Double.parseDouble(jsonObject.getString(FIELD_LNG));
        } catch (JSONException e) {
            // TODO
            // e.getMessage();
        }
    }

    public Equipment(int idCustomer, String serialNumber, String comment, double lat, double lng) {
        this.idCustomer = idCustomer;
        this.serialNumber = serialNumber;
        this.comment = comment;
        this.lat = lat;
        this.lng = lng;
    }

    public Equipment(int id, int idCustomer, String serialNumber, String comment, double lat, double lng) {
        this.id = id;
        this.idCustomer = idCustomer;
        this.serialNumber = serialNumber;
        this.comment = comment;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        try {
            if( getId() != 0 ) {
                jsonObject.accumulate(FIELD_ID, getId());
            }
            jsonObject.accumulate(FIELD_ID_CUSTOMER, getIdCustomer());
            jsonObject.accumulate(FIELD_SERIAL_NUMBER, getSerialNumber());
            jsonObject.accumulate(FIELD_COMMENT, getComment());
            jsonObject.accumulate(FIELD_LAT, Double.toString(getLat()));
            jsonObject.accumulate(FIELD_LNG, Double.toString(getLng()));
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
        str += FIELD_ID_CUSTOMER + ": " + getIdCustomer() + "\n";
        str += FIELD_SERIAL_NUMBER + ": " + getSerialNumber() + "\n";
        str += FIELD_COMMENT + ": " + getComment() + "\n";
        str += FIELD_LAT + ": " + Double.toString(getLat()) + "\n";
        str += FIELD_LNG + ": " + Double.toString(getLng()) + "\n";
        return str;
    }
}