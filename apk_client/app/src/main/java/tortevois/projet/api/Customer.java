package tortevois.projet.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Customer {
    private int id = 0;
    private String contractId;
    private String contractKey;
    private String lastname;
    private String name;

    public static final String FIELD_ID = "id_customer";
    public static final String FIELD_CONTRACT_ID = "contract_id";
    public static final String FIELD_CONTRACT_KEY = "contract_key";
    public static final String FIELD_LASTNAME = "lastname";
    public static final String FIELD_NAME = "name";

    public Customer() {
    }

    public Customer(JSONObject jsonObject) {
        try {
            if (jsonObject.length() == 5) {
                this.id = jsonObject.getInt(FIELD_ID);
            }
            this.contractId = jsonObject.getString(FIELD_CONTRACT_ID);
            this.contractKey = jsonObject.getString(FIELD_CONTRACT_KEY);
            this.lastname = jsonObject.getString(FIELD_LASTNAME);
            this.name = jsonObject.getString(FIELD_NAME);
        } catch (JSONException e) {
            // TODO
            // e.getMessage();
        }
    }

    public Customer(String contractId, String contractKey, String lastname, String name) {
        this.contractId = contractId;
        this.contractKey = contractKey;
        this.lastname = lastname;
        this.name = name;
    }

    public Customer(int id, String contractId, String contractKey, String lastname, String name) {
        this.id = id;
        this.contractId = contractId;
        this.contractKey = contractKey;
        this.lastname = lastname;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getContractKey() {
        return contractKey;
    }

    public void setContractKey(String contractKey) {
        this.contractKey = contractKey;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toJSONString() {
        JSONObject jsonObject = new JSONObject();
        try {
            if( getId() != 0 ) {
                jsonObject.accumulate(FIELD_ID, getId());
            }
            jsonObject.accumulate(FIELD_CONTRACT_ID, getContractId());
            jsonObject.accumulate(FIELD_CONTRACT_KEY, getContractKey());
            jsonObject.accumulate(FIELD_LASTNAME, getLastname());
            jsonObject.accumulate(FIELD_NAME, getName());
        } catch (JSONException e) {
            // TODO
            // e.getMessage();
        }
        return jsonObject.toString();

        /*
        String jsonString = "{";
        if(getId() != 0) {
            jsonString += "\"" + FIELD_ID + "\":\"" +  getId() + "\", ";
        }
        jsonString += "\"" + FIELD_CONTRACT_ID + "\":\"" +  getContractId() + "\", ";
        jsonString += "\"" + FIELD_CONTRACT_KEY + "\":\"" +  getContractKey() + "\", ";
        jsonString += "\"" + FIELD_LASTNAME + "\":\"" +  getLastname() + "\", ";
        jsonString += "\"" + FIELD_NAME + "\":\"" +  getName() + "\"";
        jsonString += "}";
        return jsonString;
        */
    }

    @Override
    public String toString() {
        String str = "";
        str += FIELD_ID + ": " + getId() + "\n";
        str += FIELD_CONTRACT_ID + ": " + getContractId() + "\n";
        str += FIELD_CONTRACT_KEY + ": " + getContractKey() + "\n";
        str += FIELD_LASTNAME + ": " + getLastname() + "\n";
        str += FIELD_NAME + ": " + getName() + "\n";
        return str;
    }
}
