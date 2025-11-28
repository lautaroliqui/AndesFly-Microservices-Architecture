import React, { useState, useEffect } from 'react';
import { VUELO_API_URL } from '../services/api';
import type { VueloDTO, MensajeResponde } from '../types';

export const VueloList = () => { // Eliminado React.FC
  const [vuelos, setVuelos] = useState<VueloDTO[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [filtro, setFiltro] = useState("");

  useEffect(() => {
    fetch(`${VUELO_API_URL}/vuelos`) // Asume endpoint 'GET /vuelos'
      .then(response => {
        if (!response.ok) {
          throw new Error('Error de red (Vuelos). ¿CORS habilitado y Docker corriendo en 8094?');
        }
        return response.json();
      })
      .then((data: MensajeResponde | VueloDTO[]) => {
        const lista = (data as MensajeResponde).objecto || data;
        setVuelos(Array.isArray(lista) ? lista : []);
      })
      .catch(err => {
        console.error("Error al cargar vuelos:", err);
        setError((err as Error).message);
      });
  }, []);

  const vuelosFiltrados = vuelos.filter(v =>
    v.codigo?.toLowerCase().includes(filtro.toLowerCase()) ||
    v.origen?.nombre.toLowerCase().includes(filtro.toLowerCase()) ||
    v.destino?.nombre.toLowerCase().includes(filtro.toLowerCase())
  );

  return (
    <div className="bg-white shadow rounded-lg p-6">
      <h3 className="text-xl font-semibold mb-4 text-gray-800">Catálogo de Vuelos</h3>
      <input
        type="text"
        placeholder="Buscar por código, origen o destino..."
        value={filtro}
        onChange={e => setFiltro(e.target.value)}
        className="w-full px-3 py-2 mb-4 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
      />
      {error && <div className="p-3 bg-red-100 border border-red-400 text-red-700 rounded-md">{error}</div>}
      <ul className="h-64 overflow-y-auto divide-y divide-gray-200">
        {vuelosFiltrados.length > 0 ? vuelosFiltrados.map(v => (
          <li key={v.id} className="py-3">
            <p className="font-medium text-gray-900">{v.codigo} <span className="font-normal text-sm text-gray-600">({v.cupoReservado}/{v.cupoTotal})</span></p>
            <p className="text-sm text-gray-600">{v.origen?.nombre} &rarr; {v.destino?.nombre}</p>
          </li>
        )) : <li className="py-3 text-gray-500">No se encontraron vuelos.</li>}
      </ul>
    </div>
  );
};