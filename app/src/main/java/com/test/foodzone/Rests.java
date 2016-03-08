package com.test.foodzone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class Rests extends Activity implements OnClickListener{

	Button btn1, btn2, btn3, btn4, btn5;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rests);
        
        btn1 = (Button) findViewById(R.id.Button01);
        btn1.setOnClickListener(this);
        
        btn2 = (Button) findViewById(R.id.Button02);
        btn2.setOnClickListener(this);
        
        btn3 = (Button) findViewById(R.id.Button03);
        btn3.setOnClickListener(this);
        
        btn4 = (Button) findViewById(R.id.Button04);
        btn4.setOnClickListener(this);
        
        btn5 = (Button) findViewById(R.id.Button05);
        btn5.setOnClickListener(this);
        
    }

    @Override
	public void onClick(View v) {
		
		switch(v.getId()) {
	    case R.id.Button01:
	    	Intent in = new Intent(getApplicationContext(), TabBar.class); 
			startActivity(in);
			break;
	    	
	    case(R.id.Button02):
	    	Intent in1 = new Intent(getApplicationContext(), TabBar.class); 
			startActivity(in1);
	    	break;
	    	
	    case(R.id.Button03):
	    	Intent in2 = new Intent(getApplicationContext(), TabBar.class); 
			startActivity(in2);
	    	break;

	    case(R.id.Button04):
	    	Intent in3 = new Intent(getApplicationContext(), TabBar.class); 
			startActivity(in3);
	    	break;
	    
	    case(R.id.Button05):
	    	Intent in5 = new Intent(getApplicationContext(), TabBar.class); 
			startActivity(in5);
	    	break;
	    	
	    default:
			break;
		}
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


