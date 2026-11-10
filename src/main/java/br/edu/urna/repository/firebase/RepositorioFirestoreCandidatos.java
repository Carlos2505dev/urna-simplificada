/*
 * Arquivo: FirestoreCandidateRepository.java
 * Implementacao de CandidateRepository com Firestore.
 */
package br.edu.urna.repository.firebase;

import static br.edu.urna.repository.firebase.ConexaoFirebase.aguardarResultado;

import br.edu.urna.model.Candidato;
import br.edu.urna.model.Cargo;
import br.edu.urna.repository.RepositorioCandidatos;
import br.edu.urna.util.ConstantesVotacao;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Guarda os candidatos na coleção "candidatos" do Firestore.
 */
public class RepositorioFirestoreCandidatos implements RepositorioCandidatos {

    private static final String CAMPO_NUMERO = "numero";
    private static final String CAMPO_NOME = "nome";
    private static final String CAMPO_PARTIDO = "sigla";
    private static final String CAMPO_CARGO = "cargo";
    private static final String CAMPO_FOTO = "foto";
    private static final String CAMPO_VICE = "vice";

    private final Firestore firestore;

    public RepositorioFirestoreCandidatos(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public List<Candidato> buscarPorCargo(Cargo cargo) {
        QuerySnapshot snapshot = aguardarResultado(
                colecaoCandidatos().whereEqualTo(CAMPO_CARGO, cargo.name()).get());
        List<Candidato> candidatos = new ArrayList<>();
        for (QueryDocumentSnapshot document : snapshot.getDocuments()) {
            candidatos.add(converterParaCandidato(document));
        }
        return candidatos;
    }

    @Override
    public Optional<Candidato> buscarPorCargoENumero(Cargo cargo, String numero) {
        QuerySnapshot snapshot = aguardarResultado(colecaoCandidatos()
            .whereEqualTo(CAMPO_CARGO, cargo.name())
            .whereEqualTo(CAMPO_NUMERO, numero)
                .get());
        if (snapshot.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(converterParaCandidato(snapshot.getDocuments().get(0)));
    }

    @Override
    public boolean estaVazio() {
        return aguardarResultado(colecaoCandidatos().limit(1).get()).isEmpty();
    }

    @Override
    public void salvarTodos(List<Candidato> candidatos) {
        QuerySnapshot existentes = aguardarResultado(colecaoCandidatos().get());
        WriteBatch batch = firestore.batch();
        for (QueryDocumentSnapshot documento : existentes.getDocuments()) {
            batch.delete(documento.getReference());
        }
        for (Candidato candidato : candidatos) {
            batch.set(documentoDo(candidato), dadosDo(candidato));
        }
        aguardarResultado(batch.commit());
    }

    private DocumentReference documentoDo(Candidato candidato) {
        return colecaoCandidatos().document(candidato.getCargo().name() + "_" + candidato.getNumero());
    }

    private Map<String, Object> dadosDo(Candidato candidato) {
        Map<String, Object> data = new HashMap<>();
        data.put(CAMPO_NUMERO, candidato.getNumero());
        data.put(CAMPO_NOME, candidato.getNome());
        data.put(CAMPO_PARTIDO, candidato.getSiglaPartido());
        data.put(CAMPO_CARGO, candidato.getCargo().name());
        data.put(CAMPO_FOTO, candidato.getNomeArquivoFoto());
        if (candidato.getNomeVice() != null) {
            data.put(CAMPO_VICE, candidato.getNomeVice());
        }
        return data;
    }

    private CollectionReference colecaoCandidatos() {
        return firestore.collection(ConstantesVotacao.COLECAO_CANDIDATOS);
    }

    private Candidato converterParaCandidato(QueryDocumentSnapshot document) {
        return new Candidato(
                document.getString(CAMPO_NUMERO),
                document.getString(CAMPO_NOME),
                document.getString(CAMPO_PARTIDO),
                Cargo.valueOf(document.getString(CAMPO_CARGO)),
                document.getString(CAMPO_FOTO),
                document.getString(CAMPO_VICE));
    }
}
