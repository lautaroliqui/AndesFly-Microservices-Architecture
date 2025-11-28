// --- Definiciones de Tipos de Datos ---
// Estos deben coincidir con las entidades del backend
export interface Domicilio {
  id?: number; // El ID es opcional, especialmente al crear
  calle: string;
  numero: string;
  ciudad: string;
  provincia: string;
  pais: string;
  codigoPostal: string;
  principal: boolean;
}

export interface Cliente {
  id: number;
  nombreCompleto: string;
  email: string;
  documento: string;
}

export interface VueloDTO {
  id: number;
  codigo: string;
  origen: string;
  destino: string;
  fechaSalida: string;
  fechaLlegada: string;
  cupoTotal: number;
  cupoReservado: number;
}

export interface ReservaDTO {
  id: number;
  codigo: string;
  estado: string; // ej. "GENERADA"
  cliente: string;
  CodigoVuelo: string;
  origen: string;
  destino: string;
  observaciones: string;
}

// --- Wrappers de Respuesta de la API (Lo más importante) ---
// Estos deben coincidir con las clases Payload del backend

export interface MensajeRespondeCliente {
  mensaje: string;
  cliente: Cliente | null;
  clienteLista: Cliente[] | null;
}

export interface MensajeRespondeVuelo {
  mensaje: string;
  vuelo: VueloDTO | null;
  vueloLista: VueloDTO[] | null;
}

export interface MensajeRespondeReserva {
  mensaje: string;
  reserva: ReservaDTO | null;
  reservaLista: ReservaDTO[] | null;
}
