package tortevois.projet;

import android.content.Context;
import android.content.Entity;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import tortevois.projet.api.Equipment;

public class Fragment_EquipmentsMap extends Fragment implements OnMapReadyCallback { // extends SupportMapFragment
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

    private static Context context;
    private static View view;

    private static final float DEFAULT_ZOOM = 15;
    private static final double DEFAULT_LAT = 48.862725;
    private static final double DEFAULT_LNG =  2.287592;

    private static GoogleMap googleMap;
    private static LatLng position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Debug) Log.d(TAG, "Fragment_EquipmentsMap:onCreateView");
        view = inflater.inflate(R.layout.fragment_equipment_allonmap, container, false);
        MainActivity.updateSelectedItem(MainActivity.ITEM_EQUIPMENT);
        context = getContext();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (Debug) Log.d(TAG, "onMapReady");

        googleMap = map;

        // Apply some options
        googleMap.getUiSettings().setRotateGesturesEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Set Position
        /*
        if( position == null ) {
            if (Debug) Log.d(TAG, "Lat/Lng is defined with default settings");
            position = new LatLng(DEFAULT_LAT, DEFAULT_LNG);
        }
        */

        // Builder for camera auto-zoom
        // https://stackoverflow.com/a/14828739/11339842
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        // Add Marker
        for (Map.Entry<Integer,Equipment> entry : MainActivity.appEquipments.entrySet()) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(entry.getValue().getLat(), entry.getValue().getLng()))
                    .anchor(0.5f, 0.5f)
                    .title(entry.getValue().getSerialNumber())
                    .snippet(entry.getValue().getComment())
                    //.icon(BitmapDescriptorFactory.fromResource(iconResID))
            );

            builder.include(new LatLng(entry.getValue().getLat(), entry.getValue().getLng()));
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, /* padding */ 100); // TODO calculate padding
        // Define position with default Zoom
        googleMap.moveCamera(cu);

        /*
        // Setting a click event handler for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (Debug) Log.d(TAG, "onMapClick");
                position = latLng;
                moveToLatLng();
            }
        });
        */
    }
}
