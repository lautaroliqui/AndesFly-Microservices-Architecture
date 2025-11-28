import React, { useState, useEffect } from 'react';
// 1. Importar el nuevo servicio
import { clienteApi } from '../services/apiService';
import type { Cliente, Domicilio } from '../types';

type DomicilioFormState = Partial<Omit<Domicilio, 'id' | 'cliente'>>;

interface ClientFormProps {
  onFormSuccess: () => void;
  clientToEdit: Cliente | null;
}

const createEmptyDomicilio = (esPrincipal = false): DomicilioFormState => ({
  calle: '',
  numero: '',
  ciudad: '',
  provincia: '',
  pais: '',
  codigoPostal: '',
  principal: esPrincipal,
});

export const ClientForm = ({ onFormSuccess, clientToEdit }: ClientFormProps) => {
  const [nombre, setNombre] = useState('');
  const [email, setEmail] = useState('');
  const [documento, setDocumento] = useState('');
  const [domicilios, setDomicilios] = useState<DomicilioFormState[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const isEditing = clientToEdit !== null;

  useEffect(() => {
    if (isEditing && clientToEdit) {
      setNombre(clientToEdit.nombreCompleto);
      setEmail(clientToEdit.email);
      setDocumento(clientToEdit.documento);
      setDomicilios(clientToEdit.domicilios && clientToEdit.domicilios.length > 0 ? clientToEdit.domicilios : [createEmptyDomicilio(true)]);
      setError(null);
    } else {
      setNombre('');
      setEmail('');
      setDocumento('');
      setDomicilios([createEmptyDomicilio(true)]);
    }
  }, [clientToEdit, isEditing]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    // NOTA: El 'password' y 'rol' no se manejan aquí.
    // Este formulario es solo para que el ADMIN gestione datos.
    const clientData = {
      id: isEditing ? clientToEdit?.id : undefined,
      nombreCompleto: nombre,
      email: email,
      documento: documento,
      domicilios: domicilios 
    };

    try {
      // 2. Usar clienteApi.put o clienteApi.post
      let response;
      if (isEditing) {
        response = await clienteApi.put('/cliente', clientData);
      } else {
        response = await clienteApi.post('/cliente', clientData);
      }

      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message || `Error al ${isEditing ? 'actualizar' : 'crear'} el cliente`);
      }

      onFormSuccess(); 

    } catch (err) {
      setError((err as Error).message);
    } finally {
      setIsLoading(false);
    }
  };
  
  const handleDomicilioChange = (index: number, e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    const list = [...domicilios];
    list[index] = { ...list[index], [name]: value };
    setDomicilios(list);
  };
  
  const agregarDomicilio = () => {
    setDomicilios([...domicilios, createEmptyDomicilio(false)]);
  };

  const eliminarDomicilio = (index: number) => {
    if (domicilios.length <= 1) return; 
    const list = domicilios.filter((_, i) => i !== index);
    if (index === 0 && list.length > 0) {
      list[0].principal = true;
    }
    setDomicilios(list);
  };

  const handleCancel = () => {
    onFormSuccess();
  };

  const inputClasses = "mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white dark:focus:ring-blue-600 dark:focus:border-blue-600";
  const labelClasses = "block text-sm font-medium text-gray-700 dark:text-gray-300";

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="nombre" className={labelClasses}>Nombre Completo</label>
        <input type="text" id="nombre" value={nombre} onChange={e => setNombre(e.target.value)} required className={inputClasses} />
      </div>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label htmlFor="email" className={labelClasses}>Email</label>
          <input type="email" id="email" value={email} onChange={e => setEmail(e.target.value)} required className={inputClasses} />
        </div>
        <div>
          <label htmlFor="documento" className={labelClasses}>Documento</label>
          <input type="text" id="documento" value={documento} onChange={e => setDocumento(e.target.value)} required className={inputClasses} />
        </div>
      </div>

      <hr className="dark:border-gray-700"/>

      {domicilios.map((dom, index) => (
        <div key={index} className="space-y-4 border border-gray-200 p-4 rounded-lg relative dark:border-gray-700">
          
          {domicilios.length > 1 && (
            <button 
              type="button" 
              onClick={() => eliminarDomicilio(index)}
              className="absolute top-2 right-2 px-2 py-0.5 text-xs text-red-600 bg-red-100 rounded-full hover:bg-red-200"
            >
              &times;
            </button>
          )}

          <h4 className="text-lg font-medium text-gray-800 dark:text-gray-200">
            Domicilio {index + 1} {dom.principal ? '(Principal)' : ''}
          </h4>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="md:col-span-2">
              <label htmlFor={`calle-${index}`} className={labelClasses}>Calle</label>
              <input type="text" id={`calle-${index}`} name="calle" value={dom.calle || ''} onChange={e => handleDomicilioChange(index, e)} required className={inputClasses} />
            </div>
            <div>
              <label htmlFor={`numero-${index}`} className={labelClasses}>Número</label>
              <input type="text" id={`numero-${index}`} name="numero" value={dom.numero || ''} onChange={e => handleDomicilioChange(index, e)} required className={inputClasses} />
            </div>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label htmlFor={`ciudad-${index}`} className={labelClasses}>Ciudad</label>
              <input type="text" id={`ciudad-${index}`} name="ciudad" value={dom.ciudad || ''} onChange={e => handleDomicilioChange(index, e)} required className={inputClasses} />
            </div>
            <div>
              <label htmlFor={`provincia-${index}`} className={labelClasses}>Provincia</label>
              <input type="text" id={`provincia-${index}`} name="provincia" value={dom.provincia || ''} onChange={e => handleDomicilioChange(index, e)} required className={inputClasses} />
            </div>
            <div>
              <label htmlFor={`codigoPostal-${index}`} className={labelClasses}>Cód. Postal</label>
              <input type="text" id={`codigoPostal-${index}`} name="codigoPostal" value={dom.codigoPostal || ''} onChange={e => handleDomicilioChange(index, e)} required className={inputClasses} />
            </div>
          </div>
          <div>
              <label htmlFor={`pais-${index}`} className={labelClasses}>País</label>
              <input type="text" id={`pais-${index}`} name="pais" value={dom.pais || ''} onChange={e => handleDomicilioChange(index, e)} required className={inputClasses} />
          </div>
        </div>
      ))}
      
      <button
        type="button"
        onClick={agregarDomicilio}
        className="w-full px-4 py-2 border border-dashed border-gray-400 rounded-md text-sm font-medium text-gray-700 bg-gray-50 hover:bg-gray-100
                   dark:bg-gray-700 dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-600"
      >
        + Añadir otro Domicilio
      </button>
      
      {error && <div className="p-3 bg-red-100 border border-red-400 text-red-700 rounded-md">{error}</div>}
      
      <div className="flex gap-4">
        <button
          type="submit"
          disabled={isLoading}
          className="w-full px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white
                     bg-brand-accent hover:bg-brand-accent-hover
                     focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-brand-accent disabled:bg-gray-400"
        >
          {isLoading ? 'Guardando...' : (isEditing ? 'Actualizar Cliente' : 'Guardar Cliente')}
        </button>
        
        {isEditing && (
          <button
            type="button"
            onClick={handleCancel}
            className="w-full px-4 py-2 border border-gray-300 rounded-md shadow-sm text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 
                       focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-400
                       dark:bg-gray-600 dark:text-gray-200 dark:border-gray-500 dark:hover:bg-gray-500"
          >
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
};