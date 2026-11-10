/*
 * Arquivo: ConexaoFirebase.java
 * Conexão única com o Firebase Firestore.
 */
package br.edu.urna.repository.firebase;

import br.edu.urna.repository.ExcecaoRepositorio;
import br.edu.urna.util.ConstantesVotacao;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

/**
 * Inicializa o Firebase uma única vez e expõe o cliente Firestore.
 */
public final class ConexaoFirebase {

    private static Firestore firestore;

    private ConexaoFirebase() {
    }

    public static synchronized Firestore obterFirestore() throws IOException {
        if (firestore == null) {
            try (InputStream fluxoChave = new FileInputStream(obterCaminhoCredenciais().toFile())) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(fluxoChave))
                        .build();
                FirebaseApp.initializeApp(options);
            }
            firestore = FirestoreClient.getFirestore();
        }
        return firestore;
    }

    public static boolean temCredenciaisConfiguradas() {
        return Files.isRegularFile(obterCaminhoCredenciais());
    }

    private static Path obterCaminhoCredenciais() {
        String caminhoChave = System.getenv(ConstantesVotacao.VARIAVEL_AMBIENTE_CHAVE_FIREBASE);
        if (caminhoChave == null || caminhoChave.isBlank()) {
            caminhoChave = ConstantesVotacao.CAMINHO_PADRAO_CHAVE_FIREBASE;
        }
        return Path.of(caminhoChave);
    }

    public static <T> T aguardarResultado(ApiFuture<T> futuro) {
        try {
            return futuro.get();
        } catch (InterruptedException excecao) {
            Thread.currentThread().interrupt();
            throw new ExcecaoRepositorio("Operação interrompida", excecao);
        } catch (ExecutionException excecao) {
            throw new ExcecaoRepositorio("Falha ao acessar o Firebase", excecao);
        }
    }
}
