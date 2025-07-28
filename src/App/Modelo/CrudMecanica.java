package App.Modelo;

import java.util.List;
import java.util.Map;

public interface CrudMecanica {

    //Mostar todos
    public Map<Integer, Mecanica>
    seleccinarTodo();


    public Map<String, String> obtenerTrabajosConPrecios();



    //Mostar Uno
    public Mecanica buscar(int id);

    //Insertar
    public void insertar(Mecanica mecanica);

    //Actualizar
    public void actualizar(Mecanica mecanica);

    //Eliminar
    public void eliminar(int id);
}
