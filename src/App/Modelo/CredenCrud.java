package App.Modelo;

import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CredenCrud implements CrudCredenciales{

    private final String SELECT = "SELECT * from credenciales";

    private final String SELECT_BY_ID = "SELECT * from credenciales where id=?";

    private final String INSERT = "INSERT INTO credenciales(user, password, rol) VALUES(?,?,?)";

    private final String UPDATE = "UPDATE credenciales SET user=?, password=?, rol=? WHERE id=?";

    private final String DELETE = "DELETE from credenciales where id=?";

    private Connection conn = null;

    private Connection conectar(){
        Conexion conexion = new Conexion();
        conn = conexion.conectar();
        return conn;
    }

    @Override
    public Map<Integer, Credenciales> escogerTodo() {
        Map<Integer,Credenciales> map = new LinkedHashMap<Integer,Credenciales>();
        try { Connection conn = this.conectar();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SELECT);

            while (rs.next()){
                Credenciales credenciales = new Credenciales(
                        rs.getInt("id"),
                        rs.getString("user"),
                        rs.getString("password"),
                        rs.getString("rol"));
                System.out.println(credenciales);
                map.put(rs.getInt("id"),credenciales);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return map;
    }

    @Override
    public Credenciales indagar(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Credenciales credenciales = null;

        try {conn = this.conectar();
            stmt = conn.prepareStatement(SELECT_BY_ID);
            stmt.setInt(1 ,id);
            rs = stmt.executeQuery(); rs.next();
            credenciales = new Credenciales(
                    rs.getInt("id"),
                    rs.getString("user"),
                    rs.getString("password"),
                    rs.getString("rol"));
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        return credenciales;
    }

    @Override
    public void insert(Credenciales credenciales) {
        try {
            Connection conn = this.conectar();
            PreparedStatement pstmt = conn.prepareStatement(INSERT);
            pstmt.setString(1, credenciales.getUser());
            pstmt.setString(2, credenciales.getPassword());
            pstmt.setString(3, credenciales.getRol());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void update(Credenciales credenciales) {
        try {
            conn = this.conectar();
            PreparedStatement pstmt = conn.prepareStatement(UPDATE);
            pstmt.setString(1, credenciales.getUser());
            pstmt.setString(2, credenciales.getPassword());
            pstmt.setString(3, credenciales.getRol());
            pstmt.setInt(7, credenciales.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            conn = this.conectar();
            PreparedStatement pstmt = conn.prepareStatement(DELETE);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public static void main(String[] args) {
        CredenCrud credenCrud = new CredenCrud();

        //credenCrud.escogerTodo();
        //System.out.println(credenCrud.indagar(3));
        //credenCrud.insert(new Credenciales("Sergio", "999", "Administrador"));
        //credenCrud.update(new Credenciales(5,"Sergio", "999", "Administrador"));
        //credenCrud.delete(5);
    }
}
