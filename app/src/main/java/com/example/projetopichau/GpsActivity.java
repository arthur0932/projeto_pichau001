package com.example.projetopichau;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class GpsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView textLatitude, textLongitude, textStatus;
    private LocationManager locationManager;
    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        textLatitude = findViewById(R.id.textLatitude);
        textLongitude = findViewById(R.id.textLongitude);
        textStatus = findViewById(R.id.textStatus);
        Button btnGetLocation = findViewById(R.id.btnGetLocation);
        Button btnFinish = findViewById(R.id.btnFinish);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btnGetLocation.setOnClickListener(v -> checkPermissionAndGetLocation());
        btnFinish.setOnClickListener(v -> {
            Toast.makeText(this, "Pedido finalizado! Obrigado pela compra.", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        
        // Habilitar o ponto azul de localização se tiver permissão
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void checkPermissionAndGetLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }

        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
        
        textStatus.setText("Status: Buscando...");

        // Tentar obter a última localização conhecida imediatamente
        Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnown == null) {
            lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        
        if (lastKnown != null) {
            updateUI(lastKnown);
        }

        // Solicitar atualizações contínuas
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            updateUI(location);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            Toast.makeText(GpsActivity.this, "Por favor, ative o " + provider, Toast.LENGTH_SHORT).show();
        }
    };

    private void updateUI(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        textLatitude.setText("Latitude: " + lat);
        textLongitude.setText("Longitude: " + lng);
        textStatus.setText("Status: Localizado");

        if (mMap != null) {
            LatLng userPos = new LatLng(lat, lng);
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userPos).title("Sua Localização"));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userPos, 16f));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissionAndGetLocation();
            } else {
                Toast.makeText(this, "Permissão de GPS negada", Toast.LENGTH_SHORT).show();
            }
        }
    }
}