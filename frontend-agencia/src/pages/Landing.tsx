import React from 'react';
import { Link } from 'react-router-dom';
import backgroundImage from '../assets/landing-background.webp';
export const Landing = () => {
  return (
    <div
      className="relative flex items-center justify-center text-white"
      style={{
        height: 'calc(100vh - 4rem)',
        backgroundImage: `url(${backgroundImage})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center', 
        
        // Color de fondo temporal mientras no hay imagen:
        backgroundColor: '#334155' // Un gris-azulado oscuro
      }}
    >
      {/* Capa de superposición oscura para legibilidad */}
      <div className="absolute inset-0 bg-black opacity-50"></div>

      {/* Contenido centrado */}
      <div className="relative z-10 flex flex-col items-center gap-6 p-8">
        <h1 className="text-6xl font-extrabold tracking-tight text-center"
            style={{ textShadow: '2px 2px 8px rgba(0,0,0,0.7)' }}
        >
          Bienvenido a <span className="text-white">Andes</span><span className="text-[#0EA5E9]">Fly</span>
        </h1>
        <p className="text-xl text-gray-200">
          Tu puerta de entrada a los cielos del norte.
        </p>
        
        {/* Botones de navegación */}
        <div className="flex gap-4 mt-4">
          <Link
            to="/vuelos"
            className="px-6 py-3 bg-[#F97316] text-white font-semibold rounded-lg shadow-md
                       hover:bg-[#EA580C] transition-colors focus:outline-none 
                       focus:ring-2 focus:ring-offset-2 focus:ring-[#F97316]"
          >
            Buscar Vuelos
          </Link>
          <Link
            to="/reservas"
            className="px-6 py-3 bg-white text-gray-800 font-semibold rounded-lg shadow-md
                       hover:bg-gray-200 transition-colors focus:outline-none
                       focus:ring-2 focus:ring-offset-2 focus:ring-white"
          >
            Mis Reservas
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Landing;