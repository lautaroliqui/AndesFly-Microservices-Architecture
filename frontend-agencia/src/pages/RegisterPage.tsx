import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { CLIENTE_API_URL } from '../services/api';
import type { Domicilio } from '../types';

type DomicilioFormState = Partial<Omit<Domicilio, 'id'>>;

const createEmptyDomicilio = (esPrincipal = false): DomicilioFormState => ({
  calle: '',
  numero: '',
  ciudad: '',
  provincia: '',
  pais: '',
  codigoPostal: '',
  principal: esPrincipal,
});

export const RegisterPage = () => {
  const [nombre, setNombre] = useState('');
  const [email, setEmail] = useState('');
  const [documento, setDocumento] = useState('');
  const [password, setPassword] = useState('');
  const [domicilios, setDomicilios] = useState<DomicilioFormState[]>([createEmptyDomicilio(true)]);
  const [error, setError] = useState<string | null>(null);
  const [mensaje, setMensaje] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);
    setMensaje(null);

    const nuevoCliente = {
      nombreCompleto: nombre,
      email: email,
      documento: documento,
      password: password,
      domicilios: domicilios 
    };

    try {
      // Esta URL (CLIENTE_API_URL + /auth/register) es correcta.
      // Fallará con 404 hasta que arregles el Gateway (Solución 1).
      const response = await fetch(`${CLIENTE_API_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(nuevoCliente),
      });

      if (!response.ok) {
        // Si el Gateway devuelve 404, entrará aquí
        const data = await response.json();
        throw new Error(data.message || 'Error al registrar el usuario');
      }

      setMensaje("¡Registro exitoso! Serás redirigido al Login...");
      setTimeout(() => {
        navigate('/login');
      }, 2000);

    } catch (err) {
      setError((err as Error).message);
    } finally {
      setIsLoading(false);
    }
  };

  const inputClasses = "mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white dark:focus:ring-blue-600 dark:focus:border-blue-600";
  const labelClasses = "block text-sm font-medium text-gray-700 dark:text-gray-300";

  return (
    <div className="container mx-auto px-4 py-8 flex justify-center">
      <div className="w-full max-w-2xl">
        <form 
          onSubmit={handleSubmit} 
          className="bg-white dark:bg-gray-800 shadow-lg rounded-xl p-8 space-y-4"
        >
          <h1 className="text-3xl font-bold text-center text-gray-900 dark:text-white">
            Crear una Cuenta
          </h1>
          
          {error && (
            <div className="p-3 bg-red-100 border border-red-400 text-red-700 rounded-md">
              {error}
            </div>
          )}
          {mensaje && (
            <div className="p-3 bg-green-100 border border-green-400 text-green-700 rounded-md">
              {mensaje}
            </div>
          )}

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
              <label htmlFor="password" className={labelClasses}>Contraseña</label>
              <input type="password" id="password" value={password} onChange={e => setPassword(e.target.value)} required className={inputClasses} />
            </div>
          </div>
          <div>
            <label htmlFor="documento" className={labelClasses}>Documento</label>
            <input type="text" id="documento" value={documento} onChange={e => setDocumento(e.target.value)} required className={inputClasses} />
          </div>

          <hr className="dark:border-gray-700"/>

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
            disabled={isLoading}
            className="w-full px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white 
                       bg-brand-accent hover:bg-brand-accent-hover 
                       focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-brand-accent disabled:bg-gray-400"
          >
            {isLoading ? 'Registrando...' : 'Crear Cuenta'}
          </button>
          
          <p className="text-sm text-center text-gray-600 dark:text-gray-400">
            ¿Ya tienes una cuenta? {' '}
            <Link to="/login" className="font-medium text-brand-primary hover:underline">
              Inicia sesión
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
};