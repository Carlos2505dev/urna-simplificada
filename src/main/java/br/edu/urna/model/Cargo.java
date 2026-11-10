/*
 * Arquivo: Cargo.java
 * Cargos disputados na eleição.
 */
package br.edu.urna.model;

/**
 * Cargos em disputa. Cada eleitor vota em todos os cargos configurados, sem segundo turno.
 */
public enum Cargo {

    VEREADOR("Vereador"),
    PREFEITO("Prefeito");

    private final String descricao;

    Cargo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
