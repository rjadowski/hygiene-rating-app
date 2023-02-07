package mmu.ac.hygienerating;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

public class NameSearch extends AppCompatActivity {

    String urlData;
    private RecyclerView recyclerView;
    private List<LocationDetails> locationDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        //Retrieve search data from previous Activity
        String search = getIntent().getStringExtra("mmu.ac.hygienerating.SEARCH_DATA");

        //Request data from URL using the search term
        urlData = "http://sandbox.kriswelsh.com/hygieneapi/hygiene.php?op=search_name&name=" + search;
        recyclerView = findViewById(R.id.recyclerView);
        Button refresh = findViewById(R.id.refresh);
        Button back = findViewById(R.id.back);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        locationDetails = new ArrayList<>();

        //Loads data into the Recycler View
        loadRecyclerViewData();

        //Buttons to refresh page or go back to the Main Activity
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
                Intent intent = new Intent(NameSearch.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
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
                            o.getString("PostCode"),
                    rating);

            locationDetails.add(item);
        }

        RecyclerView.Adapter adapter = new ListItemAdapter(locationDetails, getApplicationContext());
        recyclerView.setAdapter(adapter);
    }

}
