package App.Modelo;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MecaCrud implements CrudMecanica {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String SELECT = "SELECT * from mecanica";
    private final String SELECT_BY_ID = "SELECT * from mecanica where id=?";
    private final String INSERT = "INSERT INTO mecanica(cliente, mecanico, servicio, fecha, hora, precio) VALUES(?,?,?,?,?,?)";
    private final String UPDATE = "UPDATE mecanica SET cliente=?, mecanico=?, servicio=?, fecha=?, hora=?, precio=? WHERE id=?";
    private final String DELETE = "DELETE from mecanica where id=?";

    @Override
    public Map<Integer, Mecanica> seleccinarTodo() {
        Map<Integer, Mecanica> map = new LinkedHashMap<>();
        try (Connection conn = new Conexion().conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT)) {

            if (conn == null) {
                System.err.println("Error: No se pudo obtener conexión para seleccionar todo.");
                return map;
            }

            while (rs.next()) {
                Mecanica mecanica = new Mecanica(
                        rs.getInt("id"),
                        rs.getString("cliente"),
                        rs.getString("mecanico"),
                        rs.getString("servicio"),
                        LocalDate.parse(rs.getString("fecha"), DATE_FORMATTER),
                        LocalTime.parse(rs.getString("hora"), TIME_FORMATTER),
                        rs.getDouble("precio"));
                System.out.println("Cita recuperada: " + mecanica);
                map.put(rs.getInt("id"), mecanica);
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al seleccionar todas las citas: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al seleccionar todas las citas: " + e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Mecanica buscar(int id) {
        Mecanica mecanica = null;
        try (Connection conn = new Conexion().conectar();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID)) {

            if (conn == null) {
                System.err.println("Error: No se pudo obtener conexión para buscar por ID.");
                return null;
            }

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    mecanica = new Mecanica(
                            rs.getInt("id"),
                            rs.getString("cliente"),
                            rs.getString("mecanico"),
                            rs.getString("servicio"),
                            LocalDate.parse(rs.getString("fecha"), DATE_FORMATTER),
                            LocalTime.parse(rs.getString("hora"), TIME_FORMATTER),
                            rs.getDouble("precio"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al buscar cita por ID: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al buscar cita por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return mecanica;
    }

    @Override
    public void insertar(Mecanica mecanica) {
        String sql = "INSERT INTO mecanica (cliente, mecanico, servicio, fecha, hora, precio) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = new Conexion().conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                throw new SQLException("No se pudo establecer conexión a la base de datos para la inserción.");
            }

            pstmt.setString(1, mecanica.getCliente());
            pstmt.setString(2, mecanica.getMecanico());
            pstmt.setString(3, mecanica.getServicio());
            pstmt.setString(4, mecanica.getFecha().format(DATE_FORMATTER));
            pstmt.setString(5, mecanica.getHora().format(TIME_FORMATTER));
            pstmt.setDouble(6, mecanica.getPrecio());

            pstmt.executeUpdate();
            System.out.println("Cita insertada exitosamente.");

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint failed")) {
                throw new RuntimeException("Ya existe una cita con un identificador único en conflicto. " + e.getMessage(), e);
            } else if (e.getMessage() != null && e.getMessage().contains("database is locked")) {
                throw new RuntimeException("Error al guardar la cita: La base de datos está bloqueada. Intente de nuevo. " + e.getMessage(), e);
            } else {
                throw new RuntimeException("Error al guardar la cita en la base de datos: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado al insertar la cita: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Mecanica mecanica) {
        String sql = "UPDATE mecanica SET cliente = ?, mecanico = ?, servicio = ?, fecha = ?, hora = ?, precio = ? WHERE id = ?";

        try (Connection conn = new Conexion().conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (conn == null) {
                System.err.println("Error: Conexión nula en MecaCrud.actualizar().");
                throw new SQLException("No se pudo establecer conexión a la base de datos.");
            }

            pstmt.setString(1, mecanica.getCliente());
            pstmt.setString(2, mecanica.getMecanico());
            pstmt.setString(3, mecanica.getServicio());
            pstmt.setString(4, mecanica.getFecha().format(DATE_FORMATTER));
            pstmt.setString(5, mecanica.getHora().format(TIME_FORMATTER));
            pstmt.setDouble(6, mecanica.getPrecio());
            pstmt.setInt(7, mecanica.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Advertencia: La actualización no afectó ninguna fila. ID no encontrado o datos idénticos.");
            } else {
                System.out.println("Cita actualizada exitosamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al actualizar cita: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar la cita en la base de datos.", e);
        } catch (Exception e) {
            System.err.println("Error inesperado al actualizar la cita: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error inesperado al actualizar la cita.", e);
        }
    }

    @Override
    public void eliminar(int id) {
        try (Connection conn = new Conexion().conectar();
             PreparedStatement pstmt = conn.prepareStatement(DELETE)) {

            if (conn == null) {
                throw new SQLException("No se pudo establecer conexión a la base de datos para la eliminación.");
            }

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Cita eliminada exitosamente.");
            } else {
                System.out.println("No se encontró la cita con ID " + id + " para eliminar.");
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al eliminar cita: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al eliminar la cita: " + e.getMessage(), "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Error inesperado al eliminar la cita: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error inesperado al eliminar la cita: " + e.getMessage(), "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Map<String, String> obtenerTrabajosConPrecios() {
        Map<String, String> trabajosConPrecios = new HashMap<>();
        String sql = "SELECT \"Trabajo / Servicio\", \"Valor Estimado (USD)\" FROM servicios_mecanica";

        try (Connection conn = new Conexion().conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (conn == null) {
                System.err.println("Error: No se pudo obtener conexión para obtener trabajos con precios.");
                return trabajosConPrecios;
            }

            while (rs.next()) {
                String trabajo = rs.getString("Trabajo / Servicio");
                String precio = rs.getString("Valor Estimado (USD)");
                trabajosConPrecios.put(trabajo, precio);
            }

        } catch (SQLException e) {
            System.err.println("Error al seleccionar trabajos con precios: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado al obtener trabajos con precios: " + e.getMessage());
            e.printStackTrace();
        }

        return trabajosConPrecios;
    }

    public static void main(String[] args) {
        MecaCrud mecaCrud = new MecaCrud();

        System.out.println("--- Probando seleccionarTodo ---");
        mecaCrud.seleccinarTodo().forEach((id, mecanica) -> System.out.println(id + ": " + mecanica));

        System.out.println("\n--- Probando obtenerTrabajosConPrecios ---");
        mecaCrud.obtenerTrabajosConPrecios().forEach((trabajo, precio) -> System.out.println(trabajo + ": " + precio));
    }
}
