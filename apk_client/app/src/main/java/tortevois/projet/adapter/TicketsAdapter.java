package tortevois.projet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Map;

import tortevois.projet.api.Ticket;

public class TicketsAdapter extends BaseAdapter {
    private Map<Integer,Ticket> tickets;
    private Context context;
    private int listId = 0;
    private int itemId = 0;

    public TicketsAdapter(Map<Integer, Ticket> tickets, Context context, int listId, int itemId) {
        this.tickets = tickets;
        this.context = context;
        this.listId = listId;
        this.itemId = itemId;
    }

    @Override
    public int getCount() {
        return tickets.size();
    }

    @Override
    public Object getItem(int pos) {
        return getValue(pos);
    }

    // Key : (map.keySet().toArray())[position]
    private Object getKey( int position ) {
        return (tickets.keySet().toArray())[position];
    }

    // Value : (map.values().toArray())[position]
    private Object getValue( int position ) {
        return (tickets.values().toArray())[position];
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(listId, parent, false);
        TextView tv = convertView.findViewById(itemId);
        Ticket ticket = (Ticket) getItem(position);
        tv.setText(ticket.getSubject());
        return convertView;
    }
}