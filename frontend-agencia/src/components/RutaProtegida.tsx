import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface RutaProtegidaProps {
  rolRequerido?: 'ROLE_ADMIN' | 'ROLE_USER';
}

/**
 * Este componente actúa como un guardia para las rutas de React Router.
 */
export const RutaProtegida = ({ rolRequerido }: RutaProtegidaProps) => {
  const { user, token } = useAuth();

  // 1. ¿No está logueado?
  if (!token) {
    // Redirige al login, guardando la página que intentaba visitar
    return <Navigate to="/login" replace />;
  }

  // 2. ¿La ruta requiere un rol específico? (ej. ADMIN)
  if (rolRequerido && user?.rol !== rolRequerido) {
    // Tiene token, pero no el rol. Redirige al inicio.
    // (Podríamos crear una página "403 No Autorizado" aquí)
    return <Navigate to="/" replace />;
  }

  // 3. ¡Éxito! El usuario está logueado y tiene el rol correcto (o no se requiere rol).
  // <Outlet /> renderiza el componente hijo (ej. <ClientManagement />)
  return <Outlet />;
};