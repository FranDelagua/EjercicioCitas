package vista;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import controlador.CitasController;
import dao.DbConnection;
import modelo.CitaPaciente;

public class Main {
	static Connection cn;
	static DbConnection dbConn;

	public static void main(String[] args) {

		frmCitas frm = new frmCitas();
	}

}
