package App.Vista;

import App.Controlador.ValidarUser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    private JPanel panel2;
    private JTextField txtUsuario;
    private JPasswordField txtPass;
    private JComboBox<String> cmbRol;
    private JButton btnIniciar;
    private JButton btnLimpiar;

    private ValidarUser validarUser;

    public Login() {
        setTitle("Login");
        setSize(500, 400);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel2);
        setLocationRelativeTo(null);

        validarUser = new ValidarUser();

        // Activar el enter para iniciar sesión
        getRootPane().setDefaultButton(btnIniciar);

        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUsuario.getText().trim();
                String password = new String(txtPass.getPassword());
                String perfil = (String) cmbRol.getSelectedItem();

                if (validarCredenciales(user, password, perfil)) {
                    // Pasa el rol al constructor para configurar permisos en GestionCitas
                    GestionCitas g = new GestionCitas(perfil);
                    g.setVisible(true);
                    setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(Login.this, "Usuario, contraseña o rol incorrectos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtUsuario.setText("");
                txtPass.setText("");
                txtUsuario.requestFocus();
            }
        });
    }

    private boolean validarCredenciales(String usuario, String contrasena, String perfil) {
        return validarUser.sonCredencialesValidas(usuario, contrasena, perfil);
    }

    /*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setVisible(true);
        });
    }
    */
}
