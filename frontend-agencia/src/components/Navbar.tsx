import React, { useState } from 'react'; // 1. Importamos useState
import { Link } from 'react-router-dom';
import logo from '../assets/AndesFly Logo (PNG).webp';
import { ThemeToggle } from './ThemeToggle'; 
import { useAuth } from '../context/AuthContext';

export const Navbar = () => {
  const { token, user, logout } = useAuth();
  const esAdmin = user?.rol === 'ROLE_ADMIN';
  
  // 2. Estado para controlar si el menú desplegable está abierto
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <header className="bg-brand-primary shadow-md h-16 z-50 dark:bg-gray-900 relative"> 
      <div className="container mx-auto px-4 h-full flex justify-between items-center">
        
        {/* --- LOGO --- */}
        <Link to="/" className="flex items-center gap-3">
          <img src={logo} alt="AndesFly Logo" className="h-10 w-auto" /> 
          <span className="hidden sm:block text-2xl font-bold tracking-wide">
            <span className="text-white">Andes</span>
            <span className="text-logo-light">Fly</span> 
          </span>
        </Link>
        
        {/* --- NAVEGACIÓN --- */}
        <nav className="flex items-center space-x-6">
          {/* Rutas Públicas */}
          <Link to="/vuelos" className="text-white font-medium hover:text-brand-primary-light transition-colors">
            Vuelos
          </Link>
          
          {/* Rutas Autenticadas */}
          {token && (
            <Link to="/reservas" className="text-white font-medium hover:text-brand-primary-light transition-colors">
              Mis Reservas
            </Link>
          )}

          {/* Rutas Admin */}
          {esAdmin && (
            <Link to="/clientes" className="text-white font-medium hover:text-brand-primary-light transition-colors">
              Clientes
            </Link>
          )}
          
          {/* --- MENÚ DE USUARIO / LOGIN --- */}
          {token ? (
            // Si está logueado, mostramos el Icono y el Menú
            <div className="relative">
              {/* Botón del Icono */}
              <button 
                onClick={() => setIsMenuOpen(!isMenuOpen)}
                className="flex items-center justify-center w-10 h-10 rounded-full bg-white/10 hover:bg-white/20 transition-colors focus:outline-none focus:ring-2 focus:ring-white/50"
                aria-label="Menú de usuario"
              >
                {/* Icono de Usuario (SVG) */}
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" className="w-6 h-6 text-white">
                  <path fillRule="evenodd" d="M7.5 6a4.5 4.5 0 119 0 4.5 4.5 0 01-9 0zM3.751 20.105a8.25 8.25 0 0116.498 0 .75.75 0 01-.437.695A18.683 18.683 0 0112 22.5c-2.786 0-5.433-.608-7.812-1.7a.75.75 0 01-.437-.695z" clipRule="evenodd" />
                </svg>
              </button>

              {/* Menú Desplegable (Dropdown) */}
              {isMenuOpen && (
                <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-xl py-2 z-50 dark:bg-gray-800 border border-gray-100 dark:border-gray-700 animate-fade-in-down">
                  
                  {/* Cabecera del menú con el email */}
                  <div className="px-4 py-3 border-b border-gray-100 dark:border-gray-700">
                    <p className="text-xs text-gray-500 dark:text-gray-400 uppercase font-semibold tracking-wider">Cuenta</p>
                    <p className="text-sm font-medium text-gray-900 dark:text-white truncate" title={user?.sub}>
                      {user?.sub}
                    </p>
                  </div>

                  {/* Opciones */}
                  <Link 
                    to="/perfil" 
                    className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 hover:text-brand-primary dark:text-gray-300 dark:hover:bg-gray-700 dark:hover:text-white transition-colors"
                    onClick={() => setIsMenuOpen(false)}
                  >
                    Mi Perfil
                  </Link>
                  
                  <div className="border-t border-gray-100 dark:border-gray-700 my-1"></div>

                  <button
                    onClick={() => {
                        logout();
                        setIsMenuOpen(false);
                    }}
                    className="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 dark:text-red-400 dark:hover:bg-gray-700 transition-colors"
                  >
                    Cerrar Sesión
                  </button>
                </div>
              )}

              {/* Overlay invisible para cerrar el menú al hacer clic fuera */}
              {isMenuOpen && (
                <div 
                  className="fixed inset-0 z-40" 
                  onClick={() => setIsMenuOpen(false)}
                ></div>
              )}
            </div>
          ) : (
            // Si NO está logueado, mostramos el link simple
            <Link to="/login" className="text-white font-medium hover:text-brand-primary-light transition-colors">
              Login
            </Link>
          )}
          
          <div className="border-l border-brand-primary-light dark:border-gray-600 h-6 opacity-50"></div>
          <ThemeToggle />
        </nav>
      </div>
    </header>
  );
};