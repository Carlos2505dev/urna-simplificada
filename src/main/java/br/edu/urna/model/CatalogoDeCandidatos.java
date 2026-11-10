/*
 * Arquivo: CandidateCatalog.java
 * Candidatos iniciais, baseados no simulador do TSE.
 */
package br.edu.urna.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Gera os candidatos padrão gravados no Firestore na primeira execução.
 * Os candidatos a prefeito têm cadastro próprio.
 */
public final class CatalogoDeCandidatos {

    private static final String[][] DADOS_VEREADORES = {
        {"47583", "João Almeida", "Movimento Aurora Nacional", "vereadores/João_Almeida.jpg"},
        {"47147", "Carlos Nogueira", "Movimento Aurora Nacional", "vereadores/Carlos_Nogueira.jpg"},
        {"47926", "Rafael Reis", "Movimento Aurora Nacional", "vereadores/Rafael_Reis.jpg"},
        {"62184", "Lucas Martins", "Frente Popular do Horizonte", "vereadores/Lucas_Martins.jpg"},
        {"62839", "Gabriel Souza", "Frente Popular do Horizonte", "vereadores/Gabriel_Souza.jpg"},
        {"62507", "Marcelo Duarte", "Frente Popular do Horizonte", "vereadores/Marcelo_Duarte.jpg"},
        {"89316", "Ana Clara", "Aliança Cívica Brasileira", "vereadores/Ana_Clara.jpg"},
        {"89072", "Beatriz Lima", "Aliança Cívica Brasileira", "vereadores/Beatriz_Lima.jpg"},
        {"89461", "Carolina Alves", "Aliança Cívica Brasileira", "vereadores/Carolina_Alves.jpg"},
        {"34728", "Daniela Rocha", "Partido Renovador da Serra", "vereadores/Daniela_Rocha.jpg"},
        {"34905", "Fernanda Castro", "Partido Renovador da Serra", "vereadores/Fernanda_Castro.jpg"},
        {"34264", "Gabriela Moraes", "Partido Renovador da Serra", "vereadores/Gabriela_Moraes.jpg"},
        {"58741", "Juliana Monteiro", "União Democrática do Vale", "vereadores/Juliana_Monteiro.jpg"},
        {"58319", "Larissa Farias", "União Democrática do Vale", "vereadores/Larissa_Farias.jpg"},
        {"58967", "Mariana Costa", "União Democrática do Vale", "vereadores/Mariana_Costa.jpg"}
    };

    private static final String[][] DADOS_PREFEITOS = {
        {"47", "Ricardo Pacheco", "Movimento Aurora Nacional", "prefeitos/Ricardo_Pacheco.jpg", "Aline Moura"},
        {"62", "Halandindo", "Frente Popular do Horizonte", "prefeitos/Halandindo.jpg", "Bruno Saldanha"},
        {"89", "Mariana Tavares", "Aliança Cívica Brasileira", "prefeitos/Mariana_Tavares.jpg", "Cecília Nóbrega"},
        {"34", "Bruno Valença", "Partido Renovador da Serra", "prefeitos/Bruno_Valença.jpg", "Felipe Coelho"},
        {"58", "Felipe Noronha", "União Democrática do Vale", "prefeitos/Felipe_Noronha.jpg", "Henrique Campos"},
        {"73", "Camila Sampaio", "Movimento Cidadão Livre", "prefeitos/Camila_Sampaio.jpg", "João Varela"},
        {"26", "Otávio Meireles", "Partido Trabalhista da Esperança", "prefeitos/Otávio_Meireles.jpg", "Igor Neves"},
        {"41", "Rafael Bastos", "Nova Frente Social", "prefeitos/Rafael_Bastos.jpg", "Otávio Rangel"},
        {"96", "Júlia Marins", "Força Municipalista", "prefeitos/Júlia_Marins.png", "Marcos Lobo"},
    };

    private CatalogoDeCandidatos() {
    }

    public static List<Candidato> criarCandidatosPadrao() {
        List<Candidato> candidatos = new ArrayList<>();
        for (String[] linha : DADOS_VEREADORES) {
            validarNumero(linha[0], linha[2], 5);
            candidatos.add(new Candidato(linha[0], linha[1], linha[2], Cargo.VEREADOR, linha[3]));
        }
        for (String[] prefeito : DADOS_PREFEITOS) {
            validarNumero(prefeito[0], prefeito[2], 2);
            candidatos.add(new Candidato(prefeito[0], prefeito[1], prefeito[2], Cargo.PREFEITO,
                    prefeito[3], prefeito[4]));
        }
        return candidatos;
    }

    private static void validarNumero(String numero, String nomePartido, int tamanho) {
        if (numero.length() != tamanho || !numero.startsWith(prefixoPartido(nomePartido))) {
            throw new IllegalStateException("Número " + numero + " incompatível com o partido " + nomePartido);
        }
    }

    private static String prefixoPartido(String nomePartido) {
        switch (nomePartido) {
            case "Movimento Aurora Nacional":
                return "47";
            case "Frente Popular do Horizonte":
                return "62";
            case "Aliança Cívica Brasileira":
                return "89";
            case "Partido Renovador da Serra":
                return "34";
            case "União Democrática do Vale":
                return "58";
            case "Movimento Cidadão Livre":
                return "73";
            case "Partido Trabalhista da Esperança":
                return "26";
            case "Nova Frente Social":
                return "41";
            case "Força Municipalista":
                return "96";
            default:
                throw new IllegalStateException("Partido sem prefixo numérico: " + nomePartido);
        }
    }
}
