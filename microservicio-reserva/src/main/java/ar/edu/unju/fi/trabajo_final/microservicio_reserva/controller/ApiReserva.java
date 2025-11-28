package ar.edu.unju.fi.trabajo_final.microservicio_reserva.controller;

import ar.edu.unju.fi.trabajo_final.microservicio_reserva.entity.Reserva;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload.ApiResponde;
import ar.edu.unju.fi.trabajo_final.microservicio_reserva.payload.MensajeRespondeReserva;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

public interface ApiReserva {

    // ============================================================
    // ENDPOINTS PARA ROLE_USER (CLIENTE AUTENTICADO)
    // ============================================================

    @GetMapping("reservas/mis-reservas")
    @Operation(
            summary = "Mostrar mis reservas",
            description = "Devuelve todas las reservas que pertenecen al usuario autenticado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reservas encontradas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeReserva.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "mensaje": "Reservas del usuario encontradas",
                                                "reserva": null,
                                                "reservaLista": [
                                                    {
                                                        "id": 4,
                                                        "codigo": "RSV0023",
                                                        "estado": "GENERADA",
                                                        "cliente": "Parraga Jairo Marcelo",
                                                        "origen": "(ARGBAC001)- argentina: buenos aires",
                                                        "destino": "(ESPBAR001)- espana: barcelona",
                                                        "observaciones": "Asiento pasillo",
                                                        "codigoVuelo": "VUL02"
                                                    },
                                                    {
                                                        "id": 1,
                                                        "codigo": "RSV002",
                                                        "estado": "CANCELADA",
                                                        "cliente": "Parraga Jairo Marcelo",
                                                        "origen": "(ARGBAC001)- argentina: buenos aires",
                                                        "destino": "(ESPBAR001)- espana: barcelona",
                                                        "observaciones": "Reserva creada",
                                                        "codigoVuelo": "VUL02"
                                                    }
                                                ]
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "No autorizado",
                            content = @Content(
                                    examples =  @ExampleObject(
                                            value = """
                                                    
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeReserva> obtenerMisReservas(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt
    );


    @PostMapping("reservas/mia")
    @Operation(
            summary = "Crear una reserva para el usuario autenticado",
            description = "Crea una reserva asignándola automáticamente al cliente del token JWT.",
            requestBody = @RequestBody(
                    description = "Datos necesarios para crear la reserva",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservaRequestDTO.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "codigo": "RSV0023",
                                      "vueloId": 2,
                                      "observaciones": "Asiento pasillo"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Reserva creada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeReserva.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "mensaje": "Reserva (Admin) creada exitosamente",
                                                "reserva": {
                                                    "id": 4,
                                                    "codigo": "RSV0023",
                                                    "estado": "GENERADA",
                                                    "cliente": "Parraga Jairo Marcelo",
                                                    "origen": "(ARGBAC001)- argentina: buenos aires",
                                                    "destino": "(ESPBAR001)- espana: barcelona",
                                                    "observaciones": "Asiento pasillo",
                                                    "codigoVuelo": "VUL02"
                                                },
                                                "reservaLista": null
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Codigo de vuelo ya esta registrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "fecha": "2025-11-19T21:49:09.055+00:00",
                                                        "message": "El código de reserva ya existe",
                                                        "uri": "/api/v1_1/reserva"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Vuelo no registrado en el microservicio vuelo.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                                {
                                                    "fecha": "2025-11-19T21:52:30.077+00:00",
                                                    "message": "No existe un vuelo con ID: 2",
                                                    "uri": "/api/v1_1/vuelos/2"
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuario no autenticado o token inválido",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            
                                            """)
                            )
                    )           }
    )
    ResponseEntity<MensajeRespondeReserva> crearMiReserva(
            @RequestBody ReservaRequestDTO request,
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt
    );


    // ============================================================
    // ENDPOINTS ADMIN
    // ============================================================

    @PostMapping("reserva")
    @Operation(
            summary = "Crear reserva (Admin)",
            description = "Permite crear una reserva manualmente como administrador.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Entidad completa de reserva a crear",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Reserva.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "codigo": "RSV0023",
                                      "clienteId": 1,
                                      "vueloId": 2,
                                      "observaciones": "Asiento pasillo"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Reserva creada exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeReserva.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "mensaje": "Reserva (Admin) creada exitosamente",
                                                "reserva": {
                                                    "id": 4,
                                                    "codigo": "RSV0023",
                                                    "estado": "GENERADA",
                                                    "cliente": "Parraga Jairo Marcelo",
                                                    "origen": "(ARGBAC001)- argentina: buenos aires",
                                                    "destino": "(ESPBAR001)- espana: barcelona",
                                                    "observaciones": "Asiento pasillo",
                                                    "codigoVuelo": "VUL02"
                                                },
                                                "reservaLista": null
                                            }
                                    """)

                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Codigo de vuelo ya esta registrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "fecha": "2025-11-19T21:49:09.055+00:00",
                                                        "message": "El código de reserva ya existe",
                                                        "uri": "/api/v1_1/reserva"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Vuelo no registrado en el microservicio vuelo.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                                {
                                                    "fecha": "2025-11-19T21:52:30.077+00:00",
                                                    "message": "No existe un vuelo con ID: 2",
                                                    "uri": "/api/v1_1/vuelos/2"
                                                }
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Cliente no registrado en el microservicio Cliente.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                                    {
                                                        "fecha": "2025-11-19T21:57:18.914+00:00",
                                                        "message": "Cliente con ID 1 no encontrado.",
                                                        "uri": "/api/v1_1/clientes/1"
                                                    }                                                
                                            """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuario no autenticado o token inválido",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            
                                            """)
                            )
                    )

            }
    )
    ResponseEntity<MensajeRespondeReserva> crear(@RequestBody Reserva reserva);


    @GetMapping("reservas")
    @Operation(
            summary = "Listar todas las reservas",
            description = "Devuelve todas las reservas registradas en el sistema.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Listado de reservas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeReserva.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "mensaje": "Reservas encontradas",
                                                        "reserva": null,
                                                        "reservaLista": [
                                                            {
                                                                "id": 3,
                                                                "codigo": "RES-WEB-778352",
                                                                "estado": "GENERADA",
                                                                "cliente": "Parraga Jairo",
                                                                "origen": "(ARGBAC001)- argentina: buenos aires",
                                                                "destino": "(ESPBAR001)- espana: barcelona",
                                                                "observaciones": "",
                                                                "codigoVuelo": "VUL02"
                                                            },
                                                            {
                                                                "id": 2,
                                                                "codigo": "RES-ADM-376004",
                                                                "estado": "GENERADA",
                                                                "cliente": "Esteban Farfan",
                                                                "origen": "(CLSC001)- chile: santiago de chicle",
                                                                "destino": "(PRLM001)- peru: lima",
                                                                "observaciones": "Solicitad un asiento al lado de una ventana",
                                                                "codigoVuelo": "VUL03"
                                                            },
                                                            {
                                                                "id": 1,
                                                                "codigo": "RSV002",
                                                                "estado": "GENERADA",
                                                                "cliente": "Parraga Jairo Marcelo",
                                                                "origen": "(ARGBAC001)- argentina: buenos aires",
                                                                "destino": "(ESPBAR001)- espana: barcelona",
                                                                "observaciones": "Reserva creada",
                                                                "codigoVuelo": "VUL02"
                                                            }
                                                        ]
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuario no autenticado o token inválido",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeReserva> obtenerReservas();


    // ============================================================
    // ENDPOINTS COMUNES
    // ============================================================

    @PutMapping("reserva/confirmar/{id}")
    @Operation(
            summary = "Confirmar una reserva",
            description = "Actualiza el estado de la reserva a `CONFIRMADA`.",

            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reserva confirmada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeReserva.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "mensaje": "Reserva Confirmada",
                                                        "reserva": {
                                                            "id": 8,
                                                            "codigo": "RSV002",
                                                            "estado": "CONFIRMADA",
                                                            "cliente": "Parraga Jairo Marcelo",
                                                            "origen": "(ARGBAC001)- argentina: buenos aires",
                                                            "destino": "(ESPBAR001)- espana: barcelona",
                                                            "observaciones": "Reserva creada",
                                                            "codigoVuelo": "VUL02"
                                                        },
                                                        "reservaLista": null
                                                    }
                                                    """
                                    )

                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Reserva no encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponse.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "fecha": "2025-11-18T20:31:35.807+00:00",
                                                        "message": "No existe una reserva con el id: 14",
                                                        "uri": "/api/v1_1/reservas/14"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuario no autenticado o token inválido",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeReserva> confirmarReserva(
            @Parameter(description = "ID de la reserva", example = "1") @PathVariable Long id
    );


    @PutMapping("reserva/cancelar/{id}")
    @Operation(
            summary = "Cancelar una reserva",
            description = "Actualiza el estado de una reserva a `CANCELADA`.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reserva cancelada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeReserva.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "mensaje": "Reserva Cancelada",
                                                        "reserva": {
                                                            "id": 1,
                                                            "codigo": "RSV002",
                                                            "estado": "CANCELADA",
                                                            "cliente": "Parraga Jairo Marcelo",
                                                            "origen": "(ARGBAC001)- argentina: buenos aires",
                                                            "destino": "(ESPBAR001)- espana: barcelona",
                                                            "observaciones": "Reserva creada",
                                                            "codigoVuelo": "VUL02"
                                                        },
                                                        "reservaLista": null
                                                    }
                                                    """
                                    )

                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Reserva no encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "fecha": "2025-11-18T20:31:35.807+00:00",
                                                        "message": "No existe una reserva con el id: 14",
                                                        "uri": "/api/v1_1/reservas/14"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuario no autenticado o token inválido",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeReserva> cancelarReserva(
            @Parameter(description = "ID de la reserva", example = "8") @PathVariable Long id
    );


    @GetMapping("reservas/{id}")
    @Operation(
            summary = "Obtener reserva por ID",
            description = "Devuelve una reserva por su identificador único.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reserva encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeReserva.class),
                                    examples = @ExampleObject(
                                            value = """
                                                     {
                                                         "mensaje": "Reserva encontrada",
                                                         "reserva": {
                                                             "id": 1,
                                                             "codigo": "RSV002",
                                                             "estado": "GENERADA",
                                                             "cliente": "Parraga Jairo Marcelo",
                                                             "origen": "(ARGBAC001)- argentina: buenos aires",
                                                             "destino": "(ESPBAR001)- espana: barcelona",
                                                             "observaciones": "Reserva creada",
                                                             "codigoVuelo": "VUL02"
                                                         },
                                                         "reservaLista": null
                                                     }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Reserva no encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "fecha": "2025-11-18T20:31:35.807+00:00",
                                                        "message": "No existe una reserva con el id: 14",
                                                        "uri": "/api/v1_1/reservas/14"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Usuario no autenticado o token inválido",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            
                                            """)
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeReserva> obtenerReservasID(
            @Parameter(description = "ID de la reserva", example = "1") @PathVariable Long id
    );


    // ============================================================
    // PROPERTIES DEL MICROSERVICIO
    // ============================================================

    @GetMapping("reservas/read/properties")
    @Operation(
            summary = "Leer configuración del microservicio",
            description = "Devuelve las propiedades internas configuradas en este microservicio (versión, mensaje, datos de contacto).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Propiedades obtenidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = """
                                            {
                                              "msg": "Microservicio Reserva activo",
                                              "buildVersion": "1.0.3",
                                              "mailDetails": {
                                                "host": "smtp.gmail.com",
                                                "port": 587,
                                                "from": "reservas@empresa.com"
                                              }
                                            }
                                            """)
                            )
                    )
            }
    )
    String obtenerPropiedades() throws JsonProcessingException;
}
