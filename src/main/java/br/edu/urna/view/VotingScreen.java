/*
 * Arquivo: VotingScreen.java
 * Contrato da tela da urna, usado pelo controller.
 */
package br.edu.urna.view;

import br.edu.urna.model.Candidate;
import br.edu.urna.model.ElectionReport;
import br.edu.urna.model.Office;
import java.util.List;

/**
 * Operacoes de exibicao que o controller pode solicitar.
 */
public interface VotingScreen {

    void showOffice(Office office);

    void showTypedNumber(String number);

    void showCandidate(Candidate candidate);

    void showInvalidNumber(String number);

    void showBlankVote();

    void showRecording();

    void showFim();

    void setKeysEnabled(boolean enabled);

    void clearDisplay();

    void showMessage(String message);

    void showReports(List<ElectionReport> reports, String title);
}
