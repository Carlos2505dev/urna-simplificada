
package br.edu.urna;

import br.edu.urna.controller.ControladorVotacao;
import br.edu.urna.repository.RepositorioCandidatos;
import br.edu.urna.repository.RepositorioVotos;
import br.edu.urna.repository.RepositorioVotosEspelhado;
import br.edu.urna.repository.firebase.ConexaoFirebase;
import br.edu.urna.repository.firebase.RepositorioFirestoreCandidatos;
import br.edu.urna.repository.firebase.RepositorioFirestoreVotos;
import br.edu.urna.repository.local.RepositorioLocalCandidatos;
import br.edu.urna.repository.local.RepositorioLocalVotos;
import br.edu.urna.service.ServicoVotacao;
import br.edu.urna.view.JanelaVotacao;
import com.google.cloud.firestore.Firestore;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Conecta ao Firebase, monta as camadas da aplicação e abre a urna.
 */
public class Principal {

    private Principal() {
    }

    public static void main(String[] argumentos) {
        SwingUtilities.invokeLater(() -> {
            try {
                RepositorioLocalVotos votosLocais = new RepositorioLocalVotos();
                RepositorioCandidatos repositorioCandidatos = new RepositorioLocalCandidatos();
                RepositorioVotos repositorioVotos = votosLocais;

                if (ConexaoFirebase.temCredenciaisConfiguradas()) {
                    try {
                        Firestore firestore = ConexaoFirebase.obterFirestore();
                        repositorioCandidatos = new RepositorioFirestoreCandidatos(firestore);
                        repositorioVotos = new RepositorioVotosEspelhado(
                            votosLocais, new RepositorioFirestoreVotos(firestore));
                    } catch (IOException | RuntimeException exception) {
                        JOptionPane.showMessageDialog(null,
                                "Firebase indisponível. A urna continuará salvando os votos localmente.\n"
                                + exception.getMessage(),
                                "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                }

                ServicoVotacao servicoVotacao = new ServicoVotacao(repositorioCandidatos, repositorioVotos);
                servicoVotacao.prepararCandidatos();

                ControladorVotacao controlador = new ControladorVotacao(servicoVotacao);
                JanelaVotacao janela = new JanelaVotacao(controlador);
                controlador.definirTela(janela);
                janela.setVisible(true);
            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(null,
                        "Não foi possível iniciar a urna: " + exception.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
