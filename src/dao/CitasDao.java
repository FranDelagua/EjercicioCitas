package dao;

import excepciones.AforoMaximoException;
import excepciones.CamposVaciosException;
import excepciones.HistoriaClinicaException;
import modelo.CitaPaciente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitasDao {

	private Connection connection;

	public CitasDao(Connection connection) {
		this.connection = connection;
	}

	public List<CitaPaciente> getCitas(String sql) {
		List<CitaPaciente> lista = new ArrayList<>();

		try	(Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {

			while (rs.next()) {
				try {
					CitaPaciente cita = new CitaPaciente(rs.getInt("idCita"), rs.getString("historiaClinica"),
							rs.getString("nombre"), rs.getString("apellidos"), rs.getString("numTelefono"),
							rs.getString("especialidad"), rs.getString("fechaCita"), rs.getInt("numCita"));
					lista.add(cita);
				} catch (HistoriaClinicaException | CamposVaciosException e) {
					e.printStackTrace();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return lista;
	}

	public void agregarCita(CitaPaciente cita) throws AforoMaximoException, SQLException {

		// Paso 1: Comprobar aforo
		String sqlAforo = "SELECT COUNT(*) FROM citas WHERE especialidad = ? AND fechaCita = ?";
		try (PreparedStatement psAforo = connection.prepareStatement(sqlAforo)) {
			psAforo.setString(1, cita.getEspecialidad());
			psAforo.setString(2, cita.getFechaCita().toString());
			try (ResultSet rs = psAforo.executeQuery()) {
				if (rs.next() && rs.getInt(1) >= 10) {
					throw new AforoMaximoException();
				}
			}
		}

		String sqlNumCita = "SELECT IFNULL(MAX(numCita), 0) + 1 FROM citas WHERE especialidad = ? AND fechaCita = ?";
		int proximoNumCita;
		try (PreparedStatement psNum = connection.prepareStatement(sqlNumCita)) {
			psNum.setString(1, cita.getEspecialidad());
			psNum.setString(2, cita.getFechaCita().toString());
			try (ResultSet rs = psNum.executeQuery()) {
				rs.next();
				proximoNumCita = rs.getInt(1);
			}
		}
		cita.setNumCita(proximoNumCita);

		String sqlInsert = "INSERT INTO citas (historiaClinica, nombre, apellidos, numTelefono, especialidad, fechaCita, numCita) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement psInsert = connection.prepareStatement(sqlInsert)) {
			psInsert.setString(1, cita.getHistoriaClinica());
			psInsert.setString(2, cita.getNombre());
			psInsert.setString(3, cita.getApellidos());
			psInsert.setString(4, cita.getNumTelefono());
			psInsert.setString(5, cita.getEspecialidad());
			psInsert.setString(6, cita.getFechaCita().toString());
			psInsert.setInt(7, cita.getNumCita());
			psInsert.executeUpdate();
		}
	}

	public void modificarCita(int idCita, String especialidad, String nuevaFecha)
			throws AforoMaximoException, SQLException {

		String sqlAforo = "SELECT COUNT(*) FROM citas WHERE especialidad = ? AND fechaCita = ?";
		try (PreparedStatement psAforo = connection.prepareStatement(sqlAforo)) {
			psAforo.setString(1, especialidad);
			psAforo.setString(2, nuevaFecha);
			try (ResultSet rs = psAforo.executeQuery()) {
				if (rs.next() && rs.getInt(1) >= 10) {
					throw new AforoMaximoException();
				}
			}
		}

		String sqlNumCita = "SELECT IFNULL(MAX(numCita), 0) + 1 FROM citas WHERE especialidad = ? AND fechaCita = ?";
		int nuevoNumCita;
		try (PreparedStatement psNum = connection.prepareStatement(sqlNumCita)) {
			psNum.setString(1, especialidad);
			psNum.setString(2, nuevaFecha);
			try (ResultSet rs = psNum.executeQuery()) {
				rs.next();
				nuevoNumCita = rs.getInt(1);
			}
		}

		String sqlSelect = "SELECT historiaClinica, nombre, apellidos, numTelefono FROM citas WHERE idCita = ?";
		String historiaClinica, nombre, apellidos, numTelefono;
		try (PreparedStatement psSelect = connection.prepareStatement(sqlSelect)) {
			psSelect.setInt(1, idCita);
			try (ResultSet rs = psSelect.executeQuery()) {
				if (!rs.next()) {
					throw new SQLException("No se encontró la cita con idCita = " + idCita);
				}
				historiaClinica = rs.getString("historiaClinica");
				nombre = rs.getString("nombre");
				apellidos = rs.getString("apellidos");
				numTelefono = rs.getString("numTelefono");
			}
		}

		String sqlDelete = "DELETE FROM citas WHERE idCita = ?";
		try (PreparedStatement psDelete = connection.prepareStatement(sqlDelete)) {
			psDelete.setInt(1, idCita);
			psDelete.executeUpdate();
		}

		String sqlInsert = "INSERT INTO citas (historiaClinica, nombre, apellidos, numTelefono, especialidad, fechaCita, numCita) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement psInsert = connection.prepareStatement(sqlInsert)) {
			psInsert.setString(1, historiaClinica);
			psInsert.setString(2, nombre);
			psInsert.setString(3, apellidos);
			psInsert.setString(4, numTelefono);
			psInsert.setString(5, especialidad);
			psInsert.setString(6, nuevaFecha);
			psInsert.setInt(7, nuevoNumCita);
			psInsert.executeUpdate();
		}
	}

		public void borrarCita(int idCita) throws SQLException {
			String sql = "DELETE FROM citas WHERE idCita = ?	";
			try (PreparedStatement ps = connection.prepareStatement(sql)) {
				ps.setInt(1, idCita);
				ps.executeUpdate();
			}
	}
}