import { CLIENTE_API_URL, VUELO_API_URL, RESERVA_API_URL } from './api';

/**
 * Función helper para obtener el token de localStorage.
 */
const getToken = (): string | null => {
  return localStorage.getItem('authToken');
};

/**
 * Nuestro nuevo 'fetch' personalizado (interceptor).
 * Adjunta automáticamente el token JWT a todas las peticiones.
 */
export const apiFetch = async (url: string, options: RequestInit = {}): Promise<Response> => {
  const token = getToken();

  // 1. Prepara las cabeceras (headers)
  const headers = new Headers(options.headers || {});
  if (!headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  // 2. Si tenemos un token, lo adjuntamos
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  // 3. Ejecuta el fetch con las nuevas opciones
  try {
    const response = await fetch(url, {
      ...options,
      headers: headers,
    });
    
    // 4. Si la respuesta es 401 (Token inválido o expirado), deslogueamos al usuario
    if (response.status === 401) {
      localStorage.removeItem('authToken');
      window.location.href = '/login'; // Redirige al login
      throw new Error('Sesión expirada o inválida. Por favor, inicie sesión de nuevo.');
    }

    return response;

  } catch (err) {
    console.error("Error en apiFetch:", err);
    throw err;
  }
};

// --- Exportamos nuestros endpoints pre-configurados ---

export const clienteApi = {
  get: (endpoint: string, options: RequestInit = {}) => 
    apiFetch(`${CLIENTE_API_URL}${endpoint}`, { ...options, method: 'GET' }),
  post: (endpoint: string, body: any, options: RequestInit = {}) =>
    apiFetch(`${CLIENTE_API_URL}${endpoint}`, { ...options, method: 'POST', body: JSON.stringify(body) }),
  put: (endpoint: string, body: any, options: RequestInit = {}) =>
    apiFetch(`${CLIENTE_API_URL}${endpoint}`, { ...options, method: 'PUT', body: JSON.stringify(body) }),
  delete: (endpoint: string, options: RequestInit = {}) =>
    apiFetch(`${CLIENTE_API_URL}${endpoint}`, { ...options, method: 'DELETE' }),
};

export const vueloApi = {
  get: (endpoint: string, options: RequestInit = {}) => 
    apiFetch(`${VUELO_API_URL}${endpoint}`, { ...options, method: 'GET' }),
  // (puedes añadir post, put, delete si los necesitas para ADMIN)
};

export const reservaApi = {
  get: (endpoint: string, options: RequestInit = {}) => 
    apiFetch(`${RESERVA_API_URL}${endpoint}`, { ...options, method: 'GET' }),
  post: (endpoint: string, body: any, options: RequestInit = {}) =>
    apiFetch(`${RESERVA_API_URL}${endpoint}`, { ...options, method: 'POST', body: JSON.stringify(body) }),
  put: (endpoint: string, body: any = {}, options: RequestInit = {}) =>
    apiFetch(`${RESERVA_API_URL}${endpoint}`, { ...options, method: 'PUT', body: JSON.stringify(body) }),
};