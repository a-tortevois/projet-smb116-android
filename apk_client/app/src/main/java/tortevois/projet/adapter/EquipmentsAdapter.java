package tortevois.projet.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import tortevois.projet.R;
import tortevois.projet.api.Equipment;

// https://codinginflow.com/tutorials/android/room-viewmodel-livedata-recyclerview-mvvm/part-6-recyclerview-adapter
public class EquipmentsAdapter extends RecyclerView.Adapter<EquipmentsAdapter.EquipmentViewHolder> {
    private Map<Integer,Equipment> equipments;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Equipment equipment);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class EquipmentViewHolder extends RecyclerView.ViewHolder {
        public TextView field_equipment_name;

        public EquipmentViewHolder(@NonNull View itemView) {
            super(itemView);
            field_equipment_name = itemView.findViewById(R.id.equipment_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick( (Equipment) getValue(position));
                        }
                    }
                }
            });
        }
    }

    public EquipmentsAdapter(Map<Integer,Equipment> equipments, Context context) {
        this.equipments = equipments;
        this.context = context;
    }

    @NonNull
    @Override
    public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_equipment, viewGroup, false);
        return new EquipmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipmentViewHolder viewHolder, int position) {
        Equipment equipment = (Equipment) getValue(position);
        viewHolder.field_equipment_name.setText(equipment.getSerialNumber());
    }

    @Override
    public int getItemCount() {
        return equipments.size();
    }

    // Key : (map.keySet().toArray())[position]
    private Object getKey( int position ) {
        return (equipments.keySet().toArray())[position];
    }

    // Value : (map.values().toArray())[position]
    private Object getValue( int position ) {
        return (equipments.values().toArray())[position];
    }
}