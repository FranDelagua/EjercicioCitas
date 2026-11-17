package controlador;

import dao.CitasDao;
import dao.DbConnection;
import excepciones.AforoMaximoException;
import excepciones.CamposVaciosException;
import excepciones.HistoriaClinicaException;
import modelo.CitaPaciente;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CitasController {

	private List<CitaPaciente> citas;
	private Connection cn;
	private DbConnection dbConn;

	public CitasController(Connection cn) {
		this.cn = cn;
		this.citas = new ArrayList<>();
	}

	public void abrirConexion() throws SQLException {
			dbConn = new DbConnection();
		cn = dbConn.getConnection();
	}

	public void cerrarConexion() throws SQLException {
		if (dbConn != null) {
			dbConn.disconnect();
		}
		cn = null;
	}

	public List<CitaPaciente> getCitas(String sql) {
		CitasDao dao = new CitasDao(cn);
		return dao.getCitas(sql);
	}

	public void solicitarCita(String historiaClinica, String nombre, String apellidos, String numTelefono,
			String especialidad, String fechaCita) throws HistoriaClinicaException, CamposVaciosException,
			DateTimeParseException, AforoMaximoException, SQLException {

		CitaPaciente cita = new CitaPaciente(historiaClinica, nombre, apellidos, numTelefono, especialidad, fechaCita,
				0);

		CitasDao dao = new CitasDao(cn);
		dao.agregarCita(cita);
	}		

	public void modificarCita(int idCita, String especialidad, String nuevaFecha)
			throws AforoMaximoException, SQLException {

		CitasDao dao = new CitasDao(cn);
		dao.modificarCita(idCita, especialidad, nuevaFecha);
	}

	public boolean borrarCita(int idCita) {
		try {
			CitasDao dao = new CitasDao(cn);
			dao.borrarCita(idCita);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<CitaPaciente> getCitasPorEspecialidad(String especialidad) {
		try {
			String sql = "SELECT * FROM citas WHERE especialidad = '" + especialidad + "'";
			return getCitas(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public int getDisponibilidad(String especialidad, String fecha) {
		try {
			LocalDate.parse(fecha);
			String sql = "SELECT * FROM citas WHERE especialidad = '" + especialidad + "' AND fechaCita = '" + fecha
					+ "'";
			List<CitaPaciente> citasDelDia = getCitas(sql);
			return 10 - citasDelDia.size();
		} catch (DateTimeParseException e) {
			e.printStackTrace();
			return 0;
		}
	}

	public List<CitaPaciente> getHistorialPaciente(String historiaClinica) {
		String sql = "SELECT * FROM citas WHERE historiaClinica = '" + historiaClinica + "' ORDER BY fechaCita";
		return getCitas(sql);
	}
}