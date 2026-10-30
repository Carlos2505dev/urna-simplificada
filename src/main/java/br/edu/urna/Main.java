/*
 * Arquivo: Main.java
 * Ponto de entrada da Urna Eletrônica Simplificada.
 */
package br.edu.urna;

import br.edu.urna.controller.VotingController;
import br.edu.urna.repository.CandidateRepository;
import br.edu.urna.repository.MirroredVoteRepository;
import br.edu.urna.repository.VoteRepository;
import br.edu.urna.repository.firebase.FirebaseConnection;
import br.edu.urna.repository.firebase.FirestoreCandidateRepository;
import br.edu.urna.repository.firebase.FirestoreVoteRepository;
import br.edu.urna.repository.local.LocalCandidateRepository;
import br.edu.urna.repository.local.LocalVoteRepository;
import br.edu.urna.service.VotingService;
import br.edu.urna.view.VotingFrame;
import com.google.cloud.firestore.Firestore;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Conecta ao Firebase, monta as camadas da aplicação e abre a urna.
 */
public class Main {

    private Main() {
    }

    public static void main(String[] arguments) {
        SwingUtilities.invokeLater(() -> {
            try {
                LocalVoteRepository localVotes = new LocalVoteRepository();
                CandidateRepository candidateRepository = new LocalCandidateRepository();
                VoteRepository voteRepository = localVotes;

                if (FirebaseConnection.hasCredentialsConfigured()) {
                    try {
                        Firestore firestore = FirebaseConnection.getFirestore();
                        candidateRepository = new FirestoreCandidateRepository(firestore);
                        voteRepository = new MirroredVoteRepository(
                                localVotes, new FirestoreVoteRepository(firestore));
                    } catch (IOException | RuntimeException exception) {
                        JOptionPane.showMessageDialog(null,
                                "Firebase indisponível. A urna continuará salvando os votos localmente.\n"
                                + exception.getMessage(),
                                "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                }

                VotingService votingService = new VotingService(candidateRepository, voteRepository);
                votingService.prepareCandidates();

                VotingController controller = new VotingController(votingService);
                VotingFrame frame = new VotingFrame(controller);
                controller.setScreen(frame);
                frame.setVisible(true);
            } catch (IOException | RuntimeException exception) {
                JOptionPane.showMessageDialog(null,
                        "Não foi possível iniciar a urna: " + exception.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
