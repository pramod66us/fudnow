package com.test.foodzone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;


public class Tab3 extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	ImageView img1, img2, img3, img4, img5, img6, img7, img8;  
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.tab3);
	      img1 = (ImageView)findViewById(R.id.imageView1);
	      img2 = (ImageView)findViewById(R.id.imageView2);
	      img3 = (ImageView)findViewById(R.id.imageView3);
	      img4 = (ImageView)findViewById(R.id.imageView4);
	      img5 = (ImageView)findViewById(R.id.imageView5);
	      img6 = (ImageView)findViewById(R.id.imageView6);
	      img7 = (ImageView)findViewById(R.id.imageView7);
	      img8 = (ImageView)findViewById(R.id.imageView8);
	      
	      img1.setOnClickListener(this);
	      img2.setOnClickListener(this);
	      img3.setOnClickListener(this);
	      img4.setOnClickListener(this);
	      img5.setOnClickListener(this);
	      img6.setOnClickListener(this);
	      img7.setOnClickListener(this);
	      img8.setOnClickListener(this);
	    }

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
	    case R.id.imageView1:
	    	Toast.makeText(getApplicationContext(), "Thanks for Rating. We will update soon.", Toast.LENGTH_SHORT).show();
			break;
			
	    case(R.id.imageView2):
	    	Toast.makeText(getApplicationContext(), "Thanks for Rating. We will update soon.", Toast.LENGTH_SHORT).show();
	    	break;
	    	
	    case(R.id.imageView3):
	    	Toast.makeText(getApplicationContext(), "Thanks for Rating. We will update soon.", Toast.LENGTH_SHORT).show();
	    	break;
	    	
	    case(R.id.imageView4):
	    	Toast.makeText(getApplicationContext(), "Thanks for Rating. We will update soon.", Toast.LENGTH_SHORT).show();
	    	break;
	    	
	    case(R.id.imageView5):
	    	Toast.makeText(getApplicationContext(), "Thanks for Rating. We will update soon.", Toast.LENGTH_SHORT).show();
	    	break;
	    	
	    case(R.id.imageView6):
	    	Toast.makeText(getApplicationContext(), "Thanks for Rating. We will update soon.", Toast.LENGTH_SHORT).show();
	    	break;
	    	
	    case(R.id.imageView7):
	    	Toast.makeText(getApplicationContext(), "Thanks for Rating. We will update soon.", Toast.LENGTH_SHORT).show();
	    	break;
	    	
	    case(R.id.imageView8):
	    	Toast.makeText(getApplicationContext(), "Thanks for Rating. We will update soon.", Toast.LENGTH_SHORT).show();
	    	break;
	    	
	    default:
			break;
		}
	}
}
