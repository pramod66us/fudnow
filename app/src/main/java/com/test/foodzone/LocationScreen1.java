package com.test.foodzone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


public class LocationScreen1 extends Activity implements OnClickListener {

    //String[] mobileArray = {"Mysore","Hyderabad","Pune"};

    //private ListView locationsList;
    private TextView locationmsg;
    private Spinner LocationList;

    ArrayList<LocationDetails> locDetails=new ArrayList<LocationDetails>();

    //LocationDetails locDetails=new LocationDetails();

    //private ListView foodCourts;
    private TextView clickHere;
    private ProgressDialog progressDialog;

    private int loopCount = 0;
    private String[] foodCourtsList;
    private String[] foodCourtsListIds;
    private JSONObject locListObj;
    private String reqlocation;

    AlertDialogManager alert = new AlertDialogManager();

    private Button profileBtn, homeBtn, exitBtn;

    int dayOfWeek;
    List<String> Locations = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_screen);

        locationmsg=(TextView) findViewById(R.id.location_msg);


        LocationList=(Spinner) findViewById(R.id.locationspinner);

        Locations.add("Select");

        getLocationList();

        //ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.location_screen, mobileArray)

        //locationsList = (ListView) findViewById(R.id.locations);
        //locationsList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_view_style, mobileArray));

        // Spinner Drop down elements

        /*Locations.add("Select");
        Locations.add("Mysore");
        Locations.add("Hyderabad_SEZ");
        Locations.add("Hyderabad_STP");
        Locations.add("Pune_SEZ");
        Locations.add("Pune_STP");*/

       LocationList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                String selectedLocID;

                for (int i=0;i<locDetails.size();i++){
                    if(selectedItem.equals(locDetails.get(i).getLocationName())) {
                        selectedLocID = locDetails.get(i).getLocationID();
                        Intent in = new Intent(getApplicationContext(), FoodCourts.class);
                        in.putExtra("requestLocation", selectedLocID);
                        startActivity(in);
                    }
                }

                /*if (selectedItem.equals("Mysore")) {
                    Intent in = new Intent(getApplicationContext(), FoodCourts.class);
                    in.putExtra("requestLocation", selectedLocID);
                    startActivity(in);
                    // do your stuff
                } else if (selectedItem.equals("Hyderabad_SEZ")) {

                    Intent in = new Intent(getApplicationContext(), FoodCourts.class);
                    in.putExtra("requestLocation", "HYD01");
                    startActivity(in);
                    Toast.makeText(getApplicationContext(), "Serving soon at this location. Please come back later!!!", Toast.LENGTH_SHORT).show();

                } else if (selectedItem.equals("Hyderabad_STP")) {

                    Intent in = new Intent(getApplicationContext(), FoodCourts.class);
                    in.putExtra("requestLocation", "HYD02");
                    startActivity(in);

                } else*/ if (selectedItem.equals("Select")) {

                } /*else

                {
                    //Toast(getApplicationContext(),)
                    Toast.makeText(getApplicationContext(), "Serving soon at this location. Please come back later!!!", Toast.LENGTH_SHORT).show();
                } // to close the onItemSelected*/

            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    @Override
    public void onClick(View v) {

    }

    private void getLocationList() {
        //showProgressDialog();
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        Log.i("getFoodCourtList", "http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getLocationList");
        client.get("http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getLocationList", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    //cancelProgressDialog();
                    locListObj = new JSONObject(new String(responseBody));

                    Log.i("Location Count","Count"+locListObj.length());

                    for(int i=0; i<locListObj.length(); i++) {

                        //JSONArray temp = locListObj.getJSONArray(Integer.toString(i));
                        JSONObject temp = locListObj.getJSONObject(Integer.toString(i));
                        Log.i("JSON Array","JSON Array"+temp.toString());
                        //Locations.add(temp.getString(0));
                        LocationDetails temp1=new LocationDetails();
                        Log.i("ID","ID: "+temp.getString("locID"));
                        Log.i("name","name: "+temp.getString("locName"));
                        temp1.setItemID(temp.getString("locID"));
                        temp1.setItemName(temp.getString("locName"));
                        Locations.add(temp.getString("locName"));
                        locDetails.add(temp1);
                    }


                    //foodCourts.setAdapter(new ArrayAdapter<String>(LocationScreen1.this, R.layout.list_view_style, foodCourtsList));

                        // Creating adapter for spinner
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(LocationScreen1.this, R.layout.spinner_text, Locations);

                        // Drop down layout style - list view with radio button
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        // attaching data adapter to spinner
                        LocationList.setAdapter(dataAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    alert.showAlertDialog(LocationScreen1.this, "Message", "Please check the network connection.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    //cancelProgressDialog();
                    alert.showAlertDialog(LocationScreen1.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    //cancelProgressDialog();
                    alert.showAlertDialog(LocationScreen1.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                } else {
                    //cancelProgressDialog();
                    alert.showAlertDialog(LocationScreen1.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]" + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showProgressDialog() {
        progressDialog.setMessage("Loading...!");
        progressDialog.show();
    }

    private void cancelProgressDialog() {
        progressDialog.cancel();
        progressDialog.dismiss();
    }
}


