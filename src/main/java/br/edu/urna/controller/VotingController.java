/*
 * Arquivo: VotingController.java
 * Fluxo de votação: trata as teclas da urna e atualiza a tela.
 */
package br.edu.urna.controller;

import br.edu.urna.model.Candidate;
import br.edu.urna.model.ElectionStatus;
import br.edu.urna.model.Office;
import br.edu.urna.service.VotingService;
import br.edu.urna.util.VotingConstants;
import br.edu.urna.view.VotingScreen;
import java.util.Optional;
import javax.swing.Timer;

/**
 * Recebe os eventos do teclado, valida cada etapa e pede a exibição na tela.
 */
public class VotingController {

    private final VotingService votingService;
    private VotingScreen screen;
    private String typedNumber = "";
    private Candidate selectedCandidate;
    private boolean blankSelected;
    private boolean invalidNumber;
    private boolean passwordTyped;
    private boolean waitingForNextVoter;
    private int officeIndex;

    public VotingController(VotingService votingService) {
        this.votingService = votingService;
    }

    public void setScreen(VotingScreen screen) {
        this.screen = screen;
        screen.showOffice(currentOffice());
        screen.showMessage("Aguardando senha do mesário para iniciar a votação.");
    }

    public void digitPressed(String digit) {
        if (waitingForNextVoter) {
            return;
        }
        if (isClosed()) {
            screen.showMessage("Votação encerrada.");
            return;
        }
        if (typedNumber.length() >= VotingConstants.NUMBER_LENGTH) {
            return;
        }
        blankSelected = false;
        typedNumber += digit;
        screen.showTypedNumber(typedNumber);
        if (typedNumber.length() < VotingConstants.NUMBER_LENGTH) {
            return;
        }
        processCompleteNumber();
    }

    public void correctPressed() {
        if (waitingForNextVoter) {
            return;
        }
        if (isClosed()) {
            screen.showMessage("Votação encerrada.");
            return;
        }
        clearSelection();
        screen.showMessage("Digite novamente o número do candidato.");
    }

    public void blankPressed() {
        if (waitingForNextVoter) {
            return;
        }
        if (!isOpen()) {
            screen.showMessage("Aguarde a senha do mesário para iniciar a votação.");
            return;
        }
        clearSelection();
        blankSelected = true;
        screen.showBlankVote();
        screen.showMessage("Voto em branco selecionado. Aperte CONFIRMA para concluir.");
    }

    public void confirmPressed() {
        if (waitingForNextVoter) {
            return;
        }
        ElectionStatus status = votingService.getStatus();
        if (status == ElectionStatus.CLOSED) {
            screen.showMessage("Votação encerrada.");
            return;
        }
        if (passwordTyped) {
            toggleElection(status);
            return;
        }
        if (status != ElectionStatus.OPEN) {
            screen.showMessage("Aguarde a senha do mesário para iniciar a votação.");
            return;
        }
        if (selectedCandidate == null && !blankSelected && !invalidNumber) {
            screen.showMessage("Digite o número do candidato ou aperte BRANCO.");
            return;
        }
        votingService.registerVote(currentOffice(), resolveChoice());
        advanceToNextOffice();
    }

    public boolean isFinalVoteConfirmation() {
        return !waitingForNextVoter
                && votingService.getStatus() == ElectionStatus.OPEN
                && officeIndex == Office.values().length - 1
                && (selectedCandidate != null || blankSelected || invalidNumber);
    }

    private void processCompleteNumber() {
        if (VotingConstants.PASSWORD.equals(typedNumber) && officeIndex == 0) {
            passwordTyped = true;
            String action = votingService.getStatus() == ElectionStatus.NOT_STARTED ? "iniciar" : "encerrar";
            screen.showMessage("Senha correta. Confirme para " + action + " a votação.");
            return;
        }
        if (!isOpen()) {
            clearSelection();
            screen.showMessage("A votação ainda não foi iniciada pelo mesário.");
            return;
        }
        Optional<Candidate> candidate = votingService.findCandidate(currentOffice(), typedNumber);
        if (candidate.isPresent()) {
            selectedCandidate = candidate.get();
            screen.showCandidate(selectedCandidate);
            screen.showMessage("Aperte CONFIRMA para votar ou CORRIGE para digitar outro número.");
        } else {
            invalidNumber = true;
            screen.showInvalidNumber(typedNumber);
            screen.showMessage("Número errado. O voto será nulo se confirmado. Use CORRIGE para corrigir.");
        }
    }

    private void toggleElection(ElectionStatus status) {
        clearSelection();
        if (status == ElectionStatus.NOT_STARTED) {
            votingService.startElection();
            screen.showReports(votingService.buildReports(), "Relatório inicial");
            screen.showMessage("Votação iniciada. Digite o número do candidato.");
        } else {
            votingService.finishElection();
            screen.showReports(votingService.buildReports(), "Relatório final da eleição");
            screen.showMessage("Votação encerrada.");
        }
        screen.showOffice(currentOffice());
    }

    private void advanceToNextOffice() {
        clearSelection();
        officeIndex++;
        if (officeIndex == Office.values().length) {
            officeIndex = 0;
            waitingForNextVoter = true;
            screen.showFim();
            screen.showMessage("Aguarde para iniciar a votação do próximo eleitor.");
            screen.setKeysEnabled(false);

            Timer nextVoterTimer = new Timer(2000, event -> {
                screen.clearDisplay();
                screen.showOffice(currentOffice());
                screen.showMessage("Voto concluído. Próximo eleitor.");
                waitingForNextVoter = false;
                screen.setKeysEnabled(true);
            });
            nextVoterTimer.setRepeats(false);
            nextVoterTimer.start();
        } else {
            screen.showOffice(currentOffice());
            screen.showMessage("Agora vote para " + currentOffice().getDescription() + ".");
        }
    }

    private String resolveChoice() {
        if (blankSelected) {
            return VotingConstants.BLANK_CHOICE;
        }
        if (selectedCandidate != null) {
            return selectedCandidate.getNumber();
        }
        return VotingConstants.NULL_CHOICE;
    }

    private void clearSelection() {
        typedNumber = "";
        selectedCandidate = null;
        blankSelected = false;
        invalidNumber = false;
        passwordTyped = false;
        screen.clearDisplay();
    }

    private Office currentOffice() {
        return Office.values()[officeIndex];
    }

    private boolean isOpen() {
        return votingService.getStatus() == ElectionStatus.OPEN;
    }

    private boolean isClosed() {
        return votingService.getStatus() == ElectionStatus.CLOSED;
    }
}
