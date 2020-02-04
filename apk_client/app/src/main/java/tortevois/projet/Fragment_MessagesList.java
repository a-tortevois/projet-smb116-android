package tortevois.projet;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tortevois.projet.adapter.MessagesAdapter;
import tortevois.projet.api.ConfigAPI;
import tortevois.projet.api.Equipment;
import tortevois.projet.api.Message;
import tortevois.projet.api.Ticket;
import tortevois.projet.utils.HttpRequest;

public class Fragment_MessagesList extends Fragment {
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

    private static Context context;
    private static View view;

    private static View form_post_msg;
    private static EditText field_message_comment;
    private static Button btn_open;
    private static Button btn_close;
    private static Button btn_send;
    private String comment;

    private static Ticket ticket;
    private static LinkedList<Message> messagesList;
    private static ListView lv;
    private static MessagesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Debug) Log.d(TAG, "Fragment_MessagesList:onCreateView");
        view = inflater.inflate(R.layout.fragment_messages_list, container, false);
        MainActivity.updateSelectedItem(MainActivity.ITEM_TICKET);
        context = getContext();

        form_post_msg = view.findViewById(R.id.form_post_msg);
        field_message_comment = (EditText) view.findViewById(R.id.message_content);
        btn_open = (Button) view.findViewById(R.id.btn_open);
        btn_close = (Button) view.findViewById(R.id.btn_close);
        btn_send = (Button) view.findViewById(R.id.btn_send);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (Debug) Log.d(TAG, "bundle not null");
            String jsonTicket = bundle.getString("ticket");
            try {
                JSONObject jsonObject = new JSONObject(jsonTicket);
                ticket = new Ticket(jsonObject);
                getMessages();
            } catch (JSONException e) {
                if (Debug) Log.d(TAG, "JSONException: " + e.getMessage());
            }
        } else {
            ticket = null;
        }

        /**
         if (Debug && messagesList != null) {
         for ( Message m : messagesList ) {
         Log.d( TAG, "msg: " + m.toString());
         }
         } else {
         Log.d( TAG, "No msg !");
         }
         /**/

        lv = (ListView) view.findViewById(R.id.list);
        adapter = new MessagesAdapter(messagesList, context, MainActivity.appCustomer.getId());
        lv.setAdapter(adapter);

        // Check & force scroll (stackFromBottom="true")
        if (lv.getLastVisiblePosition() != lv.getAdapter().getCount() - 1) {
            lv.setSelection(lv.getCount() - 1);
        }

        return view;
    }


    // Fix EditText not updated :
    // https://stackoverflow.com/a/13414452/11339842
    @Override
    public void onResume() {
        if (Debug) Log.d(TAG, "Fragment_MessagesList:onResume");
        super.onResume();

        ((TextView) view.findViewById(R.id.serial_number)).setText("S/N: " + ((Equipment) MainActivity.appEquipments.get(ticket.getIdEquipment())).getSerialNumber());
        ((TextView) view.findViewById(R.id.subject)).setText(ticket.getSubject());

        // Keyboard Listener
        addKeyboardListener();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Debug) Log.d(TAG, "Fragment_MessagesList:onClickSend");
                MainActivity.hideKeyboard(view);

                String[] fields = {Message.FIELD_CONTENT};
                if (checkFields(fields)) {

                    int idTicket = ticket.getId();
                    int idCustomer = MainActivity.appCustomer.getId();
                    Date dateMessage = new Date();
                    Message message = new Message(idTicket, idCustomer, dateMessage, comment);

                    // Prepare request
                    String url = ConfigAPI.URL_POST_MESSAGE;
                    String params[] = {HttpRequest.METHOD_POST, url, message.toJSONString()};

                    // Do request & compute result
                    try {
                        JSONObject data = (JSONObject) MainActivity.doHttpRequest(params);
                        if (data != null) {
                            message.setId(data.getInt("id_message"));
                            messagesList.add(message);
                            adapter.notifyDataSetChanged();
                            field_message_comment.setText("");
                            MainActivity.makeToast(getString(R.string.message_success));
                        } else {
                            MainActivity.makeToast(getString(R.string.update_fail));
                        }
                    } catch (Exception e) {
                        if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
                    }
                }
            }
        });

        updateUI();
        if (MainActivity.isAdmin) {

            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Debug) Log.d(TAG, "Fragment_MessagesList:onClickOpen");
                    updateTicketStatus(Ticket.STATUS_CLOSE);
                }
            });

            btn_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Debug) Log.d(TAG, "Fragment_MessagesList:onClickClose");
                    updateTicketStatus(Ticket.STATUS_OPEN);
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Keyboard Listener
        removeKeyboardListener();
    }

    private boolean checkFields(String[] fields) {
        Pattern pattern;
        Matcher matcher;
        for (String field : fields) {
            switch (field) {
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

    protected static void getMessages() {
        if (messagesList == null) {
            messagesList = new LinkedList<>();
        } else {
            messagesList.clear();
        }

        if (ticket != null) {
            if (Debug) Log.d(TAG, "getMessages from ticket: " + ticket.toString());

            // Prepare request
            String url = ConfigAPI.URL_GET_TICKET_MESSAGES;
            url = url.replace("$ID", Integer.toString(ticket.getId()));
            String params[] = {HttpRequest.METHOD_GET, url};

            // Do request & compute result
            try {
                JSONArray data = (JSONArray) MainActivity.doHttpRequest(params);
                if (data != null) {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonObject = data.getJSONObject(i);
                        Message message = new Message(jsonObject);
                        // TODO : no messages ? ...
                        messagesList.add(message);
                    }
                } else {
                    //MainActivity.makeToast(getString(R.string.update_fail));
                }
            } catch (Exception e) {
                if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void updateTicketStatus(int ticketStatus) {
        ticket.setStatus(ticketStatus);
        ticket.setDateStatus(new Date());

        // Prepare request
        String url = ConfigAPI.URL_PUT_TICKET;
        url = url.replace("$ID", Integer.toString(ticket.getId()));
        String params[] = {HttpRequest.METHOD_PUT, url, ticket.toJSONString()};

        // Do request & compute result
        try {
            JSONObject data = (JSONObject) MainActivity.doHttpRequest(params);
            if (data != null) {
                MainActivity.makeToast(getString(R.string.ticket_put_succes));
                updateUI();
            } else {
                MainActivity.makeToast(getString(R.string.ticket_put_fail));
            }
        } catch (Exception e) {
            if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
        }
    }

    private void updateUI() {
        if(Debug) Log.d(TAG, "Fragment_MessagesList:updateUI");

        if (ticket.getStatus() == Ticket.STATUS_OPEN) {
            if(Debug) Log.d(TAG, "Ticket.STATUS_OPEN");
            view.findViewById(R.id.line2).setVisibility(View.VISIBLE);
            form_post_msg.setVisibility(View.VISIBLE);
            btn_open.setVisibility(View.GONE);
            btn_close.setVisibility(View.VISIBLE);
        } else {
            if(Debug) Log.d(TAG, "Ticket.STATUS_CLOSE");
            view.findViewById(R.id.line2).setVisibility(View.GONE);
            form_post_msg.setVisibility(View.GONE);
            btn_open.setVisibility(View.VISIBLE);
            btn_close.setVisibility(View.GONE);
        }

        if (!MainActivity.isAdmin) {
            btn_open.setVisibility(View.GONE);
            btn_close.setVisibility(View.GONE);
        }
    }

    // Fix editText when it focus
    ViewTreeObserver.OnGlobalLayoutListener keyboardListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (Debug) Log.d(TAG, "Fragment_MessagesList:onGlobalLayout");

            // Get the coordinates of area visible
            Rect r = new Rect();
            MainActivity.decorView.getWindowVisibleDisplayFrame(r);

            // Get height of the main view
            int viewHeight = MainActivity.decorView.getHeight();
            int paddingBottom = viewHeight - r.bottom - MainActivity.navigationBar.getHeight();

            if (Debug) Log.d(TAG, "viewHeight: " + viewHeight);
            if (Debug) Log.d(TAG, "r.bottom: " + r.bottom);
            if (Debug) Log.d(TAG, "paddingBottom: " + paddingBottom);

            if (r.bottom == viewHeight) {
                // keyboard is close
                form_post_msg.setPadding(0, 0, 0, 0);
            } else {
                // keyboard is open
                form_post_msg.setPadding(0, 0, 0, paddingBottom);
            }
        }
    };

    private void addKeyboardListener() {
        if (Debug) Log.d(TAG, "Fragment_MessagesList:addKeyboardListener");
        MainActivity.decorView.getViewTreeObserver().addOnGlobalLayoutListener(keyboardListener);
    }

    private void removeKeyboardListener() {
        if (Debug) Log.d(TAG, "Fragment_MessagesList:removeKeyboardListener");
        MainActivity.decorView.getViewTreeObserver().removeOnGlobalLayoutListener(keyboardListener);
    }
}
