package com.test.foodzone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Login extends Activity {

	Button btnLogin;
	private ProgressDialog progressDialog;
	EditText vendorName, pswd;
	String VendorID, RequestCategory;
	AlertDialogManager alert = new AlertDialogManager();

	private JSONObject userDetails;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		vendorName = (EditText) findViewById(R.id.vendor_name);
		pswd = (EditText) findViewById(R.id.pswd);
		
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				String UserName = vendorName.getText().toString();
				String Password = pswd.getText().toString();
				Log.i("On Click","Before Entering Validation");
				Boolean returnStatusUser=validateUserLogin(UserName, Password);
				Log.i("On Click","After Entering Validation : "+returnStatusUser.toString());
				if (returnStatusUser) {
					Intent in = new Intent(getApplicationContext(), DashBoard.class);
					in.putExtra("vendorName", VendorID);
					in.putExtra("requestCategory", "admin");
					startActivity(in);
				} else if (vendorName.getText().toString().equals(""))
					Toast.makeText(getApplicationContext(), "Please enter Username.", Toast.LENGTH_SHORT).show();

				else if (pswd.getText().toString().equals(""))
					Toast.makeText(getApplicationContext(), "Please enter Password.", Toast.LENGTH_SHORT).show();

				else
					Toast.makeText(getApplicationContext(), "Username does not exist.", Toast.LENGTH_SHORT).show();

			}
		});
		
	}

	private boolean validateUserLogin(String userName, String password) {
		showProgressDialog("Logging in. Please wait");
		final int[] returnStatus = {0};
		Log.i("User Validation","http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/validateLogin/" + userName.toString().toUpperCase() + "/" + password.toString());
        //Toast.makeText(getApplicationContext(), getIntent().getStringExtra("restaurantid") + "/" + getIntent().getStringExtra("categoryid"), Toast.LENGTH_LONG).show();
		RequestParams params = new RequestParams();
		AsyncHttpClient client = new AsyncHttpClient();
		client.get("http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/validateLogin/"+userName.toString().toUpperCase()+"/"+password.toString() , params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				cancelProgressDialog();
				try {
					userDetails = new JSONObject(new String(responseBody));

					String validationStatus=userDetails.getString("status");

					if(validationStatus.equals("Success")) {
						returnStatus[0] = 1;
						VendorID=userDetails.getString("resID");
					}

				} catch (JSONException e) {
					e.printStackTrace();
					alert.showAlertDialog(Login.this, "Message", "Unable to load. Please try again.", false);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				if (statusCode == 404) {
					cancelProgressDialog();
					alert.showAlertDialog(Login.this, "Message", "Unable to load. Please try again.", false);
					//Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
				} else if (statusCode == 500) {
					cancelProgressDialog();
					alert.showAlertDialog(Login.this, "Message", "Unable to load. Please try again.", false);
					//Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
				} else {
					cancelProgressDialog();
					alert.showAlertDialog(Login.this, "Message", "Unable to load. Please try again.", false);
					//Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]" + error, Toast.LENGTH_LONG).show();
				}
			}
		});
		if(returnStatus[0]==1)
			return true;
		else
			return  false;
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
		getMenuInflater().inflate(R.menu.login, menu);
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
