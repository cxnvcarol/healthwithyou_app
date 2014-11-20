package app.yourpersonalnurse;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
/**
	 * A placeholder fragment containing a simple view.
	 */
	public class DataFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static DataFragment newInstance(int sectionNumber) {
			DataFragment fragment = new DataFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		private TextView txtPeso;
		private TextView txtGlucosa;
		private TextView txtHeartRate;
		private TextView txtTension;
		private TextView txtComentarios;
		public void actualizarDato(String key, double valor) {
			((NurseActivity)getActivity()).dbInsertRegistroMedida(key, valor);
			msgToast(key+" actualizado");
			}
		public void actualizarDato(String key, String valorStr) {
			try{
				
			
			double valor=Double.parseDouble(valorStr);
			((NurseActivity)getActivity()).dbInsertRegistroMedida(key, valor);
			
			sendToServer(key,valorStr);
			
			
			msgToast(key+" actualizado");
			}
			catch(Exception e)
			{
				msgToast("Por favor ingrese un valor válido");
				e.printStackTrace();
			}
		}

		private void sendToServer(String key, String valorStr) {
			try {
			HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://healthserver-quot.rhcloud.com/measure/544e32f35908eba03c7627d0");
		    
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    nameValuePairs.add(new BasicNameValuePair("name", key));
		    nameValuePairs.add(new BasicNameValuePair("value", valorStr));
		    
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        BufferedReader br=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		private void msgToast(String string) {
			Toast.makeText(getActivity(), string, Toast.LENGTH_SHORT).show();
			
		}
		public DataFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_data,
					container, false);
			txtPeso=(TextView) rootView.findViewById(R.id.txtPeso);
			txtGlucosa=(TextView) rootView.findViewById(R.id.txtAzucar);
			txtHeartRate=(TextView) rootView.findViewById(R.id.txtFrecCardiaca);
			txtTension=(TextView) rootView.findViewById(R.id.txtTension1);
			txtComentarios=(TextView) rootView.findViewById(R.id.txtComentarios);
			ImageButton btnActualizarPeso=(ImageButton)rootView.findViewById(R.id.btnActualizarPeso);
			ImageButton btnActualizarComentarios=(ImageButton)rootView.findViewById(R.id.btnActualizarComentarios);
			ImageButton btnActualizarGlucosa=(ImageButton)rootView.findViewById(R.id.btnActualizarGlucosa);
			ImageButton btnActualizarHeartRate=(ImageButton)rootView.findViewById(R.id.btnActualizarHearRate);
			ImageButton btnActualizarTension=(ImageButton)rootView.findViewById(R.id.btnActualizarTension);
			
			View.OnClickListener clickActualizar=new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					actualizaCampo(v.getId());					
				}
			};
			btnActualizarPeso.setOnClickListener(clickActualizar);
			btnActualizarGlucosa.setOnClickListener(clickActualizar);
			btnActualizarHeartRate.setOnClickListener(clickActualizar);
			btnActualizarTension.setOnClickListener(clickActualizar);
			btnActualizarComentarios.setOnClickListener(clickActualizar);
			return rootView;
		}
		
		private void actualizaCampo(int idParam) {
			switch (idParam) {
			case R.id.btnActualizarPeso:				
				actualizarDato("peso", txtPeso.getText().toString());
				break;
			case R.id.btnActualizarGlucosa:						
				actualizarDato("glucosa",txtGlucosa.getText().toString());
				break;
			case R.id.btnActualizarHearRate:
				actualizarDato("heartrate", txtHeartRate.getText().toString());
				break;
			case R.id.btnActualizarTension:						
				actualizarDato("tension", txtTension.getText().toString());
				break;
			case R.id.btnActualizarComentarios:						
				break;

			default:
				break;
			}
		}
	}

