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
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import tortevois.projet.api.Equipment;

public class Fragment_EquipmentMap extends Fragment implements OnMapReadyCallback { // extends SupportMapFragment
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

    private static Context context;
    private static View view;

    private static final float DEFAULT_ZOOM = 15;
    private static final double DEFAULT_LAT = 48.862725;
    private static final double DEFAULT_LNG =  2.287592;

    private static EditText field_serial_number;
    private static Button btn_setpos;
    private static Button btn_validate;
    private static Equipment equipment;
    private static GoogleMap googleMap;
    private static LatLng position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Debug) Log.d(TAG, "Fragment_EquipmentMap:onCreateView");
        view = inflater.inflate(R.layout.fragment_equipment_map, container, false);
        MainActivity.updateSelectedItem(MainActivity.ITEM_EQUIPMENT);
        context = getContext();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        field_serial_number = (EditText) view.findViewById(R.id.serial_number);
        btn_setpos = (Button) view.findViewById(R.id.btn_setpos);
        btn_validate = (Button) view.findViewById(R.id.btn_validate);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (Debug) Log.d(TAG, "bundle not null");
            String jsonEquipment = bundle.getString("equipment");
            try {
                JSONObject jsonObject = new JSONObject(jsonEquipment);
                equipment = new Equipment(jsonObject);
                position = new LatLng(equipment.getLat(), equipment.getLng());
            } catch (JSONException e) {
                if (Debug) Log.d(TAG, "JSONException: " + e.getMessage());
            }
        } else {
            equipment = null;
            //TODO : toast Error ?
        }

        btn_setpos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Debug) Log.d(TAG, "Fragment_EquipmentView:onClickSetPosition");
                if (MainActivity.tracker != null) {
                    position = new LatLng(MainActivity.tracker.getLatitude(), MainActivity.tracker.getLongitude());
                    moveToLatLng();
                } else {
                    MainActivity.makeToast(getString(R.string.err_nopos));
                }
            }
        });

        btn_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Debug) Log.d(TAG, "Fragment_EquipmentMap:onClickValidate");
                equipment.setLat(position.latitude);
                equipment.setLng(position.longitude);
                Bundle bundle = new Bundle();
                bundle.putString("equipment", equipment.toJSONString());
                MainActivity.EQUIPMENT_VIEW_FRAGMENT.setArguments(bundle);
                MainActivity.openFragment(MainActivity.EQUIPMENT_VIEW_FRAGMENT);
            }
        });

        return view;
    }

    // Fix EditText not updated :
    // https://stackoverflow.com/a/13414452/11339842
    @Override
    public void onResume() {
        super.onResume();
        field_serial_number.setText(equipment.getSerialNumber());
        field_serial_number.setEnabled(false);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (Debug) Log.d(TAG, "onMapReady");

        googleMap = map;

        // Apply some options
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        /*
        if( ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            //getCurrentLocation();
        }
        */

        // Set Position
        if( position == null ) {
            if (Debug) Log.d(TAG, "Lat/Lng is defined with default settings");
            position = new LatLng(DEFAULT_LAT, DEFAULT_LNG);
        }

        // Add Marker
        googleMap.addMarker(new MarkerOptions().position(position));

        // Define position with default Zoom
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));

        // Setting a click event handler for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (Debug) Log.d(TAG, "onMapClick");
                position = latLng;
                moveToLatLng();
            }
        });
    }

    private void moveToLatLng() {
        if (Debug) Log.d(TAG, "moveToLatLng");

        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(position);

        // Setting the title for the marker.
        // This will be displayed on taping the marker
        markerOptions.title(position.latitude + " : " + position.longitude);
        if (Debug) Log.d(TAG, "Lat: " + position.latitude + " Lng: " + position.longitude);

        // Clears the previously touched position
        googleMap.clear();

        // Animating to the touched position
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, DEFAULT_ZOOM));

        // Placing a marker on the touched position
        googleMap.addMarker(markerOptions);
    }
}
