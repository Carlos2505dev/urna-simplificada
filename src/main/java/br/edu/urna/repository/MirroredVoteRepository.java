/*
 * Repositório que persiste localmente e tenta espelhar os votos em outro repositório.
 */
package br.edu.urna.repository;

import br.edu.urna.model.Office;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Usa o repositório local como fonte de verdade e replica votos remotamente quando possível.
 */
public class MirroredVoteRepository implements VoteRepository {

    private static final Logger LOGGER = Logger.getLogger(MirroredVoteRepository.class.getName());

    private final VoteRepository localRepository;
    private final VoteRepository remoteRepository;

    public MirroredVoteRepository(VoteRepository localRepository, VoteRepository remoteRepository) {
        this.localRepository = localRepository;
        this.remoteRepository = remoteRepository;
    }

    @Override
    public void registerVote(String electionIdentifier, Office office, String choice) {
        localRepository.registerVote(electionIdentifier, office, choice);
        try {
            remoteRepository.registerVote(electionIdentifier, office, choice);
        } catch (RuntimeException exception) {
            LOGGER.log(Level.WARNING,
                    "Voto salvo localmente, mas não foi possível enviá-lo ao Firestore.", exception);
        }
    }

    @Override
    public Map<String, Integer> countVotes(String electionIdentifier, Office office) {
        return localRepository.countVotes(electionIdentifier, office);
    }
}