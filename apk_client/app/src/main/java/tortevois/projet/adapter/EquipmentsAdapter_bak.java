package tortevois.projet.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Map;

import tortevois.projet.api.Equipment;

public class EquipmentsAdapter_bak extends BaseAdapter {
    private Map<Integer,Equipment> equipments;
    private Context context;
    private int listId = 0;
    private int itemId = 0;

    public EquipmentsAdapter_bak(Map<Integer,Equipment> equipments, Context context, int listId, int itemId) {
        this.equipments = equipments;
        this.context = context;
        this.listId = listId;
        this.itemId = itemId;
    }

    @Override
    public int getCount() {
        return equipments.size();
    }

    @Override
    public Object getItem(int pos) {
        return getValue(pos);
    }

    // Key : (map.keySet().toArray())[position]
    private Object getKey( int position ) {
        return (equipments.keySet().toArray())[position];
    }

    // Value : (map.values().toArray())[position]
    private Object getValue( int position ) {
        return (equipments.values().toArray())[position];
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
        Equipment equipment = (Equipment) getItem(position);
        tv.setText(equipment.getSerialNumber());
        return convertView;
    }
}