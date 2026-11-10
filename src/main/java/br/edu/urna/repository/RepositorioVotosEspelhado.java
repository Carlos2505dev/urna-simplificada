/*
 * Repositório que persiste localmente e tenta espelhar os votos em outro repositório.
 */
package br.edu.urna.repository;

import br.edu.urna.model.Cargo;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Usa o repositório local como fonte de verdade e replica votos remotamente quando possível.
 */
public class RepositorioVotosEspelhado implements RepositorioVotos {

    private static final Logger LOGGER = Logger.getLogger(RepositorioVotosEspelhado.class.getName());

    private final RepositorioVotos repositorioLocal;
    private final RepositorioVotos repositorioRemoto;

    public RepositorioVotosEspelhado(RepositorioVotos repositorioLocal, RepositorioVotos repositorioRemoto) {
        this.repositorioLocal = repositorioLocal;
        this.repositorioRemoto = repositorioRemoto;
    }

    @Override
    public void registrarVoto(String identificadorEleicao, Cargo cargo, String escolha) {
        repositorioLocal.registrarVoto(identificadorEleicao, cargo, escolha);
        try {
            repositorioRemoto.registrarVoto(identificadorEleicao, cargo, escolha);
        } catch (RuntimeException exception) {
            LOGGER.log(Level.WARNING,
                    "Voto salvo localmente, mas não foi possível enviá-lo ao Firestore.", exception);
        }
    }

    @Override
    public Map<String, Integer> contarVotos(String identificadorEleicao, Cargo cargo) {
        return repositorioLocal.contarVotos(identificadorEleicao, cargo);
    }
}