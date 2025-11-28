package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.controller;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Vuelo;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.payload.ApiResponde;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.payload.MensajeRespondeVuelo;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@io.swagger.v3.oas.annotations.tags.Tag(name = "Vuelo", description = "Operaciones CRUD y consultas sobre vuelos")
public interface ApiVuelo {

    // ------------------------------------------------------------------------------------
    // CREAR VUELO
    // ------------------------------------------------------------------------------------
    @PostMapping("vuelo")
    @Operation(
            summary = "Crear un nuevo vuelo",
            description = """
                    Registra un nuevo Vuelo.
                    Validaciones realizadas por el servicio:
                    - `codigo` debe ser único.
                    - `origen` y `destino` deben existir (se valida por id).
                    - `fechaSalida` debe ser fecha/hora presente o futura.
                    - `fechaLlegada` debe ser futura y posterior a fechaSalida.
                    - `cupoReservado` no puede ser mayor que `cupoTotal`.
                    """,
            requestBody = @RequestBody(
                    description = "Objeto Vuelo a crear. `origen` y `destino` se pasan como objetos conteniendo al menos su `id`.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Vuelo.class),
                            examples = @ExampleObject(
                                    name = "CrearVueloExample",
                                    value = """
                                            {
                                              "codigo": "VUL04",
                                              "origen": { "id": 2 },
                                              "destino": { "id": 1 },
                                              "fechaSalida": "2025-12-01T10:15:00",
                                              "fechaLlegada": "2025-12-01T12:30:00",
                                              "cupoTotal": 50,
                                              "cupoReservado": 0
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Vuelo creado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeVuelo.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "mensaje": "Vuelo creado existosamente.",
                                                      "vuelo": {
                                                        "id": 10,
                                                        "codigo": "VUL04",
                                                        "origen": { "id": 2, "nombre": "salta", "codigo": "ARGSLT001", "pais": "argentina" },
                                                        "destino": { "id": 1, "nombre": "jujuy", "codigo": "ARGJJY001", "pais": "argentina" },
                                                        "fechaSalida": "2025-12-01T10:15:00",
                                                        "fechaLlegada": "2025-12-01T12:30:00",
                                                        "cupoTotal": 50,
                                                        "cupoReservado": 0
                                                      },
                                                      "vueloLista": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validación fallida (fechas, cupos, campos obligatorios)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "fecha": "2025-11-19T04:11:24.714+00:00",
                                                      "message": "El total de Cupos Reservado no puede ser mayor al Cupo Total",
                                                      "uri": "/api/v1_1/vuelo"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Origen o destino no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "fecha": "2025-11-19T04:08:56.195+00:00",
                                                      "message": "Destino no registrado ID: 1",
                                                      "uri": "/api/v1_1/vuelo"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Código de vuelo duplicado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "fecha": "2025-11-19T04:06:50.583+00:00",
                                                      "message": "Ya existe un Vuelo con código: VUL04",
                                                      "uri": "/api/v1_1/vuelo"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<?> crearVuelo(@RequestBody Vuelo vuelo);

    // ------------------------------------------------------------------------------------
    // ACTUALIZAR VUELO
    // ------------------------------------------------------------------------------------
    @PutMapping("vuelo")
    @Operation(
            summary = "Actualizar un vuelo existente",
            description = """
                    Actualiza un Vuelo ya registrado.
                    Reglas:
                    - Debe existir el Vuelo con el `id` indicado.
                    - `codigo` no puede pertenecer a otro vuelo.
                    - Se validan fechas y cupos igual que en creación.
                    """,
            requestBody = @RequestBody(
                    description = "Vuelo completo con los campos a actualizar (incluyendo id).",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Vuelo.class),
                            examples = @ExampleObject(
                                    name = "ActualizarVueloExample",
                                    value = """
                                            {
                                              "id": 10,
                                              "codigo": "VUL04",
                                              "origen": { "id": 2 },
                                              "destino": { "id": 1 },
                                              "fechaSalida": "2025-12-10T09:30:00",
                                              "fechaLlegada": "2025-12-10T12:00:00",
                                              "cupoTotal": 60,
                                              "cupoReservado": 30
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vuelo actualizado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeVuelo.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "mensaje": "Vuelo actualizado existosamente.",
                                                      "vuelo": {
                                                        "id": 10,
                                                        "codigo": "VUL04",
                                                        "origen": { "id": 2 },
                                                        "destino": { "id": 1 },
                                                        "fechaSalida": "2025-12-10T09:30:00",
                                                        "fechaLlegada": "2025-12-10T12:00:00",
                                                        "cupoTotal": 60,
                                                        "cupoReservado": 30
                                                      },
                                                      "vueloLista": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Vuelo no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validación fallida",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Código de vuelo ya usado por otro vuelo",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class)
                            )
                    )
            }
    )
    ResponseEntity<?> actualizarVuelo(@RequestBody Vuelo vuelo);

    // ------------------------------------------------------------------------------------
    // ELIMINAR VUELO
    // ------------------------------------------------------------------------------------
    @DeleteMapping("vuelo/{id}")
    @Operation(
            summary = "Eliminar un vuelo por ID",
            description = "Elimina el Vuelo con el identificador provisto.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Vuelo eliminado. Respuesta sin contenido (204)."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Vuelo no existente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "fecha": "2025-11-19T04:24:31.307+00:00",
                                                      "message": "No existe Vuelo con ID : 4",
                                                      "uri": "/api/v1_1/vuelo/4"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<?> eliminarVuelo(
            @Parameter(description = "ID del vuelo a eliminar", required = true) @PathVariable Long id);

    // ------------------------------------------------------------------------------------
    // OBTENER TODOS LOS VUELOS
    // ------------------------------------------------------------------------------------
    @GetMapping("vuelos")
    @Operation(
            summary = "Obtener todos los vuelos",
            description = "Devuelve la lista completa de vuelos registrados.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vuelos obtenidos correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeVuelo.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "mensaje": "Vuelos obtenidos exitosamente.",
                                                      "vuelo": null,
                                                      "vueloLista": [
                                                        {
                                                          "id": 10,
                                                          "codigo": "VUL04",
                                                          "origen": { "id": 2 },
                                                          "destino": { "id": 1 },
                                                          "fechaSalida": "2025-12-01T10:15:00",
                                                          "fechaLlegada": "2025-12-01T12:30:00",
                                                          "cupoTotal": 50,
                                                          "cupoReservado": 0
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeVuelo> obtenerVuelos();

    // ------------------------------------------------------------------------------------
    // OBTENER VUELO POR ID
    // ------------------------------------------------------------------------------------
    @GetMapping("vuelos/{id}")
    @Operation(
            summary = "Obtener un vuelo por ID",
            description = "Devuelve el Vuelo correspondiente al identificador proporcionado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vuelo encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeVuelo.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "mensaje": "Vuelo encontrado exitosamente.",
                                                      "vuelo": {
                                                        "id": 10,
                                                        "codigo": "VUL04",
                                                        "origen": { "id": 2 },
                                                        "destino": { "id": 1 },
                                                        "fechaSalida": "2025-12-01T10:15:00",
                                                        "fechaLlegada": "2025-12-01T12:30:00",
                                                        "cupoTotal": 50,
                                                        "cupoReservado": 0
                                                      },
                                                      "vueloLista": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Vuelo no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class)
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeVuelo> obtenerID(
            @Parameter(description = "ID del vuelo", required = true) @PathVariable Long id);

    // ------------------------------------------------------------------------------------
    // BUSCAR VUELOS POR DESTINO
    // ------------------------------------------------------------------------------------
    @GetMapping("vuelos/destino/{id}")
    @Operation(
            summary = "Buscar vuelos por destino (ID)",
            description = "Devuelve los vuelos que tienen como destino el ID especificado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vuelos por destino encontrados",
                            content = @Content(schema = @Schema(implementation = MensajeRespondeVuelo.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No se encontraron vuelos para el destino solicitado",
                            content = @Content(schema = @Schema(implementation = ApiResponde.class))
                    )
            }
    )
    ResponseEntity<MensajeRespondeVuelo> obtenerDestino(
            @Parameter(description = "ID del destino", required = true) @PathVariable Long id);

    // ------------------------------------------------------------------------------------
    // BUSCAR VUELOS POR ORIGEN
    // ------------------------------------------------------------------------------------
    @GetMapping("vuelos/origen/{id}")
    @Operation(
            summary = "Buscar vuelos por origen (ID)",
            description = "Devuelve los vuelos que tienen como origen el ID especificado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vuelos por origen encontrados",
                            content = @Content(schema = @Schema(implementation = MensajeRespondeVuelo.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No se encontraron vuelos para el origen solicitado",
                            content = @Content(schema = @Schema(implementation = ApiResponde.class))
                    )
            }
    )
    ResponseEntity<MensajeRespondeVuelo> obtenerOrigen(
            @Parameter(description = "ID del origen", required = true) @PathVariable Long id);

    // ------------------------------------------------------------------------------------
    // BUSCAR VUELOS POR FECHA EXACTA DE SALIDA (DATE-TIME)
    // ------------------------------------------------------------------------------------
    @GetMapping("vuelos/fechaSalida")
    @Operation(
            summary = "Buscar vuelos por fecha exacta de salida (date-time)",
            description = "Busca vuelos cuya `fechaSalida` coincida exactamente con la fecha/hora proporcionada (ISO date-time)."
    )
    ResponseEntity<MensajeRespondeVuelo> obtenerVueloFechaSalida(
            @Parameter(description = "Fecha exacta de salida (YYYY-MM-DDTHH:mm:ss)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaSalida);

    // ------------------------------------------------------------------------------------
    // BUSCAR VUELOS POR RANGO DE FECHAS DE SALIDA
    // ------------------------------------------------------------------------------------
    @GetMapping("vuelos/fechaSalidaRango")
    @Operation(
            summary = "Buscar vuelos por rango de fecha/hora de salida",
            description = "Busca vuelos cuya `fechaSalida` esté entre `fechaSalidaMin` y `fechaSalidaMax` (ambos inclusive)."
    )
    ResponseEntity<MensajeRespondeVuelo> obtenerVueloFechaSalida(
            @Parameter(description = "Fecha mínima (YYYY-MM-DDTHH:mm:ss)", required = true)
            @RequestParam("fechaSalidaMin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaSalidaMin,

            @Parameter(description = "Fecha máxima (YYYY-MM-DDTHH:mm:ss)", required = true)
            @RequestParam("fechaSalidaMax") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaSalidaMax);

    // ------------------------------------------------------------------------------------
    // LEER CONFIGURACIÓN DEL MICROSERVICIO
    // ------------------------------------------------------------------------------------
    @GetMapping("vuelos/read/properties")
    @Operation(
            summary = "Leer configuración del microservicio",
            description = "Devuelve la configuración del microservicio de vuelos en formato JSON."
    )
    String obtenerPropierties() throws JsonProcessingException;

    // ------------------------------------------------------------------------------------
    // CONFIRMAR RESERVA (INCREMENTAR CUPOS RESERVADOS)
    // ------------------------------------------------------------------------------------
    @PutMapping("vuelo/confirmar/{id}")
    @Operation(
            summary = "Confirmar una reserva en el vuelo (incrementa cupoReservado)",
            description = "Aumenta en 1 el campo `cupoReservado` siempre que no exceda `cupoTotal`."
    )
    ResponseEntity<MensajeRespondeVuelo> confirmarReservaVuelo(
            @Parameter(description = "ID del vuelo", required = true) @PathVariable Long id);

    // ------------------------------------------------------------------------------------
    // CANCELAR RESERVA (DECREMENTAR CUPOS RESERVADOS)
    // ------------------------------------------------------------------------------------
    @PutMapping("vuelo/cancelar/{id}")
    @Operation(
            summary = "Cancelar una reserva (decrementa cupoReservado)",
            description = "Reduce en 1 el campo `cupoReservado` si es mayor que 0."
    )
    ResponseEntity<MensajeRespondeVuelo> cancelarReservaVuelo(
            @Parameter(description = "ID del vuelo", required = true) @PathVariable Long id);

    // ------------------------------------------------------------------------------------
    // BUSCAR VUELOS POR ORIGEN, DESTINO Y FECHA (CÓDIGOS Y FECHA)
    // ------------------------------------------------------------------------------------
    @GetMapping("/vuelos/buscar")
    @Operation(
            summary = "Buscar vuelos por códigos de origen/destino y fecha",
            description = """
                    Busca vuelos disponibles para:
                    - origen: código del destino de origen (ej: ARGSLT001)
                    - destino: código del destino de llegada
                    - fecha: fecha (YYYY-MM-DD). Se considera todo el día (00:00 - 23:59:59).
                    """
    )
    ResponseEntity<MensajeRespondeVuelo> buscarVuelos(
            @Parameter(description = "Código del origen", required = true) @RequestParam String origen,
            @Parameter(description = "Código del destino", required = true) @RequestParam String destino,
            @Parameter(description = "Fecha del vuelo (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    );

}
