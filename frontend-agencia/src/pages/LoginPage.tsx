import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

export const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const { login } = useAuth(); // Obtiene la función de login del Context
  const navigate = useNavigate(); // Hook para redirigir

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      // 1. Llama a la función de login del Context
      await login(email, password);
      
      // 2. Si tiene éxito, redirige a la página de clientes
      navigate('/clientes'); 

    } catch (err) {
      // 3. Si falla, muestra el error
      setError((err as Error).message || 'Error desconocido');
    } finally {
      setIsLoading(false);
    }
  };

  const inputClasses = "mt-1 w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white dark:focus:ring-blue-600 dark:focus:border-blue-600";
  const labelClasses = "block text-sm font-medium text-gray-700 dark:text-gray-300";

  return (
    <div className="container mx-auto px-4 py-8 flex justify-center">
      <div className="w-full max-w-md">
        <form 
          onSubmit={handleSubmit} 
          className="bg-white dark:bg-gray-800 shadow-lg rounded-xl p-8 space-y-6"
        >
          <h1 className="text-3xl font-bold text-center text-gray-900 dark:text-white">
            Iniciar Sesión
          </h1>
          
          {error && (
            <div className="p-3 bg-red-100 border border-red-400 text-red-700 rounded-md">
              {error}
            </div>
          )}

          <div>
            <label htmlFor="email" className={labelClasses}>Email</label>
            <input 
              type="email" 
              id="email" 
              value={email} 
              onChange={e => setEmail(e.target.value)} 
              required 
              className={inputClasses}
            />
          </div>
          <div>
            <label htmlFor="password" className={labelClasses}>Contraseña</label>
            <input 
              type="password" 
              id="password" 
              value={password} 
              onChange={e => setPassword(e.target.value)} 
              required 
              className={inputClasses}
            />
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className="w-full px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white 
                       bg-brand-accent hover:bg-brand-accent-hover 
                       focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-brand-accent disabled:bg-gray-400"
          >
            {isLoading ? 'Ingresando...' : 'Ingresar'}
          </button>
          
          <p className="text-sm text-center text-gray-600 dark:text-gray-400">
            ¿No tienes una cuenta? {' '}
            <Link to="/registro" className="font-medium text-brand-primary hover:underline">
              Regístrate aquí
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
};