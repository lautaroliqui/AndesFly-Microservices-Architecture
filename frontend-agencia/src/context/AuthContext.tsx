import React, { createContext, useContext, useState, useEffect } from 'react';
import { CLIENTE_API_URL } from '../services/api';
import { jwtDecode } from 'jwt-decode'; // <-- 1. IMPORTAR EL DECODIFICADOR

// 2. Definir la forma del Usuario (extraído del token)
// (Esto debe coincidir con los 'claims' que pones en JwtUtil.java)
interface AuthUser {
  id: number;
  sub: string; // El 'subject' (email)
  rol: string; // Ej: "ROLE_ADMIN"
}

// 3. Actualizar el Contexto para que incluya al 'user'
interface AuthContextType {
  token: string | null;
  user: AuthUser | null; // <-- Guardar el objeto de usuario
  login: (email: string, pass: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

interface LoginResponse {
  token: string;
}

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<AuthUser | null>(null); // <-- 4. Añadir estado de usuario

  // 5. Actualizar el useEffect para decodificar el token al cargar la app
  useEffect(() => {
    const storedToken = localStorage.getItem('authToken');
    if (storedToken) {
      try {
        // Decodifica el token guardado para obtener los datos del usuario
        const decodedUser: AuthUser = jwtDecode(storedToken);
        setToken(storedToken);
        setUser(decodedUser);
      } catch (e) {
        // El token es inválido o está corrupto, lo borramos
        localStorage.removeItem('authToken');
      }
    }
  }, []);

  const login = async (email: string, pass: string) => {
    try {
      const response = await fetch(`${CLIENTE_API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email, password: pass }),
      });

      if (!response.ok) {
        throw new Error('Email o contraseña incorrectos');
      }

      const data: LoginResponse = await response.json();
      
      // 6. Decodificar el token NUEVO al hacer login
      const decodedUser: AuthUser = jwtDecode(data.token);
      
      // 7. Guardar AMBOS (token y usuario)
      setToken(data.token);
      setUser(decodedUser); // <-- Guardar el usuario decodificado
      localStorage.setItem('authToken', data.token);

    } catch (err) {
      console.error("Error en el login:", err);
      // Limpiar todo si falla
      localStorage.removeItem('authToken');
      setToken(null);
      setUser(null);
      throw err;
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null); // <-- 8. Limpiar el usuario al hacer logout
    localStorage.removeItem('authToken');
    window.location.href = '/'; 
  };

  return (
    // 9. Pasar el 'user' al proveedor
    <AuthContext.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth debe ser usado dentro de un AuthProvider");
  }
  return context;
};