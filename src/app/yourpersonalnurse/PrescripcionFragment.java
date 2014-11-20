package app.yourpersonalnurse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Toast;
import app.models.Comida;
import app.models.Medicamento;

/**
	 * A placeholder fragment containing a simple view.
	 */
	public class PrescripcionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		
		
		 ExpandableListAdapter listAdapter;
		 ExpandableListView expListView;
		 List<String> listDataHeader;
		 HashMap<String, List> listDataChild;
		 HashMap<String, List> listDataChildMedic;


		private View rootView;


		public static int REQUEST_PRESCRIPCION_ACTIVITY=3;
		public static int REQUEST_COMIDA_ACTIVITY=3;

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PrescripcionFragment newInstance(int sectionNumber) {
			PrescripcionFragment fragment = new PrescripcionFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PrescripcionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_prescripciones,
					container, false);
			
			// get the listview
	        expListView = (ExpandableListView) rootView.findViewById(R.id.lvExp);
	 
	        // preparing list data
	        prepareListData();
	 
	        System.out.println("data preparada");
	        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
	 
	        // setting list adapter
	        expListView.setAdapter(listAdapter);
	        
	        
	        
	     // Listview on child click listener
	        expListView.setOnChildClickListener(new OnChildClickListener() {
	 
	            @Override
	            public boolean onChildClick(ExpandableListView parent, View v,
	                    int groupPosition, int childPosition, long id) {
	            	System.out.println("ONCHILD CLICK!!");
	            	try{
	            		Medicamento child=(Medicamento) listDataChild.get(
	                            listDataHeader.get(groupPosition)).get(
	                            childPosition);

		                Toast.makeText(
		                        getActivity(),
		                        listDataHeader.get(groupPosition)
		                                + " : "
		                                + child, Toast.LENGTH_SHORT)
		                        .show();
		                goToPrescripcionDetail(child);
	            	}
	            	catch(Exception e)
	            	{
	            		Comida child=(Comida) listDataChild.get(
	                            listDataHeader.get(groupPosition)).get(
	                            childPosition);

		                Toast.makeText(
		                        getActivity(),
		                        listDataHeader.get(groupPosition)
		                                + " : "
		                                + child, Toast.LENGTH_SHORT)
		                        .show();
		                goToComidaDetail(child);
	            		
	            	}
	            	
	                return false;
	            }
	        });
	        
			return rootView;
		}
		
		public void goToPrescripcionDetail(Medicamento med) {
			Intent intent = new Intent(getActivity(), PrescripcionActivity.class);
			Bundle b=new Bundle();
			b.putString("nombre",med.getNombre() );
			b.putString("descripcion",med.getDescripcion() );
			intent.putExtras(b);
			//b.putString("", med.);
			startActivityForResult(intent, REQUEST_PRESCRIPCION_ACTIVITY);
			
		}
		public void goToComidaDetail(Comida med) {
			Intent intent = new Intent(getActivity(), ComidaActivity.class);
			Bundle b=new Bundle();
			b.putString("nombre",med.getNombre() );
			b.putString("descripcion",med.getDescripcion() );
			intent.putExtras(b);
			//b.putString("", med.);
			startActivityForResult(intent, REQUEST_COMIDA_ACTIVITY);
			
		}
		
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (requestCode == REQUEST_PRESCRIPCION_ACTIVITY && resultCode == Activity.RESULT_OK) {
				 System.out.println("fragment ansa: prescripcion callback");
				 
			 }
		}
		
		
		public void msgToast(String msg)
		{
			System.out.println("toast: "+msg);
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
		}
		
		/*
	     * Preparing the list data
	     */
	    private void prepareListData2() {
	        listDataHeader = new ArrayList<String>();
	        //listDataChild = new HashMap<String, List<String>>();
	        listDataChild = new HashMap<String, List>();
	 
	        // Adding child data
	        listDataHeader.add("Medicamentos");
	        listDataHeader.add("Alimentación");
	 
	        // Adding child data
	        List<String> medicamentos = new ArrayList<String>();
	        medicamentos.add("Metformina");
	        medicamentos.add("Glipizide");
	 
	        List<String> comida = new ArrayList<String>();
	        comida.add("Desayuno");
	        comida.add("Medias nueves");
	        comida.add("Almuerzo");
	        comida.add("Onces");
	        comida.add("Cena");
	 
	        listDataChild.put(listDataHeader.get(0), medicamentos); // Header, Child data
	        listDataChild.put(listDataHeader.get(1), comida);
	    }
	    private void fillDefaultData(List<Medicamento> medicamentos)
	    {
	    	fillDefaultData(medicamentos);
	        // Adding child data
	        
	        medicamentos.add(new Medicamento("Metformina", "Tomar 1 antes de cada comida", new Date(), "",false));
	        Medicamento medic2=new Medicamento("Glipizide", "Tomar 1 diaria", new Date(), "FREQ=DAILY;COUNT=10",true);
	        
	        ContentResolver cr = getActivity().getContentResolver();
	        try{
	        	System.out.println("uri events: "+Events.CONTENT_URI);
	        	Uri uri = cr.insert(Events.CONTENT_URI, medic2.getValuesAlarm());
	        	long eventID = Long.parseLong(uri.getLastPathSegment());
	        	System.out.println("uri: "+uri);
	        	System.out.println("eventid: "+eventID);
	        	msgToast("Las alarmas de tus prescripciones se han sincronizado exitosamente");
	        }
	        catch(Exception ex)
	        {
	        	System.err.println("Min api not satisfied: "+ex.getMessage());
	        	msgToast("Lo sentimos, no fue posible sincronizar las alarmas en tu calendario.");
	        	
	        }
	        
	        medicamentos.add(medic2);
	    }
	    @SuppressLint("NewApi")
		private void prepareListData() {
	        listDataHeader = new ArrayList<String>();
	        listDataChild = new HashMap<String, List>();
	 
	        // Adding child data
	        listDataHeader.add("Medicamentos");
	        listDataHeader.add("Alimentación");
	        List<Medicamento> medicamentos = new ArrayList<Medicamento>();
	 
	        try{
	        	medicamentos=((NurseActivity)getActivity()).dbGetPrescripcionesMedicinas();
	        	if(medicamentos.size()==0){
	        		fillDefaultData(medicamentos);
	        	}
	        	
	        }
	        catch(Exception e)
	        {
	        	
	        	System.err.println("Error con la base de datos");
	        	System.err.println(e.getMessage());
	        	
	        }
	        
	        
	        List<Comida> comida = new ArrayList<Comida>();
	        comida.add(new Comida("Desayuno"));
	        comida.add(new Comida("Medias nueves"));
	        comida.add(new Comida("Almuerzo"));
	        comida.add(new Comida("Onces"));
	        comida.add(new Comida("Cena"));
	 
	        listDataChild.put(listDataHeader.get(0), medicamentos); // Header, Child data
	        listDataChild.put(listDataHeader.get(1), comida);
	    }
	}

