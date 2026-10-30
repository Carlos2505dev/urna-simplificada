/*
 * Arquivo: Office.java
 * Cargos disputados na eleição.
 */
package br.edu.urna.model;

/**
 * Cargos em disputa. Cada eleitor vota em todos os cargos, sem segundo turno.
 * Para trocar a dupla (ex.: Governador e Presidente), altere os valores abaixo.
 */
public enum Office {

    VEREADOR("Vereador"),
    PREFEITO("Prefeito");

    private final String description;

    Office(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
