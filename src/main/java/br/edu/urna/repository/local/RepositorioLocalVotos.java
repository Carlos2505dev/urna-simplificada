/*
 * Repositório local de votos, armazenados em formato TSV.
 */
package br.edu.urna.repository.local;

import br.edu.urna.model.Cargo;
import br.edu.urna.repository.ExcecaoRepositorio;
import br.edu.urna.repository.RepositorioVotos;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Persiste votos em data/votes.tsv para sobreviver ao fechamento da aplicação.
 */
public class RepositorioLocalVotos implements RepositorioVotos {

    private final Path caminhoArquivo;

    public RepositorioLocalVotos() {
        this(Path.of("data", "votes.tsv"));
    }

    public RepositorioLocalVotos(Path caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    @Override
    public synchronized void registrarVoto(String identificadorEleicao, Cargo cargo, String escolha) {
        String registro = identificadorEleicao + "\t" + cargo.name() + "\t" + escolha
                + System.lineSeparator();
        try {
            Path parent = caminhoArquivo.toAbsolutePath().getParent();
            Files.createDirectories(parent);
            Files.writeString(caminhoArquivo, registro, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            throw new ExcecaoRepositorio("Falha ao salvar voto localmente em " + caminhoArquivo, exception);
        }
    }

    @Override
    public synchronized Map<String, Integer> contarVotos(String identificadorEleicao, Cargo cargo) {
        if (!Files.exists(caminhoArquivo)) {
            return Map.of();
        }

        Map<String, Integer> counts = new HashMap<>();
        try {
            List<String> registros = Files.readAllLines(caminhoArquivo, StandardCharsets.UTF_8);
            for (String registro : registros) {
                String[] campos = registro.split("\t", -1);
                if (campos.length != 3) {
                        throw new ExcecaoRepositorio("Registro inválido no arquivo local de votos: " + registro,
                            new IllegalStateException("Esperados três campos separados por tabulação"));
                }
                if (campos[0].equals(identificadorEleicao) && campos[1].equals(cargo.name())) {
                    counts.merge(campos[2], 1, Integer::sum);
                }
            }
        } catch (IOException exception) {
            throw new ExcecaoRepositorio("Falha ao ler votos locais em " + caminhoArquivo, exception);
        }
        return counts;
    }
}