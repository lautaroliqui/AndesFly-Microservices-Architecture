/**
 * URL base de nuestro API Gateway
 */
export const GATEWAY_API_URL = "http://localhost:8066/api/v1_1";

/**
 * API del Microservicio de Clientes (a través del Gateway)
 */
export const CLIENTE_API_URL = GATEWAY_API_URL;

/**
 * API del Microservicio de Vuelos (a través del Gateway)
 */
export const VUELO_API_URL = GATEWAY_API_URL; 

/**
 * API del Microservicio de Reservas (a través del Gateway)
 */
export const RESERVA_API_URL = GATEWAY_API_URL;