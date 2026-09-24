package com.example.projetopichau;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class GpsActivity extends AppCompatActivity {

    private WebView webViewMapa;
    private LocationManager locationManager;

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        webViewMapa = findViewById(R.id.webViewMapa);

        Button btnGetLocation = findViewById(R.id.btnGetLocation);
        Button btnFinish = findViewById(R.id.btnFinish);

        // Configuração do WebView
        WebSettings settings = webViewMapa.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        webViewMapa.setWebViewClient(new WebViewClient());

        // Abre o mapa.html
        webViewMapa.loadUrl("file:///android_asset/mapa.html");

        // Botão para pegar localização
        btnGetLocation.setOnClickListener(v -> checkPermissionAndGetLocation());

        // Finalizar pedido
        btnFinish.setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "Pedido finalizado! Obrigado pela compra.",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        });

        locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void checkPermissionAndGetLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE
            );

            return;
        }

        boolean gpsAtivo =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        boolean redeAtiva =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsAtivo && !redeAtiva) {
            Toast.makeText(
                    this,
                    "Ative o GPS do celular.",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        // Tenta pegar localização conhecida imediatamente
        Location ultimaLocalizacao = null;

        if (gpsAtivo) {
            ultimaLocalizacao =
                    locationManager.getLastKnownLocation(
                            LocationManager.GPS_PROVIDER
                    );
        }

        if (ultimaLocalizacao == null && redeAtiva) {
            ultimaLocalizacao =
                    locationManager.getLastKnownLocation(
                            LocationManager.NETWORK_PROVIDER
                    );
        }

        if (ultimaLocalizacao != null) {
            enviarLocalizacaoParaMapa(ultimaLocalizacao);
        }

        // Começa a acompanhar a localização
        if (gpsAtivo) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    2000,
                    2,
                    locationListener
            );
        }

        if (redeAtiva) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    2000,
                    2,
                    locationListener
            );
        }
    }

    private final LocationListener locationListener =
            new LocationListener() {

                @Override
                public void onLocationChanged(@NonNull Location location) {
                    enviarLocalizacaoParaMapa(location);
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) {
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {
                }
            };

    private void enviarLocalizacaoParaMapa(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        String javascript =
                "updateLocation(" +
                        latitude +
                        "," +
                        longitude +
                        ");";

        webViewMapa.evaluateJavascript(
                "updateLocation(-23.6267, -46.6718);",
                null
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                checkPermissionAndGetLocation();

            } else {

                Toast.makeText(
                        this,
                        "Permissão de localização negada.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {

                locationManager.removeUpdates(locationListener);
            }
        }
    }
}