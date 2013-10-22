package yoga1290.bey2ollak;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class MainActivity extends Activity
{

	final Activity thisActivity=this;
	public static boolean viewer=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		((Button) findViewById(R.id.button_start)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				viewer=false;
				startActivity(new Intent(thisActivity, MapActivity.class)	);
			}
		});
		
		((Button) findViewById(R.id.button_view)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(thisActivity, MapActivity.class)	);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
