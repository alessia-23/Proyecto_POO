package App.Vista;

import App.Controlador.ServicioMeca;
import App.Controlador.ValidarHorario;
import App.Modelo.Mecanica;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Map;

public class GestionCitas extends JFrame {
    private JTextField txtCliente;
    private JTextField txtServicio;
    private JTextField txtPrecio;
    private JButton btnNuevo;
    private JButton btnMostrar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JTable table1;
    private JButton btnImprimir;
    private JPanel panel3;
    private DateTimePicker Horario;
    private JComboBox comboBox1;
    private JButton btnSalir;
    private JButton agregarTrabajosButton;
    private String clave;

    private DefaultTableModel model;
    private Object[] columns = {"Id", "cliente", "mecanico", "servicio", "fecha", "hora", "Precio"};
    private Object[] row = new Object[7];
    private Map<Integer, Mecanica> mapa = null;
    private ServicioMeca ser = new ServicioMeca();

    // NUEVO: Atributo para rol del usuario
    private String rolUsuario;

    public void setPrecioTotalServicios(double totalPrecio) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
        txtPrecio.setText(currencyFormatter.format(totalPrecio));
        System.out.println("DEBUG - GestionCitas: Recibido totalPrecio de Trabajos: " + totalPrecio + ", mostrado como: " + txtPrecio.getText());
    }

    public void setServiciosSeleccionados(String servicios) {
        txtServicio.setText(servicios);
    }

    public void obtenerRegistrosTabla() {
        model = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int filas, int columnas) {
                return false;
            }
        };

        model.setColumnIdentifiers(columns);
        table1.setModel(model);

        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        mapa = ser.seleccionarTodo();

        NumberFormat currencyFormatterForTable = NumberFormat.getCurrencyInstance(new Locale("en", "US"));

        for (Map.Entry<Integer, Mecanica> entry : mapa.entrySet()) {
            row[0] = entry.getKey();
            row[1] = entry.getValue().getCliente();
            row[2] = entry.getValue().getMecanico();
            row[3] = entry.getValue().getServicio();
            row[4] = entry.getValue().getFecha();
            row[5] = entry.getValue().getHora();
            row[6] = currencyFormatterForTable.format(entry.getValue().getPrecio());
            model.addRow(row);
        }
        limpiarCampos();

        table1.setModel(model);

        table1.getColumnModel().getColumn(0).setPreferredWidth(15);
        table1.getColumnModel().getColumn(1).setPreferredWidth(100);
        table1.getColumnModel().getColumn(2).setPreferredWidth(125);
        table1.getColumnModel().getColumn(3).setPreferredWidth(125);
        table1.getColumnModel().getColumn(4).setPreferredWidth(75);
        table1.getColumnModel().getColumn(5).setPreferredWidth(50);
        table1.getColumnModel().getColumn(6).setPreferredWidth(30);

    }

    public void limpiarCampos() {
        txtCliente.setText("");
        txtServicio.setText("");
        txtPrecio.setText("");
        Horario.clear();
        comboBox1.setSelectedIndex(0);
        clave = null;
    }

    // MODIFICADO: Constructor recibe el rol del usuario
    public GestionCitas(String rolUsuario) {
        this.rolUsuario = rolUsuario;

        setTitle("Gestion de Citas");
        setSize(650, 450);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel3);
        setLocationRelativeTo(null);

        txtServicio.setEditable(false);
        txtPrecio.setEditable(false);

        // Configurar permisos según rol
        configurarPermisosPorRol(rolUsuario);

        DatePickerSettings dateSettings = Horario.getDatePicker().getSettings();
        dateSettings.setAllowEmptyDates(false);
        dateSettings.setDateRangeLimits(LocalDate.now(), null); //Restriccion de fechas

        obtenerRegistrosTabla();

        table1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = table1.getSelectedRow();
                if (i >= 0) {
                    clave = model.getValueAt(i, 0).toString();
                    txtCliente.setText(model.getValueAt(i, 1).toString());
                    comboBox1.setSelectedItem(model.getValueAt(i, 2).toString());
                    txtServicio.setText(model.getValueAt(i, 3).toString());
                    txtPrecio.setText(model.getValueAt(i, 6).toString());

                    try {
                        LocalDate fecha = LocalDate.parse(model.getValueAt(i, 4).toString());
                        LocalTime hora = LocalTime.parse(model.getValueAt(i, 5).toString());
                        Horario.setDateTimeStrict(LocalDateTime.of(fecha, hora));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error al cargar fecha/hora seleccionada: " + ex.getMessage(), "Error de Carga", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });

        ValidarHorario validar = new ValidarHorario();

        Horario.addDateTimeChangeListener(event -> {
            LocalDateTime newDateTime = event.getNewDateTimeStrict();
            if (newDateTime != null) {
                boolean valido = validar.validar(newDateTime);
                if (!valido) {
                    Horario.clear();
                    JOptionPane.showMessageDialog(null, "Horario no válido. Fuera de horas laborales (8:00-17:00) o día no laborable.", "Horario Inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (newDateTime.isBefore(LocalDateTime.now())) {
                    Horario.clear();
                    JOptionPane.showMessageDialog(null, "No se puede seleccionar una fecha y hora pasada.", "Fecha Pasada", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        });

        btnNuevo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cliente = txtCliente.getText().trim();
                String mecanico = (String) comboBox1.getSelectedItem();
                String servicio = txtServicio.getText().trim();

                String precioStr = txtPrecio.getText().trim();
                precioStr = precioStr.replace("$", "").replace(",", "");

                if (cliente.isEmpty() || servicio.isEmpty() || precioStr.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Todos los campos deben estar llenos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double precio;
                try {
                    precio = Double.parseDouble(precioStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Precio no válido. Ingrese un número válido. (Detalles: " + ex.getMessage() + ")", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    return;
                }

                LocalDateTime fechaHora = Horario.getDateTimeStrict();
                if (fechaHora == null) {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar una fecha y hora válida.", "Fecha/Hora Vacía", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Doble verificación al intentar guardar/actualizar
                if (fechaHora.isBefore(LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(null, "No se puede agendar una cita en una fecha y hora pasada.", "Fecha Pasada", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ValidarHorario validador = new ValidarHorario();
                if (!validador.validar(fechaHora, mecanico)) {
                    return;
                }

                try {
                    ser.insertar(new Mecanica(
                            cliente,
                            mecanico,
                            servicio,
                            fechaHora.toLocalDate().toString(),
                            fechaHora.toLocalTime().toString(),
                            precio
                    ));
                    obtenerRegistrosTabla();
                    JOptionPane.showMessageDialog(null, "Cita creada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al guardar la cita en la base de datos: " + ex.getMessage(), "Error de Guardado", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        btnMostrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                obtenerRegistrosTabla();
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clave == null || clave.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Seleccione una cita de la tabla para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try {
                    int id = Integer.parseInt(clave);
                    String cliente = txtCliente.getText().trim();
                    String mecanico = (String) comboBox1.getSelectedItem();
                    String servicio = txtServicio.getText().trim();

                    String precioStr = txtPrecio.getText().trim();
                    precioStr = precioStr.replace("$", "").replace(",", "");
                    Double precio = Double.parseDouble(precioStr);

                    LocalDateTime fechaHora = Horario.getDateTimeStrict();
                    if (fechaHora == null) {
                        JOptionPane.showMessageDialog(null, "Debe seleccionar una fecha y hora válida.", "Fecha/Hora Vacía", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    // Doble verificación al intentar guardar/actualizar
                    if (fechaHora.isBefore(LocalDateTime.now())) {
                        JOptionPane.showMessageDialog(null, "No se puede actualizar una cita a una fecha y hora pasada.", "Fecha Pasada", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    LocalDate fecha = fechaHora.toLocalDate();
                    LocalTime hora = fechaHora.toLocalTime();

                    if (cliente.isEmpty() || servicio.isEmpty() || precioStr.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Todos los campos deben estar llenos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    Mecanica mecanica = new Mecanica(id, cliente, mecanico, servicio, fecha, hora, precio);
                    ser.actualizar(mecanica);

                    obtenerRegistrosTabla();
                    JOptionPane.showMessageDialog(null, "Cita actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarCampos();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "El precio ingresado no es válido. (Detalles: " + ex.getMessage() + ")", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (clave == null || clave.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Seleccione una cita de la tabla para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int id = Integer.parseInt(clave);
                try {
                    ser.eliminar(id);
                    obtenerRegistrosTabla();
                    limpiarCampos();
                    JOptionPane.showMessageDialog(null, "Cita eliminada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error al eliminar la cita: " + ex.getMessage(), "Error de Eliminación", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login login = new Login();
                login.setVisible(true);
                setVisible(false);
            }
        });

        btnImprimir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    table1.print();
                } catch (Exception e2) {
                    JOptionPane.showMessageDialog(null, "Error al imprimir la tabla: " + e2.getMessage(), "Error de Impresión", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        if (agregarTrabajosButton != null) {
            agregarTrabajosButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String currentServicios = txtServicio.getText();
                    String currentPrecioStr = txtPrecio.getText();

                    double currentPrecio = 0.0;
                    try {
                        currentPrecio = Double.parseDouble(currentPrecioStr.replace("$", "").replace(",", ""));
                    } catch (NumberFormatException ex) {
                        System.err.println("Advertencia: No se pudo parsear el precio actual al abrir Trabajos: " + currentPrecioStr);
                    }

                    Trabajos trabajosFrame = new Trabajos(GestionCitas.this);
                    trabajosFrame.cargarTrabajosIniciales(currentServicios, currentPrecio);
                    trabajosFrame.setVisible(true);
                }
            });
        }
    }

    // MÉTODO NUEVO: configurar botones según rol
    private void configurarPermisosPorRol(String rol) {
        switch (rol.toLowerCase()) {
            case "administrador":
                btnNuevo.setEnabled(true);
                btnMostrar.setEnabled(true);
                btnEliminar.setEnabled(true);
                btnActualizar.setEnabled(true);
                btnImprimir.setEnabled(true);
                if (agregarTrabajosButton != null) {
                    agregarTrabajosButton.setEnabled(true);
                }
                break;

            case "estándar":
            case "Estándar":
                btnNuevo.setEnabled(true);
                btnMostrar.setEnabled(true);
                btnActualizar.setEnabled(false);
                btnEliminar.setEnabled(false);
                btnImprimir.setEnabled(false);
                if (agregarTrabajosButton != null) {
                    agregarTrabajosButton.setEnabled(false);
                }
                break;

            case "invitado":
                btnNuevo.setEnabled(false);
                btnMostrar.setEnabled(true);
                btnEliminar.setEnabled(false);
                btnActualizar.setEnabled(false);
                btnImprimir.setEnabled(false);
                if (agregarTrabajosButton != null) {
                    agregarTrabajosButton.setEnabled(false);
                }
                break;

            default:
                btnNuevo.setEnabled(false);
                btnMostrar.setEnabled(true);
                btnEliminar.setEnabled(false);
                btnActualizar.setEnabled(false);
                btnImprimir.setEnabled(false);
                if (agregarTrabajosButton != null) {
                    agregarTrabajosButton.setEnabled(false);
                }
                break;
        }
    }
}
