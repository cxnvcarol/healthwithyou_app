package app.models;

import java.util.Date;

public class RegistroMedida {

	private String nombre;
	private double valor;
	private Date fecha;
	
	public RegistroMedida(String nombreP, double valorP, Date fechaP) {
		nombre=nombreP;
		valor=valorP;
		fecha=fechaP;
	}
	@Override
	public String toString() {
		return nombre;
	}
	
	public Date getFecha(){
		return fecha;
	}
	public String getNombre() {
		return nombre;
	}
	public double getValor() {
		return valor;
	}
	
	
}
