/*
 * Arquivo: VotingConstants.java
 * Constantes globais da urna.
 */
package br.edu.urna.util;

/**
 * Constantes usadas em toda a aplicação.
 */
public interface VotingConstants {

    String PASSWORD = "99999";
    int NUMBER_LENGTH = 5;
    String BLANK_CHOICE = "BRANCO";
    String NULL_CHOICE = "NULO";
    String CANDIDATES_COLLECTION = "candidatos";
    String VOTES_COLLECTION = "votos";
    String PHOTOS_DIRECTORY = "resources/fotos/";
    String FIREBASE_KEY_ENVIRONMENT_VARIABLE = "FIREBASE_KEY_PATH";
    String DEFAULT_FIREBASE_KEY_PATH = "firebase-key.json";
}
