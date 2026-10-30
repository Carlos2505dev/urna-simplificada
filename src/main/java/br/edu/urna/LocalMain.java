/*
 * Ponto de entrada da urna em modo local, sem dependências do Firebase.
 */
package br.edu.urna;

import br.edu.urna.controller.VotingController;
import br.edu.urna.repository.local.LocalCandidateRepository;
import br.edu.urna.repository.local.LocalVoteRepository;
import br.edu.urna.service.VotingService;
import br.edu.urna.view.VotingFrame;
import javax.swing.SwingUtilities;

/**
 * Inicia a aplicação usando persistência local.
 */
public class LocalMain {

    private LocalMain() {
    }

    public static void main(String[] arguments) {
        SwingUtilities.invokeLater(() -> {
            VotingService votingService = new VotingService(
                    new LocalCandidateRepository(), new LocalVoteRepository());
            votingService.prepareCandidates();

            VotingController controller = new VotingController(votingService);
            VotingFrame frame = new VotingFrame(controller);
            controller.setScreen(frame);
            frame.setVisible(true);
        });
    }
}