package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.controller;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Destino;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.payload.ApiResponde;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.payload.MensajeRespondeDestino;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Destino", description = "Operaciones CRUD y consultas sobre destinos (entidad Destino)")
public interface ApiDestino {

    // ------------------------------------------------------------------------------------
    // 1. CREAR DESTINO
    // ------------------------------------------------------------------------------------
    @PostMapping("destino")
    @Operation(
            summary = "Registrar un nuevo destino",
            description = """
                    Crea un nuevo Destino en la base de datos.
                    Reglas:
                    - `codigo` debe ser único.
                    - `nombre`, `codigo` y `pais` son obligatorios.
                    - Se normalizan nombre y país a minúsculas en el servicio.
                    """,
            requestBody = @RequestBody(
                    description = "Objeto Destino a crear. No incluir id (se genera automáticamente).",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Destino.class),
                            examples = @ExampleObject(
                                    name = "CrearDestinoExample",
                                    value = """
                                            {
                                              "nombre": "Salta",
                                              "codigo": "ARGSLT001",
                                              "pais": "Argentina"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Destino creado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeDestino.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "mensaje": "Se registro existosamente el destino.",
                                                      "destino": {
                                                        "id": 7,
                                                        "nombre": "salta",
                                                        "codigo": "ARGSLT001",
                                                        "pais": "argentina"
                                                      },
                                                      "destinoLista": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "El código del destino ya existe",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "fecha": "2025-11-19T04:37:47.518+00:00",
                                                      "message": "Ya existe un Destino con el codigo ARGSLT001",
                                                      "uri": "/api/v1_1/destino"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos (por ejemplo, campos vacíos)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class)
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeDestino> crearDestino(@RequestBody Destino destino);

    // ------------------------------------------------------------------------------------
    // 2. ACTUALIZAR DESTINO
    // ------------------------------------------------------------------------------------
    @PutMapping("destino")
    @Operation(
            summary = "Actualizar un destino existente",
            description = """
                    Actualiza los datos de un Destino existente.
                    Reglas:
                    - Debe existir un Destino con el `id` proporcionado.
                    - `codigo` no puede quedar usado por otro destino.
                    - Se normalizan `nombre` y `pais` a minúsculas.
                    """,
            requestBody = @RequestBody(
                    description = "Objeto Destino con los campos a actualizar (incluyendo id).",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Destino.class),
                            examples = @ExampleObject(
                                    name = "ActualizarDestinoExample",
                                    value = """
                                            {
                                              "id": 7,
                                              "nombre": "Salta - Terminal",
                                              "codigo": "ARGSLT001",
                                              "pais": "Argentina"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Destino actualizado correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeDestino.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "mensaje": "Destino actualizado existosamente.",
                                                      "destino": {
                                                        "id": 7,
                                                        "nombre": "salta - terminal",
                                                        "codigo": "ARGSLT001",
                                                        "pais": "argentina"
                                                      },
                                                      "destinoLista": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Destino no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "fecha": "2025-11-19T05:00:00.000+00:00",
                                                      "message": "No existe un Destino con ID: 999",
                                                      "uri": "/api/v1_1/destino"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Código ya utilizado por otro destino",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos inválidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class)
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeDestino> actualizarDestino(@RequestBody Destino destino);

    // ------------------------------------------------------------------------------------
    // 3. ELIMINAR DESTINO
    // ------------------------------------------------------------------------------------
    @DeleteMapping("destino/{id}")
    @Operation(
            summary = "Eliminar un destino por ID",
            description = "Elimina el Destino identificado por `id`. Si no existe, devuelve 404.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Destino eliminado correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeDestino.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "mensaje": "Destino eliminado existosamente.",
                                                      "destino": null,
                                                      "destinoLista": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Destino no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "fecha": "2025-11-19T05:10:00.000+00:00",
                                                      "message": "No existe el Destino con el ID: 999",
                                                      "uri": "/api/v1_1/destino/999"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeDestino> eliminarDestino(
            @Parameter(description = "ID del destino a eliminar", required = true) @PathVariable("id") Long id
    );

    // ------------------------------------------------------------------------------------
    // 4. LISTAR TODOS LOS DESTINOS
    // ------------------------------------------------------------------------------------
    @GetMapping("destinos")
    @Operation(
            summary = "Obtener todos los destinos",
            description = "Devuelve la lista completa de destinos registrados en el sistema.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Destinos obtenidos correctamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeDestino.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "mensaje": "Destinos obtenidos exitosamente.",
                                                      "destino": null,
                                                      "destinoLista": [
                                                        {
                                                          "id": 1,
                                                          "nombre": "salta",
                                                          "codigo": "ARGSLT001",
                                                          "pais": "argentina"
                                                        },
                                                        {
                                                          "id": 2,
                                                          "nombre": "jujuy",
                                                          "codigo": "ARGJJY001",
                                                          "pais": "argentina"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeDestino> obtenerDestinos();

    // ------------------------------------------------------------------------------------
    // 5. BUSCAR DESTINO POR ID
    // ------------------------------------------------------------------------------------
    @GetMapping("destinos/{id}")
    @Operation(
            summary = "Obtener un destino por ID",
            description = "Devuelve el Destino correspondiente al ID especificado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Destino encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeDestino.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "mensaje": "Destino encontrado exitosamente.",
                                                      "destino": {
                                                        "id": 1,
                                                        "nombre": "salta",
                                                        "codigo": "ARGSLT001",
                                                        "pais": "argentina"
                                                      },
                                                      "destinoLista": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Destino no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class)
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeDestino> obtenerID(
            @Parameter(description = "ID del destino", required = true) @PathVariable("id") Long id
    );

    // ------------------------------------------------------------------------------------
    // 6. BUSCAR DESTINO POR CÓDIGO
    // ------------------------------------------------------------------------------------
    @GetMapping("destinos/codigo")
    @Operation(
            summary = "Buscar destino por código",
            description = "Busca y devuelve el Destino cuyo `codigo` coincida exactamente con el parámetro.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Destino encontrado por código",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeDestino.class),
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                      "mensaje": "Destino encontrado.",
                                                      "destino": {
                                                        "id": 1,
                                                        "nombre": "salta",
                                                        "codigo": "ARGSLT001",
                                                        "pais": "argentina"
                                                      },
                                                      "destinoLista": null
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Destino no encontrado por código",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class)
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeDestino> obtenerCodigo(
            @Parameter(description = "Código único del destino", required = true) @RequestParam String codigo
    );

    // ------------------------------------------------------------------------------------
    // 7. BUSCAR DESTINOS POR PAÍS
    // ------------------------------------------------------------------------------------
    @GetMapping("destinos/pais")
    @Operation(
            summary = "Buscar destinos por país",
            description = "Devuelve la lista de destinos cuyo campo `pais` coincida con el parámetro.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Destinos por país encontrados",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeDestino.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No se encontraron destinos para el país solicitado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class)
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeDestino> obtenerPais(
            @Parameter(description = "Nombre del país", required = true) @RequestParam String pais
    );

    // ------------------------------------------------------------------------------------
    // 8. BUSCAR DESTINOS POR NOMBRE
    // ------------------------------------------------------------------------------------
    @GetMapping("destinos/nombre")
    @Operation(
            summary = "Buscar destinos por nombre",
            description = "Devuelve los destinos que coinciden con el nombre proporcionado.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Destinos por nombre encontrados",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MensajeRespondeDestino.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No se encontraron destinos para el nombre solicitado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiResponde.class)
                            )
                    )
            }
    )
    ResponseEntity<MensajeRespondeDestino> obtenerNombre(
            @Parameter(description = "Nombre del destino", required = true) @RequestParam String nombre
    );
}
