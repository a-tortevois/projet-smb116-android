package tortevois.projet;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tortevois.projet.api.ConfigAPI;
import tortevois.projet.api.Customer;
import tortevois.projet.api.Equipment;
import tortevois.projet.api.Ticket;
import tortevois.projet.utils.HttpRequest;

public class Fragment_Account extends Fragment {
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

    private static Context context;
    private static View view;

    private static EditText field_contract_id;
    private static EditText field_contract_key;
    private static EditText field_lastname;
    private static EditText field_name;
    private static Button btn_login;
    private static Button btn_logout;
    private static Button btn_update;
    private static Switch switch_notifications;
    private String contract_id;
    private String contract_key;
    private String lastname;
    private String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Debug) Log.d(TAG, "Fragment_Account:onCreateView");

        view = inflater.inflate(R.layout.fragment_account, container, false);
        MainActivity.updateSelectedItem(MainActivity.ITEM_ACCOUNT);
        context = getContext();

        field_contract_id = (EditText) view.findViewById(R.id.contract_id);
        field_contract_key = (EditText) view.findViewById(R.id.contract_key);
        field_lastname = (EditText) view.findViewById(R.id.lastname);
        field_name = (EditText) view.findViewById(R.id.name);
        btn_login = (Button) view.findViewById(R.id.btn_login);
        btn_logout = (Button) view.findViewById(R.id.btn_logout);
        btn_update = (Button) view.findViewById(R.id.btn_update);
        switch_notifications = (Switch) view.findViewById(R.id.switch_notifications);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MainActivity.appCustomer == null) {
            ((TextView) view.findViewById(R.id.title)).setText(getString(R.string.lbl_login));
            view.findViewById(R.id.row_lastname).setVisibility(View.GONE);
            view.findViewById(R.id.row_name).setVisibility(View.GONE);
            view.findViewById(R.id.row_btn_update).setVisibility(View.GONE);
            btn_logout.setVisibility(View.GONE);
            switch_notifications.setVisibility(View.GONE);

            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Debug) Log.d(TAG, "Fragment_Account:onClickLogin");
                    // Hide keyboard
                    MainActivity.hideKeyboard(view);

                    // Set variables
                    String[] fields = {Customer.FIELD_CONTRACT_ID, Customer.FIELD_CONTRACT_KEY};
                    if (checkFields(fields)) {
                        // Prepare request
                        String url = ConfigAPI.URL_AUTH;
                        url = url.replace("$CONTRACT_ID", contract_id);
                        url = url.replace("$CONTRACT_KEY", contract_key);
                        String params[] = {HttpRequest.METHOD_GET, url};

                        // Do request & compute result
                        try {
                            JSONArray data = (JSONArray) MainActivity.doHttpRequest(params);
                            if (Debug) Log.d(TAG, "data: " + data.toString());
                            JSONObject jsonObject = data.getJSONObject(0);
                            MainActivity.appCustomer = new Customer(jsonObject);
                            if (Debug) Log.d(TAG, "Customer: " + jsonObject.toString());

                            if (MainActivity.appCustomer != null) {
                                saveCustomer(MainActivity.appCustomer.toJSONString());
                                reloadMainActivity(R.string.login_success);
                            } else {
                                MainActivity.makeToast(getString(R.string.login_fail));
                            }
                        } catch (Exception e) {
                            if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
                        }
                    }
                }
            });
        } else {
            ((TextView) view.findViewById(R.id.title)).setText(getString(R.string.lbl_logout));
            view.findViewById(R.id.row_btn_login).setVisibility(View.GONE);

            field_contract_id.setText(MainActivity.appCustomer.getContractId());
            field_contract_key.setText(MainActivity.appCustomer.getContractKey());
            field_lastname.setText(MainActivity.appCustomer.getLastname());
            field_name.setText(MainActivity.appCustomer.getName());
            field_contract_id.setEnabled(false);
            field_contract_key.setEnabled(false);
            switch_notifications.setChecked(MainActivity.notificationIsEnable);

            btn_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Debug) Log.d(TAG, "Fragment_Account:onClickLogout");
                    MainActivity.appCustomer = null;
                    MainActivity.isAdmin = false;
                    // unsubscribe to all ticket
                    MainActivity.unsubscribeToAllTicket();
                    // save & reload
                    saveCustomer("");
                    saveNotification(false);
                    reloadMainActivity(R.string.logout_success);
                }
            });

            btn_update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Debug) Log.d(TAG, "Fragment_Account:onClickUpdate");
                    // Hide keyboard
                    MainActivity.hideKeyboard(view);

                    // Set variables
                    //int id_customer = Integer.parseInt(field_id_customer.getText().toString());
                    int id_customer = MainActivity.appCustomer.getId();
                    String[] fields = {Customer.FIELD_CONTRACT_ID, Customer.FIELD_CONTRACT_KEY, Customer.FIELD_LASTNAME, Customer.FIELD_NAME};
                    if (checkFields(fields)) {
                        Customer customer = new Customer(contract_id, contract_key, lastname, name); // no need to define id_customer here

                        // Prepare request
                        String url = ConfigAPI.URL_PUT_CUSTOMER;
                        url = url.replace("$ID", Integer.toString(id_customer));
                        String params[] = {HttpRequest.METHOD_PUT, url, customer.toJSONString()};

                        // Do request & compute result
                        try {
                            JSONObject data = (JSONObject) MainActivity.doHttpRequest(params);
                            if (data != null) {
                                customer.setId(id_customer); // don't forget to define id_customer
                                MainActivity.appCustomer = customer;
                                saveCustomer(customer.toJSONString());
                                reloadMainActivity(R.string.update_success);
                            } else {
                                MainActivity.makeToast(getString(R.string.update_fail));
                            }
                        } catch (Exception e) {
                            if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
                        }
                    }
                }
            });

            switch_notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (Debug) Log.d(TAG, "Fragment_Account:onCheckedChanged");
                    MainActivity.notificationIsEnable = isChecked;
                    saveNotification(isChecked);

                    if (isChecked) {
                        if (MainActivity.isAdmin) {
                            MainActivity.subscribeToAllTicket();
                        } else {
                            for (Map.Entry<Integer, Ticket> entry : MainActivity.appTickets.entrySet()) {
                                MainActivity.subscribeToTicket(entry.getValue());
                            }
                        }
                    } else {
                        MainActivity.unsubscribeToAllTicket();
                    }
                }
            });
        }
    }

    private void reloadMainActivity(int id_msg) {
        MainActivity.makeToast(getString(id_msg));
        try {
            getActivity().recreate();
        } catch (Exception e) {
            if (Debug) Log.d(TAG, "reloadMainActivity Exception: " + e.getMessage());
        }
    }

    private void saveCustomer(String customer) {
        if (Debug) Log.d(TAG, "Save Customer on SharedPreferences ");
        MainActivity.editor.putString(MainActivity.SP_CUSTOMER, customer);
        MainActivity.editor.apply();
    }

    private void saveNotification(boolean notification) {
        if (Debug) Log.d(TAG, "Save Notification on SharedPreferences ");
        MainActivity.editor.putBoolean(MainActivity.SP_NOTIFICATION, notification);
        MainActivity.editor.apply();
    }

    private boolean checkFields(String[] fields) {
        String str;
        Pattern pattern;
        Matcher matcher;
        for (String field : fields) {
            switch (field) {
                case Customer.FIELD_CONTRACT_ID:
                    contract_id = field_contract_id.getText().toString().trim();
                    pattern = Pattern.compile("[A-Z0-9]+");
                    matcher = pattern.matcher(contract_id);
                    if (contract_id.isEmpty() || !matcher.matches()) {
                        MainActivity.makeToast(getString(R.string.err_contract_id));
                        return false;
                    }
                    break;
                case Customer.FIELD_CONTRACT_KEY:
                    contract_key = field_contract_key.getText().toString().trim();
                    pattern = Pattern.compile("[\\w]+_[\\w]+");
                    matcher = pattern.matcher(contract_key);
                    if (contract_id.isEmpty() || !matcher.matches()) {
                        MainActivity.makeToast(getString(R.string.err_contract_key));
                        return false;
                    }
                    break;
                case Customer.FIELD_LASTNAME:
                    str = field_lastname.getText().toString().trim();
                    lastname = str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
                    pattern = Pattern.compile("[A-Za-z]+");
                    matcher = pattern.matcher(lastname);
                    if (lastname.isEmpty() || !matcher.matches()) {
                        MainActivity.makeToast(getString(R.string.err_lastname));
                        return false;
                    }
                    break;
                case Customer.FIELD_NAME:
                    str = field_name.getText().toString().trim();
                    name = str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
                    pattern = Pattern.compile("[A-Za-z]+");
                    matcher = pattern.matcher(name);
                    if (name.isEmpty() || !matcher.matches()) {
                        MainActivity.makeToast(getString(R.string.err_name));
                        return false;
                    }
                    break;
                default:
                    MainActivity.makeToast("Unknown field");
                    return false;
            }
        }
        return true;
    }
}