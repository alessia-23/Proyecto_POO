package App;
import App.Vista.Login;

import javax.swing.*;
public class Mainapp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}
