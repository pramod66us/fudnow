package com.test.foodzone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

public class DashBoard extends Activity implements OnClickListener {

    private ListView menuCategories;
    private ProgressDialog progressDialog;

    private int loopCount = 0;
    private String[] menuCategoriesList;
    private String[] menuCategoriesListIds;
    private JSONObject menuCategoriesListObj;
    
    private Button profileBtn, homeBtn, exitBtn;
    private ImageView goBack;
	AlertDialogManager alert = new AlertDialogManager();
    String reqLocation;
    String foodcourtid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dash_board);

        foodcourtid = getIntent().getStringExtra("foodcourtid");
        reqLocation=getIntent().getStringExtra("requestLocation");

		goBack = (ImageView) findViewById(R.id.goBack);
        goBack.setOnClickListener(this);

        profileBtn = (Button) findViewById(R.id.profile_button);
        profileBtn.setOnClickListener(this);

        homeBtn = (Button) findViewById(R.id.home_button);
        homeBtn.setOnClickListener(this);

        exitBtn = (Button) findViewById(R.id.exit_button);
        exitBtn.setOnClickListener(this);

        if(getIntent().getStringExtra("requestCategory").equals("cust")) {
        	profileBtn.setVisibility(View.GONE);
        }

        progressDialog = new ProgressDialog(this);
        menuCategoriesListObj = new JSONObject();

        menuCategories = (ListView) findViewById(R.id.menuCategories);
        menuCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent itemsIntent = new Intent(DashBoard.this, TabBar.class);
                itemsIntent.putExtra("foodcourtid", getIntent().getStringExtra("foodcourtid"));
                itemsIntent.putExtra("restaurantid", getIntent().getStringExtra("vendorName"));
                itemsIntent.putExtra("categoryid", menuCategoriesListIds[position]);
                itemsIntent.putExtra("requestCategory", getIntent().getStringExtra("requestCategory"));
                itemsIntent.putExtra("requestLocation",reqLocation);
                startActivity(itemsIntent);
            }
        });

        if(getIntent().getStringExtra("requestCategory").equals("cust")) {
        	getMenuCategoriesListOnTime();
        } else {
        	getMenuCategoriesList();
        }



	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if(v == goBack) {
			finish();
    	}

		else if(v == profileBtn) {
    		Intent in = new Intent(getApplicationContext(), HomeScreen.class);
            in.putExtra("requestLocation",reqLocation);
            startActivity(in);
            finish();
    	}

    	else if(v == homeBtn) {
    		Intent in = new Intent(getApplicationContext(), FoodCourts.class);
            in.putExtra("requestLocation",reqLocation);
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

	private void getMenuCategoriesListOnTime() {
        showProgressDialog("Requesting menu categories. Please wait!");
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();

        Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    	System.out.println(sdf.format(cal.getTime()));

        Log.i("Menu Time","http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getMenuCategoriesOnTime/"+getIntent().getStringExtra("vendorName").trim() + "/" + sdf.format(cal.getTime())+"/"+getIntent().getStringExtra("requestLocation").trim());
        client.get("http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getMenuCategoriesOnTime/"+getIntent().getStringExtra("vendorName").trim() + "/" + sdf.format(cal.getTime())+"/"+getIntent().getStringExtra("requestLocation").trim(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    cancelProgressDialog();
                    menuCategoriesListObj = new JSONObject(new String(responseBody));
                    System.out.println("-->" + menuCategoriesListObj.toString());

                    if(menuCategoriesListObj.length() == 0) {
                    	cancelProgressDialog();
        				alert.showAlertDialog(DashBoard.this, "Message", "Serving your appetite soon...", false);
                        //finishActivity(0);
                        //super.onBackPressed();
                        //finish();
                        //Toast.makeText(getApplicationContext(), "Serving your appetite soon..", Toast.LENGTH_LONG).show();
                    } else {

                    	if(menuCategoriesListObj.has("error")) {
	                        cancelProgressDialog();
	                        Toast.makeText(getApplicationContext(), menuCategoriesListObj.getString("error"), Toast.LENGTH_LONG).show();
	                    } else {
	                        Iterator<String> keys = menuCategoriesListObj.keys();
	                        menuCategoriesListIds = new String[menuCategoriesListObj.length()];
	                        menuCategoriesList = new String[menuCategoriesListObj.length()];
	                        while(keys.hasNext()) {
	                            String[] splitData = menuCategoriesListObj.get(keys.next()).toString().split("/");
                                String[] splitData1=splitData[2].toString().split(",");
	                            menuCategoriesListIds[loopCount] = splitData[1];
                                Integer newStartTime=0;
                                newStartTime=Integer.parseInt(splitData1[0].substring(11,16).substring(0,2).toString())+1;
	                            menuCategoriesList[loopCount] = splitData[0] + "   (" + newStartTime + ":00 -" +splitData1[1].substring(11, 16)+")" ;
                                Log.i("Test String","HI: "+newStartTime);
	                            loopCount++;

	                        }
	                        menuCategories.setAdapter(new ArrayAdapter<String>(DashBoard.this, R.layout.list_view_style, menuCategoriesList));
	                    }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
    				alert.showAlertDialog(DashBoard.this, "Message", "Please check the network connections.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    cancelProgressDialog();
    				alert.showAlertDialog(DashBoard.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    cancelProgressDialog();
    				alert.showAlertDialog(DashBoard.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                } else {
                    cancelProgressDialog();
    				alert.showAlertDialog(DashBoard.this, "Message", "Please check the network connection.", false);
//                    /Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]" + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getMenuCategoriesList() {
        showProgressDialog("Requesting menu categories. Please wait!");
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        Log.i("Menu URL", "http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getMenuCategories/" + getIntent().getStringExtra("vendorName").trim() + "/"+getIntent().getStringExtra("requestLocation").trim());
        client.get("http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getMenuCategories/"+getIntent().getStringExtra("vendorName").trim()+"/"+getIntent().getStringExtra("requestLocation").trim(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    cancelProgressDialog();
                    menuCategoriesListObj = new JSONObject(new String(responseBody));
                    System.out.println(menuCategoriesListObj.toString());
                    if(menuCategoriesListObj.has("error")) {
                        cancelProgressDialog();
                        Toast.makeText(getApplicationContext(), menuCategoriesListObj.getString("error"), Toast.LENGTH_LONG).show();
                    } else {
                        Iterator<String> keys = menuCategoriesListObj.keys();
                        menuCategoriesListIds = new String[menuCategoriesListObj.length()];
                        menuCategoriesList = new String[menuCategoriesListObj.length()];
                        while(keys.hasNext()) {
                            String[] splitData = menuCategoriesListObj.get(keys.next()).toString().split("/");
                            String[] splitData1=splitData[2].toString().split(",");
                            menuCategoriesListIds[loopCount] = splitData[1];
                            if(splitData[0].equals("Special Items"))
                                menuCategoriesList[loopCount] = splitData[0];
                            else
                                menuCategoriesList[loopCount] = splitData[0] +"\u00A0 \u00A0 (" + splitData1[0].substring(0, 5) + "-" + splitData1[1].substring(0,5) + ")";
                            loopCount++;

                        }
                        menuCategories.setAdapter(new ArrayAdapter<String>(DashBoard.this, android.R.layout.simple_list_item_1, menuCategoriesList));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
    				alert.showAlertDialog(DashBoard.this, "Message", "Please check the network connections.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    cancelProgressDialog();
    				alert.showAlertDialog(DashBoard.this, "Message", "Please check the network connections.", false);
                    //Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    cancelProgressDialog();
    				alert.showAlertDialog(DashBoard.this, "Message", "Please check the network connections.", false);
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                } else {
                    cancelProgressDialog();
    				alert.showAlertDialog(DashBoard.this, "Message", "Please check the network connections.", false);
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
		getMenuInflater().inflate(R.menu.dash_board, menu);
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
