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
import android.text.Editable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class TabNonVeg extends Activity implements OnClickListener {

    private JSONObject menuDetails;
    private JSONObject itemIds;
    private JSONObject checkedItems;
    private String[] checkedItemList;
    private String[] itemsList;
    private String[] itemIDs;
    private String[] itemIDs_cust;
    private String result = "";

    private ListView items;
    private Button btnSubmit;

    private String ItemIDUpdated,ItemIDRead;

    String reqLocation;

    ArrayList<String> checkedItemlist=new ArrayList<String>();
    ArrayList<String> checkedlistprev=null;
    ArrayList<String> Item_List_Temp=new ArrayList<String>();

    private Button profileBtn, homeBtn, exitBtn;
    private ImageView goBack;
	AlertDialogManager alert = new AlertDialogManager();

    private ArrayList<ItemsDetails> ItemDetailsList=new ArrayList<ItemsDetails>();

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
        reqLocation=getIntent().getStringExtra("requestLocation").trim();
        
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

                    ArrayList<String> CheckedItemList=new ArrayList<String>();
                    ArrayList<String> NotCheckedItemList=new ArrayList<String>();
                    int m=0,n=0;

                    int len = items.getCount();
                    Log.i("Total Item Count","Count : "+ items.getCount());
                    SparseBooleanArray checked = items.getCheckedItemPositions();
                    for (int i = 0; i < len; i++){
                        if (checked.get(i)) {
                            CheckedItemList.add(m++, items.getItemAtPosition(i).toString());
                        }
                        else {
                            NotCheckedItemList.add(n++,items.getItemAtPosition(i).toString());
                        }
                        //itemIds.add(Brand_List_Temp.get(i));
				    	  /* do whatever you want with the checked item */
                    }


                    Log.i("CheckedItemList","Size : "+CheckedItemList.size());
                    for(int i=0;i<CheckedItemList.size();i++){
                        for(int j=0;j<ItemDetailsList.size();j++){
                            if(ItemDetailsList.get(j).getItemName().equalsIgnoreCase(CheckedItemList.get(i).toString())){
                                try {
                                    Log.i("Checkeditem","Checked Item Name : "+ ItemDetailsList.get(j).getItemName());
                                    itemIds.put(ItemDetailsList.get(j).getItemID(),"added");
                                    // break;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    Log.i("CheckedItemList","Size : "+NotCheckedItemList.size());
                    for(int i=0;i<NotCheckedItemList.size();i++){
                        for(int j=0;j<ItemDetailsList.size();j++){
                            if(NotCheckedItemList.get(i).toString().equals(ItemDetailsList.get(j).getItemName())){
                                try {
                                    itemIds.put(ItemDetailsList.get(j).getItemID(),"removed");
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

            items.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {

                    //Log.v("long clicked", "pos: " + pos);
                    //Toast.makeText(getApplicationContext(), "Long clicked pos: " + itemIDs[pos], Toast.LENGTH_LONG).show();

                    //AlertDialog.Builder alert1 = new AlertDialog.Builder(TabVeg.this);

                    //final EditText edittext = new EditText(getApplicationContext());
                    //alert1.setMessage("Menu Details");

                    ItemIDUpdated = itemIDs[pos];
                    getSubItemDetails(itemIDs[pos]);

                    //alert1.setTitle(menuDetails.toString());

                    //alert1.setView(edittext);

                    //alert1.show();

                    return true;

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

                    ItemIDRead = itemIDs_cust[position];
                    getSubItemDetails(itemIDs_cust[position]);
					//Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
					
				}
			});

            items.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {

                    //Log.v("long clicked", "pos: " + pos);
                    //Toast.makeText(getApplicationContext(), "Long clicked pos: " + itemIDs_cust[pos], Toast.LENGTH_LONG).show();

                    //ItemIDRead = itemIDs_cust[pos];
                    //getSubItemDetails(itemIDs_cust[pos]);

                    Intent in = new Intent(TabNonVeg.this, FeedBack.class);
                    in.putExtra("foodcourtid", foodcourtid);
                    in.putExtra("restaurantid", restaurantid);
                    in.putExtra("categoryid", categoryid);
                    in.putExtra("itemname", ((TextView) arg1).getText().toString().trim());
                    in.putExtra("requestLocation",reqLocation);
                    startActivity(in);

                    return true;

                }
            });
            
        }
    }

    private void display(String params) {
        Toast.makeText(TabNonVeg.this, params, Toast.LENGTH_LONG).show();
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


    private void getItemsList() {
        showProgressDialog("Getting menu. Please wait");
//        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("restaurantid") + "/" + getIntent().getStringExtra("categoryid"), Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getTodayMenuAdmin/" + getIntent().getStringExtra("restaurantid").trim() + "/" + getIntent().getStringExtra("categoryid").trim()+"/2"+"/"+reqLocation, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    cancelProgressDialog();
                    menuDetails = new JSONObject(new String(responseBody));
                    itemsList = new String[menuDetails.length()];
                    checkedItemList = new String[menuDetails.length()];
                    int k = 0;
                    if (menuDetails.has("error")) {
                        Toast.makeText(getApplicationContext(), menuDetails.getString("error"), Toast.LENGTH_LONG).show();
                    } else {
                        int k1=0;
                        itemIDs = new String[menuDetails.length()];
                        for(int i=0; i<menuDetails.length(); i++) {
                            JSONArray temp = menuDetails.getJSONArray(Integer.toString(i));
                            ItemsDetails temp_item_details=new ItemsDetails();
                            temp_item_details.setItemName(temp.getString(0));
                            temp_item_details.setItemID(temp.getString(2));
                            temp_item_details.setItemCheckedStatus(temp.getString(3));

                            Item_List_Temp.add(i,temp.getString(0));

                            ItemDetailsList.add(i,temp_item_details);

                            for (int j = 0; j < temp.length(); j++) {
                                if(j < 2) {
                                    result = result + temp.get(j) + "\n";
                                } else if(j == 3) {
                                    if(temp.get(j).toString().equals("Checked")) {
                                        //checkedItems.put(temp.getString(2), "added");
                                        checkedItemlist.add(k1++,temp.getString(0));
                                        //Log.i("readChecked","Checked :"+temp.getString(0));
                                    }
                                } else {
                                    itemIDs[k] = temp.getString(j);
                                    k++;
                                }
                            }
                            itemsList[i] = result;
                            result = "";
                        }

                        ArrayAdapter<String> cs = new ArrayAdapter<String>(TabNonVeg.this, android.R.layout.simple_list_item_checked, Item_List_Temp);
                        items.setAdapter(cs);
                        cs.notifyDataSetChanged();
                        if (getIntent().getStringExtra("requestCategory").trim().equalsIgnoreCase("admin")) {
                            items.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                        }

                        if(checkedItemlist.isEmpty()==false){

                            for(int j=0;j<checkedItemlist.size();j++){
                                int pos=cs.getPosition(checkedItemlist.get(j));
                                Log.i("Itemchecked","Item Checked List : "+ checkedItemlist.get(j));
                                items.setItemChecked(pos,true);
                            }
                        }

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    cancelProgressDialog();
                    alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    cancelProgressDialog();
                    alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                } else {
                    cancelProgressDialog();
                    alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]" + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getSubItemDetails(String itemID) {
        final String t_itemID=itemID;
        showProgressDialog("Getting menu. Please wait");
//        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("restaurantid") + "/" + getIntent().getStringExtra("categoryid"), Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getSubMenu/"+ getIntent().getStringExtra("restaurantid").trim()+"/" + itemID.trim() +"/"+getIntent().getStringExtra("categoryid").trim()+"/"+reqLocation , params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    cancelProgressDialog();
                    menuDetails = new JSONObject(new String(responseBody));

                    String SubMenu=menuDetails.getString("subMenu");

                    AlertDialog.Builder alert1 = new AlertDialog.Builder(TabNonVeg.this);

                    final EditText edittext = new EditText(getApplicationContext());

                    alert1.setTitle("Item Details");

                    alert1.setMessage("Items: " + SubMenu.toString());

                    edittext.setText(SubMenu.toString());
                    if(getIntent().getStringExtra("requestCategory").trim().equalsIgnoreCase("admin")) {
                        alert1.setView(edittext);

                        alert1.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //What ever you want to do with the value
                                Editable YouEditTextValue = edittext.getText();
                                if (YouEditTextValue.toString().trim().length() != 0) {
                                    updateSubItemDetails(t_itemID, YouEditTextValue.toString().toUpperCase());
                                }

                                //OR
                                //String YouEditTextValue = edittext.getText();
                            }
                        });
                    }
                    alert1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // what ever you want to do with No option.
                        }
                    });

                    alert1.show();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    cancelProgressDialog();
                    alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    cancelProgressDialog();
                    alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                } else {
                    cancelProgressDialog();
                    alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]" + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void updateSubItemDetails(String ItemID, String EditedText) {
        showProgressDialog("Updating menu. Please wait");
        String EditedText_t=EditedText;

