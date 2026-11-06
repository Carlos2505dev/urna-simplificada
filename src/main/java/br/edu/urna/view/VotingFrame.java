/*
 * Arquivo: VotingFrame.java
 * Janela principal da urna: tela, teclado numérico e teclas BRANCO, CORRIGE e CONFIRMA.
 */
package br.edu.urna.view;

import br.edu.urna.controller.VotingController;
import br.edu.urna.model.Candidate;
import br.edu.urna.model.ElectionReport;
import br.edu.urna.model.Office;
import br.edu.urna.util.VotingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Tela da urna. Não contém regras: repassa os cliques ao controller e exibe o que ele pede.
 */
public class VotingFrame extends JFrame implements VotingScreen {

    private static final Logger LOGGER = Logger.getLogger(VotingFrame.class.getName());
    private static final AtomicBoolean AUDIO_WARNING_LOGGED = new AtomicBoolean();
    private static final int PHOTO_WIDTH = 180;
    private static final int PHOTO_HEIGHT = 230;
    private static final Color CANVAS_COLOR = new Color(238, 237, 230);
    private static final Color KEYPAD_COLOR = new Color(42, 43, 44);

    private final VotingController controller;
    private final JLabel officeLabel = new JLabel(" ", SwingConstants.LEFT);
    private final JLabel photoLabel = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel candidateNameLabel = new JLabel(" ");
    private final JLabel candidatePartyLabel = new JLabel(" ");
    private final JLabel messageLabel = new JLabel(" ");
    private final List<JLabel> numberCells = new ArrayList<>();
    private final List<JButton> keys = new ArrayList<>();
    private final JPanel displayCards = new JPanel(new CardLayout());
    private final Clip keySound;
    private final Clip fimSound;

