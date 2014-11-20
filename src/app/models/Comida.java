package app.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.content.ContentValues;
import android.provider.CalendarContract.Events;

public class Comida {

	private static final long calID = 1;
	private static final int DURACION_TOMA_MILLIS = 30*60*1000;
	private String nombre;
	private String descripcion;
	private ArrayList<String> recomendados;
	private ArrayList<String> prohibidos;
	public Comida(String nombre) {
		recomendados=new ArrayList<String>();
		prohibidos=new ArrayList<String>();
		this.nombre=nombre;
	}
	@Override
	public String toString() {
		return nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}
	public ArrayList<String> getProhibidos() {
		return prohibidos;
	}
	public ArrayList<String> getRecomendados() {
		return recomendados;
	}
	public String getNombre() {
		return nombre;
	}
}
