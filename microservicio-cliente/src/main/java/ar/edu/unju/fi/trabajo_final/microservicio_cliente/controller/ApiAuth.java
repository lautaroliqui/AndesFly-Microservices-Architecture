package ar.edu.unju.fi.trabajo_final.microservicio_cliente.controller;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;


public interface ApiAuth {

    // ───────────────────────────────────────────────────────────────
    // LOGIN
    // ───────────────────────────────────────────────────────────────

    @PostMapping("/login")
    @Operation(
            summary = "Autenticar usuario y generar token JWT",
            description = """
                    Valida las credenciales del usuario.
                    
                    Flujo real:
                    - Spring Security autentica email + contraseña.
                    - Se carga el usuario con UserDetailsServiceImpl.
                    - Si es correcto → se genera un token JWT mediante JwtUtil.
                    - El token incluye: id, email, rol.
                    
                    Devuelve un objeto LoginResponse con el JWT.
                    """,
            requestBody = @RequestBody(
                    required = true,
                    description = "Credenciales del usuario",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "email": "usuario@gmail.com",
                                      "password": "123456"
                                    }
                                    """),
                            schema = @Schema(implementation = LoginRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login exitoso. Devuelve el token JWT.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "token": "eyJhbGciOiJIUzI1NiJ9..."
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales inválidas (email o contraseña incorrectos)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponse.class),
                                    examples = @ExampleObject(value = """
                                            {
                                              "token": null
                                            }
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<LoginResponse> login(
            @org.springframework.web.bind.annotation.RequestBody LoginRequest loginRequest
    );

    // ───────────────────────────────────────────────────────────────
    // REGISTER
    // ───────────────────────────────────────────────────────────────

    @PostMapping("/register")
    @Operation(
            summary = "Registrar un nuevo usuario",
            description = """
                    Registra un nuevo cliente en el sistema.
                    
                    Lógica real aplicada:
                    - Se encripta la contraseña con PasswordEncoder.
                    - El usuario se registra con rol por defecto: ROLE_USER.
                    - Se guarda en la base de datos mediante ClienteService.
                    
                    No devuelve el objeto creado, solo un mensaje.
                    """,
            requestBody = @RequestBody(
                    required = true,
                    description = "Datos del cliente a registrar",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Cliente.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nombre": "Juan",
                                      "apellido": "Pérez",
                                      "email": "juan@gmail.com",
                                      "password": "123456",
                                      "documento": 30123456,
                                      "domicilios": [
                                        {
                                          "calle": "Av. Siempre Viva",
                                          "numero": 742,
                                          "ciudad": "San Salvador de Jujuy"
                                        }
                                      ]
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Usuario registrado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "mensaje": "Usuario registrado exitosamente"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Email ya registrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "fecha": "2025-11-18T18:07:12.127+00:00",
                                                        "message": "El email ya pertenece a otro cliente",
                                                        "uri": "/api/v1_1/auth/register"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<Map<String, String>> register(
            @org.springframework.web.bind.annotation.RequestBody Cliente cliente
    );
}
