package com.test.foodzone;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.test.custommenu.CustomMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Items extends Activity implements OnClickListener {

    private JSONObject menuDetails;
    private JSONObject itemIds;
    private JSONObject checkedItems;

    private String[] itemsList;
    private String[] itemIDs;
    private String result = "";

    private ListView items;
    private Button btnSubmit;
    
    private Button profileBtn, homeBtn, exitBtn;
    private ImageView goBack;
	AlertDialogManager alert = new AlertDialogManager();

    private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

    private HttpPost httpGet;
    private HttpResponse httpResponse;
    private HttpEntity httpEntity;

    private ProgressDialog progressDialog;

    String foodcourtid, restaurantid, categoryid;

    private DefaultHttpClient httpClient = new DefaultHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items);

        foodcourtid = getIntent().getStringExtra("foodcourtid").trim();
        restaurantid = getIntent().getStringExtra("restaurantid").trim();
        categoryid = getIntent().getStringExtra("categoryid").trim();
        
        /*goBack = (ImageView) findViewById(R.id.goBack);
        goBack.setOnClickListener(this);*/
        
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

        menuDetails = new JSONObject();
        itemIds     = new JSONObject();
        checkedItems     = new JSONObject();

        items       = (ListView) findViewById(R.id.list);
        
        btnSubmit   = (Button) findViewById(R.id.btn_submit);
        if(getIntent().getStringExtra("requestCategory").trim().equalsIgnoreCase("admin")) {
            getItemsList();
            btnSubmit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SparseBooleanArray checkedItems = items.getCheckedItemPositions();
                    if (checkedItems != null) {
                        for (int i=0; i<checkedItems.size(); i++) {
                            if (checkedItems.valueAt(i)) {
                                try {
                                    itemIds.put(itemIDs[checkedItems.keyAt(i)], "added");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    if(itemIds.has(itemIDs[checkedItems.keyAt(i)])) {
                                        itemIds.remove(itemIDs[checkedItems.keyAt(i)]);
                                        itemIds.put(itemIDs[checkedItems.keyAt(i)], "removed");
                                    } else {
                                        itemIds.put(itemIDs[checkedItems.keyAt(i)], "removed");
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    //display(itemIds.toString());
                    new UploadToServer().execute();
                    //Toast.makeText(getApplicationContext(), itemIds.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
        	
            btnSubmit.setVisibility(View.INVISIBLE);
            getTodayMenu();
            
         // Changes the height and width to the specified *pixels*
            items.getLayoutParams().height = LayoutParams.MATCH_PARENT ;
            items.getLayoutParams().width = LayoutParams.MATCH_PARENT;
            items.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					
					Intent in = new Intent(Items.this, FeedBack.class);
					in.putExtra("foodcourtid", foodcourtid);
					in.putExtra("restaurantid", restaurantid);
					in.putExtra("categoryid", categoryid);
					in.putExtra("itemname", ((TextView) view).getText().toString().trim());
					startActivity(in);
					
					//Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
					
				}
			});
            
        }
    }

    private void display(String params) {
        Toast.makeText(Items.this, params, Toast.LENGTH_LONG).show();
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


    private void getItemsList() {
        showProgressDialog("Getting menu. Please wait");
//        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("restaurantid") + "/" + getIntent().getStringExtra("categoryid"), Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://ascianalysis-application3245.rhcloud.com/rest/wtfservice/getRestaurantInfo/" + getIntent().getStringExtra("restaurantid").trim() + "/" + getIntent().getStringExtra("categoryid").trim(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    cancelProgressDialog();
                    menuDetails = new JSONObject(new String(responseBody));
                    itemsList = new String[menuDetails.length()];
                    int k =0;
                    if(menuDetails.has("error")) {
                        Toast.makeText(getApplicationContext(), menuDetails.getString("error"), Toast.LENGTH_LONG).show();
                    } else {
                        itemIDs = new String[menuDetails.length()];
                        for(int i=0; i<menuDetails.length(); i++) {
                            JSONArray temp = menuDetails.getJSONArray(Integer.toString(i));
                            for (int j = 0; j < temp.length(); j++) {
                                if(j < 2) {
                                    result = result + temp.get(j) + "\n";
                                } else if(j == 3) {
                                    if(temp.get(j).toString().equals("Checked")) {
                                        checkedItems.put(temp.getString(2), "added");
                                    }
                                } else {
                                    itemIDs[k] = temp.getString(j);
                                    k++;
                                }
                            }
                            itemsList[i] = result;
                            result = "";
                        }

                        ArrayAdapter<String> cs = new ArrayAdapter<String>(Items.this, android.R.layout.simple_list_item_checked, itemsList);
                        items.setAdapter(cs);
                        if(getIntent().getStringExtra("requestCategory").trim().equalsIgnoreCase("admin")) {
                            items.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        }

                        Iterator<String> keys = checkedItems.keys();

                        while(keys.hasNext()) {
                            String keyValue = keys.next();
                            if(checkedItems.getString(keyValue).equalsIgnoreCase("added")) {
                                items.setItemChecked(Integer.parseInt(keyValue)-1, true);
                            }
                        }
                        cs.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
    				alert.showAlertDialog(Items.this, "Message", "Unable to load. Please try again.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
    				alert.showAlertDialog(Items.this, "Message", "Unable to load. Please try again.", false);
                    //Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
    				alert.showAlertDialog(Items.this, "Message", "Unable to load. Please try again.", false);
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                } else {
    				alert.showAlertDialog(Items.this, "Message", "Unable to load. Please try again.", false);
                    //Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]" + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getTodayMenu() {
        showProgressDialog("Getting menu. Please wait");
//        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("restaurantid") + "/" + getIntent().getStringExtra("categoryid"), Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://ascianalysis-application3245.rhcloud.com/rest/wtfservice/getTodayMenu/" + getIntent().getStringExtra("restaurantid").trim() + "/" + getIntent().getStringExtra("categoryid").trim(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    cancelProgressDialog();
                    menuDetails = new JSONObject(new String(responseBody));
                    itemsList = new String[menuDetails.length()];
                    int k =0;
                    if(menuDetails.has("error")) {
                        //Toast.makeText(getApplicationContext(), menuDetails.getString("error"), Toast.LENGTH_LONG).show();
                        
                        
                        final AlertDialog.Builder builder = new AlertDialog.Builder(
    							Items.this);
    	
    					builder.setMessage(menuDetails.getString("error"))
    							.setCancelable(false)
    							.setPositiveButton("OK",
    									new DialogInterface.OnClickListener() {
    										public void onClick(
    												DialogInterface dialog, int id) {
    	
    											startActivity(new Intent(
    													Items.this,
    													FoodCourts.class));
    											finish();
    										}
    									});
    	
    					final AlertDialog alert = builder.create();
    	
    					alert.show();
                        
                        
                    } else {
                        itemIDs = new String[menuDetails.length()];
                        for(int i=0; i<menuDetails.length(); i++) {
                            JSONArray temp = menuDetails.getJSONArray(Integer.toString(i));
                            for (int j = 0; j < temp.length(); j++) {
                                if(j < 2) {
                                	if(j == 0)
                                		result = result + temp.get(j) + "\n";
                                	else
                                		result = result + "\u20B9" + temp.get(j);
                                }
                            }
                            itemsList[i] = result;
                            result = "";
                        }

                        ArrayAdapter<String> cs = new ArrayAdapter<String>(Items.this, R.layout.list_view_style, itemsList);
                        items.setAdapter(cs);
                        cs.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
    				alert.showAlertDialog(Items.this, "Message", "Unable to load. Please try again.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
    				alert.showAlertDialog(Items.this, "Message", "Unable to load. Please try again.", false);
                    //Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
    				alert.showAlertDialog(Items.this, "Message", "Unable to load. Please try again.", false);
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                } else {
    				alert.showAlertDialog(Items.this, "Message", "Unable to load. Please try again.", false);
                    //Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]" + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class UploadToServer extends AsyncTask<String,Void,String>{
        @SuppressWarnings("deprecation")
		@Override
        protected String doInBackground(String... strings) {
            try {

                nameValuePairs.add(new BasicNameValuePair("restaurantID", getIntent().getStringExtra("restaurantid").trim()));
                nameValuePairs.add(new BasicNameValuePair("category", getIntent().getStringExtra("categoryid").trim()));
                nameValuePairs.add(new BasicNameValuePair("details", itemIds.toString()));

                httpGet = new HttpPost("http://ascianalysis-application3245.rhcloud.com/Controller?requestService=13");
                httpGet.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();
                String output = EntityUtils.toString(httpEntity);
                return output;
            }  catch (ConnectTimeoutException e) {
                e.printStackTrace();
				alert.showAlertDialog(Items.this, "Message", "Unable to load. Please try again.", false);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
				alert.showAlertDialog(Items.this, "Message", "Unable to load. Please try again.", false);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
				alert.showAlertDialog(Items.this, "Message", "Unable to load. Please try again.", false);
            } catch (IOException e) {
                e.printStackTrace();
				alert.showAlertDialog(Items.this, "Message", "Unable to load. Please try again.", false);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.trim().equalsIgnoreCase("success")) {
                showAlertDialog(0, "Menu Updated Successfully");
            } else {
                showAlertDialog(0, "Menu Updation failed");
            }

        }

    }

    private void showAlertDialog(int position, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        if(position == 0) {
            alertDialog.setTitle("Menu Status");
            alertDialog.setMessage(message);
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    itemIds = new JSONObject();
                    Intent dashboardIntent = new Intent(Items.this, DashBoard.class);
                    dashboardIntent.putExtra("vendorName", getIntent().getStringExtra("restaurantid").trim());
                    dashboardIntent.putExtra("requestCategory", getIntent().getStringExtra("requestCategory").trim());
                    
                    dashboardIntent.putExtra("foodcourtid", getIntent().getStringExtra("foodcourtid").trim());
                    
                    startActivity(dashboardIntent);
                    finish();
                }
            });
        }

        alertDialog.show();
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
        getMenuInflater().inflate(R.menu.items, menu);
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