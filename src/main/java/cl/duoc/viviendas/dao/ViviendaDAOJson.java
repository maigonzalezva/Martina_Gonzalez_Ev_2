package cl.duoc.viviendas.dao;

import cl.duoc.viviendas.model.Casa;
import cl.duoc.viviendas.model.Departamento;
import cl.duoc.viviendas.model.Vivienda;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

    public class ViviendaDAOJson implements ViviendaDAO {
        private final Path archivoDeptos = Path.of("departamentos.json");
        private final Path archivoCasas = Path.of("casas.json");
        private final ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        private final List<Vivienda> datos = new ArrayList<>();

        public ViviendaDAOJson() {
            cargar();
        }

        private void cargar() {
            try {
                datos.clear();
                if (Files.exists(archivoDeptos) && Files.size(archivoDeptos) > 0) {
                    datos.addAll(mapper.readValue(archivoDeptos.toFile(),
                            new TypeReference<List<Departamento>>() {}));
                }
                if (Files.exists(archivoCasas) && Files.size(archivoCasas) > 0) {
                    datos.addAll(mapper.readValue(archivoCasas.toFile(),
                            new TypeReference<List<Casa>>() {}));
                }
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo leer el archivo JSON", e);
            }
        }

        private void persistir() {
            List<Departamento> deptos = new ArrayList<>();
            List<Casa> casas = new ArrayList<>();
            for (Vivienda v : datos) {
                if (v instanceof Departamento d) deptos.add(d);
                else if (v instanceof Casa c) casas.add(c);
            }
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(archivoDeptos.toFile(), deptos);
                mapper.writerWithDefaultPrettyPrinter().writeValue(archivoCasas.toFile(), casas);
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo guardar el archivo JSON", e);
            }
        }

        @Override
        public List<Vivienda> listar() {
            return new ArrayList<>(datos);
        }

        @Override
        public void guardar(Vivienda vivienda) {
            int indice = -1;
            for (int i = 0; i < datos.size(); i++) {
                if (datos.get(i).getCodigo().equals(vivienda.getCodigo())) { indice = i; break; }
            }
            if (indice >= 0) datos.set(indice, vivienda);
            else datos.add(vivienda);
            persistir();
        }

        @Override
        public void eliminar(String codigo) {
            datos.removeIf(v -> v.getCodigo().equals(codigo));
            persistir();
        }
    }

