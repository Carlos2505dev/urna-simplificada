/*
 * Arquivo: JanelaVotacao.java
 * Janela principal da urna: tela de votacao, teclado numerico e teclas BRANCO, CORRIGE e CONFIRMA.
 */
package br.edu.urna.view;

import br.edu.urna.controller.ControladorVotacao;
import br.edu.urna.model.Candidato;
import br.edu.urna.model.Cargo;
import br.edu.urna.model.RelatorioEleitoral;
import br.edu.urna.util.ConstantesVotacao;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Tela da urna. Nao contem regras: repassa os cliques ao controlador e exibe o que ele pede.
 */
public class JanelaVotacao extends JFrame implements TelaVotacao {

    private static final Logger LOGGER = Logger.getLogger(JanelaVotacao.class.getName());
    private static final AtomicBoolean AUDIO_WARNING_LOGGED = new AtomicBoolean();

    private static final String SENHA_MESARIO = "99999";
    private static final String TEXTO_FOTO_INDISPONIVEL = "Foto indisponível";
    private static final int TEMPO_FIM_MS = 3000;

    private static final int LARGURA_TELA = 620;
    private static final int ALTURA_TELA = 480;
    private static final int MARGEM_ESQUERDA = 30;
    private static final int ESPACO_ROTULO_VALOR = 12;
    private static final int LARGURA_VALORES = 250;
    private static final int ESPACO_LINHA = 8;
    private static final int CAIXA_LARGURA = 42;
    private static final int CAIXA_ALTURA = 56;
    private static final int CAIXA_ESPACO = 6;
    private static final int Y_CABECALHO = 18;
    private static final int Y_CARGO = 50;
    private static final int Y_NUMERO = 115;
    private static final int Y_DETALHES = 190;
    private static final int FOTO_X = 410;
    private static final int FOTO_Y = 25;
    private static final int FOTO_LARGURA = 190;
    private static final int FOTO_ALTURA = 330;
    private static final int Y_SEPARADOR = 385;
    private static final int Y_APERTE = 400;
    private static final int Y_MENSAGEM = 420;
    private static final int X_MENSAGEM = 50;
    private static final int LARGURA_MENSAGEM = 540;
    private static final int TAMANHO_FONTE_RODAPE = 17;

    private static final Color COR_AREA_TRABALHO = new Color(238, 237, 230);
    private static final Color KEYPAD_COLOR = new Color(42, 43, 44);
    private static final Color COR_TEXTO = new Color(24, 24, 24);
    private static final Color COR_BORDA_CAIXA = new Color(100, 100, 100);

    private final ControladorVotacao controlador;
    private final JPanel painelTela = new JPanel(null);
    private final JLabel seuVotoLabel = criarTexto("SEU VOTO PARA", Font.BOLD, 18);
    private final JLabel cargoLabel = criarTexto(" ", Font.BOLD, 26);
    private final JLabel numeroRotulo = criarTexto("Número:", Font.PLAIN, 18);
    private final JLabel nomeRotulo = criarTexto("Nome:", Font.PLAIN, 18);
    private final JLabel nomeValor = criarTexto(" ", Font.PLAIN, 18);
    private final JLabel partidoRotulo = criarTexto("Partido:", Font.PLAIN, 18);
    private final JLabel partidoValor = criarTexto(" ", Font.PLAIN, 18);
    private final JLabel viceRotulo = criarTexto("Vice-prefeito:", Font.PLAIN, 18);
    private final JLabel viceValor = criarTexto(" ", Font.PLAIN, 18);
    private final JLabel resultadoLabel = criarTexto(" ", Font.BOLD, 22);
    private final JLabel fimLabel = criarTexto("FIM", Font.BOLD, 100);
    private final PhotoPanel photoPanel = new PhotoPanel();
    private final JSeparator separador = new JSeparator();
    private final JLabel aperteLabel = criarTexto("Aperte a tecla:", Font.PLAIN, TAMANHO_FONTE_RODAPE);
    private final JLabel mensagemLabel = criarTexto(" ", Font.PLAIN, TAMANHO_FONTE_RODAPE);
    private final List<JLabel> celulasNumero = new ArrayList<>();
    private final List<JButton> teclas = new ArrayList<>();
    private final Clip somTecla;
    private final Clip somFim;

    private int tamanhoNumeroAtual = ConstantesVotacao.TAMANHO_NUMERO;
    private String cargoTexto;
    private String numeroAtual = "";
    private String ultimoNumero = "";
    private boolean numeroCompleto;
    private String resultadoTexto;
    private boolean detalhesVisiveis;
    private String nomeTexto;
    private String partidoTexto;
    private String viceTexto;
    private BufferedImage fotoImagem;
    private BufferedImage fotoViceImagem;
    private String fotoPlaceholder = " ";
    private boolean fimVisivel;
    private javax.swing.Timer temporizadorFim;

