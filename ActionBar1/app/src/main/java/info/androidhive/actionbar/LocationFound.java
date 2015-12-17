package info.androidhive.actionbar;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class LocationFound  extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_found);
		
		// get the action bar
		ActionBar actionBar = getActionBar();
		
		// Enabling Back navigation on Action Bar icon
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
}
