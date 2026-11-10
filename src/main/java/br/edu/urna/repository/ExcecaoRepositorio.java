/*
 * Arquivo: RepositoryException.java
 * Erro de acesso ao banco de dados.
 */
package br.edu.urna.repository;

/**
 * Erro lançado quando uma operação no banco falha.
 */
public class ExcecaoRepositorio extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExcecaoRepositorio(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
