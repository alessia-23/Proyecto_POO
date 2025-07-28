package App.Vista;

import App.Controlador.ServicioMeca;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Arrays;
import java.util.List;


public class Trabajos extends JFrame {
    private JPanel panel1;
    private JComboBox<String> boxTrabajos;
    private JButton btnAceptar; // Considera renombrarlo a 'btnAnadirATabla'
    private JButton btnEliminar;
    private JTable trabajosElejidos;
    private JTextField txtPrecio;
    private JButton btnAgregar; // Este es el botón de confirmar y cerrar

    private GestionCitas gestionCitasParent;

    private DefaultTableModel tableModel;
    private Map<String, String> todosLosTrabajosConPrecios;

    public Trabajos(GestionCitas parentFrame) {
        this.gestionCitasParent = parentFrame;
        initializeComponents();
    }

    public Trabajos() {
        this.gestionCitasParent = null;
        initializeComponents();
    }

    private void initializeComponents() {
        setTitle("Seleccionar Trabajos y Precios");
        setSize(600, 500);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setContentPane(panel1);
        setLocationRelativeTo(null);


        if (panel1 == null || boxTrabajos == null || trabajosElejidos == null ||
                txtPrecio == null || btnAgregar == null || btnAceptar == null || btnEliminar == null) {
            System.err.println("ERROR: Algunos componentes no están inicializados");

            return;
        }


        String[] columnNames = {"Trabajo Seleccionado", "Precio (USD)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        trabajosElejidos.setModel(tableModel);

        cargarTrabajosEnComboBoxYMap();

        txtPrecio.setText("0.00");
        txtPrecio.setEditable(false);


        btnAceptar.addActionListener(e -> {
            String selectedJob = (String) boxTrabajos.getSelectedItem();
            if (selectedJob != null && !"No hay trabajos disponibles".equals(selectedJob)) {
                // Verificar si el trabajo ya existe en la tabla para evitar duplicados
                boolean yaExiste = false;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (selectedJob.equals(tableModel.getValueAt(i, 0))) {
                        yaExiste = true;
                        break;
                    }
                }

                if (yaExiste) {
                    JOptionPane.showMessageDialog(Trabajos.this, "Este trabajo ya ha sido añadido.", "Duplicado", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String priceString = todosLosTrabajosConPrecios.get(selectedJob);
                if (priceString != null) {
                    tableModel.addRow(new Object[]{selectedJob, priceString});

                    actualizarPrecioTotal(); // Recalcula el total
                } else {
                    JOptionPane.showMessageDialog(Trabajos.this, "No se encontró precio para el trabajo seleccionado.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(Trabajos.this, "Por favor, selecciona un trabajo válido.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });


        // Listener para btnEliminar
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = trabajosElejidos.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                    actualizarPrecioTotal(); // Recalcula el total
                    JOptionPane.showMessageDialog(Trabajos.this, "Trabajo eliminado de la tabla.", "Eliminado", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(Trabajos.this, "Por favor, selecciona un trabajo de la tabla para eliminar.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Listener para btnAgregar (confirmar selección y cerrar)
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarSeleccionYCerrar();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

            }
        });
    }

    private void cargarTrabajosEnComboBoxYMap() {
        ServicioMeca servicioMeca = new ServicioMeca();
        todosLosTrabajosConPrecios = servicioMeca.obtenerTrabajosConPrecios();

        boxTrabajos.removeAllItems();

        if (todosLosTrabajosConPrecios.isEmpty()) {
            System.out.println("No se encontraron trabajos en la base de datos o hubo un error al cargar.");
            boxTrabajos.addItem("No hay trabajos disponibles");
        } else {
            for (String trabajo : todosLosTrabajosConPrecios.keySet()) {
                boxTrabajos.addItem(trabajo);
            }
        }
    }


    public void cargarTrabajosIniciales(String serviciosStr, double precioInicial) {

        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }

        if (serviciosStr != null && !serviciosStr.trim().isEmpty() && !serviciosStr.equals("N/A")) {
            List<String> servicios = Arrays.asList(serviciosStr.split(","));

            for (String servicio : servicios) {
                String trimmedServicio = servicio.trim();
                String precioServicio = todosLosTrabajosConPrecios.get(trimmedServicio);

                if (precioServicio != null) {

                    tableModel.addRow(new Object[]{trimmedServicio, precioServicio});
                } else {

                    tableModel.addRow(new Object[]{trimmedServicio, "N/A"});
                    System.err.println("Advertencia: El servicio inicial '" + trimmedServicio + "' no se encontró en la lista maestra de trabajos.");
                }
            }
        }

        actualizarPrecioTotal();
    }


    private void actualizarPrecioTotal() {
        double total = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String priceString = (String) tableModel.getValueAt(i, 1);
            // Si el precio es "N/A", lo ignoramos para la suma
            if ("N/A".equalsIgnoreCase(priceString)) {
                System.out.println("DEBUG: Ignorando precio 'N/A' para la suma.");
                continue;
            }

            // Limpia la cadena de precio para que solo queden los dígitos y el punto decimal.
            // Esto es CRUCIAL para evitar errores de NumberFormatException o acumulaciones incorrectas.
            priceString = priceString.replace("$", "").replace("por eje", "").replace("(según acceso)", "").replace("(variable)", "").trim();

            try {
                double price = Double.parseDouble(priceString);
                total += price;
            } catch (NumberFormatException ex) {
                System.err.println("Advertencia: No se pudo parsear el precio: '" + priceString + "'. Asegúrate de que los precios son números válidos en la base de datos.");
            }
        }
        // Formatea el total a moneda USD y lo muestra en el JTextField
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
        txtPrecio.setText(currencyFormatter.format(total));
    }

    private void confirmarSeleccionYCerrar() {
        if (gestionCitasParent != null) {
            // 1. Enviar el precio total a la ventana padre
            double finalPrice = 0.0;
            String priceText = txtPrecio.getText().replace("$", "").replace(",", ""); // Limpiar antes de parsear
            try {
                finalPrice = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear el precio final en Trabajos para enviar a GestionCitas: " + priceText);
            }
            gestionCitasParent.setPrecioTotalServicios(finalPrice);

            // 2. Enviar la cadena de servicios a la ventana padre
            StringBuilder serviciosBuilder = new StringBuilder();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String servicio = (String) tableModel.getValueAt(i, 0); // Columna 0 es el nombre del trabajo
                serviciosBuilder.append(servicio);
                if (i < tableModel.getRowCount() - 1) {
                    serviciosBuilder.append(", "); // Separar con coma y espacio
                }
            }
            gestionCitasParent.setServiciosSeleccionados(serviciosBuilder.toString());
        }
        dispose(); // Cierra la ventana Trabajos
    }
/*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Trabajos trabajos = new Trabajos(null); // Para probar de forma independiente
            trabajos.setVisible(true);
        });
    }

 */
}