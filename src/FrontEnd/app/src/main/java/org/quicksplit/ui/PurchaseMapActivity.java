package org.quicksplit.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.quicksplit.R;

public class PurchaseMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final float DEFAULT_ZOOM = 15f;

    private GoogleMap mMap;
    private Bundle myBundle;
    private double mLongitude;
    private double mLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_map);
        initMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myBundle = this.getIntent().getExtras();
        mLongitude = myBundle.getDouble("longitude");
        mLatitude = myBundle.getDouble("latitude");
        moveCamera((new LatLng(mLatitude, mLongitude)), DEFAULT_ZOOM,"");
    }

    private void moveCamera(LatLng latlng, float zoom, String title){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));

        if(!title.equals("MyLocation")){
            MarkerOptions options = new MarkerOptions()
                    .position(latlng)
                    .title(title);
            mMap.addMarker(options);
        }
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(PurchaseMapActivity.this);
    }

}