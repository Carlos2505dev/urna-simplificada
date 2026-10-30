/*
 * Arquivo: FirestoreVoteRepository.java
 * Implementacao de VoteRepository com Firestore.
 */
package br.edu.urna.repository.firebase;

import static br.edu.urna.repository.firebase.FirebaseConnection.waitForResult;

import br.edu.urna.model.Office;
import br.edu.urna.repository.VoteRepository;
import br.edu.urna.util.VotingConstants;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

/**
 * Guarda cada voto como um documento na coleção "votos" do Firestore.
 */
public class FirestoreVoteRepository implements VoteRepository {

    private static final String ELECTION_FIELD = "eleicao";
    private static final String OFFICE_FIELD = "cargo";
    private static final String CHOICE_FIELD = "escolha";

    private final Firestore firestore;

    public FirestoreVoteRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void registerVote(String electionIdentifier, Office office, String choice) {
        Map<String, Object> vote = new HashMap<>();
        vote.put(ELECTION_FIELD, electionIdentifier);
        vote.put(OFFICE_FIELD, office.name());
        vote.put(CHOICE_FIELD, choice);
        waitForResult(firestore.collection(VotingConstants.VOTES_COLLECTION).add(vote));
    }

    @Override
    public Map<String, Integer> countVotes(String electionIdentifier, Office office) {
        QuerySnapshot snapshot = waitForResult(firestore.collection(VotingConstants.VOTES_COLLECTION)
                .whereEqualTo(ELECTION_FIELD, electionIdentifier)
                .whereEqualTo(OFFICE_FIELD, office.name())
                .get());
        Map<String, Integer> counts = new HashMap<>();
        for (QueryDocumentSnapshot document : snapshot.getDocuments()) {
            counts.merge(document.getString(CHOICE_FIELD), 1, Integer::sum);
        }
        return counts;
    }
}
