package App.Modelo;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // Ruta relativa al directorio desde donde se ejecuta el JAR
    private final String rutaArchivodb = "base_datos/prog.db";

    public Connection conectar() {
        // Obtener ruta absoluta al archivo
        File archivoDB = new File(rutaArchivodb);
        String url = "jdbc:sqlite:" + archivoDB.getAbsolutePath();

        // Verificar si el archivo existe antes de conectarse
        if (!archivoDB.exists()) {
            System.err.println("El archivo de base de datos no se encontró en: " + archivoDB.getAbsolutePath());
            return null;
        }

        try {
            Connection conn = DriverManager.getConnection(url);
            System.out.println("Conexión exitosa a la base de datos.");
            return conn;
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void cerrarConexion(Connection conn) {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.err.println("Error cerrando la conexión: " + e.getMessage());
        }
    }
}
