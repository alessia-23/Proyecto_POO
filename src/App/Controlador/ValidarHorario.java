package App.Controlador;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.swing.JOptionPane;

import App.Controlador.ServicioMeca;
import App.Modelo.Mecanica;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

public class ValidarHorario {
    private final LocalTime inicioTrabajo = LocalTime.of(8, 0);
    private final LocalTime finTrabajo = LocalTime.of(17, 0);
    private final LocalTime inicioAlmuerzo = LocalTime.of(13, 0);
    private final LocalTime finAlmuerzo = LocalTime.of(14, 0);
    private final LocalTime recepcionAlmuerzo = LocalTime.of(12, 0);
    private final LocalTime recepcionFinJornada = LocalTime.of(16, 0);
    private final LocalTime finSabado = LocalTime.of(10, 0);

    public boolean validar(LocalDateTime fechaHora) {
        if (fechaHora == null) return false;

        DayOfWeek diaSemana = fechaHora.getDayOfWeek();
        LocalTime seleccionarTiempo = fechaHora.toLocalTime();

        // Validar domingo
        if (diaSemana == DayOfWeek.SUNDAY) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se puede agendar en domingo. Seleccione otro día.",
                    "Día no permitido",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        // Validar sábado
        if (diaSemana == DayOfWeek.SATURDAY) {
            if (seleccionarTiempo.isBefore(inicioTrabajo) || seleccionarTiempo.isAfter(finSabado)) {
                JOptionPane.showMessageDialog(
                        null,
                        "El horario para los sábados es de 08:00 AM a 10:00 AM.",
                        "Horario no válido para sábado",
                        JOptionPane.WARNING_MESSAGE
                );
                return false;
            }
            // Si pasó las validaciones, es válido el sábado
            return true;
        }

        // Validación para lunes a viernes

        // Fuera de horario laboral
        if (seleccionarTiempo.isBefore(inicioTrabajo) || seleccionarTiempo.isAfter(finTrabajo)) {
            JOptionPane.showMessageDialog(
                    null,
                    "El horario seleccionado está fuera del horario laboral (08:00 AM - 5:00 PM).",
                    "Horario No Válido",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        // Hora de almuerzo
        if (!seleccionarTiempo.isBefore(inicioAlmuerzo) && seleccionarTiempo.isBefore(finAlmuerzo)) {
            JOptionPane.showMessageDialog(
                    null,
                    "El horario seleccionado corresponde a la hora de almuerzo.\nSeleccione una hora antes de las 13:00 o después de las 14:00.",
                    "Horario no disponible",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        // Horario recepción cerca de almuerzo (12:00 - 13:00)
        if (!seleccionarTiempo.isBefore(recepcionAlmuerzo) && seleccionarTiempo.isBefore(inicioAlmuerzo)) {
            int respuesta = JOptionPane.showConfirmDialog(
                    null,
                    "Este horario (12:00 PM - 1:00 PM) está cerca de la hora de almuerzo.\n¿Desea agendar de todas formas?",
                    "Horario de recepción limitado",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (respuesta != JOptionPane.YES_OPTION) {
                return false;
            }
        }

        // Horario recepción cerca fin jornada (16:00 - 17:00)
        if (!seleccionarTiempo.isBefore(recepcionFinJornada) && seleccionarTiempo.isBefore(finTrabajo)) {
            int respuesta = JOptionPane.showConfirmDialog(
                    null,
                    "Este horario (4:00 PM - 5:00 PM) es muy cercano al fin de la jornada.\n¿Desea agendar de todas formas?",
                    "Horario de recepción limitado",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (respuesta != JOptionPane.YES_OPTION) {
                return false;
            }
        }

        // Si pasó todas las validaciones, es válido
        return true;
    }

    public boolean validar(LocalDateTime fechaHora, String mecanico) {
        boolean esHorarioValido = validar(fechaHora); // Llama al método anterior
        if (!esHorarioValido) return false;

        ServicioMeca ser = new ServicioMeca();
        Map<Integer, Mecanica> citas = ser.seleccionarTodo();
        LocalDate fecha = fechaHora.toLocalDate();
        LocalTime hora = fechaHora.toLocalTime();

        for (Mecanica cita : citas.values()) {
            if (cita.getMecanico().equalsIgnoreCase(mecanico)
                    && cita.getFecha().equals(fecha)) {

                LocalTime horaExistente = cita.getHora();

                // Rango de conflicto: ±30 minutos
                if (!hora.isBefore(horaExistente.minusMinutes(30))
                        && !hora.isAfter(horaExistente.plusMinutes(30))) {

                    JOptionPane.showMessageDialog(
                            null,
                            "El mecánico '" + mecanico + "' ya tiene una cita agendada a las " + horaExistente + " del " + fecha + ".\n" +
                                    "Por favor, elija otra hora o cambie de mecánico.",
                            "Conflicto de Horario",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return false;
                }
            }
        }

        return true;
    }
}
