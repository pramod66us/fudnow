package com.test.foodzone;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.EmptyStackException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

public class FeedBack extends Activity implements OnClickListener {
	
    String foodcourtid, restaurantid, categoryid, itemname;
    
    private ProgressDialog progressDialog;
    private RatingBar ratingBar;
    String reqLocation;
    private EditText txtEmpNo, txtMsg;
    private Button btnSubmit;
    
    String rating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_back);
		
		progressDialog = new ProgressDialog(this);

        foodcourtid = getIntent().getStringExtra("foodcourtid").trim();
        restaurantid = getIntent().getStringExtra("restaurantid").trim();
        categoryid = getIntent().getStringExtra("categoryid").trim();
        itemname = getIntent().getStringExtra("itemname").trim();
        reqLocation=getIntent().getStringExtra("requestLocation");
        
        itemname.replaceAll(" ", "%20");
        
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        
        txtEmpNo = (EditText) findViewById(R.id.txt_emp_no);
        txtMsg = (EditText) findViewById(R.id.txt_msg);
        
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == btnSubmit) {
			rating = String.valueOf(ratingBar.getRating());
			uploadFeedback();
			//Toast.makeText(FeedBack.this, String.valueOf(ratingBar.getRating()), Toast.LENGTH_SHORT).show();
		}
	}

	private void uploadFeedback() {
        
		showProgressDialog("Sending feedback. Please wait");
        
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        String FullItemName=itemname.replaceAll("\n", " ");
        String URLFull="";
        try {
            URLFull= URLEncoder.encode(FullItemName,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.i("Feedback", "http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/updateFeedback/" + foodcourtid + "/" + restaurantid + "/" +
                categoryid + "/" + URLFull + "/" + txtEmpNo.getText().toString().trim() + "/" + txtMsg.getText().toString().trim() + "/" + rating + "/"+reqLocation);
        client.get("http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/updateFeedback/" + foodcourtid + "/" + restaurantid + "/" +
                        categoryid + "/" + URLFull + "/" + txtEmpNo.getText().toString().trim() + "/" + txtMsg.getText().toString().trim() + "/" + rating + "/"+reqLocation,
                params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            cancelProgressDialog();
                            Toast.makeText(getApplicationContext(), new String(responseBody), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if (statusCode == 404) {
                            Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                        } else if (statusCode == 500) {
                            Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                        } else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]" + error, Toast.LENGTH_LONG).show();
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
		getMenuInflater().inflate(R.menu.feed_back, menu);
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
