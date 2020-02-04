package tortevois.projet.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

// AsyncTask<Params, Progress, Result>
public class HttpRequest extends AsyncTask<String, Void, JSONObject> implements HttpRequestInterface {
    private static final boolean Debug = true;
    private static final String TAG = "ALT";

    /**
     * params[0] : Request Method
     * params[1] : Request URL
     * params[2] : Data to send
     */
    @Override
    protected JSONObject doInBackground(String... params) { //Params
        JSONObject jsonObject = null;

        try {
            if (Debug) Log.d(TAG, "Request URL: " + params[0] + " " + params[1]);
            URL url = new URL(params[1]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set methods and timeouts
            connection.setRequestMethod(params[0]);
            connection.setReadTimeout(TIMEOUT_READ);
            connection.setConnectTimeout(TIMEOUT_CONNECTION);

            if (params[0] == METHOD_POST || params[0] == METHOD_PUT) {
                connection.setRequestProperty("Content-Type", "application/json");
                if (params[2] != null) {
                    if (Debug) Log.d(TAG, "Data: " + params[2]);
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    writer.write(params[2]);
                    writer.flush();
                }
            }

            connection.connect();

            if (connection.getResponseCode() == 200) {
                String inputLine;
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                String result = stringBuilder.toString();
                if (Debug) Log.d(TAG, "Result: " + result);

                try {
                    jsonObject = new JSONObject(result);
                } catch (JSONException e) {
                    // TODO : JSONException
                    if (Debug) Log.d(TAG, "JSONException: " + e.getMessage());
                }

            } else {
                // TODO : something with ResponseCode...
            }

        } catch (Exception e) {
            // TODO : Exception
            if (Debug) Log.d(TAG, "Exception: " + e.getMessage());
        }

        return jsonObject;
    }
}
