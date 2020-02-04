package tortevois.projet;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import tortevois.projet.api.ConfigAPI;
import tortevois.projet.api.Customer;
import tortevois.projet.api.Equipment;
import tortevois.projet.api.Ticket;
import tortevois.projet.utils.HttpRequest;
import tortevois.projet.utils.Tracker;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;

public class MainActivity extends AppCompatActivity {
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

    protected static final Fragment ACCOUNT_FRAGMENT = new Fragment_Account();
    protected static final Fragment EQUIPMENTS_LIST_FRAGMENT = new Fragment_EquipmentsList();
    protected static final Fragment EQUIPMENTS_MAP_FRAGMENT = new Fragment_EquipmentsMap();
    protected static final Fragment EQUIPMENT_VIEW_FRAGMENT = new Fragment_EquipmentView();
    protected static final Fragment EQUIPMENT_MAP_FRAGMENT = new Fragment_EquipmentMap();
    protected static final Fragment TICKETS_LIST_FRAGMENT = new Fragment_TicketsList();
    protected static final Fragment TICKET_NEW_FRAGMENT = new Fragment_NewTicket();
    protected static final Fragment MESSAGES_LIST_FRAGMENT = new Fragment_MessagesList();
    protected static final Fragment EQUIPMENT_NEW_FRAGMENT = new Fragment_NewEquipment();

    protected static final String SP_CUSTOMER = "customer";
    protected static final String SP_NOTIFICATION = "notifications";

    protected static final int ITEM_ACCOUNT = R.id.nav_item1;
    protected static final int ITEM_EQUIPMENT = R.id.nav_item2;
    protected static final int ITEM_TICKET = R.id.nav_item3;

    private static final String SHARED_PREFS_FILE = "MySharedPreferences";
    private static final int REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_ACCESS_NETWORK_STATE = 2;
    private static final long MAX_TIME_BEFORE_UPDATE_LOCATION = 600000; // =10min
    private static final long MIN_DIST_BEFORE_UPDATE_LOCATION = 1; // in meter

    private static Context context;
    protected static View decorView;
    protected static int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    //| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    //| View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    protected static Toolbar toolbar;

    protected static Customer appCustomer;
    protected static Map<Integer, Equipment> appEquipments;
    protected static Map<Integer, Ticket> appTickets;
    protected static boolean isAdmin = false;
    private int ticketToOpen = -1;
    protected static boolean notificationIsEnable = false;

