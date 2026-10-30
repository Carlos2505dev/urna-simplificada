/*
 * Repositório local de votos, armazenados em formato TSV.
 */
package br.edu.urna.repository.local;

import br.edu.urna.model.Office;
import br.edu.urna.repository.RepositoryException;
import br.edu.urna.repository.VoteRepository;
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
public class LocalVoteRepository implements VoteRepository {

    private final Path filePath;

    public LocalVoteRepository() {
        this(Path.of("data", "votes.tsv"));
    }

    public LocalVoteRepository(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public synchronized void registerVote(String electionIdentifier, Office office, String choice) {
        String record = electionIdentifier + "\t" + office.name() + "\t" + choice
                + System.lineSeparator();
        try {
            Path parent = filePath.toAbsolutePath().getParent();
            Files.createDirectories(parent);
            Files.writeString(filePath, record, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException exception) {
            throw new RepositoryException("Falha ao salvar voto localmente em " + filePath, exception);
        }
    }

    @Override
    public synchronized Map<String, Integer> countVotes(String electionIdentifier, Office office) {
        if (!Files.exists(filePath)) {
            return Map.of();
        }

        Map<String, Integer> counts = new HashMap<>();
        try {
            List<String> records = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            for (String record : records) {
                String[] fields = record.split("\t", -1);
                if (fields.length != 3) {
                        throw new RepositoryException("Registro inválido no arquivo local de votos: " + record,
                            new IllegalStateException("Esperados três campos separados por tabulação"));
                }
                if (fields[0].equals(electionIdentifier) && fields[1].equals(office.name())) {
                    counts.merge(fields[2], 1, Integer::sum);
                }
            }
        } catch (IOException exception) {
            throw new RepositoryException("Falha ao ler votos locais em " + filePath, exception);
        }
        return counts;
    }
}