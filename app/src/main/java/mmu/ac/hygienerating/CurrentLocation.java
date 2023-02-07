package mmu.ac.hygienerating;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CurrentLocation extends AppCompatActivity {

    String lat;
    String lon;
    String urlData;
    private RecyclerView recyclerView;
    private List<LocationDetails> locationDetails;
    private List<LocationData> locationDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view_current_location);

        //Check permissions to access the internet and current location
        String[] requiredPermissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE
        };

        boolean ok = true;
        for (String requiredPermission : requiredPermissions) {
            int result = ActivityCompat.checkSelfPermission(this, requiredPermission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                ok = false;
            }
        }

        if (!ok) {
            ActivityCompat.requestPermissions(this, requiredPermissions, 1);
            System.exit(0);

            //Proceed with Activity if permissions are given
        } else {


            //Find current latitude and longitude
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            lat = Double.toString(location.getLatitude());
            lon = Double.toString(location.getLongitude());

            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    lat = Double.toString(location.getLatitude());
                    lon = Double.toString(location.getLongitude());
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            //Request data from URL
            urlData = "http://sandbox.kriswelsh.com/hygieneapi/hygiene.php?op=search_location&lat=" + lat + "&long=" + lon;
            recyclerView = findViewById(R.id.recyclerView);
            Button refresh = findViewById(R.id.refresh);
            Button back = findViewById(R.id.back);
            Button mapSearch = findViewById(R.id.mapSearch);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            locationDetails = new ArrayList<>();
            locationDataset = new ArrayList<>();


            //Loads data into the Recycler View
            loadRecyclerViewData();

            //Buttons to refresh page, go back to the Main Activity or view locations on a map
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CurrentLocation.this, MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            });

            mapSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CurrentLocation.this, MapSearch.class);
                    ArrayList<LocationData> locationData = new ArrayList<>(locationDataset);
                    ArrayList<LocationDetails> locationDetails = new ArrayList<>(CurrentLocation.this.locationDetails);
                    ArrayList<String> currentLocation = new ArrayList<>();
                    currentLocation.add(lat);
                    currentLocation.add(lon);
                    //Current location, food place details and food place location data sent to MapSearch
                    intent.putExtra("mmu.ac.hygienerating.CURRENT_LOCATION", currentLocation);
                    intent.putExtra("mmu.ac.hygienerating.LOCATION_DATA", locationData);
                    intent.putExtra("mmu.ac.hygienerating.LOCATION_DETAILS", locationDetails);
                    startActivity(intent);
                }
            });
        }
    }

    //This method loads the data gained from the URL
    private void loadRecyclerViewData() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                urlData,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        try {
                            retrieveLocationData(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //This method presents the data in a readable format using the ListItemAdapter
    private void retrieveLocationData(String response) throws JSONException {
        JSONArray array = new JSONArray(response);

        for (int i = 0; i < array.length(); i++) {
            JSONObject o = array.getJSONObject(i);
            JSONObject latLonObj = o.getJSONObject("Location");
            String rating;
            if (o.getString("RatingValue").equals("-1")) {
                rating = "Exempt";
            } else {
                rating = o.getString("RatingValue");
            }
            LocationDetails item = new LocationDetails(
                    o.getString("BusinessName"),
                    o.getString("AddressLine1") + "\n" +
                            o.getString("AddressLine2") + "\n" +
                            o.getString("AddressLine3") + "\n" +
                            o.getString("PostCode") + "\n" +
                            "Distance(km): " + o.getString("DistanceKM").substring(0, 4),
                    rating);

            LocationData locationData = new LocationData(
                    latLonObj.getString("Latitude"),
                    latLonObj.getString("Longitude"));
            locationDataset.add(locationData);
            locationDetails.add(item);

        }

        RecyclerView.Adapter adapter = new ListItemAdapter(locationDetails, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

}
