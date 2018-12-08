package com.test.foodzone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;
ewkjefjkwefjwjfkwjf

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Iterator;


public class FoodCourts extends Activity implements OnClickListener {

    private ListView foodCourts;
    private TextView clickHere;
    private ProgressDialog progressDialog;

    private int loopCount = 0;
    private String[] foodCourtsList;
    private String[] foodCourtsListIds;
    private JSONObject foodCourtsListObj;
    private String reqlocation;

	AlertDialogManager alert = new AlertDialogManager();
	
    private Button profileBtn, homeBtn, exitBtn;
    
    int dayOfWeek;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_courts);
        
        Calendar c = Calendar.getInstance();
        dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        
        System.out.println("dayOfWeek: " + dayOfWeek);

        progressDialog = new ProgressDialog(this);
        foodCourtsListObj = new JSONObject();

        reqlocation=getIntent().getStringExtra("requestLocation");

        foodCourts = (ListView) findViewById(R.id.foodCourts);
        foodCourts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent floorScreen = new Intent(FoodCourts.this, Floors.class);
                floorScreen.putExtra("foodcourtname", foodCourtsList[position]);
                floorScreen.putExtra("foodcourtid", foodCourtsListIds[position]);
                floorScreen.putExtra("requestCategory", "cust");
                floorScreen.putExtra("requestLocation",reqlocation);
                startActivity(floorScreen);
            }
        });

        clickHere = (TextView) findViewById(R.id.admin_login);
        clickHere.setOnClickListener(this);
        
        profileBtn = (Button) findViewById(R.id.profile_button);
        profileBtn.setOnClickListener(this);
        
        homeBtn = (Button) findViewById(R.id.home_button);
        homeBtn.setOnClickListener(this);
        
        exitBtn = (Button) findViewById(R.id.exit_button);
        exitBtn.setOnClickListener(this);
        
        //profileBtn.setVisibility(View.GONE);

        getFoodCourtsList();
        
    }

    @Override
	public void onClick(View v) {
    	
    	if(v == clickHere) {
    		Intent in = new Intent(getApplicationContext(), HomeScreen.class);
            in.putExtra("requestLocation",reqlocation);
            startActivity(in);
            finish();
    	}
    	
    	else if(v == profileBtn) {
    		Intent in = new Intent(getApplicationContext(), HomeScreen.class);
            in.putExtra("requestLocation",reqlocation);
            startActivity(in);
            finish();
    	}
    	
    	else if(v == homeBtn) {
    		Intent in = new Intent(getApplicationContext(), FoodCourts.class);
            in.putExtra("requestLocation",reqlocation);
            startActivity(in);
            finish();
    	}
    	
    	else if(v == exitBtn) {
    		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Exit Application?");
            alertDialogBuilder
                    .setMessage("Click yes to exit!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    moveTaskToBack(true);
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                }
                            })

                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
    	}
	}

    private void getFoodCourtsList() {
        showProgressDialog("Requesting food courts. Please wait!");
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        Log.i("getFoodCourtList", "http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getFoodCourtsList/" + (dayOfWeek - 1) + "/" + reqlocation);
        client.get("http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getFoodCourtsList/" + (dayOfWeek - 1) + "/" + reqlocation, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    cancelProgressDialog();
                    foodCourtsListObj = new JSONObject(new String(responseBody));

                    System.out.println("foodCourtsListObj: " + foodCourtsListObj);

                    if(foodCourtsListObj.has("status")){
                        Toast.makeText(getApplicationContext(),"Food courts coming up soon. Try later!!!",Toast.LENGTH_LONG).show();
                    }else if (foodCourtsListObj.has("error")) {
                        Toast.makeText(getApplicationContext(), foodCourtsListObj.getString("error"), Toast.LENGTH_LONG).show();
                    } else {
                        Iterator<String> keys = foodCourtsListObj.keys();
                        foodCourtsListIds = new String[foodCourtsListObj.length()];
                        foodCourtsList = new String[foodCourtsListObj.length()];

                        while (keys.hasNext()) {
                            String[] splitData = foodCourtsListObj.get(keys.next()).toString().split("/");
                            foodCourtsListIds[loopCount] = splitData[1];
                            foodCourtsList[loopCount] = splitData[0];
                            loopCount++;
                        }

                        Integer[] imageId = {
                                R.drawable.arrow_right
                        };

                        foodCourts.setAdapter(new ArrayAdapter<String>(FoodCourts.this, R.layout.list_view_style, foodCourtsList));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    alert.showAlertDialog(FoodCourts.this, "Message", "Please check the network connection.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    cancelProgressDialog();
                    alert.showAlertDialog(FoodCourts.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    cancelProgressDialog();
                    alert.showAlertDialog(FoodCourts.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                } else {
                    cancelProgressDialog();
                    alert.showAlertDialog(FoodCourts.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]" + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void cancelProgressDialog() {
        progressDialog.cancel();
        progressDialog.dismiss();
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}


