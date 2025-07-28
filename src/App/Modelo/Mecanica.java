package App.Modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Mecanica {

    private int id;
    private String cliente;
    private String mecanico;
    private String servicio;
    private LocalDate fecha;
    private LocalTime hora;
    private Double precio;

    public Mecanica(int id, String cliente, String mecanico, String servicio, LocalDate fecha, LocalTime hora, Double precio) {
        this.id = id;
        this.cliente = cliente;
        this.mecanico = mecanico;
        this.servicio = servicio;
        this.fecha = fecha;
        this.hora = hora;
        this.precio = precio;
    }

    public Mecanica(String cliente, String mecanico, String servicio, String fecha, String hora, Double precio) {
        this.cliente = cliente;
        this.mecanico = mecanico;
        this.servicio = servicio;
        this.fecha = LocalDate.parse(fecha);
        this.hora = LocalTime.parse(hora);
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Mecanica{" +
                "id=" + id +
                ", cliente='" + cliente + '\'' +
                ", mecanico='" + mecanico + '\'' +
                ", servicio='" + servicio + '\'' +
                ", fecha=" + fecha +
                ", hora=" + hora +
                ", precio=" + precio +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public String getMecanico() {
        return mecanico;
    }

    public String getServicio() {
        return servicio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public Double getPrecio() {
        return precio;
    }
}
