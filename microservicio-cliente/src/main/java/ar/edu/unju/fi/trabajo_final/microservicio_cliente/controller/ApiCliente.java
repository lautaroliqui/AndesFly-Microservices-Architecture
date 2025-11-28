package ar.edu.unju.fi.trabajo_final.microservicio_cliente.controller;

import ar.edu.unju.fi.trabajo_final.microservicio_cliente.entity.Cliente;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.payload.ApiResponde;
import ar.edu.unju.fi.trabajo_final.microservicio_cliente.payload.MensajeRespondeCliente;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

public interface ApiCliente {

    @GetMapping("cliente/mi-perfil")
    @Operation(
            summary = "Obtener mi perfil (solo USER)",
            description = "Obtiene los datos del cliente autenticado usando el ID extraído del token JWT.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Perfil obtenido exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeCliente.class),
                                    examples = @ExampleObject(
                                            value = """
                                            {
                                              "mensaje": "Perfil obtenido exitosamente",
                                              "cliente": {
                                                "id": 12,
                                                "nombreCompleto": "Juan Pérez",
                                                "email": "juan@example.com",
                                                "documento": "12345678",
                                                "domicilios": [
                                                  {
                                                    "id": 1,
                                                    "calle": "Belgrano",
                                                    "numero": "450",
                                                    "ciudad": "San Salvador"
                                                  }
                                                ]
                                              }
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "El cliente no existe",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponde.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                    "fecha": "2025-11-18T18:07:12.127+00:00",
                                                    "message": "Cliente con ID 12 no encontrado.",
                                                    "uri": "/api/v1_1/cliente/mi-perfil"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeCliente> obtenerMiPerfil(
            @Parameter(description = "Token JWT del usuario", hidden = true)
            @AuthenticationPrincipal Jwt jwt
    );


    @PutMapping("cliente/mi-perfil")
    @Operation(
            summary = "Actualizar mi perfil (solo USER)",
            description = """
                    Actualiza los datos del cliente autenticado.
                    Restricciones:
                    - El email NO puede modificarse desde este endpoint.
                    - Se valida la unicidad del documento.
                    - Actualiza también la lista de domicilios.
                    """,
            requestBody = @RequestBody(
                    description = "Nuevos datos del perfil",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Cliente.class),
                            examples = @ExampleObject(
                                    name = "PerfilUpdateExample",
                                    value = """
                                        {
                                          "nombreCompleto": "Juan Pérez Actualizado",
                                          "documento": "98765432",
                                          "domicilios": [
                                            {
                                              "calle": "Lavalle",
                                              "numero": "123",
                                              "ciudad": "Palpalá"
                                            }
                                          ]
                                        }
                                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Perfil actualizado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                            {
                                              "mensaje": "Perfil actualizado exitosamente.",
                                              "cliente": {
                                                "id": 12,
                                                "nombreCompleto": "Juan Pérez Actualizado",
                                                "email": "juan@example.com",
                                                "documento": "98765432"
                                              }
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "El cliente no existe",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                    "fecha": "2025-11-18T18:07:12.127+00:00",
                                                    "message": "Cliente no existente.",
                                                    "uri": "/api/v1_1/cliente/mi-perfil"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Documento duplicado",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                    "fecha": "2025-11-18T18:07:12.127+00:00",
                                                    "message": "El documento ya pertenece a otro cliente.",
                                                    "uri": "/api/v1_1/cliente/mi-perfil"
                                                    }
                                                    """
                                    )
                            )

                    )
            }
    )
    ResponseEntity<MensajeRespondeCliente> actualizarMiPerfil(
            @RequestBody Cliente clienteNuevosDatos,
            @AuthenticationPrincipal Jwt jwt
    );

    @PostMapping("cliente")
    @Operation(
            summary = "[ADMIN] Crear cliente",
            description = """
                    Crea un nuevo cliente validando:
                    - Email único
                    - Documento único
                    - Domicilios asociados correctamente.
                    """,
            requestBody = @RequestBody(
                    description = "Datos del cliente a crear",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Cliente.class),
                            examples = @ExampleObject(
                                    name = "ClienteCreateExample",
                                    value = """
                                        {
                                          "nombreCompleto": "María Gómez",
                                          "email": "maria@example.com",
                                          "documento": "32165498",
                                          "password": "123456",
                                          "rol": "ROLE_USER",
                                          "domicilios": [
                                            {
                                              "calle": "Independencia",
                                              "numero": "1000",
                                              "ciudad": "San Salvador"
                                            }
                                          ]
                                        }
                                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Cliente creado exitosamente",
                            content = @Content(
                                    schema = @Schema(implementation = MensajeRespondeCliente.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    "mensaje":"Cliente guardado existosamente",
                                                    "cliente":{
                                                              "id": 3,
                                                              "nombreCompleto": "María Gómez",
                                                              "email": "maria@example.com",
                                                              "documento": "32165498",
                                                              "password": "123456",
                                                              "rol": "ROLE_USER",
                                                              "domicilios": [
                                                                {
                                                                  "calle": "Independencia",
                                                                  "numero": "1000",
                                                                  "ciudad": "San Salvador"
                                                                }
                                                              ]
                                                            } 
                                                    """
                                    )
                            )
                    ),


                    @ApiResponse(
                            responseCode = "409",
                            description = "Documento duplicado",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                    "fecha": "2025-11-18T18:07:12.127+00:00",
                                                    "message": "El documento ya pertenece a otro cliente.",
                                                    "uri": "/api/v1_1/cliente/mi-perfil"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Email duplicado",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                    "fecha": "2025-11-18T18:07:12.127+00:00",
                                                    "message": "El email ya pertenece a otro cliente",
                                                    "uri": "/api/v1_1/cliente/mi-perfil"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeCliente> crearCliente(@RequestBody Cliente cliente);


    @PutMapping("cliente")
    @Operation(
            summary = "[ADMIN] Actualizar cliente",
            description = """
                    Actualiza un cliente existente.
                    Validaciones:
                    - El cliente debe existir.
                    - Email y documento no deben duplicarse.
                    - Admin puede modificar el email libremente.
                    """,
            requestBody = @RequestBody(
                    description = "Datos del cliente a actualizar",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Cliente.class),
                            examples = @ExampleObject(
                                    name = "ClienteUpdateExample",
                                    value = """
                                        {
                                          "id": 15,
                                          "nombreCompleto": "Carlos López",
                                          "email": "carlos.lopez@example.com",
                                          "documento": "55512345",
                                          "domicilios": [
                                            {
                                              "calle": "Güemes",
                                              "numero": "742",
                                              "ciudad": "Perico"
                                            }
                                          ]
                                        }
                                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente actualizado exitosamente",
                            content = @Content(
                                    schema = @Schema(implementation = MensajeRespondeCliente.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    "mensaje":"Cliente actualizado existosamente",
                                                    "cliente":{
                                                              "id": 15,
                                                              "nombreCompleto": "Carlos López",
                                                              "email": "carlos.lopez@example.com",
                                                              "documento": "55512345",
                                                              "domicilios": [
                                                                {
                                                                  "calle": "Güemes",
                                                                  "numero": "742",
                                                                  "ciudad": "Perico"
                                                                }
                                                              ]
                                                            }
                                                    """
                                    )
                            )
                    ),

                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente no encontrado",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                    "fecha": "2025-11-18T18:07:12.127+00:00",
                                                    "message": "Cliente no encotrado.",
                                                    "uri": "/api/v1_1/cliente"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Documento duplicado",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                    "fecha": "2025-11-18T18:07:12.127+00:00",
                                                    "message": "El documento ya pertenece a otro cliente.",
                                                    "uri": "/api/v1_1/cliente"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Email duplicado",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                    "fecha": "2025-11-18T18:07:12.127+00:00",
                                                    "message": "El email ya pertenece a otro cliente",
                                                    "uri": "/api/v1_1/cliente"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeCliente> actualizarCliente(@RequestBody Cliente cliente);


    @DeleteMapping("cliente/{id}")
    @Operation(
            summary = "[ADMIN] Eliminar cliente",
            description = "Elimina un cliente por ID si existe.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Cliente eliminado exitosamente",
                            content = @Content(
                                    examples = @ExampleObject(
                                            value = """
                                            {
                                              "mensaje": "Cliente eliminado exitosamente."
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente no encontrado",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                    "fecha": "2025-11-18T18:07:12.127+00:00",
                                                    "message": "Cliente no encontrado.",
                                                    "uri": "/api/v1_1/cliente/12"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeCliente> eliminarCliente(
            @Parameter(description = "ID del cliente a eliminar", required = true)
            @PathVariable Long id
    );


    @GetMapping("clientes")
    @Operation(
            summary = "[ADMIN] Listar todos los clientes",
            description = "Devuelve la lista completa de clientes registrados.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Clientes obtenidos exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                            {
                                              "mensaje": "Clientes obtenidos exitosamente.",
                                              "clienteLista": [
                                                { "id": 1, "nombreCompleto": "Juan Pérez" },
                                                { "id": 2, "nombreCompleto": "María Gómez" }
                                              ]
                                            }
                                            """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeCliente> obtenerClientes();


    @GetMapping("clientes/{id}")
    @Operation(
            summary = "[ADMIN] Obtener cliente por ID",
            description = "Devuelve un cliente existente.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cliente encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                            {
                                              "mensaje": "Cliente encontrado exitosamente.",
                                              "cliente": {
                                                "id": 15,
                                                "nombreCompleto": "Carlos López",
                                                "email": "carlos.lopez@example.com",
                                                "documento": "55512345"
                                              }
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente no encontrado",
                            content = @Content(
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                    "fecha": "2025-11-18T18:07:12.127+00:00",
                                                    "message": "Cliente no encontrado.",
                                                    "uri": "/api/v1_1/clientes/12",
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeCliente> obtenerClientePorId(@PathVariable Long id);


    @GetMapping("clientes/read/properties")
    @Operation(
            summary = "Leer propiedades del microservicio",
            description = "Devuelve la configuración del servicio y sus propiedades.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Propiedades obtenidas correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                            {
                                              "message": "Cliente MS",
                                              "version": "1.1.0",
                                              "mailDetails": {
                                                "host": "smtp.gmail.com",
                                                "port": 587
                                              }
                                            }
                                            """
                                    )
                            )
                    )
            }
    )
    String obtenerPropiedades() throws JsonProcessingException;
}
