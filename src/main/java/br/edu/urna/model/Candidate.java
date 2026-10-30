/*
 * Arquivo: Candidate.java
 * Representa um candidato da urna.
 */
package br.edu.urna.model;

/**
 * Candidato com número, nome, partido, cargo e foto.
 */
public class Candidate {

    private final String number;
    private final String name;
    private final String partyAcronym;
    private final Office office;
    private final String photoFileName;

    public Candidate(String number, String name, String partyAcronym, Office office, String photoFileName) {
        this.number = number;
        this.name = name;
        this.partyAcronym = partyAcronym;
        this.office = office;
        this.photoFileName = photoFileName;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getPartyAcronym() {
        return partyAcronym;
    }

    public Office getOffice() {
        return office;
    }

    public String getPhotoFileName() {
        return photoFileName;
    }
}
