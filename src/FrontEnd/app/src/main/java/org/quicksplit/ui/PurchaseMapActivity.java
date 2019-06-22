package org.quicksplit.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class PurchaseMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    protected GoogleApiClient mGoogleApiClient;
    private static final float DEFAULT_ZOOM = 15f;

    private GoogleMap mMap;
    private Bundle myBundle;
    private Double mLongitude;
    private Double mLatitude;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myBundle = this.getIntent().getExtras();
        mLongitude = myBundle.getDouble("longitude");
        mLatitude = myBundle.getDouble("latitude");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        moveMap();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap != null) {
            moveMap();
        }
    }

    public void moveMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude), DEFAULT_ZOOM));
    }
}
