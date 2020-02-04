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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tortevois.projet.api.ConfigAPI;
import tortevois.projet.api.Equipment;
import tortevois.projet.api.Message;
import tortevois.projet.api.Ticket;
import tortevois.projet.utils.HttpRequest;

public class Fragment_NewTicket extends Fragment {
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

    private static Context context;
    private static View view;

    private static Spinner spinner_equipment;
    private static EditText field_ticket_title;
    private static EditText field_message_comment;
    private static View form_layout;
    private static Button btn_send;

    private final ArrayList<String> spinnerArray = new ArrayList<>();
    private String title;
    private String comment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Debug) Log.d(TAG, "Fragment_NewTicket:onCreateView");
        view = inflater.inflate(R.layout.fragment_ticket_new, container, false);
        MainActivity.updateSelectedItem(MainActivity.ITEM_TICKET);
        context = getContext();

        /*
        if( MainActivity.appEquipments == null ) {
            MainActivity.setAppEquipments();
        }
        */
        spinner_equipment = (Spinner) view.findViewById(R.id.spinner_equipment);
        field_ticket_title = (EditText) view.findViewById(R.id.ticket_subject);
        field_message_comment = (EditText) view.findViewById(R.id.message_content);
        form_layout = (View) view.findViewById(R.id.layout_form);
        btn_send = (Button) view.findViewById(R.id.btn_send);

        // fill the spinner
        spinnerArray.add(getString(R.string.lbl_select));
        for (Map.Entry<Integer, Equipment> entry : MainActivity.appEquipments.entrySet()) {
            spinnerArray.add(((Equipment) entry.getValue()).getSerialNumber());
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_equipment.setAdapter(spinnerArrayAdapter);

        return view;
    }

    // Fix EditText not updated :
    // https://stackoverflow.com/a/13414452/11339842
    @Override
    public void onResume() {
        if (Debug) Log.d(TAG, "Fragment_NewTicket:onResume");
        super.onResume();
        spinner_equipment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Debug)
                    Log.d(TAG, "Fragment_NewTicket:onItemSelected : " + position + " -> " + spinnerArray.get(position));

                if (position != 0) {
                    form_layout.setVisibility(View.VISIBLE);
                } else {
                    form_layout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if (Debug) Log.d(TAG, "Fragment_NewTicket:onNothingSelected");
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Debug) Log.d(TAG, "Fragment_NewTicket::onClickSend");
                MainActivity.hideKeyboard(view);
                String[] fields = {Ticket.FIELD_SUBJECT, Message.FIELD_CONTENT};
                if (checkFields(fields)) {
                    Date dateMessage = new Date();
                    int idEquipment = 0;
                    for (Map.Entry<Integer, Equipment> entry : MainActivity.appEquipments.entrySet()) {
                        if (((Equipment) entry.getValue()).getSerialNumber().equals(spinner_equipment.getSelectedItem())) {
                            idEquipment = entry.getKey();
                        }
                    }
                    // TODO if idEquipment != 0

                    // Prepare request
                    Ticket ticket = new Ticket(idEquipment, title, Ticket.STATUS_OPEN, dateMessage);
                    String paramsTicket[] = {HttpRequest.METHOD_POST, ConfigAPI.URL_POST_TICKET, ticket.toJSONString()};

                    // Do request & compute result
                    try {
                        JSONObject dataTicket = (JSONObject) MainActivity.doHttpRequest(paramsTicket);
                        if (dataTicket != null) {
                            int idTicket = dataTicket.getInt("id_ticket");
                            // TODO if idTicket != 0
                            ticket.setId(idTicket);

                            // Prepare request
                            Message message = new Message(idTicket, MainActivity.appCustomer.getId(), dateMessage, comment);
                            String paramsMessage[] = {HttpRequest.METHOD_POST, ConfigAPI.URL_POST_MESSAGE, message.toJSONString()};

                            // Do request & compute result
                            JSONObject dataMessage = (JSONObject) MainActivity.doHttpRequest(paramsMessage);
                            if (dataMessage != null) {
                                MainActivity.appTickets.put(idTicket, ticket);
                                Bundle bundle = new Bundle();
                                bundle.putString("ticket", ticket.toJSONString());
                                MainActivity.MESSAGES_LIST_FRAGMENT.setArguments(bundle);
                                MainActivity.openFragment(MainActivity.MESSAGES_LIST_FRAGMENT);
                                MainActivity.makeToast(getString(R.string.ticket_post_success));

                                //Firebase subscription
                                MainActivity.subscribeToTicket(ticket);
                            } else {
                                MainActivity.makeToast(getString(R.string.ticket_post_fail));
                                //TODO delete ticket ?
                            }
                        } else {
                            MainActivity.makeToast(getString(R.string.update_fail));
                            //TODO
                        }
                    } catch (Exception e) {
                        if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
                    }


                    /*
                    // Send request & Compute result
                    HttpRequest ticketRequest = new HttpRequest();
                    try {
                        JSONObject jsonTicket = ticketRequest.execute(paramsTicket).get();
                        if (jsonTicket != null) {
                            try {
                                if (jsonTicket.getInt("status") > 0) {
                                    JSONObject dataTicket = jsonTicket.getJSONObject("msg");
                                    int idTicket = dataTicket.getInt("id_ticket");
                                    // TODO if idTicket != 0
                                    ticket.setId(idTicket);

                                    // Prepare request
                                    Message message = new Message(idTicket, MainActivity.appCustomer.getId(), dateMessage, comment);
                                    String paramsMessage[] = {HttpRequest.METHOD_POST, ConfigAPI.URL_POST_MESSAGE, message.toJSONString()};

                                    // Send request & Compute result
                                    HttpRequest messageRequest = new HttpRequest();
                                    try {
                                        JSONObject jsonMessage = messageRequest.execute(paramsMessage).get();
                                        if (jsonMessage != null) {
                                            try {
                                                if (jsonMessage.getInt("status") > 0) {
                                                    /**
                                                    JSONObject dataMessage = jsonTicket.getJSONObject("msg");
                                                    MainActivity.lastMessageId = dataMessage.getInt("id_message");
                                                    if (Debug) Log.d(TAG, "lastMessageId: " + MainActivity.lastMessageId);
                                                    /**

                                                    MainActivity.appTickets.put(idTicket, ticket);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("ticket", ticket.toJSONString());
                                                    MainActivity.MESSAGES_LIST_FRAGMENT.setArguments(bundle);
                                                    MainActivity.openFragment(MainActivity.MESSAGES_LIST_FRAGMENT);
                                                    MainActivity.makeToast(getString(R.string.ticket_post_success));

                                                    //Firebase subscription
                                                    MainActivity.subscribeToTicket(ticket);

                                                } else {
                                                    if (Debug) Log.d(TAG, "Error: " + jsonMessage.getInt("status"));
                                                    MainActivity.makeToast(getString(R.string.ticket_post_fail));
                                                    //TODO delete ticket ?
                                                }
                                            } catch (JSONException e) {
                                                if (Debug) Log.d(TAG, "JSONException: " + e.getMessage());
                                            }
                                        } else {
                                            if (Debug) Log.d(TAG, "Error: json is null");
                                        }
                                    } catch (Exception e) {
                                        if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
                                    }
                                } else {
                                    if (Debug) Log.d(TAG, "Error: " + jsonTicket.getInt("status"));
                                    MainActivity.makeToast(getString(R.string.update_fail));
                                }
                            } catch (JSONException e) {
                                if (Debug) Log.d(TAG, "JSONException: " + e.getMessage());
                            }
                        } else {
                            if (Debug) Log.d(TAG, "Error: json is null");
                        }
                    } catch (Exception e) {
                        if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
                    }
                    */
                } else {
                    MainActivity.makeToast("Error: equipment is null");
                }
            }
        });
    } //onResume

    private boolean checkFields(String[] fields) {
        Pattern pattern;
        Matcher matcher;
        for (String field : fields) {
            switch (field) {
                case Ticket.FIELD_SUBJECT:
                    title = field_ticket_title.getText().toString();
                    pattern = Pattern.compile("[\\w\\W\\s]+");
                    matcher = pattern.matcher(title);
                    if (title.isEmpty() || !matcher.matches()) {
                        MainActivity.makeToast(getString(R.string.err_comment));
                        return false;
                    }
                    break;

                case Message.FIELD_CONTENT:
                    comment = field_message_comment.getText().toString();
                    pattern = Pattern.compile("[\\w\\W\\s]+");
                    matcher = pattern.matcher(comment);
                    if (comment.isEmpty() || !matcher.matches()) {
                        MainActivity.makeToast(getString(R.string.err_comment));
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
