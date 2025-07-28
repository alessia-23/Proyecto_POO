package App.Controlador;

import App.Modelo.Conexion;
import App.Modelo.Credenciales;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ValidarUser {
    private static final String SQL_VALIDAR = "SELECT id FROM credenciales WHERE user = ? AND password = ? AND rol = ? LIMIT 1";

    public Credenciales validarCredenciales(String usuario, String password, String rol) {
        try (Connection conn = new Conexion().conectar();
             PreparedStatement pstmt = conn.prepareStatement(SQL_VALIDAR)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, password);
            pstmt.setString(3, rol);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Credenciales(
                            rs.getInt("id"),
                            usuario,
                            password,
                            rol);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
        }
        return null;
    }

    // Versión simplificada que solo retorna boolean
    public boolean sonCredencialesValidas(String usuario, String password, String rol) {
        try (Connection conn = new Conexion().conectar();
             PreparedStatement pstmt = conn.prepareStatement(SQL_VALIDAR)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, password);
            pstmt.setString(3, rol);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al validar credenciales: " + e.getMessage());
            return false;
        }
    }
}
