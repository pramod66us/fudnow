package com.test.foodzone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class Floors extends Activity implements OnClickListener {

    private int loopCount = 0;

    private String foodcourtid = "";

    private String[] floorsList;
    private String[] floorsListIds;
    private JSONObject floorsListObj;
    
    private Button profileBtn, homeBtn, exitBtn;
    private ImageView goBack;
	AlertDialogManager alert = new AlertDialogManager();

    private TextView foodCourtName;
    private ListView floorList;
    private ProgressDialog progressDialog;
    private String reqlocation;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floors);

        goBack = (ImageView) findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        
        homeBtn = (Button) findViewById(R.id.home_button);
        homeBtn.setOnClickListener(this);
        
        profileBtn = (Button) findViewById(R.id.profile_button);
        profileBtn.setOnClickListener(this);
        
        exitBtn = (Button) findViewById(R.id.exit_button);
        exitBtn.setOnClickListener(this);
        
        if(getIntent().getStringExtra("requestCategory").equals("cust")) {
        	profileBtn.setVisibility(View.GONE);
        }

        foodCourtName = (TextView) findViewById(R.id.fc_name);
        foodCourtName.setText(getIntent().getStringExtra("foodcourtname"));
        reqlocation=getIntent().getStringExtra("requestLocation");
        foodcourtid = getIntent().getStringExtra("foodcourtid");
        progressDialog = new ProgressDialog(this);


        floorList = (ListView) findViewById(R.id.floors);
        floorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent dashboard = new Intent(Floors.this, DashBoard.class);
                dashboard.putExtra("vendorName", floorsListIds[position]);
                dashboard.putExtra("foodcourtid", foodcourtid);
                dashboard.putExtra("requestCategory", "cust");
                dashboard.putExtra("requestLocation", reqlocation);
                startActivity(dashboard);
            }
        });
        
        getFloorsList();

        
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(v == goBack) {
			finish();
    	}
		
		else if(v == profileBtn) {
    		Intent in = new Intent(getApplicationContext(), HomeScreen.class);
            startActivity(in);
            finish();
    	}
    	
    	else if(v == homeBtn) {
    		Intent in = new Intent(getApplicationContext(), FoodCourts.class);
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


    private void getFloorsList() {
        showProgressDialog("Requesting food courts. Please wait!");
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        Log.i("Test","http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getFloorList/"+foodcourtid+"/"+reqlocation);
        client.get("http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getFloorList/"+foodcourtid+"/"+reqlocation, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    cancelProgressDialog();
                    floorsListObj = new JSONObject(new String(responseBody));
                    if(floorsListObj.has("error")) {
                        Toast.makeText(getApplicationContext(), floorsListObj.getString("error"), Toast.LENGTH_LONG).show();
                    } else {
                        Iterator<String> keys = floorsListObj.keys();
                        floorsListIds = new String[floorsListObj.length()];
                        floorsList = new String[floorsListObj.length()];
                        while(keys.hasNext()) {
//                            JSONArray foodCourtListArr = foodCourtsListObj.getJSONArray(keys.next().toString());
                            String[] splitData = floorsListObj.get(keys.next()).toString().split("/");
                            floorsListIds[loopCount] = splitData[0];
                            floorsList[loopCount] = splitData[1] + " - "+ splitData[2].toString();
                            loopCount++;
                        }
                        floorList.setAdapter(new ArrayAdapter<String>(Floors.this, R.layout.list_view_style, floorsList));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
    				alert.showAlertDialog(Floors.this, "Message", "Please check the network connection.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    cancelProgressDialog();
    				alert.showAlertDialog(Floors.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    cancelProgressDialog();
    				alert.showAlertDialog(Floors.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                } else {
                    cancelProgressDialog();
    				alert.showAlertDialog(Floors.this, "Message", "Please check the network connection.", false);
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


