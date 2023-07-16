package com.example.currentlocationfeatcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    EditText edt_country, edt_city, edt_pincode, edt_address, edt_latitude, edt_longitude;
    Button btn_get_location, btn_get_map;
    private  final  static int REQUEST_CODE=100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt_country=findViewById(R.id.edt_country);
        edt_city=findViewById(R.id.edt_city);
        edt_pincode=findViewById(R.id.edt_pincode);
        edt_address=findViewById(R.id.edt_address);
        edt_latitude=findViewById(R.id.edt_latitude);
        edt_longitude=findViewById(R.id.edt_longitude);
        btn_get_location=findViewById(R.id.btn_get_location);
        btn_get_map=findViewById(R.id.btn_get_map);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        btn_get_location.setOnClickListener(view -> getLastLocation());

        btn_get_map.setOnClickListener(view -> getMap());
    }

    private void getMap() {
        String latitude = edt_latitude.getText().toString().trim();
        String longgitude = edt_longitude.getText().toString().trim();
        if(!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longgitude))
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + Double.parseDouble(latitude)  + ">,<" + Double.parseDouble(longgitude) + ">?q=<" + Double.parseDouble(latitude)  + ">,<" + Double.parseDouble(longgitude) + ">(" + "Yoy are here" + ")"));
            startActivity(intent);

        }
        else {
            Toast.makeText(this, "Address is not valid, enter correct address/fetch again. ", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location !=null){
                            Geocoder geocoder=new Geocoder(MainActivity.this, Locale.getDefault());
                            List<Address> addresses;
                            try {
                                addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                if(addresses!=null)
                                {
                                    edt_country.setText(addresses.get(0).getCountryName());
                                    edt_city.setText(addresses.get(0).getLocality());
                                    edt_pincode.setText(addresses.get(0).getPostalCode());
                                    edt_address.setText(addresses.get(0).getAddressLine(0));
                                    edt_latitude.setText(String.valueOf(addresses.get(0).getLatitude()));
                                    edt_longitude.setText(String.valueOf(addresses.get(0).getLongitude()));
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Unable to getting address try again.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    });
        }else
        {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}