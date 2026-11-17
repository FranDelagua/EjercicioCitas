package modelo;

import excepciones.CamposVaciosException;
import excepciones.HistoriaClinicaException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class CitaPaciente {

	private int idCita;
	private String historiaClinica;
	private String nombre;
	private String apellidos;
	private String numTelefono;
	private String especialidad;
	private LocalDate fechaCita;
	private int numCita;

	public CitaPaciente() {
	}

	public CitaPaciente(String historiaClinica, String nombre, String apellidos, String numTelefono,
			String especialidad, String fechaCita, int numCita)
			throws HistoriaClinicaException, CamposVaciosException, DateTimeParseException {

		setHistoriaClinica(historiaClinica);
		setNombre(nombre);
		setApellidos(apellidos);
		setNumTelefono(numTelefono);
		setEspecialidad(especialidad);
		setFechaCita(fechaCita);
		setNumCita(numCita);
	}

	public CitaPaciente(int idCita, String historiaClinica, String nombre, String apellidos, String numTelefono,
			String especialidad, String fechaCita, int numCita)
			throws HistoriaClinicaException, CamposVaciosException, DateTimeParseException {

		this.idCita = idCita;
		setHistoriaClinica(historiaClinica);
		setNombre(nombre);
		setApellidos(apellidos);
		setNumTelefono(numTelefono);
		setEspecialidad(especialidad);
		setFechaCita(fechaCita);
		setNumCita(numCita);
	}

	public int getIdCita() {
		return idCita;
	}

	public String getHistoriaClinica() {
		return historiaClinica;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public String getNumTelefono() {
		return numTelefono;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public LocalDate getFechaCita() {
		return fechaCita;
	}

	public int getNumCita() {
		return numCita;
	}

	public void setHistoriaClinica(String historiaClinica) throws HistoriaClinicaException, CamposVaciosException {
		if (historiaClinica == null || historiaClinica.trim().isEmpty()) {
			throw new CamposVaciosException();
		}
		if (!compruebaCodBarras(historiaClinica)) {
			throw new HistoriaClinicaException();
		}
		this.historiaClinica = historiaClinica;
	}

	public void setNombre(String nombre) throws CamposVaciosException {
		if (nombre == null || nombre.trim().isEmpty()) {
			throw new CamposVaciosException();
		}
		this.nombre = nombre;
	}

	public void setApellidos(String apellidos) throws CamposVaciosException {
		if (apellidos == null || apellidos.trim().isEmpty()) {
			throw new CamposVaciosException();
		}
		this.apellidos = apellidos;
	}

	public void setNumTelefono(String numTelefono) throws CamposVaciosException {
		if (numTelefono == null || !numTelefono.matches("\\d{9}")) {
			throw new CamposVaciosException();
		}
		this.numTelefono = numTelefono;
	}

	public void setEspecialidad(String especialidad) throws CamposVaciosException {
		if (especialidad == null || especialidad.trim().isEmpty()) {
			throw new CamposVaciosException();
		}
		this.especialidad = especialidad;
	}

	public void setFechaCita(String fechaCita) throws DateTimeParseException {
		this.fechaCita = LocalDate.parse(fechaCita, DateTimeFormatter.ISO_LOCAL_DATE);
	}

	public void setNumCita(int numCita) {
		this.numCita = numCita;
	}

	private boolean compruebaCodBarras(String codigo) {
		boolean correcto = false;
		codigo = codigo.replaceAll("-", "");
		// System.out.println(isbn);
		double isb = Double.parseDouble(codigo);
		char DC = codigo.charAt(codigo.length() - 1);
		int DCint = Integer.parseInt(Character.toString(DC));
		// System.out.println(DCint);
		int suma = 0;
		int cont = 1;
		int multi = 0;
		for (int x = 0; x < codigo.length() - 1; x++) {

			char c = codigo.charAt(x);
			int num = Integer.parseInt(Character.toString(c));
			if (cont % 2 != 0)
				multi = num * 1;
			else
				multi = num * 3;
			suma += multi;
			cont++;
		}
		// System.out.println(suma);
		int dc = 10 - (suma % 10);
		// System.out.println("dc ="+dc);
		if (dc == 10)
			dc = 0;
		if (dc == DCint)
			correcto = true;
		return correcto;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CitaPaciente that = (CitaPaciente) o;
		return numCita == that.numCita && Objects.equals(historiaClinica, that.historiaClinica);
	}

	@Override
	public int hashCode() {
		return Objects.hash(historiaClinica, numCita);
	}

	@Override
	public String toString() {
		return "Cita #" + numCita + " │ " + fechaCita + " │ " + especialidad + " │ [" + historiaClinica + "] "
				+ apellidos + ", " + nombre + " - Tlf: " + numTelefono;
	}
}