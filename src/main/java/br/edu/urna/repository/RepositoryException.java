/*
 * Arquivo: RepositoryException.java
 * Erro de acesso ao banco de dados.
 */
package br.edu.urna.repository;

/**
 * Erro lançado quando uma operação no banco falha.
 */
public class RepositoryException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
