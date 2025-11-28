import React, { useState } from 'react';
import type { ReservaDTO } from '../types';

interface ReservaListProps {
  reservas: ReservaDTO[];
  onConfirmarClick: (reservaId: number) => void;
  onCancelarClick: (reservaId: number) => void;
}

export const ReservaList = ({ reservas, onConfirmarClick, onCancelarClick }: ReservaListProps) => { 
  const [filtro, setFiltro] = useState("");

  const reservasFiltradas = reservas.filter(r =>
    (r.codigo?.toLowerCase() || '').includes(filtro.toLowerCase()) ||
    (r.estado?.toLowerCase() || '').includes(filtro.toLowerCase())
  );

  const parseCodigoAeropuerto = (textoOrigen: string = "") => {
    return textoOrigen.split('-')[0]?.trim() || "N/A";
  };

  const parseNombreDestino = (textoDestino: string = "") => {
    return textoDestino.split('-').slice(1).join('-').trim() || textoDestino;
  };

  return (
    <div className="bg-white shadow rounded-lg p-6 dark:bg-gray-800">
      <div className="flex justify-between items-center mb-4">
        <h3 className="text-xl font-semibold text-gray-800 dark:text-gray-100">Listado de Reservas</h3>
      </div>
      <input
        type="text"
        placeholder="Buscar por código o estado..."
        value={filtro}
        onChange={e => setFiltro(e.target.value)}
        className="w-full px-3 py-2 mb-4 border border-gray-300 rounded-md 
                   focus:outline-none focus:ring-2 focus:ring-blue-500
                   dark:bg-gray-700 dark:border-gray-600 dark:text-white"
      />
      
      <ul className="h-96 overflow-y-auto divide-y divide-gray-200 dark:divide-gray-700">
        {reservasFiltradas.length > 0 ? reservasFiltradas.map(r => (
          <li key={r.id} className="py-4 px-2">
            <div className="flex justify-between items-center mb-2">
              <span className="font-bold text-lg text-gray-900 dark:text-white">{r.codigo}</span>
              <span className={`px-2 py-0.5 text-xs font-semibold rounded-full ${
                r.estado === 'CONFIRMADA' ? 'bg-green-100 text-green-800' :
                r.estado === 'CANCELADA' ? 'bg-red-100 text-red-800' :
                'bg-yellow-100 text-yellow-800'
              }`}>
                {r.estado}
              </span>
            </div>
            
            <p className="text-sm text-gray-600 mt-1 dark:text-gray-300">
              <span className="font-medium">Cliente:</span> {r.cliente}
            </p>
            <p className="text-sm text-gray-600 dark:text-gray-300">
              <span className="font-medium">Vuelo:</span> 
              <span className="ml-1 font-mono bg-gray-100 px-1 rounded dark:bg-gray-700 dark:text-gray-200">
                {r.codigoVuelo} {parseCodigoAeropuerto(r.origen)}
              </span>
            </p>
            <p className="text-sm text-gray-600 mt-1 capitalize dark:text-gray-300">
              <span className="font-medium">Ruta:</span> 
              <span className="ml-1">{parseNombreDestino(r.origen)} &rarr; {parseNombreDestino(r.destino)}</span>
            </p>

            <div className="mt-3 flex gap-4">
              {r.estado === 'GENERADA' && (
                <button
                  onClick={() => onConfirmarClick(r.id)}
                  className="px-3 py-1 text-sm font-medium text-white bg-green-600 hover:bg-green-700 rounded-md shadow-sm"
                >
                  Confirmar
                </button>
              )}

              {(r.estado === 'GENERADA' || r.estado === 'CONFIRMADA') && (
                <button
                  onClick={() => onCancelarClick(r.id)}
                  className="px-3 py-1 text-sm font-medium text-red-700 bg-red-100 hover:bg-red-200 rounded-md"
                >
                  Cancelar
                </button>
              )}
            </div>

          </li>
        )) : <li className="py-3 text-gray-500 dark:text-gray-400">No se encontraron reservas.</li>}
      </ul>
    </div>
  );
};

export default ReservaList;