package ar.edu.unju.fi.trabajo_final.microservicio_cliente.enums;

/**
 * Define los posibles estados de una Reserva, cruciales para la lógica de cupos del Vuelo.
 */

public enum EstadoReserva {
    GENERADA,
    CONFIRMADA,
    CANCELADA, PENDIENTE,
    //Podemos añadir PENDIENTE_PAGO, EXPIRADA
}