    protected static Tracker tracker;
    protected static FragmentManager fragmentManager;
    private static SharedPreferences prefs;
    protected static SharedPreferences.Editor editor;
    protected static BottomNavigationView navigationBar;
    private static Fragment DEFAULT_FRAGMENT = ACCOUNT_FRAGMENT;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (Debug) Log.d(TAG, "MainActivity:onNavigationItemSelected");
            Fragment fragment = null;
            switch (item.getItemId()) {
                case ITEM_ACCOUNT:
                    fragment = ACCOUNT_FRAGMENT;
                    break;
                case ITEM_EQUIPMENT:
                    fragment = EQUIPMENTS_LIST_FRAGMENT;
                    break;
                case ITEM_TICKET:
                    fragment = TICKETS_LIST_FRAGMENT;
                    break;
            }
            return openFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Debug) Log.d(TAG, "MainActivity:onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        decorView = getWindow().getDecorView();
        if( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(uiOptions);
                    }
                }
            });
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        navigationBar = (BottomNavigationView) findViewById(R.id.navigation);
        navigationBar.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();

        // Firebase -->
        // Initilize Firebase App
        FirebaseApp.initializeApp(context);

        // https://firebase.google.com/docs/cloud-messaging/android/receive
        // cf. https://wajahatkarim.com/2018/05/firebase-notifications-in-background--foreground-in-android/
        Bundle bundle = getIntent().getExtras();
        if (Debug) Log.d(TAG, "bundle: " + bundle);
        if (bundle != null && bundle.keySet() != null) {
            for (String key : new TreeSet<String>(bundle.keySet())) {
                if (Debug) Log.i(TAG, key + " = " + bundle.get(key));
                if( key.equals("id_ticket") ) {
                    ticketToOpen = Integer.parseInt((String) bundle.get("id_ticket"));
                    if (Debug) Log.d(TAG, "Find a ticket to open : " + ticketToOpen);
                }
            }
        }

        // cf. https://www.vogella.com/tutorials/AndroidBroadcastReceiver/article.html
        // 6.2. Using the package manager to disable static receivers
        ComponentName receiver = new ComponentName(this, MyFirebaseMessageReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        // Firebase <--


        // Load SharedPreferences
        prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        editor = prefs.edit();

        // Check if user is already logged from SharedPreferences
        String jsonAppCustomer = prefs.getString(SP_CUSTOMER, "");
        if (jsonAppCustomer != null) {
            if (!jsonAppCustomer.isEmpty()) {
                setAppCustomer(jsonAppCustomer);
                appEquipments = new LinkedHashMap<>();
                appTickets = new LinkedHashMap<>();
            }
        }

        notificationIsEnable = prefs.getBoolean(SP_NOTIFICATION, false);
        if (Debug) Log.d(TAG,"notificationIsEnable: " + notificationIsEnable);

        // checkSelfPermission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        } else {
            tracker = new Tracker(this);
        }
    }

    @Override
    protected void onResume() {
        if (Debug) Log.d(TAG, "MainActivity:onResume");
        super.onResume();

        // Check Network
        isNetworkAvailable();

        // Hide the Navigation Bar
        // https://developer.android.com/training/system-ui/navigation
        // This work only for android 4.4+
        // https://stackoverflow.com/a/22839594/11339842
        if( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            decorView.setSystemUiVisibility(uiOptions);
        }

        // Set LatLng
        // getCurrentLocation();
        if (tracker != null)
        if (!tracker.canGetLocation()) {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            tracker.showSettingsAlert();
        }

        if (appCustomer == null) {
            // Disable Navigation Item
            findViewById(ITEM_EQUIPMENT).setEnabled(false);
            findViewById(ITEM_TICKET).setEnabled(false);
        } else {
            // Refresh data
            setAppEquipments();
            setAppTickets();
        }

        if( ticketToOpen > 0 ) {
            if (Debug) Log.d(TAG, "Open ticket : " + ticketToOpen);
            Ticket ticket = appTickets.get(ticketToOpen);
            Bundle bundle = new Bundle();
            bundle.putString("ticket", ticket.toJSONString());
            // MESSAGES_LIST_FRAGMENT.setArguments(bundle);
            // openFragment(MESSAGES_LIST_FRAGMENT);
            Fragment fragment = new Fragment_MessagesList();
            fragment.setArguments(bundle);
            openFragment(fragment);
            ticketToOpen = 0;
        } else {
            if (Debug) Log.d(TAG, "No ticket to open : " + ticketToOpen);
            // Set default Fragment
            openFragment(DEFAULT_FRAGMENT);
        }
    }

    @Override
    protected void onPause() {
        if (Debug) Log.d(TAG, "MainActivity:onPause");
        super.onPause();
    }

    // --> Toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (Debug) Log.d(TAG, "MainActivity:onOptionsItemSelected");
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.tb_btn_finish:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    // Toolbar <--

    @Override
    public void onBackPressed() {
        if (Debug) Log.d(TAG, "MainActivity:onBackPressed");
        // to fix first blank Fragment
        // https://stackoverflow.com/a/38883688/11339842
        if (fragmentManager.getBackStackEntryCount() == 1) {
            finish();
        } else if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
        //super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (Debug) Log.d(TAG, "MainActivity:onRequestPermissionsResult");
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Debug) Log.d(TAG, "Permission ACCESS_FINE_LOCATION granted");
                    tracker = new Tracker(this);
                } else {
                    if (Debug) Log.d(TAG, "Permission ACCESS_FINE_LOCATION denied");
                    Toast.makeText(this, "Permission ACCESS_FINE_LOCATION needed", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;

            case REQUEST_ACCESS_NETWORK_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Debug) Log.d(TAG, "Permission ACCESS_NETWORK_STATE granted");
                } else {
                    if (Debug) Log.d(TAG, "Permission ACCESS_NETWORK_STATE denied");
                    Toast.makeText(this, "Permission ACCESS_NETWORK_STATE needed", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (Debug) Log.d(TAG, "MainActivity:onWindowFocusChanged");
        super.onWindowFocusChanged(hasFocus);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    protected static boolean openFragment(Fragment fragment) {
        if( fragment != null ) {
            /*
            if( DEFAULT_FRAGMENT.getArguments() != null ) {
                DEFAULT_FRAGMENT.getArguments().clear();
                if (Debug) Log.d(TAG, "Bundle is cleared");
            }
            */
            if (Debug) Log.d(TAG, "openFragment: " + fragment);
            fragmentManager.beginTransaction()
                    .replace(R.id.framelayout, fragment)
                    .addToBackStack(null)
                    .commit();
            DEFAULT_FRAGMENT = fragment;
            return true;
        }
        return false;
    }

    protected static void updateSelectedItem(int itemId) {
        navigationBar.getMenu().findItem(itemId).setChecked(true);
    }

    protected static void makeToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    protected static void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            if (Debug) Log.d(TAG, "hideKeyboard Exception: " + e.getMessage());
        }
    }

    protected static void setAppCustomer(String json) {
        if (Debug) Log.d(TAG, "Try to create appCustomer: " + json);
        if (json != null) {
            if (!json.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    appCustomer = new Customer(jsonObject);
                    if (appCustomer != null) {
                        if (Debug) Log.d(TAG, "appCustomer restored successfully: " + appCustomer.toJSONString());
                        if (appCustomer.getContractId().contains("SAV")) {
                            isAdmin = true;
                        }
                    }
                } catch (JSONException e) {
                    // TODO : JSONException
                    if (Debug) Log.d(TAG, "JSONException: " + e.getMessage());
                }
            } else {
                if (Debug) Log.d(TAG, "Error: jsonAppCustomer is empty");
            }
        } else {
            if (Debug) Log.d(TAG, "Error: jsonAppCustomer is null");
        }
    }

    protected static void setAppEquipments() {
        if (appEquipments != null)
            appEquipments.clear();

        // Prepare request
        String url;
        if (isAdmin) {
            url = ConfigAPI.URL_GET_EQUIPMENTS;
        } else {
            url = ConfigAPI.URL_GET_CUSTOMER_EQUIPEMENTS;
            url = url.replace("$ID", Integer.toString(MainActivity.appCustomer.getId()));
        }
        String params[] = {HttpRequest.METHOD_GET, url};

        // Do request & compute result
        try {
            JSONArray data = (JSONArray) MainActivity.doHttpRequest(params);
            if (Debug) Log.d(TAG, "data: " + data.toString());
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                Equipment equipment = new Equipment(jsonObject);
                // TODO : no equipments ? ...
                appEquipments.put(equipment.getId(), equipment);
            }
        } catch (Exception e) {
            if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
        }
    }

    protected static void setAppTickets() {
        if (appTickets != null)
            appTickets.clear();

        // Prepare request
        String url;
        if (isAdmin) {
            url = ConfigAPI.URL_GET_TICKETS;
        } else {
            url = ConfigAPI.URL_GET_CUSTOMER_TICKETS;
            url = url.replace("$ID", Integer.toString(MainActivity.appCustomer.getId()));
        }
        String params[] = {HttpRequest.METHOD_GET, url};

        // Do request & compute result
        try {
            JSONArray data = (JSONArray) MainActivity.doHttpRequest(params);
            if (Debug) Log.d(TAG, "data: " + data.toString());
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                Ticket ticket = new Ticket(jsonObject);
                // TODO : no tickets ? ...
                appTickets.put(ticket.getId(), ticket);
            }
        } catch (Exception e) {
            if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
        }
    }

    private static List<Ticket> getAllTicketsId() {
        List<Ticket> list = new ArrayList<>();

        // Prepare request
        String url = ConfigAPI.URL_GET_TICKETS;
        String params[] = {HttpRequest.METHOD_GET, url};

        // Do request & compute result
        try {
            JSONArray data = (JSONArray) MainActivity.doHttpRequest(params);
            if (Debug) Log.d(TAG, "data: " + data.toString());
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                Ticket ticket = new Ticket(jsonObject);
                // TODO : no tickets ? ...
                list.add(ticket);
            }
            return list;
        } catch (Exception e) {
            if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
        }
        return null;
    }

    protected static Map<Integer,Customer> getAllCustomer() {
        Map<Integer,Customer> map = new HashMap<>();

        // Prepare request
        String url = ConfigAPI.URL_GET_CUSTOMERS;
        String params[] = {HttpRequest.METHOD_GET, url};

        // Do request & compute result
        try {
            JSONArray data = (JSONArray) MainActivity.doHttpRequest(params);
            if (Debug) Log.d(TAG, "data: " + data.toString());
            for (int i = 0; i < data.length(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                Customer customer = new Customer(jsonObject);
                // TODO : no tickets ? ...
                map.put(customer.getId(), customer);
            }
            return map;
        } catch (Exception e) {
            if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
        }
        return null;
    }

    protected static Object doHttpRequest(String[] params) {
        // Get result & compute
        HttpRequest getRequest = new HttpRequest();
        try {
            JSONObject json = getRequest.execute(params).get();
            if (json != null) {
                try {
                    int status = json.getInt("status");
                    if (status > 0) {
                        Object msg = json.get("msg");
                        if (msg instanceof JSONObject)
                            return json.getJSONObject("msg");

                        if (msg instanceof JSONArray)
                            return json.getJSONArray("msg");
                    } else {
                        if (Debug) Log.d(TAG, "Error: " + status);
                    }
                } catch (JSONException e) {
                    if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
                }
            } else {
                if (Debug) Log.d(TAG, "Error: json is null");
            }
        } catch (Exception e) {
            if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
        }
        return null;
    }

    protected static void subscribeToTicket(Ticket ticket){
        if( ticket.getStatus() == Ticket.STATUS_OPEN ) {
            int idTicket = ticket.getId();
            if (Debug) Log.d(TAG, "FirebaseMessaging: subscribe to ticket_" + idTicket);
            FirebaseMessaging.getInstance().subscribeToTopic("ticket_" + idTicket).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()) {
                        if (Debug) Log.d(TAG, "failed");
                    } else {
                        if (Debug) Log.d(TAG, "successful");
                    }
                }
            });
        }
    }

    protected static void unsubscribeToTicket(Ticket ticket){
        int idTicket = ticket.getId();
        if (Debug) Log.d(TAG, "FirebaseMessaging: unubscribe to ticket_" + idTicket);
        FirebaseMessaging.getInstance().unsubscribeFromTopic("ticket_" + idTicket).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    if (Debug) Log.d(TAG, "failed");
                } else {
                    if (Debug) Log.d(TAG, "successful");
                }
            }
        });
    }

    protected static void unsubscribeToAllTicket(){
        List<Ticket> list = getAllTicketsId();
        for (Ticket ticket : list) {
            unsubscribeToTicket(ticket);
        }
    }

    protected static void subscribeToAllTicket(){
        List<Ticket> list = getAllTicketsId();
        for (Ticket ticket : list) {
            subscribeToTicket(ticket);
        }
    }

    public static Context getAppContext() {
        return context;
    }

    protected boolean isNetworkAvailable() {
        // checkSelfPermission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, REQUEST_ACCESS_NETWORK_STATE);
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network allNetworks[] = cm.getAllNetworks ();
        for(Network network : allNetworks ) {
            if (Debug) Log.d(TAG, "Check network: " + network.toString());
            if(cm.getNetworkCapabilities(network).hasCapability(NET_CAPABILITY_INTERNET)) {
                if (Debug) Log.d(TAG, "hasCapability : NET_CAPABILITY_INTERNET");
                return true;
            }
        }
        if (Debug) Log.d(TAG, "No Network Available");
        Toast.makeText(this, "No Network Available", Toast.LENGTH_SHORT).show();
        finish();
        return false;
    }

    /*
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null && !isOldLocation(location.getTime())) {
                if(Debug) Log.d( TAG, "getCurrentLocation: from LastKnownLocation");
                if(Debug) Log.d( TAG, "Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
                myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                if(Debug) Log.d( TAG, "getCurrentLocation: request LocationUpdate");
                LocationListener locationListener = new MyLocationListener();
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MAX_TIME_BEFORE_UPDATE_LOCATION, MIN_DIST_BEFORE_UPDATE_LOCATION, locationListener);
            }
        }
    }

    private boolean isOldLocation( long locationTime ){
        if (Debug) Log.d(TAG, "isOldLocation: " + (System.currentTimeMillis() - locationTime) );
        return (System.currentTimeMillis() - locationTime) > MAX_TIME_BEFORE_UPDATE_LOCATION;
    }

    // https://stackoverflow.com/a/10917500/11339842
    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if(Debug) Log.d( TAG, "onLocationChanged");
            if(Debug) Log.d( TAG, "Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
            Toast.makeText(getApplication(), "Position is updated", Toast.LENGTH_LONG).show();
            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
    */
}
