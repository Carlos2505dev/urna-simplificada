/*
 * Arquivo: CandidateCatalog.java
 * Candidatos iniciais, baseados no simulador do TSE.
 */
package br.edu.urna.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Gera os candidatos padrão gravados no Firestore na primeira execução.
 * Os candidatos a prefeito usam o mesmo nome e o número deslocado em 100.
 * Altere os nomes em VEREADOR_ROWS se quiser candidatos próprios.
 */
public final class CandidateCatalog {

    private static final int MAYOR_NUMBER_OFFSET = 100;

    private static final String[][] VEREADOR_ROWS = {
        {"91001", "Golfe", "PEsp", "golfe.png"},
        {"91002", "Beisebol", "PEsp", "beisebol.png"},
        {"91003", "Ginástica Artística", "PEsp", "ginastica.png"},
        {"92001", "Rock", "PMus", "rock.png"},
        {"92002", "Sertanejo", "PMus", "sertanejo.png"},
        {"92003", "Música Popular Brasileira", "PMus", "mpb.png"},
        {"93001", "Astronauta", "PProf", "astronauta.png"},
        {"93002", "Pintor", "PProf", "pintor.png"},
        {"93003", "Enfermeira", "PProf", "enfermeira.png"},
        {"94001", "Páscoa", "PFest", "pascoa.png"},
        {"94002", "Oktoberfest", "PFest", "oktoberfest.png"},
        {"94003", "Réveillon", "PFest", "reveillon.png"},
        {"95001", "Curupira", "PFolc", "curupira.png"},
        {"95002", "Cabra Cabriola", "PFolc", "cabra.png"},
        {"95003", "Cuca", "PFolc", "cuca.png"}
    };

    private CandidateCatalog() {
    }

    public static List<Candidate> createDefaultCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        for (String[] row : VEREADOR_ROWS) {
            candidates.add(new Candidate(row[0], row[1], row[2], Office.VEREADOR, row[3]));
            String mayorNumber = String.valueOf(Integer.parseInt(row[0]) + MAYOR_NUMBER_OFFSET);
            candidates.add(new Candidate(mayorNumber, row[1], row[2], Office.PREFEITO, row[3]));
        }
        return candidates;
    }
}
