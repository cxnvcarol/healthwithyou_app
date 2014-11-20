package app.yourpersonalnurse;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class FileOpenerActivity extends Activity{
	
	public static final String URI_PRESC = "URI PRESCRIPTION";
	private Uri uri;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		final Intent intent = getIntent();		
		final String action = intent.getAction();
	
		if(Intent.ACTION_VIEW.equals(action)){
			uri = intent.getData();
			System.out.println("uri callback: "+uri);
			Intent mainA=new Intent(this,NurseActivity.class);
			mainA.putExtra(URI_PRESC, uri.toString());
			startActivity(mainA);
		
		}
	
	}
	
}
