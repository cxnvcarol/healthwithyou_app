package app.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.ContentValues;
import android.provider.CalendarContract.Events;

public class Medicamento {

	private static final long calID = 1;
	private static final int DURACION_TOMA_MILLIS = 30*60*1000;
	private String nombre;
	private String descripcion;
	private ArrayList<Date> historial;
	private Date nextDate;
	private Date lastDate;
	private Date firstDay;
	private ContentValues valuesAlarm;
	private String rrule;
	
	public Medicamento(String nombreP, String descripcionP, Date firstDay, String rrule, boolean tieneAlarma) {
		nombre=nombreP;
		descripcion=descripcionP;
		this.rrule=rrule;
		this.firstDay=firstDay;
		historial=new ArrayList<Date>();
		
		if(tieneAlarma)
			{
				setEventsAlarm();
				
			}
	}
	public ContentValues getValuesAlarm() {
		return valuesAlarm;
	}
	
	private void setEventsAlarm() {
		//TODO take count of real dates!!
		long startMillis = 0; 
		long endMillis = 0;     
		Calendar beginTime = Calendar.getInstance();
		//beginTime.set(firstDay.getYear(), firstDay.getMonth(), firstDay.getDate(),firstDay.getHours(), firstDay.getMinutes());
		beginTime.set(2014,9,16,9,15);
		startMillis = beginTime.getTimeInMillis();
		//Calendar endTime = Calendar.getInstance();
		//endTime=beginTime+DURACION_TOMA_MILLIS;
		endMillis = startMillis+DURACION_TOMA_MILLIS;

		valuesAlarm = new ContentValues();
		valuesAlarm.put(Events.DTSTART, startMillis);
		valuesAlarm.put(Events.DTEND, endMillis); 
		valuesAlarm.put(Events.TITLE, "Prescripción médica");
		valuesAlarm.put(Events.DESCRIPTION, "Tomar "+nombre);
		valuesAlarm.put(Events.CALENDAR_ID, calID);
		valuesAlarm.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
		valuesAlarm.put(Events.RRULE,rrule);
		valuesAlarm.put(Events.HAS_ALARM, true);
	}
	
	@Override
	public String toString() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}
	public Date getNextDate() {
		return nextDate;
	}
	public String getNombre() {
		return nombre;
	}
	public String getRrule() {
		return rrule;
	}
	public Date getLastDate() {
		return lastDate;
	}
	
	public void tomarAhora() {
		historial.add(new Date());

	}
	
	public void tomarPasada(Date fechaPasada) {
		historial.add(fechaPasada);

	}
	
	
}
