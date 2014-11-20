package app.healthwithyou;

import java.util.ArrayList;
import java.util.Arrays;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.jjoe64.graphview.*;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import app.models.GraphViewData;
import app.yourpersonalnurse.R;

/**
	 * A placeholder fragment containing a simple view.
	 */
	public class SeguimientoFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static SeguimientoFragment newInstance(int sectionNumber) {
			SeguimientoFragment fragment = new SeguimientoFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}
		private XYPlot plotPeso;
		private XYPlot plotGlucosa;
		private XYPlot plotHeartBeat;

		public SeguimientoFragment() {
		}

		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_seguimiento,
					container, false);
			Button btnActualizar = (Button)rootView.findViewById(R.id.btnActualizarGraficos);
			btnActualizar.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					actualizarPlots();
					System.out.println("plots actualizadas");
				}
			});
			plotPeso = (XYPlot) rootView.findViewById(R.id.plotPeso);
			plotGlucosa = (XYPlot) rootView.findViewById(R.id.plotGlucosa);
			plotHeartBeat = (XYPlot) rootView.findViewById(R.id.plotHearRate);
			
			actualizarPlots();
			
			return rootView;
		}
		private void actualizarPlots() {
			
			fillPlot(plotPeso,"peso");
			fillPlot(plotGlucosa,"glucosa");
			fillPlot(plotHeartBeat, "heartrate");
			plotPeso.invalidate();
			plotGlucosa.invalidate();
			plotHeartBeat.invalidate();

		}
		private void fillPlot(XYPlot plot,String key) {
			plot.clear();
			
			ArrayList<Double> arrayPeso=((NurseActivity)getActivity()).dbGetHistorialMedidas(key);
			Number[] pesos=new Number[arrayPeso.size()];
			for (int i = 0; i < pesos.length; i++) {
				pesos[i]=(Double)arrayPeso.get(i);
				System.out.println(i+":"+pesos[i]);
			}
	        // Turn the above arrays into XYSeries':
	        XYSeries series1 = new SimpleXYSeries(Arrays.asList(pesos),SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,"");          
	        LineAndPointFormatter series1Format = new LineAndPointFormatter();
	        series1Format.setPointLabelFormatter(new PointLabelFormatter());
	        series1Format.configure(getActivity(),R.xml.line_point_formater_1);
	 
	        plot.addSeries(series1, series1Format);
	        plot.setTicksPerRangeLabel(3);
	        plot.getGraphWidget().setDomainLabelOrientation(-45);
	    }
		public void msgToast(String msg)
		{
			System.out.println("toast: "+msg);
			Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
		}
	}

