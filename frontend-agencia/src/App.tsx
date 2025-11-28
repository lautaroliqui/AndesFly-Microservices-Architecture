import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Navbar } from './components/Navbar';
import { Landing } from './pages/Landing';
import { FlightSearch } from './pages/FlightSearch';
import { Reservations } from './pages/Reservations';
import { ClientManagement } from './pages/ClientManagement';
import { LoginPage } from './pages/LoginPage'; 
import { RegisterPage } from './pages/RegisterPage';
import { RutaProtegida } from './components/RutaProtegida';
// 1. Importa la nueva página
import { PerfilPage } from './pages/PerfilPage';

const NotFound = () => <div className="p-8 text-center text-2xl font-semibold bg-red-50 rounded-lg">Error 404 - Página no encontrada</div>;

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900">
        <Navbar /> 
        <main>
          <Routes>
            {/* --- Rutas Públicas --- */}
            <Route path="/" element={<Landing />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/registro" element={<RegisterPage />} />
            <Route path="/vuelos" element={<FlightSearch />} />

            {/* --- Rutas Protegidas (Cualquier usuario logueado) --- */}
            <Route element={<RutaProtegida />}>
              <Route path="/reservas" element={<Reservations />} />
              {/* 2. Añade la nueva ruta de perfil aquí */}
              <Route path="/perfil" element={<PerfilPage />} />
            </Route>

            {/* --- Rutas SOLO para ADMIN --- */}
            <Route element={<RutaProtegida rolRequerido="ROLE_ADMIN" />}>
              <Route path="/clientes" element={<ClientManagement />} />
            </Route>
            
            <Route path="*" element={<NotFound />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  );
}

export default App;