//        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("restaurantid") + "/" + getIntent().getStringExtra("categoryid"), Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/updateSubMenu/"+ getIntent().getStringExtra("restaurantid").trim()+"/" + ItemID.trim() +"/"+getIntent().getStringExtra("categoryid").trim() +"/"+EditedText_t.trim()+"/"+reqLocation , params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    cancelProgressDialog();
                    menuDetails = new JSONObject(new String(responseBody));

                    String status=menuDetails.getString("status");

                    if (status.equals("Success")){

                        getSubItemDetails(ItemIDUpdated);
                    }


                } catch (JSONException e) {

                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    cancelProgressDialog();
                    alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    cancelProgressDialog();
                    alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                } else {
                    cancelProgressDialog();
                    alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]" + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void getTodayMenu() {
        showProgressDialog("Getting menu. Please wait");
//        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("restaurantid") + "/" + getIntent().getStringExtra("categoryid"), Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        Log.i("Get Menu URL","http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getTodayMenu/" + getIntent().getStringExtra("restaurantid").trim() + "/" + getIntent().getStringExtra("categoryid").trim() + "/2"+"/"+reqLocation);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/getTodayMenu/" + getIntent().getStringExtra("restaurantid").trim() + "/" + getIntent().getStringExtra("categoryid").trim() + "/2"+"/"+reqLocation, params, new AsyncHttpResponseHandler() {
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
    							TabNonVeg.this);
    	
    					builder.setMessage(menuDetails.getString("error"))
    							.setCancelable(false)
    							.setPositiveButton("OK",
    									new DialogInterface.OnClickListener() {
    										public void onClick(
    												DialogInterface dialog, int id) {
    	
    											startActivity(new Intent(
    													TabNonVeg.this,
    													FoodCourts.class));
    											finish();
    										}
    									});
    	
    					final AlertDialog alert = builder.create();
    	
    					alert.show();
                        
                        
                    } else {
                        itemIDs_cust = new String[menuDetails.length()];
                        String testtemp="";
                        for(int i=0; i<menuDetails.length(); i++) {
                            JSONArray temp = menuDetails.getJSONArray(Integer.toString(i));
                            for (int j = 0; j < temp.length(); j++) {
                                if(j < 3) {
                                    if(j == 0) {
                                        //result = result + temp.get(j) + "\n";
                                        itemIDs_cust[i] = temp.get(j).toString();
                                    } else if(j==1)
                                        result = result + temp.get(j) + "\u00A0 \u00A0";
                                    else
                                        result = result + "\u20B9" + temp.get(j);
                                }
                                //testtemp=testtemp+temp.get(j);
                            }
                            itemsList[i] = result;
                            result = "";
                        }

                        ArrayAdapter<String> cs = new ArrayAdapter<String>(TabNonVeg.this, R.layout.list_view_style, itemsList);
                        items.setAdapter(cs);
                        cs.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    cancelProgressDialog();
                    e.printStackTrace();
    				alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    cancelProgressDialog();
    				alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    cancelProgressDialog();
    				alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                } else {
                    cancelProgressDialog();
    				alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
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
                nameValuePairs.add(new BasicNameValuePair("locationID", getIntent().getStringExtra("requestLocation").trim()));

                httpGet = new HttpPost("http://wtfapp-nutcrackers.rhcloud.com/Controller?requestService=13");
                httpGet.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpResponse = httpClient.execute(httpGet);
                httpEntity = httpResponse.getEntity();
                String output = EntityUtils.toString(httpEntity);
                return output;
            }  catch (ConnectTimeoutException e) {
                e.printStackTrace();
				alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
				alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
				alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
            } catch (IOException e) {
                e.printStackTrace();
				alert.showAlertDialog(TabNonVeg.this, "Message", "Please check the network connection.", false);
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
                    Intent dashboardIntent = new Intent(TabNonVeg.this, DashBoard.class);
                    dashboardIntent.putExtra("vendorName", getIntent().getStringExtra("restaurantid").trim());
                    dashboardIntent.putExtra("requestCategory", getIntent().getStringExtra("requestCategory").trim());
                    
                    dashboardIntent.putExtra("foodcourtid", getIntent().getStringExtra("foodcourtid").trim());
                    dashboardIntent.putExtra("requestLocation",reqLocation);
                    
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