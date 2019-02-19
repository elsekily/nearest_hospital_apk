package com.example.yousif.icu_hospitals;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    //https://hospitals-icu.herokuapp.com/hospital/?lat=31.22646326853553&long=29.93881165981293
    final String user_location_url = "http://hospitals-icu.herokuapp.com/hospital/";

    String latitude;
    String longitude;

    String nameResult;
    String addressResult;
    double longResult;
    double latResult;

    RequestParams params = new RequestParams();

    TextView hosp;
    Button navi;

    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        getCurrentLocation();
        hosp = (TextView) findViewById(R.id.hospital);
        navi = (Button) findViewById(R.id.Nav);

    }

    private void getCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Gps Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Gps Disabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void findnear(View v) {
        hosp.setText("Hello");
        navi.setVisibility(View.INVISIBLE);
        getCurrentLocation();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        try
        {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLatitude());
            Toast.makeText(MainActivity.this, latitude+","+longitude, Toast.LENGTH_SHORT).show();
            params.put("lat", latitude);
            params.put("long", longitude);
            search_for_a_hospital(params);
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this, "Searching For Current Location", Toast.LENGTH_SHORT).show();
            getCurrentLocation();
        }
    }

    private void search_for_a_hospital(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(user_location_url,params ,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response)
            {
                try{
                    nameResult=response.getString("h_name");
                    addressResult = response.getString("address");
                    longResult = response.getDouble("long");
                    latResult = response.getDouble("lati");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Request Success", Toast.LENGTH_SHORT).show();

                hosp.setText(nameResult + "\n" + addressResult);
                navi.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response)
            {
                Toast.makeText(MainActivity.this, "Request Failed", Toast.LENGTH_SHORT).show();
                hosp.setText("fail");
            }
        });
    }
}

