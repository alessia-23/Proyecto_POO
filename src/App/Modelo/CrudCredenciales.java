package App.Modelo;

import java.util.Map;

public interface CrudCredenciales {

    //Mostar todos
    public Map<Integer, Credenciales>
    escogerTodo();

    //Mostar Uno
    public Credenciales indagar(int id);

    //Insertar
    public void insert(Credenciales credenciales);

    //Actualizar
    public void update(Credenciales credenciales);

    //Eliminar
    public void delete(int id);
}
