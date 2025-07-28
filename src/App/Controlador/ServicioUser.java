package App.Controlador;

import App.Modelo.*;

import javax.swing.*;
import java.util.Map;

public class ServicioUser {

    private CrudCredenciales acceder = new CredenCrud();

    public Map<Integer, Credenciales> escogerTodo() {
        return acceder.escogerTodo();
    }

    public void insert(Credenciales credenciales) {
        acceder.insert(credenciales);
    }

    public void update(Credenciales credenciales) {
        acceder.update(credenciales);
    }

    public void delete(int id){
        if(JOptionPane.showConfirmDialog(null,"Esta seguro que desea eliminar registro?",
                "WARING",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_NO_OPTION){
            acceder.delete(id);
        }
    }
}
