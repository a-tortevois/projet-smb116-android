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
import android.widget.Button;
import android.widget.ListView;

import tortevois.projet.adapter.TicketsAdapter;
import tortevois.projet.api.Ticket;

public class Fragment_TicketsList extends Fragment {
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

    private static Context context;
    private static View view;

    private static Button btn_newticket;

    private static ListView lv;
    private static TicketsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Debug) Log.d(TAG, "Fragment_TicketsList:onCreateView");
        view = inflater.inflate(R.layout.fragment_tickets_list, container, false);
        MainActivity.updateSelectedItem(MainActivity.ITEM_TICKET);
        context = getContext();

        /*
        if( MainActivity.appEquipments == null ) {
            MainActivity.setAppEquipments();
        }
        */

        btn_newticket = (Button) view.findViewById(R.id.btn_newticket);

        lv = (ListView) view.findViewById(R.id.list);
        adapter = new TicketsAdapter(MainActivity.appTickets, context, R.layout.item_ticket, R.id.ticket_title);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Ticket ticket = (Ticket) MainActivity.appTickets.values().toArray()[(int) id];
                if (Debug) Log.d(TAG, "onItemClick : " + ticket.toJSONString());
                Bundle bundle = new Bundle();
                bundle.putString("ticket", ticket.toJSONString());
                MainActivity.MESSAGES_LIST_FRAGMENT.setArguments(bundle);
                MainActivity.openFragment(MainActivity.MESSAGES_LIST_FRAGMENT);
            }
        });

        if(MainActivity.isAdmin) {
            btn_newticket.setVisibility(View.GONE);
        }
        else {
            btn_newticket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Debug) Log.d(TAG, "Fragment_EquipmentView:onClickNewTicket");
                    MainActivity.openFragment(MainActivity.TICKET_NEW_FRAGMENT);
                }
            });
        }

        return view;
    }
}
