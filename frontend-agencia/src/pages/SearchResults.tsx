import React from 'react';
// 1. Importamos el hook para leer los parámetros de la URL
import { useSearchParams } from 'react-router-dom';

export const SearchResults = () => {
    // 2. Extraemos los parámetros de búsqueda
    const [searchParams] = useSearchParams();
    
    const origen = searchParams.get('origen');
    const destino = searchParams.get('destino');
    const fecha = searchParams.get('fecha');

    // 3. Simulación de la función de búsqueda de la API
    // Aquí es donde harías la llamada real a tu API REST de vuelos
    const vuelosEncontrados = [
        { id: 1, vuelo: `${origen} -> ${destino}`, hora: "08:00", precio: "US$ 150" },
        { id: 2, vuelo: `${origen} -> ${destino}`, hora: "14:30", precio: "US$ 180" },
        { id: 3, vuelo: `${origen} -> ${destino}`, hora: "20:15", precio: "US$ 165" },
    ];
    
    // Si la ruta se visita sin parámetros, mostramos un mensaje
    if (!origen || !destino) {
        return (
            <div className="p-8 text-center text-2xl font-semibold bg-red-100 rounded-lg">
                Faltan parámetros de Origen o Destino para la búsqueda.
            </div>
        );
    }


    return (
        <div className="space-y-8 max-w-4xl mx-auto">
            <h1 className="text-3xl font-bold text-gray-900">
                Vuelos Encontrados: {origen} &rarr; {destino}
            </h1>
            <p className="text-xl text-gray-600">
                Fecha de Viaje: **{fecha || 'Cualquier día'}**
            </p>

            <div className="space-y-4">
                {vuelosEncontrados.map((vuelo) => (
                    <div 
                        key={vuelo.id} 
                        className="p-6 bg-white rounded-xl shadow-lg flex justify-between items-center border-l-4 border-blue-500"
                    >
                        <div>
                            <p className="text-lg font-semibold">{vuelo.vuelo}</p>
                            <p className="text-sm text-gray-500">Hora de salida: {vuelo.hora}</p>
                        </div>
                        <div className="text-xl font-bold text-green-600">
                            {vuelo.precio}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};