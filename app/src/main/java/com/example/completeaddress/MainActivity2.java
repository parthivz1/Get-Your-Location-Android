package com.example.completeaddress;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView country,city,address,longitude,latitude;
    Button getLocation;
    private  final  static int REQUEST_CODE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        country=findViewById(R.id.country);
        city=findViewById(R.id.city);
        address=findViewById(R.id.address);
        longitude=findViewById(R.id.longitude);
        latitude=findViewById(R.id.lagitude);
        getLocation = findViewById(R.id.get_location_btn);
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
                getBarCode();

            }
        });

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

    }

    private void getBarCode() {
        //ImageView for generated QR code
        ImageView imageCode = findViewById(R.id.bar);

        //textView for generated QR code
        TextView textView = findViewById(R.id.city);

        //getting text from input text field.
        String myText = textView.getText().toString().trim();

        //initializing MultiFormatWriter for QR code
        MultiFormatWriter mWriter = new MultiFormatWriter();

        try {
            //BitMatrix class to encode entered text and set Width &amp; Height
            BitMatrix mMatrix = mWriter.encode(myText, BarcodeFormat.QR_CODE, 400,400);

            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
            imageCode.setImageBitmap(mBitmap);//Setting generated QR code to imageView

            // to hide the keyboard
            InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(textView.getApplicationWindowToken(), 0);

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

           fusedLocationProviderClient.getLastLocation()
                   .addOnSuccessListener(new OnSuccessListener<Location>() {
                       @Override
                       public void onSuccess(Location location) {
                           if (location !=null){
                               Geocoder geocoder=new Geocoder(MainActivity2.this, Locale.getDefault());
                               List<Address> addresses= null;
                               try {
                                   addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                   latitude.setText("Lagitude :" +addresses.get(0).getLatitude());
                                   longitude.setText("Longitude :"+addresses.get(0).getLongitude());
                                   address.setText("Address :"+addresses.get(0).getAddressLine(0));
                                   city.setText("City :"+addresses.get(0).getLocality());
                                   country.setText("Country :"+addresses.get(0).getCountryName());

                               } catch (IOException e) {
                                   e.printStackTrace();
                               }



                           }

                       }
                   });


        }else
        {

            askPermission();

        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(MainActivity2.this, new String[]
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