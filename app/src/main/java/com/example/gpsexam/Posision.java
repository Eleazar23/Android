package com.example.gpsexam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.MessageFormat;
import java.util.ArrayList;

public class Posision extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private static final int ALL_PERMISSIONS_RESULT = 1111 ;
    //Instancias de la API de Google
    private  GoogleApiClient client;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissions = new ArrayList<>();
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private TextView locationTextview;

    private LocationRequest locationRequest;

    //Tiempos para la actualizacion
    public static final long UPDATE_INTERVAL = 5000;
    public static final long FASTEST_INTERVAL = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posision);

        locationTextview = findViewById(R.id.location_text_view);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(Posision.this);

        //Permisos necesarios para conocer la ubicacion del usuario
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            permissionsToRequest = permissionsToRequest(permissions);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (permissionsToRequest.size()>0){
                    requestPermissions(permissionsToRequest.toArray(
                            new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT
                            );
                }
            }

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        //Obteniendo todos los permisos
        for (String perm : wantedPermissions){
            if (!hasPermission(perm)){
                result.add(perm);
            }
        }
        return result;
    }

    private  void checkPlayServices(){
        int errorCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS){
            Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, errorCode, errorCode, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Toast.makeText(Posision.this, "Sin servicio", Toast.LENGTH_LONG).show();
                }
            });
            errorDialog.show();
            finish();
        }else{
            Toast.makeText(Posision.this, "Todo esta bien", Toast.LENGTH_LONG).show();
        }
    }

    //Verificacion de permisos de acuerdo a la version de android
    private boolean hasPermission(String perm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
         return    checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }



    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            locationTextview.setText(MessageFormat.format("Lat: {0}Long:{1}", location.getLatitude(),
                    location.getLongitude()));
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //Obtiene la ubicacion pero puede ser null
                        if (location != null){
                            locationTextview.setText("Lat:" + location.getLatitude()
                                    + "Lon:" + location.getLongitude());
                        }
                    }
                });

        startLocationUpdates();
    }

    //Obteniendo la ubicacion
    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL); //Cada 5 segundos obtendra una ubicacion
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
        Toast.makeText(Posision.this, "Necesitas dar permisios para mostrar tu ubicacion", Toast.LENGTH_LONG).show();

        }

        LocationServices.getFusedLocationProviderClient(Posision.this).requestLocationUpdates(locationRequest, new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult != null){
                    Location location = locationResult.getLastLocation();
                    locationTextview.setText("Lat:"+ location.getLatitude()+ "Lon: " + location.getLongitude() );
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        }, null);




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case  ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest){
                    if (!hasPermission(perm)){
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size()>0){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))){
                            new AlertDialog.Builder(Posision.this)
                                    .setMessage("Este permiso es obligatorio")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();
                        }
                    }
                } else {
                    if (client != null){
                        client.connect();
                    }
                }
                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (client != null){
            client.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        client.disconnect();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        checkPlayServices();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (client != null && client.isConnected()){
            LocationServices.getFusedLocationProviderClient(this)
            .removeLocationUpdates(new LocationCallback(){});
            client.disconnect();

        }
    }
}
