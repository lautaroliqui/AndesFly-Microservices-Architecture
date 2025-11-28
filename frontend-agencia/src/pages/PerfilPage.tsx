import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { clienteApi } from '../services/apiService';
import { useAuth } from '../context/AuthContext';
import type { Cliente, Domicilio } from '../types';

// Tipo helper para el estado del formulario de domicilio
type DomicilioFormState = Partial<Omit<Domicilio, 'id' | 'cliente'>>;

// Función helper para crear un objeto de domicilio vacío
const createEmptyDomicilio = (esPrincipal = false): DomicilioFormState => ({
  calle: '',
  numero: '',
  ciudad: '',
  provincia: '',
  pais: '',
  codigoPostal: '',
  principal: esPrincipal,
});

export const PerfilPage = () => {
  const { user } = useAuth(); // Obtenemos el usuario para el email
  const navigate = useNavigate();

  // Estado del formulario
  const [nombreCompleto, setNombreCompleto] = useState('');
  const [documento, setDocumento] = useState('');
  const [email, setEmail] = useState(user?.sub || ''); // Email del token (no editable)
  const [domicilios, setDomicilios] = useState<DomicilioFormState[]>([createEmptyDomicilio(true)]);

  // Estado de la UI
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  // --- Carga de Datos (GET) ---
  useEffect(() => {
    const fetchPerfil = async () => {
      setIsLoading(true);
      setError(null);
      try {
        const response = await clienteApi.get('/cliente/mi-perfil');
        if (!response.ok) {
          throw new Error("No se pudo cargar la información de tu perfil.");
        }
        const data: { mensaje: string, cliente: Cliente } = await response.json();
        
        // Poblamos el formulario con los datos de la API
        setNombreCompleto(data.cliente.nombreCompleto);
        setDocumento(data.cliente.documento);
        setEmail(data.cliente.email); // Sincroniza el email por si acaso
        if (data.cliente.domicilios && data.cliente.domicilios.length > 0) {
          setDomicilios(data.cliente.domicilios);
        }

      } catch (err) {
        setError((err as Error).message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchPerfil();
  }, []); // Se ejecuta solo una vez al cargar

  // --- Lógica de Domicilios (Idéntica a RegisterPage) ---
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

  // --- Envío de Datos (PUT) ---
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSaving(true);
    setError(null);
    setSuccess(null);

    const perfilActualizado = {
      // El backend ignora el email, pero lo enviamos por completitud del DTO
      email: email, 
      nombreCompleto: nombreCompleto,
      documento: documento,
      domicilios: domicilios
      // No enviamos 'password' ni 'rol'
    };

    try {
      const response = await clienteApi.put('/cliente/mi-perfil', perfilActualizado);

      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message || 'Error al actualizar el perfil');
      }
      
      setSuccess("¡Perfil actualizado exitosamente!");

    } catch (err) {
      setError((err as Error).message);
    } finally {
      setIsSaving(false);
    }
  };

  // Clases de Tailwind
  const inputClasses = "mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white dark:focus:ring-blue-600 dark:focus:border-blue-600";
  const labelClasses = "block text-sm font-medium text-gray-700 dark:text-gray-300";
  
  if (isLoading) {
    return <p className="text-center p-8 dark:text-gray-300">Cargando tu perfil...</p>;
  }

  return (
    <div className="container mx-auto px-4 py-8 flex justify-center">
      <div className="w-full max-w-2xl">
        <form 
          onSubmit={handleSubmit} 
          className="bg-white dark:bg-gray-800 shadow-lg rounded-xl p-8 space-y-4"
        >
          <h1 className="text-3xl font-bold text-center text-gray-900 dark:text-white">
            Mi Perfil
          </h1>
          
          {error && (
            <div className="p-3 bg-red-100 border border-red-400 text-red-700 rounded-md">
              {error}
            </div>
          )}
          {success && (
            <div className="p-3 bg-green-100 border border-green-400 text-green-700 rounded-md">
              {success}
            </div>
          )}

          {/* --- Datos Personales --- */}
          <div>
            <label htmlFor="email" className={labelClasses}>Email (No se puede cambiar)</label>
            <input 
              type="email" 
              id="email" 
              value={email} 
              disabled // El email es el 'username', no debe cambiarse
              className={`${inputClasses} bg-gray-100 dark:bg-gray-800`}
            />
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label htmlFor="nombre" className={labelClasses}>Nombre Completo</label>
              <input type="text" id="nombre" value={nombreCompleto} onChange={e => setNombreCompleto(e.target.value)} required className={inputClasses} />
            </div>
            <div>
              <label htmlFor="documento" className={labelClasses}>Documento</label>
              <input type="text" id="documento" value={documento} onChange={e => setDocumento(e.target.value)} required className={inputClasses} />
            </div>
          </div>

          <hr className="dark:border-gray-700"/>

          {/* --- Domicilios Dinámicos --- */}
          <h2 className="text-xl font-semibold text-gray-800 dark:text-gray-100">Mis Domicilios</h2>
          {domicilios.map((dom, index) => (
            <div key={index} className="space-y-4 border border-gray-200 p-4 rounded-lg relative dark:border-gray-700">
              {domicilios.length > 1 && (
                <button type="button" onClick={() => eliminarDomicilio(index)} className="absolute top-2 right-2 px-2 py-0.5 text-xs text-red-600 bg-red-100 rounded-full hover:bg-red-200">
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
          
          <button type="button" onClick={agregarDomicilio} className="w-full px-4 py-2 border border-dashed border-gray-400 rounded-md text-sm font-medium text-gray-700 bg-gray-50 hover:bg-gray-100 dark:bg-gray-700 dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-600">
            + Añadir otro Domicilio
          </button>
          
          <button
            type="submit"
            disabled={isSaving}
            className="w-full px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white 
                       bg-brand-accent hover:bg-brand-accent-hover 
                       focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-brand-accent disabled:bg-gray-400"
          >
            {isSaving ? 'Guardando...' : 'Guardar Cambios'}
          </button>
        </form>
      </div>
    </div>
  );
};