package ar.edu.unju.fi.trabajo_final.microservicio_vuelo.service.impl;

import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.entity.Destino;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.ElementoExistenteExcepction;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.exception.ElementoNoEncontradoException;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.repository.DestinoRepository;
import ar.edu.unju.fi.trabajo_final.microservicio_vuelo.service.DestinoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DestinoServiceImpl implements DestinoService {
    private final  DestinoRepository destinoRepository;
    public DestinoServiceImpl(DestinoRepository destinoRepository) {this.destinoRepository = destinoRepository;}

    @Override
    @Transactional
    public Destino crear(Destino destino) throws  ElementoExistenteExcepction {
        destinoRepository.findByCodigo(destino.getCodigo()).ifPresent( x -> {throw  new ElementoExistenteExcepction("Ya existe un Destino con el codigo "+destino.getCodigo());});
        destino.setPais(destino.getPais());
        destino.setNombre(destino.getNombre());
        return destinoRepository.save(destino);
    }
    @Override
    public List<Destino> listarTodos() {
        return destinoRepository.findAll();
    }
    @Override
    public Destino buscarPorID(long id) throws  ElementoNoEncontradoException {
        Destino destino;
        destino = destinoRepository.findById(id).orElseThrow(() ->  new ElementoNoEncontradoException("No existe un Destino con ID: " + id));
        return destino;
    }
    @Override
    public Destino buscarPorCodigo(String codigo) throws  ElementoNoEncontradoException {
        Destino destino;
        destino = destinoRepository.findByCodigo(codigo).orElseThrow(() ->  new ElementoNoEncontradoException("No existe un Destino con el codigo: " + codigo) );
        return destino;
    }
    @Override
    public List<Destino> buscarPorNombre(String nombre) throws ElementoNoEncontradoException{
        List<Destino> listaDestino;
        listaDestino = destinoRepository.findByNombre(nombre.toLowerCase()).orElseThrow(() ->  new ElementoNoEncontradoException("No existe un Destino con el Nombre: " + nombre));
        return listaDestino;

    }
    @Override
    public List<Destino> buscarPorPais(String pais) throws ElementoNoEncontradoException{
        List<Destino> listaDestino;
        listaDestino = destinoRepository.findByPais(pais.toLowerCase()).orElseThrow(() ->  new ElementoNoEncontradoException("No existe un Destino con el Pais: " + pais) );
        return listaDestino;
    }
    @Override
    @Transactional
    public Destino actualizar(Destino destino) throws ElementoNoEncontradoException, ElementoExistenteExcepction {
        Destino destinoId =destinoRepository.findById(destino.getId()).orElseThrow(() -> new ElementoNoEncontradoException("No existe un Destino con el ID: " + destino.getId()));
        destinoRepository.findByCodigo(destino.getCodigo()).ifPresent( unDestino -> {
            if (!(unDestino.getId().equals(destino.getId()))) {
                throw new ElementoExistenteExcepction("Ya existe un Destino con el codigo "+destino.getCodigo());
            }
        });
        destinoId.setCodigo(destino.getCodigo());
        destinoId.setPais(destino.getPais().toLowerCase());
        destinoId.setNombre(destino.getNombre().toLowerCase());
        return destinoRepository.save(destinoId);
    }
    @Override
    @Transactional
    public void eliminar(Long id) throws ElementoNoEncontradoException {
        destinoRepository.findById(id).ifPresentOrElse(
                destinoRepository::delete,
                () -> {throw new ElementoNoEncontradoException("No existe el Destino con el ID: "+id);}
        );

    }


}
