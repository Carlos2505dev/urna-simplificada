/*
 * Arquivo: ApuracaoEleitoral.java
 * Tela de apuracao inspirada nos graficos exibidos pelas emissoras de televisao.
 */
package br.edu.urna.view;

import br.edu.urna.model.Candidato;
import br.edu.urna.model.Cargo;
import br.edu.urna.model.RelatorioEleitoral;
import br.edu.urna.util.ConstantesVotacao;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Apresenta a contagem final com entrada progressiva dos candidatos, das barras e dos numeros.
 */
public final class ApuracaoEleitoral extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(ApuracaoEleitoral.class.getName());
    private static final Locale LOCALE_BR = Locale.forLanguageTag("pt-BR");

    private static final int LARGURA_TELA = 1080;
    private static final int ALTURA_TELA = 620;
    private static final int MAX_CANDIDATOS_EXIBIDOS = 4;
    private static final int INTERVALO_QUADRO_MS = 16;
    private static final int ATRASO_ENTRE_LINHAS_MS = 220;
    private static final int ATRASO_BARRA_MS = 160;
    private static final float DURACAO_ENTRADA_LINHA_MS = 520f;
    private static final float DURACAO_FOTO_MS = 700f;
    private static final float DURACAO_BARRA_MS = 1200f;
    private static final float DURACAO_CABECALHO_MS = 600f;
    private static final float DURACAO_CONTADOR_URNAS_MS = 1400f;
    private static final float DURACAO_RODAPE_MS = 700f;
    private static final int ATRASO_RODAPE_MS = 300;
    private static final int PAUSA_FINAL_MS = 4000;
    private static final String TEXTO_ELEITO = "Eleito";
    private static final String TEXTO_SEGUNDO_TURNO = "2° TURNO";
    private static final double PERCENTUAL_MINIMO_ELEITO = 50.1;

    private static final int X_CABECALHO = 28;
    private static final int Y_CABECALHO = 58;
    private static final int X_FOTO = 70;
    private static final int TAMANHO_FOTO = 62;
    private static final int X_TEXTO = 150;
    private static final int LARGURA_TRILHA = 600;
    private static final int ALTURA_BARRA = 8;
    private static final int X_VALORES_DIREITA = 880;
    private static final int Y_PRIMEIRA_LINHA = 112;
    private static final int ESPACAMENTO_LINHAS = 90;
    private static final int Y_RODAPE = 510;
    private static final int X_RODAPE_BRANCOS = 70;
    private static final int X_RODAPE_NULOS = 330;
    private static final int X_RODAPE_VALIDOS = 570;
    private static final int RAIO_FOTO = 14;
    private static final double PERCENTUAL_URNAS_APURADAS = 98.63;
    private static final double PERCENTUAL_URNAS_SEGUNDO_TURNO = 79.41;

    private static final Color COR_CIDADE = new Color(164, 112, 42);
    private static final Color COR_TRILHA_URNAS = new Color(220, 224, 228);
    private static final Color COR_ARCO_URNAS = new Color(243, 166, 35);
    private static final Color COR_TEXTO_URNAS = new Color(40, 40, 40);
    private static final Color COR_TEXTO = new Color(30, 30, 30);
    private static final Color COR_SECUNDARIA = new Color(110, 110, 110);
    private static final Color COR_ROTULO_RODAPE = new Color(90, 90, 90);
    private static final Color COR_TAG_ELEITO = new Color(0, 230, 118);
    private static final Color COR_BORDA_TAG_ELEITO = new Color(0, 181, 190);
    private static final Color COR_TEXTO_TAG_ELEITO = new Color(0, 77, 48);
    private static final Color COR_TAG_SEGUNDO_TURNO = new Color(255, 158, 30);
    private static final Color COR_BORDA_SEGUNDO_TURNO = new Color(216, 119, 0);
    private static final Color COR_TEXTO_TAG_SEGUNDO_TURNO = new Color(74, 47, 0);
    private static final Color CINZA_LINHA = new Color(226, 226, 226);
    private static final Color COR_FOTO_VAZIA = new Color(232, 235, 238);
    private static final Color COR_BARRA_CANDIDATO = new Color(36, 153, 235);
    private static final Color COR_BARRA_ELEITO = new Color(0, 230, 118);
    private static final Color COR_BARRA_SEGUNDO_TURNO = new Color(255, 158, 30);
    private static final Color COR_BORDA_FOTO_ELEITO = new Color(0, 230, 118);
    private static final Color COR_BORDA_FOTO_SEGUNDO_TURNO = new Color(255, 158, 30);

    private static final Font FONTE_CARGO = new Font(Font.SANS_SERIF, Font.BOLD, 38);
    private static final Font FONTE_CIDADE = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    private static final Font FONTE_UF = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    private static final Font FONTE_CONTADOR = carregarFonteRoboto("Roboto-Medium.ttf", 15f, Font.BOLD);
    private static final Font FONTE_LEGENDA_CONTADOR = carregarFonteRoboto("Roboto-Medium.ttf", 18f, Font.PLAIN);
    private static final Font FONTE_NOME = carregarFonteRoboto("Roboto-Medium.ttf", 20f, Font.PLAIN);
    private static final Font FONTE_PARTIDO = carregarFonteRoboto("Roboto-Medium.ttf", 13f, Font.PLAIN);
    private static final Font FONTE_PERCENTUAL = carregarFonteRoboto("Roboto-Regular.ttf", 20f, Font.PLAIN);
    private static final Font FONTE_VOTOS = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    private static final Font FONTE_TAG = carregarFonteRoboto("Roboto-Bold.ttf", 11f, Font.BOLD);
    private static final Font FONTE_ROTULO_RODAPE = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Font FONTE_VALOR_RODAPE = new Font(Font.SANS_SERIF, Font.BOLD, 18);

    private static Font carregarFonteRoboto(String arquivo, float tamanho, int estiloFallback) {
        Path caminho = Path.of("resources", "Roboto_fonts", arquivo);
        try {
            return Font.createFont(Font.TRUETYPE_FONT, caminho.toFile()).deriveFont(tamanho);
        } catch (IOException | FontFormatException exception) {
            LOGGER.log(Level.WARNING, "Nao foi possivel carregar a fonte " + caminho, exception);
            return new Font(Font.SANS_SERIF, estiloFallback, Math.round(tamanho));
        }
    }

    private final List<RelatorioEleitoral> relatorios;
    private final PainelApuracao painel;
    private final Timer temporizador;
    private final Clip somFinalEleicao;
    private final Map<String, BufferedImage> cacheFotos = new HashMap<>();
    private int indiceRelatorio;
    private long inicioAnimacao;

    public ApuracaoEleitoral(List<RelatorioEleitoral> relatorios) {
        super("Apuração dos votos");
        this.relatorios = new ArrayList<>();
        for (RelatorioEleitoral relatorio : relatorios) {
            if (relatorio.getCargo() == Cargo.PREFEITO) {
                this.relatorios.add(relatorio);
            }
        }
        painel = new PainelApuracao();
        setContentPane(painel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(new Dimension(LARGURA_TELA, ALTURA_TELA));
        setLocationRelativeTo(null);
        somFinalEleicao = carregarSomFinal();
        temporizador = new Timer(INTERVALO_QUADRO_MS, event -> atualizarAnimacao());
        iniciarRelatorio();
        reproduzirSomFinal();
    }

    private void iniciarRelatorio() {
        indiceRelatorio = Math.min(indiceRelatorio, Math.max(0, relatorios.size() - 1));
        inicioAnimacao = System.currentTimeMillis();
        if (!temporizador.isRunning()) {
            temporizador.start();
        }
    }

    private Clip carregarSomFinal() {
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(
                Path.of("resources", "audios", "final_eleicao.wav").toFile())) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException exception) {
            LOGGER.log(Level.WARNING, "Nao foi possivel reproduzir o audio final da eleicao.", exception);
            return null;
        }
    }

    private void reproduzirSomFinal() {
        if (somFinalEleicao != null) {
            somFinalEleicao.setFramePosition(0);
            somFinalEleicao.start();
        }
    }

    private void atualizarAnimacao() {
        if (relatorios.isEmpty()) {
            temporizador.stop();
            return;
        }
        long decorrido = System.currentTimeMillis() - inicioAnimacao;
        int quantidadeLinhas = quantidadeLinhas(relatorios.get(indiceRelatorio));
        long fimDaAnimacao = duracaoDoRelatorio(quantidadeLinhas);
        painel.repaint();
        if (decorrido > fimDaAnimacao + PAUSA_FINAL_MS) {
            if (indiceRelatorio + 1 < relatorios.size()) {
                indiceRelatorio++;
                inicioAnimacao = System.currentTimeMillis();
            } else {
                temporizador.stop();
            }
        }
    }

    private int quantidadeLinhas(RelatorioEleitoral relatorio) {
        return Math.min(MAX_CANDIDATOS_EXIBIDOS, relatorio.getCandidatos().size());
    }

    private long duracaoDoRelatorio(int quantidadeLinhas) {
        long fimDasLinhas = Math.max(0, quantidadeLinhas - 1) * (long) ATRASO_ENTRE_LINHAS_MS
                + ATRASO_BARRA_MS + (long) DURACAO_BARRA_MS;
        long fimDoRodape = inicioRodapeMs(quantidadeLinhas) + (long) DURACAO_RODAPE_MS;
        return Math.max(fimDasLinhas, fimDoRodape);
    }

    private long inicioRodapeMs(int quantidadeLinhas) {
        return (long) quantidadeLinhas * ATRASO_ENTRE_LINHAS_MS + ATRASO_RODAPE_MS;
    }

    private static float progresso(long tempoMs, float duracaoMs) {
        return limitar((float) tempoMs / duracaoMs);
    }

    private static float limitar(float valor) {
        return Math.max(0f, Math.min(1f, valor));
    }

    private static float easeOutCubic(float progresso) {
        float restante = 1f - progresso;
        return 1f - restante * restante * restante;
    }

    private static float easeOutBack(float progresso) {
        final float c1 = 1.70158f;
        final float c3 = c1 + 1f;
        float restante = progresso - 1f;
        return 1f + c3 * restante * restante * restante + c1 * restante * restante;
    }

    private static double percentual(int parte, int total) {
        return total == 0 ? 0.0 : parte * 100.0 / total;
    }

    private static void desenharAlinhadoADireita(Graphics2D g, String texto, int xDireita, int y) {
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(texto, xDireita - metrics.stringWidth(texto), y);
    }

    private BufferedImage carregarFotoRelatorio(Candidato candidato) {
        String nome = Normalizer.normalize(candidato.getNome(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase().replace(" ", "_");
        String chave = nome;
        if (cacheFotos.containsKey(chave)) {
            return cacheFotos.get(chave);
        }
        Path diretorio = Path.of("resources", "fotos", "relatorio");
        BufferedImage foto = null;
        try {
            Path caminho;
            try (java.util.stream.Stream<Path> arquivos = Files.list(diretorio)) {
                caminho = arquivos
                    .filter(Files::isRegularFile)
                    .filter(arquivo -> nome.equals(nomeNormalizado(arquivo.getFileName().toString())))
                    .findFirst()
                    .orElse(null);
            }
            if (caminho != null) {
                foto = ImageIO.read(caminho.toFile());
            }
        } catch (IOException exception) {
            LOGGER.log(Level.WARNING, "Nao foi possivel localizar a foto de " + candidato.getNome(), exception);
        }
        cacheFotos.put(chave, foto);
        return foto;
    }

    private String nomeNormalizado(String nomeArquivo) {
        int extensao = nomeArquivo.lastIndexOf('.');
        String base = extensao < 0 ? nomeArquivo : nomeArquivo.substring(0, extensao);
        return Normalizer.normalize(base, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase().replace(" ", "_");
    }

    private final class PainelApuracao extends JPanel {

        private static final long serialVersionUID = 1L;

        private PainelApuracao() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (relatorios.isEmpty()) {
                return;
            }
            RelatorioEleitoral relatorio = relatorios.get(indiceRelatorio);
            List<Candidato> candidatos = new ArrayList<>(relatorio.getCandidatos());
            candidatos.sort(Comparator.comparingInt(
                    (Candidato candidato) -> relatorio.getVotosPara(candidato.getNumero())).reversed());
            int totalVotosCandidatos = candidatos.stream()
                    .mapToInt(candidato -> relatorio.getVotosPara(candidato.getNumero())).sum();
            if (candidatos.size() > MAX_CANDIDATOS_EXIBIDOS) {
                candidatos = new ArrayList<>(candidatos.subList(0, MAX_CANDIDATOS_EXIBIDOS));
            }
            boolean segundoTurno = !candidatos.isEmpty()
                    && percentual(relatorio.getVotosPara(candidatos.get(0).getNumero()),
                            totalVotosCandidatos) < PERCENTUAL_MINIMO_ELEITO;
            long decorrido = System.currentTimeMillis() - inicioAnimacao;
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                desenharCabecalho(g, relatorio, decorrido, getWidth(), segundoTurno);
                for (int indice = 0; indice < candidatos.size(); indice++) {
                    desenharLinha(g, relatorio, candidatos.get(indice), indice, decorrido,
                            totalVotosCandidatos, segundoTurno);
                }
                desenharRodape(g, relatorio, decorrido, candidatos.size());
            } finally {
                g.dispose();
            }
        }

        private void desenharCabecalho(Graphics2D g, RelatorioEleitoral relatorio, long decorrido,
                int largura, boolean segundoTurno) {
            float alpha = easeOutCubic(progresso(decorrido, DURACAO_CABECALHO_MS));
            Graphics2D c = (Graphics2D) g.create();
            try {
                c.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                c.translate(0, (1f - alpha) * -12f);

                String cargo = relatorio.getCargo().getDescricao();
                c.setColor(COR_TEXTO);
                c.setFont(FONTE_CARGO);
                c.drawString(cargo, X_CABECALHO, Y_CABECALHO);
                int x = X_CABECALHO + c.getFontMetrics().stringWidth(cargo) + 14;

                c.setColor(COR_CIDADE);
                c.setFont(FONTE_CIDADE);
                c.drawString("Salvador", x, Y_CABECALHO);
                x += c.getFontMetrics().stringWidth("Salvador") + 6;

                c.setFont(FONTE_UF);
                c.drawString("BA", x, Y_CABECALHO);

                desenharIndicadorUrnas(c, decorrido, largura, segundoTurno);
            } finally {
                c.dispose();
            }
        }

        private void desenharIndicadorUrnas(Graphics2D g, long decorrido, int largura,
                boolean segundoTurno) {
            int circulo = 88;
            int circuloX = largura - 116;
            int circuloY = 6;
            float contagem = easeOutCubic(progresso(decorrido, DURACAO_CONTADOR_URNAS_MS));
            double percentualFinal = segundoTurno
                    ? PERCENTUAL_URNAS_SEGUNDO_TURNO : PERCENTUAL_URNAS_APURADAS;
            int graus = (int) Math.round(360 * (percentualFinal / 100.0) * contagem);
            String percentual = String.format(LOCALE_BR, "%.2f%%",
                    percentualFinal * contagem);

            g.setColor(COR_TEXTO_URNAS);
            g.setFont(FONTE_LEGENDA_CONTADOR);
            String rotulo = "Urnas Apuradas";
            g.drawString(rotulo, circuloX - 18 - g.getFontMetrics().stringWidth(rotulo), 55);

            g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(COR_TRILHA_URNAS);
            g.drawOval(circuloX + 4, circuloY + 4, circulo - 8, circulo - 8);
            g.setColor(COR_ARCO_URNAS);
            g.drawArc(circuloX + 4, circuloY + 4, circulo - 8, circulo - 8, 90, -graus);

            g.setFont(FONTE_CONTADOR);
            FontMetrics metrics = g.getFontMetrics();
            int textoX = circuloX + (circulo - metrics.stringWidth(percentual)) / 2;
            int textoY = circuloY + (circulo - metrics.getHeight()) / 2 + metrics.getAscent();
            g.setColor(COR_TEXTO_URNAS);
            g.drawString(percentual, textoX, textoY);
        }

        private void desenharLinha(Graphics2D g, RelatorioEleitoral relatorio, Candidato candidato,
                int indice, long decorrido, int totalVotosCandidatos, boolean segundoTurno) {
            long tempo = decorrido - (long) indice * ATRASO_ENTRE_LINHAS_MS;
            if (tempo < 0) {
                return;
            }
            int topo = Y_PRIMEIRA_LINHA + indice * ESPACAMENTO_LINHAS;
            float entrada = easeOutCubic(progresso(tempo, DURACAO_ENTRADA_LINHA_MS));
            float contagem = easeOutCubic(progresso(tempo - ATRASO_BARRA_MS, DURACAO_BARRA_MS));
            float escalaFoto = Math.max(0.01f, easeOutBack(progresso(tempo, DURACAO_FOTO_MS)));

            int votos = relatorio.getVotosPara(candidato.getNumero());
            double percentual = percentual(votos, totalVotosCandidatos);
            String textoBadge = indice == 0 && percentual >= PERCENTUAL_MINIMO_ELEITO
                    ? TEXTO_ELEITO.toUpperCase(LOCALE_BR)
                    : segundoTurno && indice < 2 ? TEXTO_SEGUNDO_TURNO : null;
            boolean eleito = TEXTO_ELEITO.toUpperCase(LOCALE_BR).equals(textoBadge);
            boolean vaiAoSegundoTurno = TEXTO_SEGUNDO_TURNO.equals(textoBadge);
            Color corDestaque = eleito ? COR_BARRA_ELEITO
                    : vaiAoSegundoTurno ? COR_BARRA_SEGUNDO_TURNO : COR_BARRA_CANDIDATO;

            Graphics2D l = (Graphics2D) g.create();
            try {
                l.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, entrada));
                l.translate((1f - entrada) * -40f, 0);

                desenharFoto(l, candidato, X_FOTO, topo, TAMANHO_FOTO, escalaFoto,
                        eleito ? COR_BORDA_FOTO_ELEITO
                                : vaiAoSegundoTurno ? COR_BORDA_FOTO_SEGUNDO_TURNO : null);

                int trilhaY = topo + 60;
                l.setColor(CINZA_LINHA);
                l.fillRect(X_TEXTO, trilhaY, LARGURA_TRILHA, ALTURA_BARRA);
                l.setColor(corDestaque);
                int larguraBarra = (int) Math.round(LARGURA_TRILHA * (percentual / 100.0) * contagem);
                l.fillRect(X_TEXTO, trilhaY, larguraBarra, ALTURA_BARRA);

                l.setColor(COR_TEXTO);
                l.setFont(FONTE_NOME);
                l.drawString(candidato.getNome(), X_TEXTO, topo + 26);
                int larguraNome = l.getFontMetrics().stringWidth(candidato.getNome());

                if (textoBadge != null) {
                    if (TEXTO_ELEITO.toUpperCase(LOCALE_BR).equals(textoBadge)) {
                        desenharTagEleito(l, textoBadge, X_TEXTO + larguraNome + 12, topo + 8);
                    } else {
                        desenharTagSegundoTurno(l, textoBadge, X_TEXTO + larguraNome + 12, topo + 8);
                    }
                }

                l.setColor(COR_SECUNDARIA);
                l.setFont(FONTE_PARTIDO);
                l.drawString(candidato.getSiglaPartido(), X_TEXTO, topo + 46);

                l.setColor(COR_TEXTO);
                l.setFont(FONTE_PERCENTUAL);
                desenharAlinhadoADireita(l, String.format(LOCALE_BR, "%.2f%%", percentual * contagem),
                        X_VALORES_DIREITA, topo + 26);

                l.setColor(COR_SECUNDARIA);
                l.setFont(FONTE_VOTOS);
                desenharAlinhadoADireita(l, String.format(LOCALE_BR, "%,d",
                        Math.round(votos * contagem)), X_VALORES_DIREITA, topo + 46);
            } finally {
                l.dispose();
            }
        }

        private void desenharTagEleito(Graphics2D g, String texto, int x, int y) {
            g.setFont(FONTE_TAG);
            int largura = g.getFontMetrics().stringWidth(texto) + 20;
            g.setColor(COR_BORDA_TAG_ELEITO);
            g.fillRoundRect(x, y, largura, 22, 8, 8);
            g.setColor(COR_TAG_ELEITO);
            g.fillRoundRect(x + 2, y + 2, largura - 4, 18, 6, 6);
            g.setColor(COR_TEXTO_TAG_ELEITO);
            g.drawString(texto, x + 10, y + 15);
        }

        private void desenharTagSegundoTurno(Graphics2D g, String texto, int x, int y) {
            g.setFont(FONTE_TAG);
            int largura = g.getFontMetrics().stringWidth(texto) + 20;
            g.setColor(COR_BORDA_SEGUNDO_TURNO);
            g.fillRoundRect(x, y, largura, 22, 8, 8);
            g.setColor(COR_TAG_SEGUNDO_TURNO);
            g.fillRoundRect(x + 2, y + 2, largura - 4, 18, 6, 6);
            g.setColor(COR_TEXTO_TAG_SEGUNDO_TURNO);
            g.drawString(texto, x + 10, y + 15);
        }

        private void desenharFoto(Graphics2D g, Candidato candidato, int x, int y, int tamanho,
                float escala, Color corBorda) {
            Graphics2D f = (Graphics2D) g.create();
            try {
                double centroX = x + tamanho / 2.0;
                double centroY = y + tamanho / 2.0;
                f.translate(centroX, centroY);
                f.scale(escala, escala);
                f.translate(-centroX, -centroY);

                Shape forma = new RoundRectangle2D.Double(x, y, tamanho, tamanho, RAIO_FOTO, RAIO_FOTO);
                f.setColor(COR_FOTO_VAZIA);
                f.fill(forma);

                BufferedImage foto = carregarFotoRelatorio(candidato);
                if (foto != null) {
                    double escalaFoto = Math.max((double) tamanho / foto.getWidth(),
                            (double) tamanho / foto.getHeight());
                    int largura = (int) Math.round(foto.getWidth() * escalaFoto);
                    int altura = (int) Math.round(foto.getHeight() * escalaFoto);
                    Graphics2D imagem = (Graphics2D) f.create();
                    try {
                        imagem.setClip(forma);
                        imagem.drawImage(foto, x + (tamanho - largura) / 2,
                                y + (tamanho - altura) / 2, largura, altura, null);
                    } finally {
                        imagem.dispose();
                    }
                }
                if (corBorda != null) {
                    f.setColor(corBorda);
                    f.setStroke(new BasicStroke(3f));
                    f.draw(forma);
                }
            } finally {
                f.dispose();
            }
        }

        private void desenharRodape(Graphics2D g, RelatorioEleitoral relatorio, long decorrido,
                int quantidadeLinhas) {
            float alpha = easeOutCubic(progresso(decorrido - inicioRodapeMs(quantidadeLinhas),
                    DURACAO_RODAPE_MS));
            if (alpha <= 0f) {
                return;
            }
            Graphics2D r = (Graphics2D) g.create();
            try {
                r.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                r.translate(0, (1f - alpha) * 10f);

                int total = relatorio.getTotalVotes();
                int brancos = relatorio.getVotosPara(ConstantesVotacao.ESCOLHA_BRANCO);
                int nulos = relatorio.getVotosPara(ConstantesVotacao.ESCOLHA_NULO);
                int validos = total - brancos - nulos;

                desenharColunaRodape(r, X_RODAPE_BRANCOS, "Brancos", percentual(brancos, total), brancos);
                desenharColunaRodape(r, X_RODAPE_NULOS, "Nulos", percentual(nulos, total), nulos);
                desenharColunaRodape(r, X_RODAPE_VALIDOS, "Válidos", percentual(validos, total), validos);
            } finally {
                r.dispose();
            }
        }

        private void desenharColunaRodape(Graphics2D r, int x, String rotulo, double percentual,
                int quantidade) {
            r.setColor(COR_ROTULO_RODAPE);
            r.setFont(FONTE_ROTULO_RODAPE);
            r.drawString(rotulo, x, Y_RODAPE);
            r.setColor(COR_TEXTO);
            r.setFont(FONTE_VALOR_RODAPE);
            r.drawString(String.format(LOCALE_BR, "%.2f%%", percentual), x, Y_RODAPE + 24);
            r.setColor(COR_ROTULO_RODAPE);
            r.setFont(FONTE_ROTULO_RODAPE);
            r.drawString(String.format(LOCALE_BR, "%,d", quantidade), x, Y_RODAPE + 44);
        }

    }
}