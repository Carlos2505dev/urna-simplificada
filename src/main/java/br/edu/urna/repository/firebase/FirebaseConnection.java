/*
 * Arquivo: FirebaseConnection.java
 * Conexão única com o Firebase Firestore.
 */
package br.edu.urna.repository.firebase;

import br.edu.urna.repository.RepositoryException;
import br.edu.urna.util.VotingConstants;
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
public final class FirebaseConnection {

    private static Firestore firestore;

    private FirebaseConnection() {
    }

    public static synchronized Firestore getFirestore() throws IOException {
        if (firestore == null) {
            try (InputStream keyStream = new FileInputStream(getCredentialsPath().toFile())) {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(keyStream))
                        .build();
                FirebaseApp.initializeApp(options);
            }
            firestore = FirestoreClient.getFirestore();
        }
        return firestore;
    }

    public static boolean hasCredentialsConfigured() {
        return Files.isRegularFile(getCredentialsPath());
    }

    private static Path getCredentialsPath() {
        String keyPath = System.getenv(VotingConstants.FIREBASE_KEY_ENVIRONMENT_VARIABLE);
        if (keyPath == null || keyPath.isBlank()) {
            keyPath = VotingConstants.DEFAULT_FIREBASE_KEY_PATH;
        }
        return Path.of(keyPath);
    }

    public static <T> T waitForResult(ApiFuture<T> future) {
        try {
            return future.get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RepositoryException("Operação interrompida", exception);
        } catch (ExecutionException exception) {
            throw new RepositoryException("Falha ao acessar o Firebase", exception);
        }
    }
}
