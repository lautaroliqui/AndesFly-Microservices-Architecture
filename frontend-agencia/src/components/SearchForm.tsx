import React, { useState } from 'react';
// 1. Importar el nuevo servicio
import { vueloApi } from '../services/apiService'; 
import type { VueloDTO, MensajeRespondeVuelo } from '../types';

interface SearchFormProps {
  onSearchStart: () => void;
  onSearchComplete: (resultados: VueloDTO[]) => void;
  onSearchError: (error: string) => void;
}

export const SearchForm = ({ onSearchStart, onSearchComplete, onSearchError }: SearchFormProps) => {
  const [origen, setOrigen] = useState('');
  const [destino, setDestino] = useState('');
  const [fecha, setFecha] = useState('');

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    onSearchStart();

    if (!origen || !destino || !fecha) {
      onSearchError("Por favor, completa los campos de Origen, Destino y Fecha.");
      return;
    }
    
    const queryParams = new URLSearchParams({
        origen: origen,
        destino: destino,
        fecha: fecha
    });

    try {
      // 2. Usar vueloApi.get (ahora envía el token)
      const response = await vueloApi.get(`/vuelos/buscar?${queryParams.toString()}`);
      
      if (!response.ok) {
        throw new Error("Error de red al buscar vuelos.");
      }

      const data: MensajeRespondeVuelo = await response.json();
      onSearchComplete(data.vueloLista || []);

    } catch (err) {
      console.error("Error buscando vuelos:", err);
      onSearchError((err as Error).message);
    }
  };

  const inputClasses = "px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white dark:focus:ring-blue-600 dark:focus:border-blue-600";
  const labelClasses = "mb-1 font-medium text-sm text-gray-700 dark:text-gray-300";

  return (
    <div className="bg-white shadow-lg rounded-xl p-6 mb-8 dark:bg-gray-800">
      <h2 className="text-3xl font-bold mb-6 text-gray-800 dark:text-gray-100">
        Busca tu Próximo Destino
      </h2>
      
      <form onSubmit={handleSubmit} className="grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
        
        <div className="flex flex-col">
          <label htmlFor="origen" className={labelClasses}>Origen (Cód. Aeropuerto)</label>
          <input id="origen" type="text" value={origen} onChange={e => setOrigen(e.target.value.toUpperCase())} placeholder="Ej: JUJ" required className={inputClasses} />
        </div>
        <div className="flex flex-col">
          <label htmlFor="destino" className={labelClasses}>Destino (Cód. Aeropuerto)</label>
          <input id="destino" type="text" value={destino} onChange={e => setDestino(e.target.value.toUpperCase())} placeholder="Ej: AEP" required className={inputClasses} />
        </div>
        <div className="flex flex-col">
          <label htmlFor="fecha" className={labelClasses}>Fecha de Salida</label>
          <input id="fecha" type="date" value={fecha} onChange={e => setFecha(e.target.value)} required className={inputClasses} />
        </div>

        <button
          type="submit"
          className="px-4 py-2 w-full border border-transparent rounded-md shadow-sm text-sm font-medium text-white 
                     bg-brand-accent hover:bg-brand-accent-hover 
                     focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-brand-accent md:mt-0 mt-4"
        >
          Buscar Vuelos
        </button>
      </form>
    </div>
  );
};