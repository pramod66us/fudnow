package com.test.foodzone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


public class HomeScreen extends Activity {

    //Button btnLogin;
    //EditText vendorName, pswd;
    TextView contUser;

    Button btnLogin;
    private ProgressDialog progressDialog;
    EditText vendorName, pswd;
    private String VendorID, RequestCategory,FoodCourID;
    AlertDialogManager alert = new AlertDialogManager();

    private JSONObject userDetails;
    private String validationStatus;
    private Boolean returnStatusUser;
	//AlertDialogManager alert = new AlertDialogManager();
    String reqLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        progressDialog = new ProgressDialog(this);
        reqLocation=getIntent().getStringExtra("requestLocation");
        vendorName = (EditText) findViewById(R.id.username);
        pswd = (EditText) findViewById(R.id.password);

        contUser = (TextView) findViewById(R.id.contUser);
        contUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent in = new Intent(getApplicationContext(), FoodCourts.class);
                in.putExtra("requestLocation",reqLocation);
                startActivity(in);
            }
        });

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String UserName = vendorName.getText().toString();
                String Password = pswd.getText().toString();
                Log.i("On Click", "Before Entering Validation : " + UserName.toUpperCase());
                //validateUserLogin(UserName.toUpperCase(), Password);
                //Log.i("On Click","After Entering Validation : "+returnStatusUser.toString());
                validateUserLogin(UserName.toUpperCase(), Password);
            }
        });
    }

    private void validateUserLogin(String userName, String password) {
        showProgressDialog("Please wait");
        final int[] returnStatus = {0};
        Log.i("User Validation","http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/validateLogin/" + userName.toString() + "/" + password.toString());
        //Toast.makeText(getApplicationContext(), getIntent().getStringExtra("restaurantid") + "/" + getIntent().getStringExtra("categoryid"), Toast.LENGTH_LONG).show();
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://wtfapp-nutcrackers.rhcloud.com/rest/wtfservice/validateLogin/" + userName.toString() + "/" + password.toString(), params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                cancelProgressDialog();
                try {
                    userDetails = new JSONObject(new String(responseBody));

                    validationStatus = userDetails.getString("status");

                    if (validationStatus.equals("Success")) {
                           VendorID = userDetails.getString("resID");
                           FoodCourID=userDetails.getString("fooID");
                            Intent in = new Intent(getApplicationContext(), DashBoard.class);
                            in.putExtra("vendorName", VendorID);
                            in.putExtra("foodcourtid", FoodCourID);
                            in.putExtra("requestCategory", "admin");
                            in.putExtra("requestLocation",reqLocation);

                            startActivity(in);
                        } else if (vendorName.getText().toString().trim().equals(""))
                            alert.showAlertDialog(HomeScreen.this, "Message", "Please enter Username.", false);
                            //Toast.makeText(getApplicationContext(), "Please enter Username.", Toast.LENGTH_SHORT).show();

                        else if (pswd.getText().toString().trim().equals(""))
                            alert.showAlertDialog(HomeScreen.this, "Message", "Please enter Password.", false);
                            //Toast.makeText(getApplicationContext(), "Please enter Password.", Toast.LENGTH_SHORT).show();

                        else if (vendorName.getText().toString().trim().equalsIgnoreCase("pentafour") && !pswd.getText().toString().trim().equals("123456"))
                            alert.showAlertDialog(HomeScreen.this, "Message", "Username and password do not match.", false);
                            //Toast.makeText(getApplicationContext(), "Username and password do not match.", Toast.LENGTH_SHORT).show();

                        else
                            alert.showAlertDialog(HomeScreen.this, "Message", "Username does not exist.", false);
                        //Toast.makeText(getApplicationContext(), "Username does not exist.", Toast.LENGTH_SHORT).show();


                } catch (JSONException e) {
                    e.printStackTrace();
                    alert.showAlertDialog(HomeScreen.this, "Message", "Please check the network connection.", false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (statusCode == 404) {
                    cancelProgressDialog();
                    alert.showAlertDialog(HomeScreen.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Requested resource not found" + error, Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    cancelProgressDialog();
                    alert.showAlertDialog(HomeScreen.this, "Message", "Please check the network connection.", false);
                    //Toast.makeText(getApplicationContext(), "Something went wrong at server end" + error, Toast.LENGTH_LONG).show();
                } else {
                    cancelProgressDialog();
                    alert.showAlertDialog(HomeScreen.this, "Message", "Please check the network connection.", false);
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
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
