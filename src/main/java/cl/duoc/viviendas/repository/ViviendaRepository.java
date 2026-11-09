package cl.duoc.viviendas.repository;

import cl.duoc.viviendas.dao.ViviendaDAO;
import cl.duoc.viviendas.model.Vivienda;

import java.util.List;

public class ViviendaRepository {
    private final ViviendaDAO dao;

    public ViviendaRepository(ViviendaDAO dao) {
        this.dao = dao;
    }

    public List<Vivienda> listar() {
        return dao.listar();
    }

    public boolean existeCodigo(String codigo) {
        for (Vivienda v : dao.listar()) {
            if (v.getCodigo().equalsIgnoreCase(codigo)) {
                return true;
            }
        }
        return false;
    }

    public void registrar(Vivienda vivienda) {
        if (existeCodigo(vivienda.getCodigo())) {
            throw new IllegalArgumentException("Ya existe una vivienda con ese código");
        }
        dao.guardar(vivienda);
    }

    public void eliminar(String codigo) {
        dao.eliminar(codigo);
    }
}
