package App.Controlador;

import App.Modelo.MecaCrud;
import App.Modelo.Mecanica;
import java.util.Map;
import javax.swing.JOptionPane;

public class ServicioMeca {

    private MecaCrud implementacion = new MecaCrud();


    public Map<Integer, Mecanica> seleccionarTodo() {
        return implementacion.seleccinarTodo();
    }


    public Map<String, String> obtenerTrabajosConPrecios() {
        return implementacion.obtenerTrabajosConPrecios(); //
    }


    public void insertar(Mecanica mecanica) {
        implementacion.insertar(mecanica);
    }

    public void actualizar(Mecanica mecanica) {
        implementacion.actualizar(mecanica);
    }

    public void eliminar(int id) {
        if(JOptionPane.showConfirmDialog(null, "¿Estás seguro de que quieres eliminar este elemento?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION){
            implementacion.eliminar(id);
        }
    }
}