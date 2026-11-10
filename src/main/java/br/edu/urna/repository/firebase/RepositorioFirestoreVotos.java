/*
 * Arquivo: FirestoreVoteRepository.java
 * Implementacao de VoteRepository com Firestore.
 */
package br.edu.urna.repository.firebase;

import static br.edu.urna.repository.firebase.ConexaoFirebase.aguardarResultado;

import br.edu.urna.model.Cargo;
import br.edu.urna.repository.RepositorioVotos;
import br.edu.urna.util.ConstantesVotacao;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

/**
 * Guarda cada voto como um documento na coleção "votos" do Firestore.
 */
public class RepositorioFirestoreVotos implements RepositorioVotos {

    private static final String CAMPO_ELEICAO = "eleicao";
    private static final String CAMPO_CARGO = "cargo";
    private static final String CAMPO_ESCOLHA = "escolha";

    private final Firestore firestore;

    public RepositorioFirestoreVotos(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void registrarVoto(String identificadorEleicao, Cargo cargo, String escolha) {
        Map<String, Object> voto = new HashMap<>();
        voto.put(CAMPO_ELEICAO, identificadorEleicao);
        voto.put(CAMPO_CARGO, cargo.name());
        voto.put(CAMPO_ESCOLHA, escolha);
        aguardarResultado(firestore.collection(ConstantesVotacao.COLECAO_VOTOS).add(voto));
    }

    @Override
    public Map<String, Integer> contarVotos(String identificadorEleicao, Cargo cargo) {
        QuerySnapshot snapshot = aguardarResultado(firestore.collection(ConstantesVotacao.COLECAO_VOTOS)
                .whereEqualTo(CAMPO_ELEICAO, identificadorEleicao)
                .whereEqualTo(CAMPO_CARGO, cargo.name())
                .get());
        Map<String, Integer> contagens = new HashMap<>();
        for (QueryDocumentSnapshot documento : snapshot.getDocuments()) {
            contagens.merge(documento.getString(CAMPO_ESCOLHA), 1, Integer::sum);
        }
        return contagens;
    }
}
