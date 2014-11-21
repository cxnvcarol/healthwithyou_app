/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.healthwithyou;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import app.healthwithyou.R;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class PrescripcionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.prescripcion_view);

        // Set result CANCELED in case the user backs out
        setResult(Activity.RESULT_OK);
        
        Button btnTomar=(Button) findViewById(R.id.btnTomar);
        Button btnTomarPasada=(Button) findViewById(R.id.btnTomarPasada);
        TextView txtTitulo=(TextView) findViewById(R.id.txtTituloPrescripcion);
        TextView txtDesc=(TextView) findViewById(R.id.txtDescripcion);
        final TextView txtUltima=(TextView) findViewById(R.id.txtUltimoDiaTomada);
        final TextView txtSiguiente=(TextView) findViewById(R.id.txtSiguienteToma);
        txtTitulo.setText(getIntent().getExtras().getString("nombre"));
        txtDesc.setText(getIntent().getExtras().getString("descripcion"));
        
        Date siguiente=new Date();
        siguiente.setTime(siguiente.getTime()+1000*60*60*8);
        txtSiguiente.setText(siguiente.toString());
        
        
        btnTomar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
			       Toast.makeText(getApplicationContext(), "Medicina tomada registrada", Toast.LENGTH_SHORT).show();
			       txtUltima.setText(new Date().toString());
				
			}
		});
btnTomarPasada.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//TODO
			       
			       Date date;
			       try {
			    	   if(txtSiguiente.getText().equals(""))
			    		   throw new Exception("Formato inválido");
					date = new SimpleDateFormat("dd/MM/yyyy").parse(txtSiguiente.getText().toString());
					Toast.makeText(getApplicationContext(), "Medicina tomada registrada", Toast.LENGTH_SHORT).show();
					if(date!=null)
				    	   {
							Date siguiente=new Date(date.getTime());
							siguiente.setTime(siguiente.getTime()+1000*60*60*8);
					        txtSiguiente.setText(siguiente.toString());
							txtUltima.setText(date.toString());
				    	   }
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), "Por favor ingrese la fecha en formato dd/mm/yyyy", Toast.LENGTH_SHORT).show();
					//e.printStackTrace();
				}
			    		   
			       
				
			}
		});
    }
    
    

    
    
}



