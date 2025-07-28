package App.Modelo;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


public class MecaCrud implements CrudMecanica {

    private final String SELECT = "SELECT * from mecanica";

    private final String SELECT_BY_ID = "SELECT * from mecanica where id=?";

    private final String INSERT = "INSERT INTO mecanica(cliente, mecanico, servicio, fecha, hora, precio) VALUES(?,?,?,?,?,?)";

    private final String UPDATE = "UPDATE mecanica SET cliente=?, mecanico=?, servicio=?, fecha=?, hora=?, precio=? WHERE id=?";

    private final String DELETE = "DELETE from mecanica where id=?";

    private Connection conn = null;

    private Connection conectar(){
        Conexion conexion = new Conexion();
        conn = conexion.conectar();
        return conn;
    }

    @Override
    public Map<Integer, Mecanica> seleccinarTodo() {
        Map<Integer,Mecanica> map = new LinkedHashMap<Integer,Mecanica>();
        try { Connection conn = this.conectar();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT);

            while (rs.next()){
                Mecanica mecanica = new Mecanica(
                        rs.getInt("id"),
                        rs.getString("cliente"),
                        rs.getString("mecanico"),
                        rs.getString("servicio"),
                        LocalDate.parse(rs.getString("fecha")),
                        LocalTime.parse(rs.getString("hora")),
                        rs.getDouble("precio"));
                System.out.println(mecanica);
                map.put(rs.getInt("id"),mecanica);

            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return map;
    }

    @Override
    public Mecanica buscar(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Mecanica mecanica = null;

        try {conn = this.conectar();
            stmt = conn.prepareStatement(SELECT_BY_ID);
            stmt.setInt(1 ,id);
            rs = stmt.executeQuery(); rs.next();
            mecanica = new Mecanica(
                    rs.getInt("id"),
                    rs.getString("cliente"),
                    rs.getString("mecanico"),
                    rs.getString("servicio"),
                    LocalDate.parse(rs.getString("fecha")),
                    LocalTime.parse(rs.getString("hora")),
                    rs.getDouble("precio"));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return mecanica;
    }

    public void insertar(Mecanica mecanica) {
        String sql = "INSERT INTO mecanica (cliente, mecanico, servicio, fecha, hora, precio) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, mecanica.getCliente());
            pstmt.setString(2, mecanica.getMecanico());
            pstmt.setString(3, mecanica.getServicio());
            pstmt.setString(4, mecanica.getFecha().toString());  // Asegúrate que sea tipo String o Date según usas
            pstmt.setString(5, mecanica.getHora().toString());   // Igualmente, asegúrate del tipo
            pstmt.setDouble(6, mecanica.getPrecio());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new RuntimeException("Ya existe una cita agendada en ese horario. Por favor elige otro.");
            } else {
                throw new RuntimeException("Error al guardar la cita en la base de datos: " + e.getMessage());
            }
        }
    }


    // Asegúrate de que tu método 'actualizar' tenga un manejo de errores similar
    public void actualizar(Mecanica mecanica) {
        String sql = "UPDATE mecanica SET cliente = ?, mecanico = ?, servicio = ?, fecha = ?, hora = ?, precio = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            Conexion conexion = new Conexion();
            conn = conexion.conectar();
            if (conn == null) {
                System.err.println("Error: Conexión nula en MecaCrud.actualizar().");
                throw new SQLException("No se pudo establecer conexión a la base de datos.");
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, mecanica.getCliente());
            pstmt.setString(2, mecanica.getMecanico());
            pstmt.setString(3, mecanica.getServicio());
            pstmt.setString(4, mecanica.getFecha().toString());
            pstmt.setString(5, mecanica.getHora().toString());
            pstmt.setDouble(6, mecanica.getPrecio());
            pstmt.setInt(7, mecanica.getId()); // Asumo que el ID es la clave para actualizar

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Advertencia: La actualización no afectó ninguna fila. ID no encontrado o datos idénticos.");
            }
        } catch (SQLException e) {
            System.err.println("Error SQL al actualizar cita: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar la cita en la base de datos.", e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar PreparedStatement en actualizar: " + e.getMessage());
            } finally {
                Conexion.cerrarConexion(conn);
            }
        }
    }

    @Override
    public void eliminar(int id) {
        try {
            conn = this.conectar();
            PreparedStatement pstmt = conn.prepareStatement(DELETE);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public Map<String, String> obtenerTrabajosConPrecios() {
        Map<String, String> trabajosConPrecios = new HashMap<>();
        String sql = "SELECT \"Trabajo / Servicio\", \"Valor Estimado (USD)\" FROM servicios_mecanica";

        try (Connection conn = new Conexion().conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String trabajo = rs.getString("Trabajo / Servicio");
                String precio = rs.getString("Valor Estimado (USD)");
                trabajosConPrecios.put(trabajo, precio);
            }

        } catch (SQLException e) {
            System.err.println("Error al seleccionar trabajos con precios: " + e.getMessage());
        }

        return trabajosConPrecios;
    }


    public static void main(String[] args) {
        MecaCrud mecaCrud = new MecaCrud();

        mecaCrud.seleccinarTodo();
        //System.out.println(mecaCrud.buscar(5));
        //mecaCrud.insertar(new Mecanica("Anthon Chang", "Roger Grefa", "Cambio de aceite", LocalDate.of(2025,07,15), LocalTime.of(16,30), 150.99));
        //mecaCrud.actualizar(new Mecanica(11,"Anthon Chang", "Roger Grefa", "Cambio de aceite", LocalDate.of(2025,07,15), LocalTime.of(16,30), 299));
        //mecaCrud.eliminar(6);
    }
}
