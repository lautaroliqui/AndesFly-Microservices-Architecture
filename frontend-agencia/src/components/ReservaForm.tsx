import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { clienteApi, vueloApi, reservaApi } from '../services/apiService';
import { useAuth } from '../context/AuthContext';
import type { 
  Cliente, 
  VueloDTO, 
  MensajeRespondeCliente, 
  MensajeRespondeVuelo,
  MensajeRespondeReserva 
} from '../types';

interface ReservaFormProps {
  onReservaCreada: () => void;
}

export const ReservaForm = ({ onReservaCreada }: ReservaFormProps) => {
  const location = useLocation();
  const { user } = useAuth(); // <-- 2. OBTENER EL USUARIO LOGUEADO
  const esAdmin = user?.rol === 'ROLE_ADMIN'; // <-- 3. CHEQUEAR ROL

  const preSelectedVueloId = location.state?.selectedVueloId;

  const [clienteId, setClienteId] = useState('');
  const [vueloId, setVueloId] = useState(preSelectedVueloId ? String(preSelectedVueloId) : '');
  const [observaciones, setObservaciones] = useState('');
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [vuelos, setVuelos] = useState<VueloDTO[]>([]);
  const [mensaje, setMensaje] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    // 4. Solo carga la lista de clientes SI el usuario es ADMIN
    if (esAdmin) {
      clienteApi.get('/clientes')
        .then(res => {
          if (!res.ok) throw new Error('Error al cargar clientes');
          return res.json()
        })
        .then((data: MensajeRespondeCliente) => {
          setClientes(data.clienteLista || []);
        })
        .catch(err => {
          console.error("Error cargando clientes:", err);
          setError("No se pudieron cargar los clientes (requiere rol ADMIN).");
        });
    }

    // Carga los vuelos (todos pueden verlos)
    vueloApi.get('/vuelos')
      .then(res => {
        if (!res.ok) throw new Error('Error al cargar vuelos');
        return res.json()
      })
      .then((data: MensajeRespondeVuelo) => {
        setVuelos(data.vueloLista || []);
      })
      .catch(err => {
        console.error("Error cargando vuelos:", err);
        setError("No se pudieron cargar los vuelos.");
      });
  
  // 5. Vuelve a ejecutar este efecto si cambia el rol (ej. el admin hace logout)
  }, [esAdmin]); 

  // ... (Limpiar estado de navegación) ...
  useEffect(() => {
    if (preSelectedVueloId) {
      window.history.replaceState({}, document.title)
    }
  }, [preSelectedVueloId]);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setMensaje(null);
    setError(null);
    setIsLoading(true);

    // 6. Validación: Si es Admin, debe seleccionar un cliente.
    if ((esAdmin && !clienteId) || !vueloId) {
      setError("Por favor, seleccione un cliente y un vuelo.");
      setIsLoading(false);
      return;
    }

    // 7. Decide qué endpoint y qué body usar según el ROL
    let endpoint = '';
    let body: any;

    if (esAdmin) {
      // El ADMIN llama al endpoint antiguo y envía el ID del cliente
      endpoint = '/reserva';
      body = {
        codigo: `RES-ADM-${Date.now().toString().slice(-6)}`,
        clienteId: parseInt(clienteId),
        vueloId: parseInt(vueloId),
        observaciones: observaciones
      };
    } else {
      // El USER llama al nuevo endpoint y NO envía el ID del cliente
      endpoint = '/reservas/mia';
      body = {
        codigo: `RES-WEB-${Date.now().toString().slice(-6)}`,
        vueloId: parseInt(vueloId),
        observaciones: observaciones
      };
    }

    try {
      const response = await reservaApi.post(endpoint, body);
      const data: MensajeRespondeReserva = await response.json();
      
      if (!response.ok) {
        throw new Error(data.mensaje || 'Error desconocido al crear la reserva');
      }

      setMensaje(`¡Reserva creada! Código: ${data.reserva?.codigo}`);
      setClienteId('');
      setVueloId('');
      setObservaciones('');
      onReservaCreada();
      
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setIsLoading(false);
    }
  };

  const inputClasses = "px-3 py-2 bg-white border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white dark:focus:ring-blue-600 dark:focus:border-blue-600";
  const labelClasses = "mb-1 font-medium text-sm text-gray-700 dark:text-gray-300";

  return (
    <div className="bg-white shadow rounded-lg p-6 dark:bg-gray-800">
      <h2 className="text-2xl font-semibold mb-5 text-gray-800 dark:text-gray-100">Registrar Nueva Reserva</h2>
      
      {error && !mensaje && (
        <div className="mb-4 p-3 text-center bg-red-100 border border-red-400 text-red-700 rounded-md">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        
        {/* --- ¡AQUÍ ESTÁ EL CAMBIO! --- */}
        {/* 8. Muestra el dropdown de Cliente SOLO SI es Admin */}
        {esAdmin && (
          <div className="flex flex-col">
            <label htmlFor="cliente" className={labelClasses}>Cliente (Modo Admin)</label>
            <select
              id="cliente"
              value={clienteId}
              onChange={e => setClienteId(e.target.value)}
              required={esAdmin} // Solo requerido si es Admin
              className={inputClasses}
              disabled={clientes.length === 0}
            >
              <option value="">-- Seleccione un Cliente --</option>
              {clientes.map(c => (
                <option key={c.id} value={c.id}>
                  {c.nombreCompleto}
                </option>
              ))}
            </select>
          </div>
        )}

        <div className="flex flex-col">
          <label htmlFor="vuelo" className={labelClasses}>Vuelo</label>
          <select
            id="vuelo"
            value={vueloId}
            onChange={e => setVueloId(e.target.value)}
            required
            className={inputClasses}
            disabled={vuelos.length === 0}
          >
            <option value="">-- Seleccione un Vuelo --</option>
            {vuelos.map(v => (
              <option key={v.id} value={v.id}>
                {v.codigo} ({v.origen} &rarr; {v.destino})
              </option>
            ))}
          </select>
        </div>

        <div className="flex flex-col">
          <label htmlFor="observaciones" className={labelClasses}>Observaciones</label>
          <input
            id="observaciones"
            type="text"
            value={observaciones}
            onChange={e => setObservaciones(e.target.value)}
            className={inputClasses}
          />
        </div>

        <button
          type="submit"
          disabled={isLoading || (esAdmin && clientes.length === 0) || vuelos.length === 0}
          className="w-full px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white 
                     bg-brand-accent hover:bg-brand-accent-hover 
                     focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-brand-accent 
                     disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          {isLoading ? 'Registrando...' : 'Registrar'}
        </button>
      </form>

      {/* Mensajes de éxito/error del formulario */}
      {mensaje && (
        <div className="mt-4 p-3 text-center bg-green-100 border border-green-400 text-green-800 rounded-md">
          {mensaje}
        </div>
      )}
      {error && mensaje && ( 
        <div className="mt-4 p-3 text-center bg-red-100 border border-red-400 text-red-700 rounded-md">
          {error}
        </div>
      )}
    </div>
  );
};