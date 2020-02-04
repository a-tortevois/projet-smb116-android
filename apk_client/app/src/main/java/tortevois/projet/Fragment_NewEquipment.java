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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tortevois.projet.api.ConfigAPI;
import tortevois.projet.api.Customer;
import tortevois.projet.api.Equipment;
import tortevois.projet.utils.HttpRequest;

public class Fragment_NewEquipment extends Fragment {
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

    private static Context context;
    private static View view;

    private static Spinner spinner_customer;
    private static EditText field_serial_number;
    private static EditText field_comment;
    private static EditText field_lat;
    private static EditText field_lng;
    private static Button btn_viewonmap;
    private static Button btn_setpos;
    private static Button btn_update;
    private int idCustomer;
    private String serial_number;
    private String comment;
    private double lat;
    private double lng;

    private final Map<Integer, String> spinnerMap = new LinkedHashMap<>();
    private Equipment equipment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Debug) Log.d(TAG, "Fragment_NewEquipment:onCreateView");
        view = inflater.inflate(R.layout.fragment_equipment_view, container, false);
        MainActivity.updateSelectedItem(MainActivity.ITEM_EQUIPMENT);
        context = getContext();

        spinner_customer = (Spinner) view.findViewById(R.id.spinner_customer);
        field_serial_number = (EditText) view.findViewById(R.id.serial_number);
        field_comment = (EditText) view.findViewById(R.id.comment);
        field_lat = (EditText) view.findViewById(R.id.lat);
        field_lng = (EditText) view.findViewById(R.id.lng);
        btn_viewonmap = (Button) view.findViewById(R.id.btn_viewonmap);
        btn_setpos = (Button) view.findViewById(R.id.btn_setpos);
        btn_update = (Button) view.findViewById(R.id.btn_update);

        /*
        equipment = null;
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (Debug) Log.d(TAG, "bundle not null");
            String jsonEquipment = bundle.getString("equipment");
            if (Debug) Log.d(TAG, "jsonEquipment: " + jsonEquipment);
            if (jsonEquipment != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonEquipment);
                    equipment = new Equipment(jsonObject);
                } catch (JSONException e) {
                    if (Debug) Log.d(TAG, "JSONException: " + e.getMessage());
                }
            }
        }
        */

        if (MainActivity.isAdmin) {
            // fill the spinner
            spinnerMap.put(0, getString(R.string.lbl_select));
            for (Map.Entry<Integer, Customer> entry : MainActivity.getAllCustomer().entrySet()) {
                spinnerMap.put(((Customer) entry.getValue()).getId(), ((Customer) entry.getValue()).getName() + " " + ((Customer) entry.getValue()).getLastname());
            }
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, new ArrayList<String>(spinnerMap.values())); //new ArrayList<>(spinnerMap.entrySet())
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_customer.setAdapter(spinnerArrayAdapter);
        } else {
            view.findViewById(R.id.row_select_customer).setVisibility(View.GONE);
        }

        return view;
    }

    // Fix EditText not updated :
    // https://stackoverflow.com/a/13414452/11339842
    @Override
    public void onResume() {
        if (Debug) Log.d(TAG, "Fragment_NewEquipment:onResume");
        super.onResume();
        if (equipment != null) {
            /**
            field_serial_number.setText(equipment.getSerialNumber());
            field_comment.setText(equipment.getComment());
            field_lat.setText(Double.toString(equipment.getLat()));
            field_lng.setText(Double.toString(equipment.getLng()));

            if (!MainActivity.isAdmin) {
                field_serial_number.setEnabled(false);
                field_lat.setEnabled(false);
                field_lng.setEnabled(false);
            } else {
                int position = new ArrayList<Integer>(spinnerMap.keySet()).indexOf(equipment.getIdCustomer());
                spinner_customer.setSelection(position);
                if (Debug) Log.d(TAG, "position: " + position);
            }

            btn_viewonmap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Debug) Log.d(TAG, "Fragment_NewEquipment:onClickMap");
                    Bundle bundle = new Bundle();
                    bundle.putString("equipment", equipment.toJSONString());
                    MainActivity.EQUIPMENT_MAP_FRAGMENT.setArguments(bundle);
                    MainActivity.openFragment(MainActivity.EQUIPMENT_MAP_FRAGMENT);
                }
            });
            /**/
        } else {
            if (MainActivity.isAdmin) {
                spinner_customer.setSelection(0);
                field_serial_number.setText("");
                field_comment.setText("");
                field_lat.setText("0.0");
                field_lng.setText("0.0");
                btn_update.setText(getText(R.string.lbl_btn_add));
                btn_setpos.setVisibility(View.GONE);
            } else {
                MainActivity.makeToast("Error: equipment is null");
            }
        }

        btn_setpos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Debug) Log.d(TAG, "Fragment_NewEquipment:onClickSetPosition");

                if (MainActivity.tracker != null) {
                    field_lat.setText(Double.toString(MainActivity.tracker.getLatitude())); //myLatLng.latitude
                    field_lng.setText(Double.toString(MainActivity.tracker.getLongitude())); //myLatLng.longitude
                    equipment.setLat(MainActivity.tracker.getLatitude());
                    equipment.setLng(MainActivity.tracker.getLongitude());
                } else {
                    MainActivity.makeToast(getString(R.string.err_nopos));
                }
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Debug) Log.d(TAG, "Fragment_NewEquipment:onClickUpdate");
                MainActivity.hideKeyboard(view);

                String[] fields = {Equipment.FIELD_ID_CUSTOMER, Equipment.FIELD_SERIAL_NUMBER, Equipment.FIELD_COMMENT, Equipment.FIELD_LAT, Equipment.FIELD_LNG};
                if (checkFields(fields)) {
                    equipment = new Equipment(idCustomer, serial_number, comment, lat, lng);

                    // Prepare request
                    String url = ConfigAPI.URL_POST_EQUIPMENT;
                    String params[] = {HttpRequest.METHOD_POST, url, equipment.toJSONString()};

                    // Do request & compute result
                    try {
                        JSONObject data = (JSONObject) MainActivity.doHttpRequest(params);
                        if (data != null) {
                            MainActivity.appEquipments.put(equipment.getId(), equipment);
                            MainActivity.makeToast(getString(R.string.update_success));
                        } else {
                            MainActivity.makeToast(getString(R.string.update_fail));
                        }
                    } catch (Exception e) {
                        if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
                    }
                }
            }
        });
    }

    private boolean checkFields(String[] fields) {
        Pattern pattern;
        Matcher matcher;
        for (String field : fields) {
            switch (field) {
                case Equipment.FIELD_ID_CUSTOMER:
                    if (MainActivity.isAdmin) {
                        String name = spinner_customer.getSelectedItem().toString();
                        idCustomer = new ArrayList<String>(spinnerMap.values()).indexOf(name);
                        if (Debug) Log.d(TAG, "idCustomer: " + idCustomer);
                        if (idCustomer == 0) {
                            MainActivity.makeToast(getString(R.string.err_id_customer));
                            return false;
                        }
                    }
                    break;

                case Equipment.FIELD_SERIAL_NUMBER:
                    serial_number = field_serial_number.getText().toString();
                    //TODO
                    break;

                case Equipment.FIELD_COMMENT:
                    comment = field_comment.getText().toString();
                    pattern = Pattern.compile("[\\w\\s]+");
                    matcher = pattern.matcher(comment);
                    if (comment.isEmpty() || !matcher.matches()) {
                        MainActivity.makeToast(getString(R.string.err_comment));
                        return false;
                    }
                    break;
                case Equipment.FIELD_LAT:
                    String lat_str = field_lat.getText().toString().trim();
                    pattern = Pattern.compile("([0-9.-]+).+?([0-9.-]+)");
                    matcher = pattern.matcher(lat_str);
                    if (!matcher.matches()) {
                        MainActivity.makeToast(getString(R.string.err_lat));
                        return false;
                    }
                    lat = Double.parseDouble(lat_str);
                    break;

                case Equipment.FIELD_LNG:
                    String lng_str = field_lng.getText().toString().trim();
                    pattern = Pattern.compile("([0-9.-]+).+?([0-9.-]+)");
                    matcher = pattern.matcher(lng_str);
                    if (!matcher.matches()) {
                        MainActivity.makeToast(getString(R.string.err_lat));
                        return false;
                    }
                    lng = Double.parseDouble(lng_str);
                    break;
                default:
                    MainActivity.makeToast("Unknown field");
                    return false;
            }
        }
        return true;
    }
}
