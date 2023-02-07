package mmu.ac.hygienerating;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapSearch extends AppCompatActivity implements
        OnMapReadyCallback {

    private MapView mapView;
    private static final String MAKI_ICON_LOCATIONS = "restaurant-15";
    private static final String ICON_CURRENT = "my-marker";
    private SymbolManager symbolManagerCurrentLocation;
    private Symbol symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

// Mapbox access token is configured here. This needs to be called either in your application
// object or in the same activity which contains the map view.
        Mapbox.getInstance(this, getString(R.string.api_token));

// This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.map_search);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        mapboxMap.setStyle(Style.DARK, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                //Retrieve data from Current Location Activity

                //Food location data (Latitude & Longitude)
                List<LocationData> locationData =
                        (ArrayList<LocationData>) getIntent().getSerializableExtra("mmu.ac.hygienerating.LOCATION_DATA");

                //Food location details (Business name and address)
                List<LocationDetails> locationDetails =
                        (ArrayList<LocationDetails>) getIntent().getSerializableExtra("mmu.ac.hygienerating.LOCATION_DETAILS");

                //Current location data (Latitude & Longitude)
                List<String> currentLocation =
                        (ArrayList<String>) getIntent().getSerializableExtra("mmu.ac.hygienerating.CURRENT_LOCATION");


                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.mapbox_marker_icon_default);
                Objects.requireNonNull(mapboxMap.getStyle()).addImage("my-marker", bm);


                // Set up a SymbolManager instance for food locations
                final SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);
                symbolManager.setIconAllowOverlap(true);
                symbolManager.setTextAllowOverlap(true);

                // Set up a SymbolManager instance for current location
                symbolManagerCurrentLocation = new SymbolManager(mapView, mapboxMap, style);
                symbolManagerCurrentLocation.setIconAllowOverlap(true);
                symbolManagerCurrentLocation.setTextAllowOverlap(true);


                //Set camera position to current location
                assert currentLocation != null;
                mapboxMap.setCameraPosition(
                        new CameraPosition.Builder()
                                .target(new LatLng(Double.parseDouble(currentLocation.get(0)), Double.parseDouble((currentLocation.get(1)))))
                                .zoom(12.0)
                                .build()
                );

                //Add current location marker
                SymbolOptions symbolOptions = new SymbolOptions()
                        .withLatLng(new LatLng(Double.parseDouble(currentLocation.get(0)), Double.parseDouble((currentLocation.get(1)))))
                        .withIconImage(ICON_CURRENT)
                        .withIconSize(1.3f)
                        .withSymbolSortKey(10.0f);
                symbol = symbolManagerCurrentLocation.create(symbolOptions);

                // Add click listener and change the symbol to a cafe icon on click
                symbolManagerCurrentLocation.addClickListener(new OnSymbolClickListener() {
                    @Override
                    public void onAnnotationClick(Symbol symbol) {
                        Toast.makeText(MapSearch.this,
                                "Current Location", Toast.LENGTH_SHORT).show();
                        symbolManagerCurrentLocation.update(symbol);
                    }
                });

                //Iterate through each location and make adjustment to Longitude if both
                //Latitude and Longitude are roughly the same as another location.
                //This prevents map markers stacking on top of each other.
                assert locationData != null;
                for (int i = 0; i < locationData.size(); i++) {
                    for (int j = i + 1; j < locationData.size(); j++) {

                        String iLat = locationData.get(i).getLatitude().substring(0, 5);
                        String iLon = locationData.get(i).getLongitude().substring(0, 5);
                        String jLat = locationData.get(j).getLatitude().substring(0, 5);
                        String jLon = locationData.get(j).getLongitude().substring(0, 5);

                        if ((iLat.equals(jLat)) && (iLon.equals(jLon))) {
                            double adjustedLon = Double.parseDouble(locationData.get(i).getLongitude()) - 0.00015;
                            locationData.get(i).setLongitude(Double.toString(adjustedLon));
                        }
                    }
                }

                //Add markers to the map, assign name to each marker
                final List<SymbolOptions> symbolOptionsList = new ArrayList<>();
                for (int i = 0; i < locationData.size(); i++) {
                    assert locationDetails != null;
                    symbolOptionsList.add(new SymbolOptions()
                            .withLatLng(new LatLng(Double.parseDouble(locationData.get(i).getLatitude()), Double.parseDouble(locationData.get(i).getLongitude())))
                            .withIconImage(MAKI_ICON_LOCATIONS)
                            .withIconSize(1.5f)
                            .withTextField(locationDetails.get(i).getHeader() + "\nHygiene Rating: " + locationDetails.get(i).getRating())
                            .withTextOpacity(0f)
                            .withDraggable(false));
                    symbolManager.create(symbolOptionsList);
                }

                //Set click listener to show name of business on click
                symbolManager.addClickListener(new OnSymbolClickListener() {
                    @Override
                    public void onAnnotationClick(Symbol symbol) {
                        Toast.makeText(MapSearch.this, symbol.getTextField(), Toast.LENGTH_SHORT).show();
                        symbolManager.update(symbol);
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}