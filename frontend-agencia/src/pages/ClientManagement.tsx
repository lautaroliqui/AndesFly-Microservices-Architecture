import React, { useState, useEffect, useMemo } from 'react';
// 1. Importar el nuevo servicio
import { clienteApi } from '../services/apiService'; 
import type { Cliente, MensajeRespondeCliente } from '../types';
import { ClientList } from '../components/ClientList';
import { ClientForm } from '../components/ClientForm';

export const ClientManagement = () => {
  const [allClients, setAllClients] = useState<Cliente[]>([]);
  const [filtro, setFiltro] = useState("");
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [clientToEdit, setClientToEdit] = useState<Cliente | null>(null);
  const [isFormExpanded, setIsFormExpanded] = useState(false);
  const isEditing = clientToEdit !== null;

  const fetchClients = async () => {
    setIsLoading(true);
    setError(null);
    try {
      // 2. Usar clienteApi.get (ahora envía el token)
      const response = await clienteApi.get('/clientes');
      
      if (!response.ok) throw new Error('Error de red al cargar clientes.');
      
      const data: MensajeRespondeCliente = await response.json();
      setAllClients(data.clienteLista || []);
    } catch (err) {
      console.error("Error cargando clientes:", err);
      setError("No se pudo cargar la lista de clientes.");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchClients();
  }, []);

  const filteredClients = useMemo(() => {
    if (!filtro) return allClients;
    const lowerFiltro = filtro.toLowerCase();
    return allClients.filter(c => 
      c.nombreCompleto.toLowerCase().includes(lowerFiltro) ||
      c.email.toLowerCase().includes(lowerFiltro) ||
      c.documento.toLowerCase().includes(lowerFiltro)
    );
  }, [allClients, filtro]);

  const handleEditClick = (cliente: Cliente) => {
    setClientToEdit(cliente);
    setIsFormExpanded(true); 
    setTimeout(() => {
      document.getElementById('client-form-section')?.scrollIntoView({ behavior: 'smooth' });
    }, 100); 
  };
  
  const handleFormSuccessAndClose = () => {
    setClientToEdit(null); 
    fetchClients();       
    setIsFormExpanded(false); 
  };
  
  const handleDeleteClick = async (clienteId: number) => {
    if (!window.confirm("¿Estás seguro de que quieres eliminar a este cliente? Esta acción no se puede deshacer.")) {
      return;
    }
    setError(null);
    try {
      // 3. Usar clienteApi.delete (ahora envía el token)
      const response = await clienteApi.delete(`/cliente/${clienteId}`);

      if (!response.ok && response.status !== 204) { 
        throw new Error('Error al eliminar el cliente.');
      }
      fetchClients(); 
    } catch (err) {
      console.error("Error eliminando cliente:", err);
      setError((err as Error).message);
    }
  };
  
  const toggleForm = () => {
    setIsFormExpanded(prev => !prev);
    if (isFormExpanded) {
      setClientToEdit(null);
    }
  };

  // El JSX/return no cambia en absoluto.
  // (Omitido por brevedad, solo la lógica de fetch cambió)

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-4xl font-extrabold text-gray-900 mb-8 dark:text-white">
        Gestión de Clientes
      </h1>
      
      <div className="mb-12">
        <h2 className="text-3xl font-bold text-gray-800 border-b pb-4 mb-6 dark:text-gray-200 dark:border-gray-700">
          Listado de Clientes
        </h2>
        
        <input
          type="text"
          placeholder="Buscar por nombre, email o documento..."
          value={filtro}
          onChange={e => setFiltro(e.target.value)}
          className="w-full px-3 py-2 mb-4 border border-gray-300 rounded-md 
                     focus:outline-none focus:ring-2 focus:ring-blue-500
                     dark:bg-gray-800 dark:border-gray-600 dark:text-white"
        />
        
        {error && <div className="p-3 bg-red-100 border border-red-400 text-red-700 rounded-md mb-4">{error}</div>}
        
        {isLoading ? (
          <p className="text-center text-lg text-gray-600 dark:text-gray-400">Cargando clientes...</p>
        ) : (
          <ClientList 
            clientes={filteredClients} 
            onEditClick={handleEditClick}
            onDeleteClick={handleDeleteClick} 
          />
        )}
      </div>

      <section id="client-form-section" className="bg-white p-6 rounded-lg shadow-lg dark:bg-gray-800">
        <h2 
          className="text-2xl font-semibold text-gray-800 flex justify-between items-center cursor-pointer dark:text-gray-100"
          onClick={toggleForm}
          aria-expanded={isFormExpanded}
          aria-controls="form-content"
        >
          {isEditing ? `Editando Cliente: ${clientToEdit?.nombreCompleto}` : 'Registrar Nuevo Cliente'}
          
          <svg
              className={`w-6 h-6 transform transition-transform duration-300 ${isFormExpanded ? 'rotate-180' : ''}`}
              fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"
          >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path>
          </svg>
        </h2>
        
        <div
          id="form-content"
          className={`overflow-hidden transition-all duration-500 ease-in-out ${isFormExpanded ? 'max-h-[2000px] opacity-100 pt-6' : 'max-h-0 opacity-0'}`}
        >
          <ClientForm 
            clientToEdit={clientToEdit}
            onFormSuccess={handleFormSuccessAndClose} 
          />
        </div>
      </section>
      
    </div>
  );
};