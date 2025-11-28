import { useNavigate } from 'react-router-dom';
import type { VueloDTO } from '../types';

interface FlightListProps {
  vuelos: VueloDTO[];
  isLoading: boolean;
  error: string | null;
}

export const FlightList = ({ vuelos, isLoading, error }: FlightListProps) => {
  const navigate = useNavigate();

  const handleSelectVuelo = (vueloSeleccionado: VueloDTO) => {
    navigate('/reservas', { 
      state: {
        selectedVueloId: vueloSeleccionado.id 
      } 
    });
  };

  if (isLoading) {
    return <div className="text-center p-4 dark:text-gray-400">Buscando vuelos...</div>;
  }

  if (error) {
    return <div className="p-3 bg-red-100 border border-red-400 text-red-700 rounded-md">{error}</div>;
  }

  if (vuelos.length === 0) {
    return <div className="text-center p-4 text-gray-500 dark:text-gray-400">No se encontraron vuelos para esa fecha.</div>;
  }

  return (
    <div className="bg-white shadow rounded-lg p-6 dark:bg-gray-800">
      <h3 className="text-xl font-semibold text-gray-800 mb-4 dark:text-gray-100">Vuelos Encontrados</h3>
      <ul className="divide-y divide-gray-200 dark:divide-gray-700">
        {vuelos.map(vuelo => (
          <li key={vuelo.id} className="py-4 flex justify-between items-center">
            <div>
              <p className="text-lg font-medium text-brand-primary dark:text-brand-primary-light">{vuelo.codigo}</p>
              <p className="text-md text-gray-700 dark:text-gray-200">{vuelo.origen} &rarr; {vuelo.destino}</p>
              <p className="text-sm text-gray-500 dark:text-gray-400">
                Sale: {new Date(vuelo.fechaSalida).toLocaleString()}
              </p>
            </div>
            {/* 5. Conectar el botón al handler */}
            <button 
              onClick={() => handleSelectVuelo(vuelo)}
              className="px-4 py-2 bg-green-500 text-white text-sm font-medium rounded-md hover:bg-green-600"
            >
              Seleccionar
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
};