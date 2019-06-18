package org.quicksplit.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class PurchaseMapActivity extends AppCompatActivity implements OnMapReadyCallback {


    private static final float DEFAULT_ZOOM = 15f;

    private GoogleMap mMap;
    private Bundle myBundle;
    private double mLongitude;
    private double mLatitude;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myBundle = this.getIntent().getExtras();
        mLongitude = myBundle.getDouble("Longitude");
        mLatitude = myBundle.getDouble("Latitude");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude), DEFAULT_ZOOM));
    }

}
