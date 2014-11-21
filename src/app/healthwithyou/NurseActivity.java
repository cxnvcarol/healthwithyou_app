package app.healthwithyou;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResult;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import app.models.Medicamento;
import app.models.RegistroMedida;
import app.healthwithyou.R;

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

	 private static final int REQUEST_OAUTH = 1;

	    /**
	     *  Track whether an authorization activity is stacking over the current activity, i.e. when
	     *  a known auth error is being resolved, such as showing the account chooser or presenting a
	     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
	     */
	    private static final String AUTH_PENDING = "auth_state_pending";
	    private boolean authInProgress = false;

	    private GoogleApiClient mClient = null;

		private OnDataPointListener mListener;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        buildFitnessClient();

        

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
	private void registerHeartRateListener()
	{
		mListener = new OnDataPointListener() {
            @Override
            public void onDataPoint(DataPoint dataPoint) {
                for (Field field : dataPoint.getDataType().getFields()) {
                    Value val = dataPoint.getValue(field);
                    Log.i("", "Detected DataPoint field: " + field.getName());
                    Log.i("", "Detected DataPoint value: " + val);
                    //double valor=Double.parseDouble(""+val);
                    if(field.getName().contains("eart"))
                    	actualizarDato2("heartrate", val.toString());
                }
            }
        };

        
		Fitness.SensorsApi.add(
                mClient,
                new SensorRequest.Builder()
                        //.setDataSource(DataSource.) // Optional but recommended for custom data sets.
                        .setDataType(DataType.TYPE_HEART_RATE_BPM) // Can't be omitted.
                        .setSamplingRate(2, TimeUnit.MINUTES)
                        .build(),
                mListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i("", "Listener registered!");
                        } else {
                            Log.i("", "Listener not registered.");
                        }
                    }
                });
	}

	private void buildFitnessClient() {

		mClient = new GoogleApiClient.Builder(this)
        .addApi(Fitness.API)
        .addScope(Fitness.SCOPE_LOCATION_READ)
        .addScope(Fitness.SCOPE_BODY_READ_WRITE)
        .addScope(Fitness.SCOPE_ACTIVITY_READ)
        .addConnectionCallbacks(
                new GoogleApiClient.ConnectionCallbacks() {

                    @Override
                    public void onConnected(Bundle bundle) {
                        //Log.i(TAG, "Connected!!!");
                    	System.out.println("connected!!");
                        // Now you can make calls to the Fitness APIs.
                        // Put application specific code here.
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        // If your connection to the sensor gets lost at some point,
                        // you'll be able to determine the reason and react to it here.
                        if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                            //Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                        	System.err.println("Network lost");
                        } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                            //Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                        	System.err.println("Service disconnected");
                        }
                    }
                }
        )
        .addOnConnectionFailedListener(
                new GoogleApiClient.OnConnectionFailedListener() {
                    // Called whenever the API client fails to connect.
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        //Log.i(TAG, "Connection failed. Cause: " + result.toString());
                    	System.err.println("connectionfailed: "+result.toString());
                        if (!result.hasResolution()) {
                            // Show the localized error dialog
                            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                    NurseActivity.this, 0).show();
                            return;
                        }
                        // The failure has a resolution. Resolve it.
                        // Called typically when the app is not yet authorized, and an
                        // authorization dialog is displayed to the user.
                        if (!authInProgress) {
                            try {
                                //Log.i(TAG, "Attempting to resolve failed connection");
                            	System.out.println("solving failed connection");
                                authInProgress = true;
                                result.startResolutionForResult(NurseActivity.this,
                                        REQUEST_OAUTH);
                            } catch (IntentSender.SendIntentException e) {
                            	e.printStackTrace();
                                //Log.e(TAG,"Exception while starting resolution activity", e);
                            }
                        }
                    }
                }
        )
        .build();
	}

	private void dbInit() {
		mydatabase = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
		mydatabase.execSQL("CREATE TABLE IF NOT EXISTS medidas(nombre VARCHAR,valor NUMBER);");
		mydatabase.execSQL("CREATE TABLE IF NOT EXISTS medicinas(nombre VARCHAR,descripcion VARCHAR, firstdate DATE);");
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
	public boolean dbInsertRegistroMedicina(String nombreMedida, String valor, Date fecha)
	{
		try {
			ContentValues values=new ContentValues();
			values.put("nombre", nombreMedida);
			values.put("descripcion", valor);
			values.put("firstdate", fecha.toString());
			mydatabase.insert("medicinas", null, values);
			return true;

		} catch (Exception e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
	public void readFitnessHistory(){
		 // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        SimpleDateFormat dateFormat = new SimpleDateFormat();
        Log.i("", "Range Start: " + dateFormat.format(startTime));
        Log.i("", "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                // The data request can specify multiple data types to return, effectively
                // combining multiple data queries into one call.
                // In this example, it's very unlikely that the request is for several hundred
                // datapoints each consisting of a few steps and a timestamp.  The more likely
                // scenario is wanting to see how many steps were walked per day, for 7 days.
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                //.aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_POWER_SUMMARY)
                // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                // bucketByTime allows for a time span, whereas bucketBySession would allow
                // bucketing by "sessions", which would need to be defined in code.
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        
        DataReadResult dataReadResult =null;
                Fitness.HistoryApi.readData(mClient, readRequest).setResultCallback(
                		new ResultCallback<DataReadResult>() {

							@Override
							public void onResult(DataReadResult dataReadResult) {
								int caltotales=0;
								for(Bucket bu:dataReadResult.getBuckets())
						        {
						        	List<DataSet> dsets=bu.getDataSets();
						        	for (DataSet dataSet : dsets) {
										caltotales+=dumpDataSet(dataSet);
									}
						        }
								System.out.println("caltotales: "+caltotales);
						        //msgToast("Hola! sabemos que has gastado "+caltotales+"calorías esta semana");
							}
							
                		});//.await();//.await(1, TimeUnit.MINUTES);
        
        
	}
	
	
	private int dumpDataSet(DataSet dataSet) {
        System.out.println("Data returned for Data type: " + dataSet.getDataType().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat();

        int caltotales=0;
        for (DataPoint dp : dataSet.getDataPoints()) {
            System.out.println("Data point:");
            System.out.println("\tType: " + dp.getDataType().getName());
            System.out.println("\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            System.out.println("\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
            	System.out.println("\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            	//if(fiel)

            	if(field.getName().equalsIgnoreCase("steps"))
            	{

            		Value pasos=dp.getValue(field);
            		
            		int calorias=(int) (130*0.57*pasos.asInt()/2100);
            		actualizarDato2("calorías", calorias+"");
            		caltotales+=calorias;
            	}
            	else if(field.getName().equalsIgnoreCase("average"))
            	{
            		Value hrate=dp.getValue(field);
            		
            		actualizarDato2("heartrate", hrate.asFloat()+"");
            	}
            }
        }
        return caltotales;
    }
	
	public void actualizarDato2(String key, String valorStr) {
		double valor=80;
		if(key.equalsIgnoreCase("comentarios"))
		{
			DataFragment.sendToServer(key,valorStr);
			msgToast(key+" enviados");
			return;
		}
		try{
			
		
		valor=Double.parseDouble(valorStr);
		dbInsertRegistroMedida(key, valor);
		
		DataFragment.sendToServer(key,valorStr);
		
		
		msgToast(key+" actualizado");
		}
		catch(Exception e)
		{
			msgToast("Por favor ingrese un valor válido");
			e.printStackTrace();
		}
		
		if(key.equalsIgnoreCase("heartrate"))
		{
			if(valor>200||valor<40)
			{
				comenzarLocalizacion();
			}
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
		if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        }
		else if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			//Attend event for opening offline file
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
	

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
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
				dbInsertRegistroMedicina(meds[0], meds[1],new Date());
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
	
	@Override
	protected void onStart() {
		mClient.connect();
		super.onStart();
	}
	@Override
	protected void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }
	
	private void mostrarPosicion(Location location) {
		System.out.println("posicionando");
    	if(location!=null)
    	{    		
    		try{
    			double latitude=location.getLatitude();
    			double longitude=location.getLongitude();
    			String msgs= "Emergencia! Estoy aquí ("+latitude+","+longitude+")";
    			msgToast("msg: "+msgs);
    			
    			sendsms(msgs);
    		}
    		catch(Exception ex)
    		{
    			
    		}
    	}
    	String msgs= "Emergencia! no fue posible la localización";
		msgToast("msg: "+msgs);
		
		sendsms(msgs);
	}
	

    private void sendsms(String msgs) {
    	SmsManager smsManager = SmsManager.getDefault();
    	smsManager.sendTextMessage("3142471630", null, msgs, null, null);
		
	}

	public void comenzarLocalizacion()
    {
        //Obtenemos una referencia al LocationManager
    	  LocationManager handle = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	  
    	  if (!handle.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
              msgToast("Por favor active el GPS");
         }
     
        //Obtenemos la �ltima posici�n conocida
        Location loc =handle.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        
     
        //Mostramos la �ltima posici�n conocida
        if(loc!=null)
        	mostrarPosicion(loc);
     
        //Nos registramos para recibir actualizaciones de la posici�n
        LocationListener locListener = new LocationListener() {
   		 
		    public void onLocationChanged(Location location) {
		    	if(location!=null)
		    		mostrarPosicion(location);
		    }
		    
			public void onProviderDisabled(String provider){
				/*
				 txtLatitud.setText("El GPS esta desactivado");
				 txtDireccion.setEnabled(true);				 
				 txtDireccion.setHint(R.string.hintDireccion);
				 */
		    }
		 
		    public void onProviderEnabled(String provider){
		       //Proveedor encendido
		    }
		 
		    public void onStatusChanged(String provider, int status, Bundle extras){
		        //txtLatitud.setText("Provider Status: " + status);
		    }
		};
     
        handle.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 30000, 0, locListener);
    }


}
