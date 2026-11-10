/*
 * Arquivo: ExcecaoRepositorio.java
 * Erro ocorrido durante o acesso ou a persistência de dados.
 */
package br.edu.urna.repository;

/**
 * Erro lançado quando uma operação de acesso ou persistência falha.
 */
public class ExcecaoRepositorio extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExcecaoRepositorio(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
