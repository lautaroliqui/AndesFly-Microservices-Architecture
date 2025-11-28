import React, { useEffect, useState } from 'react';
import { ReservaForm } from '../components/ReservaForm';
import ReservaList from '../components/ReservaList';
import { reservaApi } from '../services/apiService'; 
import type { ReservaDTO, MensajeRespondeReserva, AuthUser } from '../types';
import { useAuth } from '../context/AuthContext'; 

export const Reservations = () => {
    const [reservas, setReservas] = useState<ReservaDTO[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const { user } = useAuth(); // 3. Obtener el usuario logueado
    const esAdmin = user?.rol === 'ROLE_ADMIN';

    const fetchReservas = async () => {
        setIsLoading(true);
        setError(null);
        
        const endpoint = esAdmin ? '/reservas' : '/reservas/mis-reservas';

        try {
        
            const response = await reservaApi.get(endpoint);

            if (!response.ok) {
                const status = response.status;
                throw new Error(`Error ${status}: El servicio de Reservas no responde.`);
            }
            
            const data: MensajeRespondeReserva = await response.json();
            setReservas(data.reservaLista || []);
        } catch (err) {
            console.error("Error cargando reservas:", err);
            setError((err as Error).message);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchReservas();
    }, [esAdmin]); 

    const handleReservaCreada = () => {
        fetchReservas();
    };

    const handleConfirmar = async (reservaId: number) => {
        setError(null);
        try {

            const response = await reservaApi.put(`/reserva/confirmar/${reservaId}`);

            if (!response.ok) {
                const data = await response.json();
                throw new Error(data.mensaje || 'Error al confirmar la reserva.');
            }
            fetchReservas();
        } catch (err) {
            console.error("Error confirmando reserva:", err);
            setError((err as Error).message);
        }
    };

    const handleCancelar = async (reservaId: number) => {
        if (!window.confirm("¿Estás seguro de que quieres cancelar esta reserva?")) {
            return;
        }
        setError(null);
        try {

            const response = await reservaApi.put(`/reserva/cancelar/${reservaId}`);

            if (!response.ok) {
                const data = await response.json();
                throw new Error(data.mensaje || 'Error al cancelar la reserva.');
            }
            fetchReservas();
        } catch (err) {
            console.error("Error cancelando reserva:", err);
            setError((err as Error).message);
        }
    };

    return (
        <div className="container mx-auto px-4 py-8">
            <div className="space-y-12">
                <h1 className="text-4xl font-extrabold text-gray-900 dark:text-white">
                    Gestión de Reservas
                </h1>

                <ReservaForm 
                    onReservaCreada={handleReservaCreada} 
                    user={user as AuthUser} // Pasa el usuario al formulario
                />

                <h2 className="text-3xl font-bold text-gray-800 border-b pb-4 dark:text-gray-200 dark:border-gray-700">
                    {esAdmin ? 'Listado de Todas las Reservas' : 'Listado de Mis Reservas'}
                </h2>

                {error && (
                    <div className="p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
                        {error}
                    </div>
                )}

                {isLoading ? (
                    <p className="text-center text-lg text-gray-600 dark:text-gray-400">Cargando listado de reservas...</p>
                ) : (
                    <ReservaList 
                        reservas={reservas} 
                        onConfirmarClick={handleConfirmar}
                        onCancelarClick={handleCancelar}
                    />
                )}
            </div>
        </div>
    );
};