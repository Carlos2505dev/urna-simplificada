/*
 * Arquivo: FirestoreCandidateRepository.java
 * Implementacao de CandidateRepository com Firestore.
 */
package br.edu.urna.repository.firebase;

import static br.edu.urna.repository.firebase.FirebaseConnection.waitForResult;

import br.edu.urna.model.Candidate;
import br.edu.urna.model.Office;
import br.edu.urna.repository.CandidateRepository;
import br.edu.urna.util.VotingConstants;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Guarda os candidatos na coleção "candidatos" do Firestore.
 */
public class FirestoreCandidateRepository implements CandidateRepository {

    private static final String NUMBER_FIELD = "numero";
    private static final String NAME_FIELD = "nome";
    private static final String PARTY_FIELD = "sigla";
    private static final String OFFICE_FIELD = "cargo";
    private static final String PHOTO_FIELD = "foto";
    private static final String VICE_FIELD = "vice";

    private final Firestore firestore;

    public FirestoreCandidateRepository(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public List<Candidate> findByOffice(Office office) {
        QuerySnapshot snapshot = waitForResult(
                candidatesCollection().whereEqualTo(OFFICE_FIELD, office.name()).get());
        List<Candidate> candidates = new ArrayList<>();
        for (QueryDocumentSnapshot document : snapshot.getDocuments()) {
            candidates.add(toCandidate(document));
        }
        return candidates;
    }

    @Override
    public Optional<Candidate> findByOfficeAndNumber(Office office, String number) {
        QuerySnapshot snapshot = waitForResult(candidatesCollection()
                .whereEqualTo(OFFICE_FIELD, office.name())
                .whereEqualTo(NUMBER_FIELD, number)
                .get());
        if (snapshot.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(toCandidate(snapshot.getDocuments().get(0)));
    }

    @Override
    public boolean isEmpty() {
        return waitForResult(candidatesCollection().limit(1).get()).isEmpty();
    }

    @Override
    public void saveAll(List<Candidate> candidates) {
        for (Candidate candidate : candidates) {
            Map<String, Object> data = new HashMap<>();
            data.put(NUMBER_FIELD, candidate.getNumber());
            data.put(NAME_FIELD, candidate.getName());
            data.put(PARTY_FIELD, candidate.getPartyAcronym());
            data.put(OFFICE_FIELD, candidate.getOffice().name());
            data.put(PHOTO_FIELD, candidate.getPhotoFileName());
            if (candidate.getViceName() != null) {
                data.put(VICE_FIELD, candidate.getViceName());
            }
            waitForResult(candidatesCollection().add(data));
        }
    }

    private CollectionReference candidatesCollection() {
        return firestore.collection(VotingConstants.CANDIDATES_COLLECTION);
    }

    private Candidate toCandidate(QueryDocumentSnapshot document) {
        return new Candidate(
                document.getString(NUMBER_FIELD),
                document.getString(NAME_FIELD),
                document.getString(PARTY_FIELD),
                Office.valueOf(document.getString(OFFICE_FIELD)),
                document.getString(PHOTO_FIELD),
                document.getString(VICE_FIELD));
    }
}
