import React, { useState, useEffect } from 'react';
import { SearchForm } from '../components/SearchForm';
import { FlightList } from '../components/FlightList';
import type { VueloDTO, MensajeRespondeVuelo } from '../types';
// Importamos el servicio de API
import { vueloApi } from '../services/apiService';

export const FlightSearch = () => {
  // Estado para los resultados de BÚSQUEDA
  const [searchResults, setSearchResults] = useState<VueloDTO[]>([]);
  const [isSearchLoading, setIsSearchLoading] = useState(false);
  const [searchError, setSearchError] = useState<string | null>(null);
  const [hasSearched, setHasSearched] = useState(false); // Para saber si el usuario ya buscó

  // Estado para los vuelos INICIALES (Recomendados/Recientes)
  const [initialFlights, setInitialFlights] = useState<VueloDTO[]>([]);
  const [isInitialLoading, setIsInitialLoading] = useState(true);

  // 1. Cargar vuelos iniciales al montar el componente
  useEffect(() => {
    const loadInitialFlights = async () => {
      try {
        // Llamamos al endpoint público GET /vuelos
        const response = await vueloApi.get('/vuelos');
        
        if (response.ok) {
          const data: MensajeRespondeVuelo = await response.json();
          // Tomamos solo los primeros 5 para no saturar la pantalla
          setInitialFlights((data.vueloLista || []).slice(0, 5));
        }
      } catch (error) {
        console.error("No se pudieron cargar los vuelos iniciales", error);
        // No mostramos error en UI para esto, simplemente no mostramos la lista
      } finally {
        setIsInitialLoading(false);
      }
    };

    loadInitialFlights();
  }, []);

  // --- Handlers del Buscador ---

  const handleSearchStart = () => {
    setIsSearchLoading(true);
    setSearchError(null);
    setSearchResults([]);
    setHasSearched(true); // Marcamos que se inició una búsqueda
  };

  const handleSearchComplete = (resultados: VueloDTO[]) => {
    setIsSearchLoading(false);
    setSearchResults(resultados);
  };

  const handleSearchError = (error: string) => {
    setIsSearchLoading(false);
    setSearchError(error);
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex flex-col gap-8">
        
        {/* Formulario de Búsqueda */}
        <SearchForm 
          onSearchStart={handleSearchStart}
          onSearchComplete={handleSearchComplete}
          onSearchError={handleSearchError}
        />

        {/* --- LÓGICA DE VISUALIZACIÓN --- */}

        {/* CASO 1: Buscando... */}
        {isSearchLoading && (
          <div className="text-center p-8">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
            <p className="text-lg text-gray-600 mt-4 dark:text-gray-400">Buscando vuelos...</p>
          </div>
        )}

        {/* CASO 2: Resultados de la Búsqueda (Si ya buscó) */}
        {!isSearchLoading && hasSearched && (
          <div>
             <h3 className="text-2xl font-bold text-gray-800 mb-4 dark:text-white">
                Resultados de la Búsqueda
             </h3>
             <FlightList 
               vuelos={searchResults} 
               isLoading={false} 
               error={searchError} 
             />
             {searchResults.length === 0 && !searchError && (
                <p className="text-center text-gray-500 mt-4 dark:text-gray-400">
                    No se encontraron vuelos con esos criterios. 
                    <button 
                        onClick={() => setHasSearched(false)} 
                        className="ml-2 text-brand-primary hover:underline"
                    >
                        Ver todos los vuelos
                    </button>
                </p>
             )}
          </div>
        )}

        {/* CASO 3: Vuelos Disponibles (Si NO ha buscado aún) */}
        {!isSearchLoading && !hasSearched && (
          <div className="animate-fade-in">
            <h3 className="text-2xl font-bold text-gray-800 mb-4 dark:text-white">
              Próximos Vuelos Disponibles
            </h3>
            {isInitialLoading ? (
                <div className="space-y-4">
                    {/* Esqueletos de carga simple */}
                    {[1,2,3].map(i => (
                        <div key={i} className="h-24 bg-gray-100 rounded-lg animate-pulse dark:bg-gray-800"></div>
                    ))}
                </div>
            ) : (
                <FlightList 
                  vuelos={initialFlights} 
                  isLoading={false} 
                  error={null} 
                />
            )}
            {initialFlights.length === 0 && !isInitialLoading && (
                <p className="text-gray-500 dark:text-gray-400">No hay vuelos registrados en el sistema actualmente.</p>
            )}
          </div>
        )}

      </div>
    </div>
  );
};