    public VotingFrame(VotingController controller) {
        super("Urna Eletrônica Simplificada");
        this.controller = controller;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel content = new JPanel(new BorderLayout(18, 0));
        content.setBorder(new EmptyBorder(18, 18, 18, 18));
        content.setBackground(CANVAS_COLOR);
        content.add(createDisplayPanel(), BorderLayout.CENTER);
        content.add(createKeypadPanel(), BorderLayout.EAST);
        setContentPane(content);
        keySound = loadSound("teclas.wav", -8.0f);
        fimSound = loadSound("fim.wav", -4.0f);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    @Override
    public void showOffice(Office office) {
        officeLabel.setVisible(true);
        officeLabel.setText(office.getDescription().toUpperCase());
    }

    @Override
    public void showTypedNumber(String number) {
        showCandidateCard();
        updateNumberCells(number);
        candidateNameLabel.setText(" ");
        candidatePartyLabel.setText(" ");
        clearPhoto();
    }

    @Override
    public void showCandidate(Candidate candidate) {
        showCandidateCard();
        updateNumberCells(candidate.getNumber());
        candidateNameLabel.setText(wrapText(candidate.getName(), 350));
        String candidateDetails = "Partido: " + candidate.getPartyAcronym();
        if (candidate.getViceName() != null) {
            candidateDetails += " | Vice-prefeito: " + candidate.getViceName();
        }
        candidatePartyLabel.setText(wrapText(candidateDetails, 350));
        setPhoto(loadPhoto(candidate.getPhotoFileName()));
    }

    @Override
    public void showInvalidNumber(String number) {
        showCandidateCard();
        updateNumberCells(number);
        candidateNameLabel.setText("Número não encontrado");
        candidatePartyLabel.setText("O voto será nulo se confirmado.");
        clearPhoto();
    }

    @Override
    public void showBlankVote() {
        showCandidateCard();
        updateNumberCells("");
        candidateNameLabel.setText("VOTO EM BRANCO");
        candidatePartyLabel.setText("Aperte CONFIRMA para concluir.");
        clearPhoto();
    }

    @Override
    public void showRecording() {
        updateNumberCells("");
        candidateNameLabel.setText("VOTO COMPUTADO");
        candidatePartyLabel.setText("Aguarde.");
        clearPhoto();
    }

    @Override
    public void showFim() {
        officeLabel.setVisible(false);
        ((CardLayout) displayCards.getLayout()).show(displayCards, "fim");
        playSound(fimSound);
    }

    @Override
    public void setKeysEnabled(boolean enabled) {
        for (JButton key : keys) {
            key.setEnabled(enabled);
        }
    }

    @Override
    public void clearDisplay() {
        showCandidateCard();
        updateNumberCells("");
        candidateNameLabel.setText(" ");
        candidatePartyLabel.setText(" ");
        clearPhoto();
    }

    @Override
    public void showMessage(String message) {
        messageLabel.setText(wrapText(message, 540));
    }

    @Override
    public void showReports(List<ElectionReport> reports, String title) {
        ReportDialog.show(this, title, reports);
    }

    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(95, 95, 95), 1), new EmptyBorder(22, 22, 16, 22)));
        panel.setPreferredSize(new Dimension(650, 450));

        officeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
        panel.add(officeLabel, BorderLayout.NORTH);

        JPanel informationPanel = new JPanel();
        informationPanel.setBackground(Color.WHITE);
        informationPanel.setLayout(new javax.swing.BoxLayout(informationPanel, javax.swing.BoxLayout.Y_AXIS));

        JPanel numberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        numberPanel.setBackground(Color.WHITE);
        for (int index = 0; index < VotingConstants.NUMBER_LENGTH; index++) {
            JLabel cell = new JLabel(" ", SwingConstants.CENTER);
            cell.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
            cell.setBackground(Color.WHITE);
            cell.setOpaque(true);
            cell.setPreferredSize(new Dimension(42, 54));
            cell.setBorder(new LineBorder(new Color(100, 100, 100), 1));
            numberCells.add(cell);
            numberPanel.add(cell);
        }
        informationPanel.add(numberPanel);
        informationPanel.add(javax.swing.Box.createVerticalStrut(20));

        candidateNameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 23));
        candidateNameLabel.setForeground(new Color(24, 24, 24));
        candidatePartyLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        candidatePartyLabel.setForeground(new Color(45, 45, 45));
        informationPanel.add(candidateNameLabel);
        informationPanel.add(javax.swing.Box.createVerticalStrut(8));
        informationPanel.add(candidatePartyLabel);

        photoLabel.setPreferredSize(new Dimension(PHOTO_WIDTH, PHOTO_HEIGHT));
        photoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        photoLabel.setForeground(new Color(65, 65, 65));

        JPanel candidatePanel = new JPanel(new BorderLayout(18, 0));
        candidatePanel.setBackground(Color.WHITE);
        candidatePanel.add(informationPanel, BorderLayout.CENTER);
        candidatePanel.add(photoLabel, BorderLayout.EAST);

        JPanel fimPanel = new JPanel(new BorderLayout());
        fimPanel.setBackground(Color.WHITE);
        JLabel fimLabel = new JLabel("FIM", SwingConstants.CENTER);
        fimLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 100));
        fimLabel.setForeground(new Color(22, 22, 22));
        fimPanel.add(fimLabel, BorderLayout.CENTER);

        displayCards.setBackground(Color.WHITE);
        displayCards.add(candidatePanel, "candidate");
        displayCards.add(fimPanel, "fim");
        panel.add(displayCards, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(0, 8));
        footer.setBackground(Color.WHITE);
        footer.add(new JSeparator(), BorderLayout.NORTH);
        messageLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        messageLabel.setForeground(new Color(32, 32, 32));
        footer.add(messageLabel, BorderLayout.CENTER);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private void showCandidateCard() {
        ((CardLayout) displayCards.getLayout()).show(displayCards, "candidate");
    }

    private JPanel createKeypadPanel() {
        JPanel keypad = new JPanel(new GridLayout(5, 3, 8, 8));
        keypad.setBorder(new EmptyBorder(14, 14, 14, 14));
        keypad.setBackground(KEYPAD_COLOR);
        keypad.setPreferredSize(new Dimension(306, 450));
        for (int digit = 1; digit <= 9; digit++) {
            String label = String.valueOf(digit);
            keypad.add(createKey(label, new Color(54, 68, 77), Color.WHITE,
                    event -> controller.digitPressed(label)));
        }
        keypad.add(createKeypadSpacer());
        keypad.add(createKey("0", new Color(54, 68, 77), Color.WHITE,
                event -> controller.digitPressed("0")));
        keypad.add(createKeypadSpacer());
        keypad.add(createKey("BRANCO", new Color(245, 245, 242), new Color(25, 25, 25),
                event -> controller.blankPressed()));
        keypad.add(createKey("CORRIGE", new Color(255, 87, 24), Color.BLACK,
                event -> controller.correctPressed()));
        keypad.add(createKey("CONFIRMA", new Color(76, 175, 80), Color.WHITE,
                event -> controller.confirmPressed()));
        return keypad;
    }

    private JButton createKey(String label, Color background, Color foreground, ActionListener listener) {
        JButton button = new JButton(label);
        int fontSize = label.length() == 1 ? 30 : 13;
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(background.darker(), 2), new EmptyBorder(5, 3, 5, 3)));
        button.setPreferredSize(new Dimension(82, 64));
        button.getAccessibleContext().setAccessibleName(label);
        button.addActionListener(event -> {
            if ("CONFIRMA".equals(label)) {
                if (controller.isFinalVoteConfirmation()) {
                    stopSound(keySound);
                } else {
                    playSound(keySound);
                }
            }
            listener.actionPerformed(event);
        });
        keys.add(button);
        return button;
    }

    private Clip loadSound(String fileName, float gainDecibels) {
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(Path.of(fileName).toFile())) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(Math.max(gain.getMinimum(), Math.min(gainDecibels, gain.getMaximum())));
            }
            return clip;
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exception) {
            logAudioFailure(exception);
            return null;
        }
    }

    private void playSound(Clip clip) {
        if (clip == null) {
            return;
        }
        stopSound(clip);
        clip.setFramePosition(0);
        clip.start();
    }

    private void stopSound(Clip clip) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    private void logAudioFailure(Exception exception) {
        if (AUDIO_WARNING_LOGGED.compareAndSet(false, true)) {
            LOGGER.log(Level.WARNING, "Nao foi possivel reproduzir os sons da urna.", exception);
        }
    }

    private JPanel createKeypadSpacer() {
        JPanel spacer = new JPanel();
        spacer.setBackground(KEYPAD_COLOR);
        return spacer;
    }

    private void updateNumberCells(String number) {
        for (int index = 0; index < numberCells.size(); index++) {
            String digit = index < number.length() ? number.substring(index, index + 1) : " ";
            numberCells.get(index).setText(digit);
        }
    }

    private void setPhoto(ImageIcon photo) {
        photoLabel.setIcon(photo);
        photoLabel.setText(photo == null ? "Foto indisponível" : "");
    }

    private void clearPhoto() {
        photoLabel.setIcon(null);
        photoLabel.setText(" ");
    }

    private String wrapText(String text, int width) {
        return "<html><div style='width:" + width + "px'>" + escapeHtml(text) + "</div></html>";
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private ImageIcon loadPhoto(String photoFileName) {
        ImageIcon icon = new ImageIcon(VotingConstants.PHOTOS_DIRECTORY + photoFileName);
        if (icon.getIconWidth() <= 0) {
            return null;
        }
        double scale = Math.min((double) PHOTO_WIDTH / icon.getIconWidth(),
                (double) PHOTO_HEIGHT / icon.getIconHeight());
        int scaledWidth = (int) (icon.getIconWidth() * scale);
        int scaledHeight = (int) (icon.getIconHeight() * scale);
        Image scaled = icon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