    public JanelaVotacao(ControladorVotacao controlador) {
        super("Urna Eletrônica Simplificada");
        this.controlador = controlador;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel content = new JPanel(new BorderLayout(18, 0));
        content.setBorder(new EmptyBorder(18, 18, 18, 18));
        content.setBackground(COR_AREA_TRABALHO);
        content.add(createDisplayPanel(), BorderLayout.CENTER);
        content.add(createKeypadPanel(), BorderLayout.EAST);
        setContentPane(content);
        somTecla = carregarSom("teclas.wav", -8.0f);
        somFim = carregarSom("fim.wav", -4.0f);
        renderizar();
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    @Override
    public void mostrarCargo(Cargo cargo) {
        cargoTexto = cargo.getDescricao();
        tamanhoNumeroAtual = cargo == Cargo.VEREADOR ? 5 : 2;
        limparEstado();
        renderizar();
    }

    @Override
    public void mostrarNumeroDigitado(String numero) {
        boolean senha = SENHA_MESARIO.equals(numero);
        ultimoNumero = numero;
        numeroAtual = senha ? "" : numero;
        numeroCompleto = !senha && numero.length() >= tamanhoNumeroAtual;
        resultadoTexto = null;
        detalhesVisiveis = false;
        fotoImagem = null;
        fotoViceImagem = null;
        fotoPlaceholder = " ";
        renderizar();
    }

    @Override
    public void mostrarCandidato(Candidato candidato) {
        ultimoNumero = candidato.getNumero();
        numeroAtual = candidato.getNumero();
        numeroCompleto = true;
        resultadoTexto = null;
        detalhesVisiveis = true;
        nomeTexto = candidato.getNome();
        partidoTexto = candidato.getSiglaPartido();
        viceTexto = textoOuNulo(candidato.getNomeVice());
        fotoImagem = carregarFoto(candidato.getNomeArquivoFoto());
        fotoViceImagem = candidato.getCargo() == Cargo.PREFEITO
            ? carregarFotoVice(candidato.getNomeVice()) : null;
        fotoPlaceholder = TEXTO_FOTO_INDISPONIVEL;
        renderizar();
    }

    @Override
    public void mostrarNumeroInvalido(String numero) {
        ultimoNumero = numero;
        numeroAtual = numero;
        numeroCompleto = true;
        resultadoTexto = "Número não encontrado";
        detalhesVisiveis = false;
        fotoImagem = null;
        fotoViceImagem = null;
        fotoPlaceholder = " ";
        renderizar();
    }

    @Override
    public void mostrarVotoBranco() {
        ultimoNumero = "";
        numeroAtual = "";
        numeroCompleto = false;
        resultadoTexto = "VOTO EM BRANCO";
        detalhesVisiveis = false;
        fotoImagem = null;
        fotoViceImagem = null;
        fotoPlaceholder = " ";
        renderizar();
    }

    @Override
    public void mostrarVotoComputado() {
        ultimoNumero = "";
        numeroAtual = "";
        numeroCompleto = false;
        resultadoTexto = "VOTO COMPUTADO";
        detalhesVisiveis = false;
        fotoImagem = null;
        fotoViceImagem = null;
        fotoPlaceholder = " ";
        renderizar();
    }

    /**
     * Mostra a tela FIM e, depois de alguns segundos, volta automaticamente para a tela de votacao
     * do proximo eleitor.
     */
    @Override
    public void mostrarFim() {
        fimVisivel = true;
        renderizar();
        reproduzirSom(somFim);

        if (temporizadorFim != null) {
            temporizadorFim.stop();
        }
        temporizadorFim = new javax.swing.Timer(TEMPO_FIM_MS, event -> voltarParaProximoEleitor());
        temporizadorFim.setRepeats(false);
        temporizadorFim.start();
    }

    @Override
    public void definirTeclasAtivas(boolean ativas) {
        for (JButton tecla : teclas) {
            tecla.setEnabled(ativas);
        }
    }

    @Override
    public void limparTela() {
        limparEstado();
        renderizar();
    }

    @Override
    public void mostrarMensagem(String mensagem) {
        mensagemLabel.setText(quebrarTexto(mensagem, LARGURA_MENSAGEM));
        renderizar();
    }

    @Override
    public void mostrarRelatorios(List<RelatorioEleitoral> relatorios, String titulo) {
        DialogoRelatorio.mostrar(this, titulo, relatorios);
    }

    private void voltarParaProximoEleitor() {
        fimVisivel = false;
        limparEstado();
        renderizar();
    }

    private void limparEstado() {
        numeroAtual = "";
        ultimoNumero = "";
        numeroCompleto = false;
        resultadoTexto = null;
        detalhesVisiveis = false;
        nomeTexto = null;
        partidoTexto = null;
        viceTexto = null;
        fotoImagem = null;
        fotoViceImagem = null;
        fotoPlaceholder = " ";
    }

    /**
     * Posiciona todos os elementos conforme o estado atual.
     * As caixas de numero ficam sempre visiveis. O nome do cargo aparece durante a digitacao do
     * eleitor, mas nao quando o mesario digita a senha. "SEU VOTO PARA" e os dados aparecem somente
     * quando ha informacoes completas.
     */
    private void renderizar() {
        boolean informacoes = !fimVisivel && (numeroCompleto || resultadoTexto != null);
        boolean cargo = !fimVisivel && cargoTexto != null && !ehPrefixoDaSenha(ultimoNumero);
        boolean detalhes = !fimVisivel && detalhesVisiveis;
        boolean rotuloNumero = !fimVisivel && numeroCompleto && resultadoTexto == null;

        int larguraRotulos = Math.max(
                larguraSe(rotuloNumero, numeroRotulo),
                Math.max(larguraSe(detalhes && nomeTexto != null, nomeRotulo),
                        Math.max(larguraSe(detalhes && partidoTexto != null, partidoRotulo),
                                larguraSe(detalhes && viceTexto != null, viceRotulo))));
        int colunaValores = MARGEM_ESQUERDA + larguraRotulos + ESPACO_ROTULO_VALOR;

        seuVotoLabel.setBounds(MARGEM_ESQUERDA, Y_CABECALHO, 300, 26);
        seuVotoLabel.setVisible(informacoes);

        cargoLabel.setText(cargoTexto == null ? " " : cargoTexto);
        cargoLabel.setBounds(MARGEM_ESQUERDA, Y_CARGO, 400, 36);
        cargoLabel.setVisible(cargo);

        boolean caixas = !fimVisivel && resultadoTexto == null;
        int xCaixas = rotuloNumero ? colunaValores : MARGEM_ESQUERDA;
        for (int indice = 0; indice < celulasNumero.size(); indice++) {
            JLabel celula = celulasNumero.get(indice);
            celula.setText(indice < numeroAtual.length() ? numeroAtual.substring(indice, indice + 1) : " ");
            celula.setBounds(xCaixas + indice * (CAIXA_LARGURA + CAIXA_ESPACO), Y_NUMERO,
                    CAIXA_LARGURA, CAIXA_ALTURA);
            celula.setVisible(caixas && indice < tamanhoNumeroAtual);
        }
        int alturaRotuloNumero = numeroRotulo.getPreferredSize().height;
        numeroRotulo.setBounds(MARGEM_ESQUERDA, Y_NUMERO + (CAIXA_ALTURA - alturaRotuloNumero) / 2,
                numeroRotulo.getPreferredSize().width, alturaRotuloNumero);
        numeroRotulo.setVisible(rotuloNumero);

        boolean resultado = !fimVisivel && resultadoTexto != null;
        resultadoLabel.setText(resultado ? resultadoTexto : " ");
        resultadoLabel.setBounds(MARGEM_ESQUERDA, Y_DETALHES, 400, 32);
        resultadoLabel.setVisible(resultado);

        int y = Y_DETALHES;
        y = posicionarLinha(nomeRotulo, nomeValor, nomeTexto, detalhes, y, colunaValores);
        y = posicionarLinha(partidoRotulo, partidoValor, partidoTexto, detalhes, y, colunaValores);
        posicionarLinha(viceRotulo, viceValor, viceTexto, detalhes, y, colunaValores);

        photoPanel.mostrarFotos(fotoImagem, fotoViceImagem, fotoPlaceholder);
        photoPanel.setBounds(FOTO_X, FOTO_Y, FOTO_LARGURA, FOTO_ALTURA);
        photoPanel.setVisible(detalhes);

        separador.setBounds(20, Y_SEPARADOR, LARGURA_TELA - 40, 2);
        aperteLabel.setBounds(MARGEM_ESQUERDA, Y_APERTE, 320, aperteLabel.getPreferredSize().height);
        mensagemLabel.setBounds(X_MENSAGEM, Y_MENSAGEM, LARGURA_MENSAGEM,
                mensagemLabel.getPreferredSize().height);

        fimLabel.setBounds(0, 60, LARGURA_TELA, ALTURA_TELA - 170);
        fimLabel.setVisible(fimVisivel);

        painelTela.revalidate();
        painelTela.repaint();
    }

    /**
     * Indica se o texto digitado ainda pode ser o inicio da senha do mesario (ex.: "9", "99", "9999").
     */
    private boolean ehPrefixoDaSenha(String numero) {
        return !numero.isEmpty() && SENHA_MESARIO.startsWith(numero);
    }

    private int larguraSe(boolean condicao, JLabel rotulo) {
        return condicao ? rotulo.getPreferredSize().width : 0;
    }

    private int posicionarLinha(JLabel rotulo, JLabel valor, String texto, boolean visivel,
            int y, int colunaValores) {
        boolean exibir = visivel && texto != null;
        rotulo.setVisible(exibir);
        valor.setVisible(exibir);
        if (!exibir) {
            return y;
        }
        valor.setText(quebrarTexto(texto, LARGURA_VALORES));
        int larguraRotulo = rotulo.getPreferredSize().width;
        int altura = Math.max(rotulo.getPreferredSize().height, valor.getPreferredSize().height);
        rotulo.setBounds(MARGEM_ESQUERDA, y, larguraRotulo, altura);
        valor.setBounds(colunaValores, y, LARGURA_VALORES, altura);
        return y + altura + ESPACO_LINHA;
    }

    private JPanel createDisplayPanel() {
        painelTela.setBackground(Color.WHITE);
        painelTela.setPreferredSize(new Dimension(LARGURA_TELA, ALTURA_TELA));

        fimLabel.setHorizontalAlignment(SwingConstants.CENTER);

        for (int indice = 0; indice < ConstantesVotacao.TAMANHO_NUMERO; indice++) {
            JLabel celula = new JLabel(" ", SwingConstants.CENTER);
            celula.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
            celula.setForeground(COR_TEXTO);
            celula.setOpaque(true);
            celula.setBackground(Color.WHITE);
            celula.setBorder(new LineBorder(COR_BORDA_CAIXA, 1));
            celulasNumero.add(celula);
            painelTela.add(celula);
        }

        painelTela.add(seuVotoLabel);
        painelTela.add(cargoLabel);
        painelTela.add(numeroRotulo);
        painelTela.add(nomeRotulo);
        painelTela.add(nomeValor);
        painelTela.add(partidoRotulo);
        painelTela.add(partidoValor);
        painelTela.add(viceRotulo);
        painelTela.add(viceValor);
        painelTela.add(resultadoLabel);
        painelTela.add(fimLabel);
        painelTela.add(photoPanel);
        painelTela.add(separador);
        painelTela.add(aperteLabel);
        painelTela.add(mensagemLabel);

        JPanel moldura = new JPanel(new BorderLayout());
        moldura.setBackground(Color.WHITE);
        moldura.setBorder(new LineBorder(new Color(95, 95, 95), 1));
        moldura.add(painelTela, BorderLayout.CENTER);
        return moldura;
    }

    private JPanel createKeypadPanel() {
        JPanel keypad = new JPanel(new GridLayout(5, 3, 8, 8));
        keypad.setBorder(new EmptyBorder(14, 14, 14, 14));
        keypad.setBackground(KEYPAD_COLOR);
        keypad.setPreferredSize(new Dimension(306, ALTURA_TELA));
        for (int digit = 1; digit <= 9; digit++) {
            String label = String.valueOf(digit);
            keypad.add(createKey(label, new Color(54, 68, 77), Color.WHITE,
                    event -> controlador.teclaDigitada(label)));
        }
        keypad.add(createKeypadSpacer());
        keypad.add(createKey("0", new Color(54, 68, 77), Color.WHITE,
                event -> controlador.teclaDigitada("0")));
        keypad.add(createKeypadSpacer());
        keypad.add(createKey("BRANCO", new Color(245, 245, 242), new Color(25, 25, 25),
                event -> controlador.teclaBranco()));
        keypad.add(createKey("CORRIGE", new Color(255, 87, 24), Color.BLACK,
                event -> controlador.teclaCorrigir()));
        keypad.add(createKey("CONFIRMA", new Color(76, 175, 80), Color.WHITE,
                event -> controlador.teclaConfirmar()));
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
                if (controlador.eConfirmacaoDoUltimoVoto()) {
                    pararSom(somTecla);
                } else {
                    reproduzirSom(somTecla);
                }
            }
            listener.actionPerformed(event);
        });
        teclas.add(button);
        return button;
    }

    private JPanel createKeypadSpacer() {
        JPanel spacer = new JPanel();
        spacer.setBackground(KEYPAD_COLOR);
        return spacer;
    }

    private static JLabel criarTexto(String texto, int estilo, int tamanho) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font(Font.SANS_SERIF, estilo, tamanho));
        label.setForeground(COR_TEXTO);
        return label;
    }

    private Clip carregarSom(String nomeArquivo, float ganhoDecibeis) {
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(Path.of(nomeArquivo).toFile())) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(Math.max(gain.getMinimum(), Math.min(ganhoDecibeis, gain.getMaximum())));
            }
            return clip;
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exception) {
            logAudioFailure(exception);
            return null;
        }
    }

    private void reproduzirSom(Clip clip) {
        if (clip == null) {
            return;
        }
        pararSom(clip);
        clip.setFramePosition(0);
        clip.start();
    }

    private void pararSom(Clip clip) {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    private void logAudioFailure(Exception exception) {
        if (AUDIO_WARNING_LOGGED.compareAndSet(false, true)) {
            LOGGER.log(Level.WARNING, "Nao foi possivel reproduzir os sons da urna.", exception);
        }
    }

    private String textoOuNulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto;
    }

    private String quebrarTexto(String texto, int largura) {
        String textoHtml = escaparHtml(texto).replace("\r\n", "\n").replace("\r", "\n")
                .replace("\n", "<br>");
        return "<html><div style='width:" + largura + "px'>" + textoHtml + "</div></html>";
    }

    private String escaparHtml(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private BufferedImage carregarFoto(String nomeArquivoFoto) {
        if (nomeArquivoFoto == null || nomeArquivoFoto.isBlank()) {
            return null;
        }
        Path photoPath = Path.of(ConstantesVotacao.DIRETORIO_FOTOS, nomeArquivoFoto);
        try {
            return ImageIO.read(photoPath.toFile());
        } catch (IOException exception) {
            LOGGER.log(Level.WARNING, "Nao foi possivel carregar a foto " + photoPath, exception);
            return null;
        }
    }

    private BufferedImage carregarFotoVice(String nomeVice) {
        if (nomeVice == null || nomeVice.isBlank()) {
            return null;
        }
        return carregarFoto("vice-prefeitos/" + nomeVice.replace(" ", "_") + ".jpg");
    }

    /**
     * Painel que desenha a foto encostada no canto superior direito, na resolucao da tela.
     */
    private static final class PhotoPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private BufferedImage image;
        private BufferedImage viceImage;
        private String placeholderText = " ";

        private PhotoPanel() {
            setBackground(Color.WHITE);
        }

        private void mostrarFotos(BufferedImage photo, BufferedImage vicePhoto, String placeholder) {
            image = photo;
            viceImage = vicePhoto;
            placeholderText = placeholder;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            try {
                graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                        RenderingHints.VALUE_RENDER_QUALITY);
                graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (image == null && viceImage == null) {
                    drawPlaceholder(graphics2D);
                } else {
                    drawPhotos(graphics2D);
                }
            } finally {
                graphics2D.dispose();
            }
        }

        private void drawPhotos(Graphics2D graphics2D) {
            int alturaFoto = viceImage == null ? getHeight() : (getHeight() - 6) / 2;
            drawPhoto(graphics2D, image, 0, alturaFoto);
            if (viceImage != null) {
                drawPhoto(graphics2D, viceImage, alturaFoto + 6, alturaFoto);
            }
        }

        private void drawPhoto(Graphics2D graphics2D, BufferedImage photo, int y, int alturaMaxima) {
            if (photo == null) {
                return;
            }
            double scale = Math.min((double) getWidth() / photo.getWidth(),
                    (double) alturaMaxima / photo.getHeight());
            int drawWidth = (int) Math.round(photo.getWidth() * scale);
            int drawHeight = (int) Math.round(photo.getHeight() * scale);
            int drawX = getWidth() - drawWidth;
            int drawY = y + (alturaMaxima - drawHeight) / 2;
            graphics2D.drawImage(photo, drawX, drawY, drawWidth, drawHeight, null);
        }

        private void drawPlaceholder(Graphics2D graphics2D) {
            if (" ".equals(placeholderText)) {
                return;
            }
            graphics2D.setColor(Color.DARK_GRAY);
            FontMetrics metrics = graphics2D.getFontMetrics();
            int textX = (getWidth() - metrics.stringWidth(placeholderText)) / 2;
            int textY = (getHeight() + metrics.getAscent()) / 2;
            graphics2D.drawString(placeholderText, textX, textY);
        }
    }
}