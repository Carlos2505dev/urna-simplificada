/*
 * Arquivo: Candidato.java
 * Representa um candidato da urna.
 */
package br.edu.urna.model;

/**
 * Candidato com número, nome, partido, cargo e foto.
 */
public class Candidato {

    private final String numero;
    private final String nome;
    private final String siglaPartido;
    private final Cargo cargo;
    private final String nomeArquivoFoto;
    private final String nomeVice;

    public Candidato(String numero, String nome, String siglaPartido, Cargo cargo, String nomeArquivoFoto) {
        this(numero, nome, siglaPartido, cargo, nomeArquivoFoto, null);
    }

    public Candidato(String numero, String nome, String siglaPartido, Cargo cargo,
            String nomeArquivoFoto, String nomeVice) {
        this.numero = numero;
        this.nome = nome;
        this.siglaPartido = siglaPartido;
        this.cargo = cargo;
        this.nomeArquivoFoto = nomeArquivoFoto;
        this.nomeVice = nomeVice;
    }

    public String getNumero() {
        return numero;
    }

    public String getNome() {
        return nome;
    }

    public String getSiglaPartido() {
        return siglaPartido;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public String getNomeArquivoFoto() {
        return nomeArquivoFoto;
    }

    public String getNomeVice() {
        return nomeVice;
    }
}
