package tortevois.projet;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONObject;

import java.util.List;

import tortevois.projet.adapter.EquipmentsAdapter;
import tortevois.projet.api.ConfigAPI;
import tortevois.projet.api.Equipment;
import tortevois.projet.utils.HttpRequest;
import tortevois.projet.utils.SwipeHelper;

public class Fragment_EquipmentsList extends Fragment {
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

    private static Context context;
    private static View view;

    private static Button btn_viewonmap;
    private static Button btn_add;
    /*
    private static ListView lv;
    private static EquipmentsAdapter_bak adapter;
    */

    private static RecyclerView rv;
    private static EquipmentsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Debug) Log.d(TAG, "Fragment_EquipmentsList:onCreateView");
        view = inflater.inflate(R.layout.fragment_equipments_list, container, false);
        MainActivity.updateSelectedItem(MainActivity.ITEM_EQUIPMENT);
        context = getContext();

        //MainActivity.toolbar.setTitle("My title");

        /*
        TODO
        if( MainActivity.appEquipments == null ) {
            MainActivity.setAppEquipments();
        }
        */

        btn_viewonmap = (Button) view.findViewById(R.id.btn_viewonmap);
        btn_add = (Button) view.findViewById(R.id.btn_add);

        btn_viewonmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Debug) Log.d(TAG, "Fragment_EquipmentsList:onClickViewOnMap");
                MainActivity.openFragment(MainActivity.EQUIPMENTS_MAP_FRAGMENT);
            }
        });

        if(!MainActivity.isAdmin) {
            btn_add.setVisibility(View.GONE);
        } else {
            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Debug) Log.d(TAG, "Fragment_EquipmentsList:onClickAdd");
                    MainActivity.openFragment(MainActivity.EQUIPMENT_NEW_FRAGMENT);
                }
            });
        }

        /*
        lv = (ListView) view.findViewById(R.id.list);
        adapter = new EquipmentsAdapter_bak(MainActivity.appEquipments, context, R.layout.item_equipment, R.id.equipment_name);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Equipment equipment = (Equipment) MainActivity.appEquipments.values().toArray()[(int) id];
                if (Debug) Log.d(TAG, "onItemClick : " + equipment.toJSONString());
                Bundle bundle = new Bundle();
                bundle.putString("equipment", equipment.toJSONString());
                MainActivity.EQUIPMENT_VIEW_FRAGMENT.setArguments(bundle);
                MainActivity.openFragment(MainActivity.EQUIPMENT_VIEW_FRAGMENT);
            }
        });
        */

        rv = view.findViewById(R.id.list);
        rv.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        adapter = new EquipmentsAdapter(MainActivity.appEquipments, context);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(new EquipmentsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Equipment equipment) {
                if (Debug) Log.d(TAG, "onItemClick : " + equipment.toJSONString());
                Bundle bundle = new Bundle();
                bundle.putString("equipment", equipment.toJSONString());
                MainActivity.EQUIPMENT_VIEW_FRAGMENT.setArguments(bundle);
                MainActivity.openFragment(MainActivity.EQUIPMENT_VIEW_FRAGMENT);
            }
        });

        if (MainActivity.isAdmin) {
            SwipeHelper swipeHelper = new SwipeHelper(context, rv) {
                @Override
                public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                    underlayButtons.add(new SwipeHelper.UnderlayButton(
                            "",
                            R.drawable.ic_delete,
                            Color.parseColor("#FF3C30"),
                            new SwipeHelper.UnderlayButtonClickListener() {
                                @Override
                                public void onClick(int pos) {
                                    // TODO: onDelete
                                    if (Debug) Log.d(TAG, "SwipeHelper:onClickDelete: " + pos);
                                    deleteEquipment(pos);
                                }
                            }
                    ));

                /*
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Transfer",
                        0,
                        Color.parseColor("#FF9502"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnTransfer
                            }
                        }
                ));
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        "Unshare",
                        0,
                        Color.parseColor("#C7C7CB"),
                        new SwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnUnshare
                            }
                        }
                ));
                */
                }
            };
        }

        return view;
    }

    private void deleteEquipment(int pos) {
        Equipment equipment = (Equipment) MainActivity.appEquipments.values().toArray()[(int) pos];
        if (Debug) Log.d(TAG, "Delete: " + equipment.getSerialNumber());

        // Prepare request
        String url = ConfigAPI.URL_DELETE_EQUIPMENT;
        url = url.replace("$ID", Integer.toString(equipment.getId()));
        String params[] = {HttpRequest.METHOD_DELETE, url};

        // Do request & compute result
        JSONObject data = (JSONObject) MainActivity.doHttpRequest(params);
        if (data != null) {
            MainActivity.appEquipments.remove(equipment.getId());
            MainActivity.makeToast(getString(R.string.equipment_delete_success));
            adapter.notifyDataSetChanged();
        } else {
            MainActivity.makeToast(getString(R.string.equipment_delete_fail));
        }
    }
}