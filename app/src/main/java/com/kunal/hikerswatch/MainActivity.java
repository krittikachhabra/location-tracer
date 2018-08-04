package com.kunal.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

           startListening();
        }
    }

    public void startListening()
    {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void locationUpdate(Location location)
    {
        //Toast.makeText(getApplication(), location.toString(), Toast.LENGTH_LONG).show();
        TextView latText =(TextView)findViewById(R.id.textView2);
        TextView lonText =(TextView)findViewById(R.id.textView3);
        TextView accText =(TextView)findViewById(R.id.textView4);
        TextView altText =(TextView)findViewById(R.id.textView5);
        latText.setText("Latitude : "+ location.getLatitude());

        lonText.setText("Longitude : "+ location.getLongitude());
        accText.setText("Accuracy : "+ location.getAccuracy());
        altText.setText("Altitude : "+ location.getAltitude());
        if(Geocoder.isPresent()){
        Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            String address ="Could not find address";
            List<Address> listAddress = geoCoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if(listAddress!=null && listAddress.size()>0){
                //Toast.makeText(getApplication(), listAddress.get(0).toString(), Toast.LENGTH_LONG).show();
                address ="Address :";
                if(listAddress.get(0).getSubThoroughfare()!=null){
                    address += listAddress.get(0).getSubThoroughfare() + " ";
                }
                if(listAddress.get(0).getThoroughfare()!=null){
                    address += listAddress.get(0).getThoroughfare() + "\n";
                }
                if(listAddress.get(0).getLocality()!=null){
                    address += listAddress.get(0).getLocality() + "\n";
                }
                if(listAddress.get(0).getPostalCode()!=null){
                    address += listAddress.get(0).getPostalCode() + "\n";
                }
                if(listAddress.get(0).getCountryName()!=null){
                    address += listAddress.get(0).getCountryName() + "\n";
                }
            }
            TextView addressTextView = (TextView)findViewById(R.id.textView6);
            addressTextView.setText(address);
            String data="Longitude : "+ location.getLongitude()+"\nAccuracy : "+ location.getAccuracy()+"\nAltitude : "+ location.getAltitude();
            data=data+"\n"+address;
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL,  new String[] {"kunal1998gupta@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "data");
            intent.putExtra(Intent.EXTRA_TEXT, data);
            //intent.setType("message/rfc822");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent,"send mail...."));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //Toast.makeText(getApplication(), location.toString(), Toast.LENGTH_LONG).show();
                locationUpdate(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(Build.VERSION.SDK_INT<23){
            startListening();
        }else{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null){
                    //Toast.makeText(getApplication(), location.toString(), Toast.LENGTH_LONG).show();
                    locationUpdate(location);
                }
            }
        }
    }
}
