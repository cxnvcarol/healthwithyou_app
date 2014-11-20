package app.yourpersonalnurse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import app.models.Medicamento;
import app.models.RegistroMedida;

public class NurseActivity extends ActionBarActivity  implements
ActionBar.TabListener{

	private static final int REQUEST_ABOUT_ACTIVITY = 1;
	
	private static final long ENABLE_WAIT_TIME_MS = 5000;

	private static final int READ_REQUEST_CODE = 2;

	private static final String DATABASE_NAME = "NurseDatabase";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private SQLiteDatabase mydatabase;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        StrictMode.setThreadPolicy(policy);
	        
		setContentView(R.layout.activity_nurse);

		dbInit();
		
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		if(getIntent().getExtras()!=null&&getIntent().getExtras().containsKey(FileOpenerActivity.URI_PRESC))
		{
			Uri fileU=Uri.parse(getIntent().getExtras().getString(FileOpenerActivity.URI_PRESC));
			
			try {
				readTextFromUri(fileU);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void dbInit() {
		mydatabase = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
		mydatabase.execSQL("CREATE TABLE IF NOT EXISTS medidas(nombre VARCHAR,valor NUMBER);");
		mydatabase.execSQL("CREATE TABLE IF NOT EXISTS medicinas(nombre VARCHAR,descripcion VARCHAR);");
		System.out.println("db creada exitosamente");
	}
	public boolean dbInsertRegistroMedida(String nombreMedida, double valor)
	{
		try {
			ContentValues values=new ContentValues();
			values.put("nombre", nombreMedida);
			values.put("valor", valor);
			mydatabase.insert("medidas", null, values);
			return true;
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
	private void dbDropTableMedicinas() {
		mydatabase.delete("medicinas", "", null);
	}
	public boolean dbInsertRegistroMedicina(String nombreMedida, String valor)
	{
		try {
			ContentValues values=new ContentValues();
			values.put("nombre", nombreMedida);
			values.put("descripcion", valor);
			mydatabase.insert("medicinas", null, values);
			return true;
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
	public ArrayList<Double> dbGetHistorialMedidas(String nombre)
	{
		ArrayList<Double> retornado=new ArrayList<Double>();
		Cursor resultSet = mydatabase.rawQuery("Select valor from medidas where nombre='"+nombre+"';",null);
		int total=resultSet.getCount();
		resultSet.moveToFirst();
		for (int i = 0; i < total; i++) {
			try{
				retornado.add(resultSet.getDouble(0));
			}
			catch(Exception e)
			{
				retornado.add(new Double(50));
			}
			resultSet.moveToNext();
		}
		return retornado;
	}
	
	public ArrayList<Medicamento> dbGetPrescripcionesMedicinas()
	{
		ArrayList<Medicamento> retornado=new ArrayList<Medicamento>();
		Cursor resultSet = mydatabase.rawQuery("Select * from medicinas;",null);
		int total=resultSet.getCount();
		resultSet.moveToFirst();
		for (int i = 0; i < total; i++) {
			try{
				retornado.add(new Medicamento(resultSet.getString(0), resultSet.getString(1), new Date(), "", false));
			}
			catch(Exception e)
			{
				System.err.println("errmm: "+e.getMessage());
				//retornado.add(new Double(50));
			}
			resultSet.moveToNext();
		}
		return retornado;
	}

	public class EnableAsync extends AsyncTask<View, Integer, Integer>
	{
		
		private View v;
		public EnableAsync(View v) {
			this.v=v;
		}
		@Override
		protected Integer doInBackground(View... params) {
			try {
				Thread.sleep(ENABLE_WAIT_TIME_MS);
				//params[0].setVisibility(View.VISIBLE);
				
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			return null;
		}
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			v.setEnabled(true);
			v.postInvalidate();
			v.invalidate();
			v.refreshDrawableState();
			super.onPostExecute(result);
		}
		
	}
	/*
	public void onClickListo(View v)
	{
		v.setEnabled(false);
		//v.setVisibility(View.INVISIBLE);
		msgToast("Comida tomada registrada");
		new EnableAsync(v).execute(v);
		
		
	}*/
	
	public void msgToast(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.prescripciones, menu);
		return true;
	}
	public void performFileSearch() {

	    // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
	    // browser.
	    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

	    // Filter to only show results that can be "opened", such as a
	    // file (as opposed to a list of contacts or timezones)
	    intent.addCategory(Intent.CATEGORY_OPENABLE);

	    // Filter to show only images, using the image MIME data type.
	    // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
	    // To search for all documents available via installed storage providers,
	    // it would be "*/*".
	    intent.setType("*/*");
	    

	    startActivityForResult(intent, READ_REQUEST_CODE);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Intent serverIntent = null;
		switch(id)
		{
		case R.id.about:
			//return true;
			serverIntent = new Intent(this, AboutActivity.class);
			startActivityForResult(serverIntent, REQUEST_ABOUT_ACTIVITY);
			return true;
		case R.id.actualizarPrescripcion:
			performFileSearch();
			return true;
		case R.id.settings:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent resultData) {
		 if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
		        // The document selected by the user won't be returned in the intent.
		        // Instead, a URI to that document will be contained in the return intent
		        // provided to this method as a parameter.
		        // Pull that URI using resultData.getData().
		        Uri uri = null;
		        if (resultData != null) {
		            uri = resultData.getData();
		            System.out.println("Uri: " + uri.toString());
		            try {
						readTextFromUri(uri);
					} catch (IOException e) {
						e.printStackTrace();
					}
		        }
		    }
		 else if (requestCode == PrescripcionFragment.REQUEST_PRESCRIPCION_ACTIVITY && resultCode == Activity.RESULT_OK) {
			 System.out.println("prescripcion on activity result");
			 
		 }
		 super.onActivityResult(requestCode, resultCode, resultData);
	}

	private String readTextFromUri(Uri uri) throws IOException {
	    InputStream inputStream = getContentResolver().openInputStream(uri);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(
	            inputStream));
	    StringBuilder stringBuilder = new StringBuilder();
	    try{
	    	
	    
	    dbDropTableMedicinas();
	    String line;
	    
	    while ((line = reader.readLine()) != null) {
	        System.out.println("rr:"+line);
	        String[] meds=line.split(",");
	        dbInsertRegistroMedicina(meds[0], meds[1]);
	    }
	    inputStream.close();
	    }
	    catch(Exception e)
	    {
	    	msgToast("Lo sentimos, ha sucedido un error actualizando sus prescripciones");
	    }
	    return stringBuilder.toString();
	}
	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	public void actualizarDato(View v) {
	Toast.makeText(this, "Dato actualizado", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch(position)
            {
            case 0:
            	return PrescripcionFragment.newInstance(position + 1);
            case 1:            	
            	return DataFragment.newInstance(position + 1);
            case 2:            	
            	return SeguimientoFragment.newInstance(position + 1);
            }
            return null;//shoudn't happen
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_prescripcion).toUpperCase(l);
			case 1:
				return getString(R.string.title_data).toUpperCase(l);
			case 2:
				return getString(R.string.title_seguimiento).toUpperCase(l);
			}
			return null;
		}
	}

	}
