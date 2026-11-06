/*
 * Arquivo: CandidateCatalog.java
 * Candidatos iniciais, baseados no simulador do TSE.
 */
package br.edu.urna.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Gera os candidatos padrão gravados no Firestore na primeira execução.
 * Os candidatos a prefeito têm cadastro próprio e usam números iniciados em 101.
 */
public final class CandidateCatalog {

    private static final String[][] VEREADOR_ROWS = {
        {"91001", "Golfe Almeida", "PEsp", "golfe.png"},
        {"91002", "Beisebol Nogueira", "PEsp", "beisebol.png"},
        {"91003", "Ginástica Artística", "PEsp", "ginastica.png"},
        {"92001", "Rock", "PMus", "rock.png"},
        {"92002", "Sertanejo", "PMus", "sertanejo.png"},
        {"92003", "Música Popular Brasileira", "PMus", "mpb.png"},
        {"93001", "Astronauta Reis", "PProf", "astronauta.png"},
        {"93002", "Pintor", "PProf", "pintor.png"},
        {"93003", "Enfermeira", "PProf", "enfermeira.png"},
        {"94001", "Páscoa", "PFest", "pascoa.png"},
        {"94002", "Oktoberfest", "PFest", "oktoberfest.png"},
        {"94003", "Réveillon", "PFest", "reveillon.png"},
        {"95001", "Curupira Monteiro", "PFolc", "curupira.png"},
        {"95002", "Cabra Cabriola", "PFolc", "cabra.png"},
        {"95003", "Cuca Farias", "PFolc", "cuca.png"}
    };

    private static final String[][] MAYOR_ROWS = {
        {"91101", "Ricardo Pacheco", "PEsp", "golfe.png", "Aline Moura"},
        {"92101", "Halandinho", "PMus", "rock.png", "Bruno Saldanha"},
        {"93101", "Mariana Tavares", "PProf", "astronauta.png", "Cecília Nóbrega"},
        {"94101", "Caio Montenegro", "PFest", "pascoa.png", "Daniela Freire"},
        {"95101", "Letícia Prado", "PFolc", "curupira.png", "Eduardo Valença"},
        {"91102", "Bruno Valença", "PEsp", "beisebol.png", "Fernanda Coelho"},
        {"92102", "Helena Duarte", "PMus", "sertanejo.png", "Gustavo Peixoto"},
        {"93102", "Felipe Noronha", "PProf", "pintor.png", "Isabela Campos"},
        {"94102", "Camila Sampaio", "PFest", "oktoberfest.png", "João Varela"},
        {"95102", "Otávio Meireles", "PFolc", "cabra.png", "Larissa Neves"},
        {"91103", "André Vilar", "PEsp", "ginastica.png", "Marcelo Furtado"},
        {"92103", "Bianca Ferraz", "PMus", "mpb.png", "Natália Reis"},
        {"93103", "Rafael Bastos", "PProf", "enfermeira.png", "Otávio Rangel"},
        {"94103", "Júlia Marins", "PFest", "reveillon.png", "Patrícia Lobo"},
        {"95103", "Diego Alencar", "PFolc", "cuca.png", "Renato Paes"}
    };

    private CandidateCatalog() {
    }

    public static List<Candidate> createDefaultCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        for (int index = 0; index < VEREADOR_ROWS.length; index++) {
            String[] row = VEREADOR_ROWS[index];
            validatePartyNumber(row[0], row[2]);
            candidates.add(new Candidate(row[0], row[1], row[2], Office.VEREADOR, row[3]));
            String[] mayor = MAYOR_ROWS[index];
            validatePartyNumber(mayor[0], mayor[2]);
            candidates.add(new Candidate(mayor[0], mayor[1], mayor[2], Office.PREFEITO, mayor[3], mayor[4]));
        }
        return candidates;
    }

    private static void validatePartyNumber(String number, String partyAcronym) {
        if (!number.startsWith(partyPrefix(partyAcronym))) {
            throw new IllegalStateException("Número " + number + " incompatível com o partido " + partyAcronym);
        }
    }

    private static String partyPrefix(String partyAcronym) {
        switch (partyAcronym) {
            case "PEsp":
                return "91";
            case "PMus":
                return "92";
            case "PProf":
                return "93";
            case "PFest":
                return "94";
            case "PFolc":
                return "95";
            default:
                throw new IllegalStateException("Partido sem prefixo numérico: " + partyAcronym);
        }
    }
}
