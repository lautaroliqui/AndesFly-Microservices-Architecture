package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.service.impl;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Destino;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Vuelo;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.ElementoExistenteExcepction;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.ElementoNoEncontradoException;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.ValidacionException;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.repository.DestinoRepository;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.repository.VueloRepository;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.service.VueloService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VueloServiceImpl implements VueloService {
    private final VueloRepository vueloRepository;
    private final DestinoRepository destinoRepository;

    public VueloServiceImpl(VueloRepository vueloRepository,  DestinoRepository destinoRepository) {
        this.vueloRepository = vueloRepository;
        this.destinoRepository = destinoRepository;
    }
    @Override
    @Transactional
    public Vuelo crear(Vuelo unVuelo) throws ElementoNoEncontradoException, ValidacionException {
        vueloRepository.findByCodigo(unVuelo.getCodigo()).ifPresent(x -> {throw new ElementoExistenteExcepction("Ya existe un Vuelo con código: " + unVuelo.getCodigo());});
        destinoRepository.findById(unVuelo.getDestino().getId()).orElseThrow(() -> new ElementoNoEncontradoException("Destino no registrado ID: "+unVuelo.getDestino().getId()));
        destinoRepository.findById(unVuelo.getOrigen().getId()).orElseThrow(() ->new ElementoNoEncontradoException("Origen no registrado ID: "+unVuelo.getOrigen().getId()));
        validacionVuelo(unVuelo);
        return vueloRepository.save(unVuelo);
    }
    @Override
    @Transactional
    public Vuelo actualizar(Vuelo unVuelo) throws ElementoNoEncontradoException, ValidacionException,ElementoExistenteExcepction {
        vueloRepository.findById(unVuelo.getId()).orElseThrow(() -> new ElementoNoEncontradoException("Vuelo no existe"));
        vueloRepository.findByCodigo(unVuelo.getCodigo()).ifPresent(x -> {
            if (!x.getId().equals(unVuelo.getId())) {
                throw new ElementoExistenteExcepction("Ya existe un Vuelo con código: " + unVuelo.getCodigo());
            }
        });
        destinoRepository.findById(unVuelo.getDestino().getId()).orElseThrow(() -> new ElementoNoEncontradoException("Destino no registrado ID: "+unVuelo.getDestino().getId()));
        destinoRepository.findById(unVuelo.getOrigen().getId()).orElseThrow(() ->new ElementoNoEncontradoException("Origen no registrado ID: "+unVuelo.getOrigen().getId()));
        validacionVuelo(unVuelo);
        return vueloRepository.save(unVuelo);
    }
    @Override
    @Transactional
    public void eliminar(Long id) throws ElementoNoEncontradoException {
        vueloRepository.findById(id).ifPresentOrElse(vueloRepository::delete,
                ()->{throw new ElementoNoEncontradoException("No existe Vuelo con ID : " + id);});
    }
    @Override
    public List<Vuelo> obtenerTodosVuelos() {return vueloRepository.findAll();}
    @Override
    public Vuelo obtenerPorId(Long id) throws ElementoNoEncontradoException {
        Vuelo vuelo;
        vuelo=vueloRepository.findById(id).orElseThrow(() -> new ElementoNoEncontradoException("No existe un vuelo con ID: " + id));
        return vuelo;
    }
    @Override
    public List<Vuelo> obtenerOrigen (Long id) throws ElementoNoEncontradoException {
        List<Vuelo> listaVuelo = vueloRepository.findByOrigen_Id(id);
        if (listaVuelo.isEmpty()) {
            throw new ElementoNoEncontradoException("No existen vuelos cuyo Origen tenga el ID: " + id);
        }
        return listaVuelo;
    }
    @Override
    public List<Vuelo> obtenerDestino (Long id) throws ElementoNoEncontradoException{
        List<Vuelo> listaVuelo = vueloRepository.findByDestino_Id(id);
        if (listaVuelo.isEmpty()) {
            throw new ElementoNoEncontradoException("No existen vuelos cuyo Destino tenga el ID: " + id);
        }
        return listaVuelo;
    }
    @Override
    public List<Vuelo> buscarPorFechaDeSalida(LocalDateTime fechaExacta){
        return vueloRepository.findByFechaSalida(fechaExacta);
    }
    @Override
    public List<Vuelo> buscarPorFechaDeSalida(LocalDateTime fechaMin, LocalDateTime fechaMax)throws ValidacionException{
        if(fechaMin.isBefore(fechaMax)){
            return vueloRepository.findByFechaSalidaBetween(fechaMin, fechaMax);
        }else{
            throw new ValidacionException("Fecha de salida invalido:\n Rango invalido["+fechaMin+","+fechaMax+"]");
        }
    }

    private static void validacionVuelo(Vuelo unVuelo) throws ValidacionException {
        if (unVuelo.getCupoReservado() > unVuelo.getCupoTotal()) {
            throw new ValidacionException("El total de Cupos Reservado no puede ser mayor al Cupo Total");
        }
        if (unVuelo.getFechaLlegada().isBefore(unVuelo.getFechaSalida())) {
            throw new ValidacionException("La Fecha de Llegada no puede ser anterior a la Fecha de Salida.");
        }
    }

    @Override
    @Transactional
    public Vuelo confirmarReserva(Long idVuelo)
            throws ElementoNoEncontradoException, ValidacionException {

        Vuelo vuelo = vueloRepository.findById(idVuelo)
                .orElseThrow(() -> new ElementoNoEncontradoException("No existe vuelo con id: " + idVuelo));

        if (vuelo.getCupoReservado() + 1 > vuelo.getCupoTotal()) {
            throw new ValidacionException("El vuelo ha alcanzado el cupo total. No se pueden agregar más reservas.");
        }

        vuelo.setCupoReservado(vuelo.getCupoReservado() + 1);
        return vueloRepository.save(vuelo);
    }

    @Override
    @Transactional
    public Vuelo cancelarReserva(Long idVuelo)
            throws ElementoNoEncontradoException {

        Vuelo vuelo = vueloRepository.findById(idVuelo)
                .orElseThrow(() -> new ElementoNoEncontradoException("No existe vuelo con id: " + idVuelo));

        // Solo decrementa si hay cupos reservados
        if (vuelo.getCupoReservado() > 0) {
            vuelo.setCupoReservado(vuelo.getCupoReservado() - 1);
        }

        return vueloRepository.save(vuelo);
    }

    @Override
    @Transactional(readOnly = true) // Es una consulta, no modifica datos
    public List<Vuelo> buscarVuelosPor(String origenCodigo, String destinoCodigo, LocalDate fecha)
            throws ElementoNoEncontradoException {

        // 1. Buscar las entidades de Destino usando los códigos
        Destino origen = destinoRepository.findByCodigo(origenCodigo)
                .orElseThrow(() -> new ElementoNoEncontradoException("No se encontró el destino de origen con código: " + origenCodigo));

        Destino destino = destinoRepository.findByCodigo(destinoCodigo)
                .orElseThrow(() -> new ElementoNoEncontradoException("No se encontró el destino de llegada con código: " + destinoCodigo));

        // 2. Definir el rango del día completo (de 00:00 a 23:59)
        LocalDateTime inicioDelDia = fecha.atStartOfDay(); // Ej: 2025-12-01T00:00:00
        LocalDateTime finDelDia = fecha.atTime(23, 59, 59); // Ej: 2025-12-01T23:59:59

        // 3. Llamar al nuevo método del repositorio
        return vueloRepository.findByOrigenAndDestinoAndFechaSalidaBetween(origen, destino, inicioDelDia, finDelDia);
    }
}
