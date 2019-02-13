package com.example;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/*
 * ========================================
 * You should not need to modify this file.
 * ========================================
 */

/**
 * Represents a standard playing card from a 52 card deck.
 */
public class Card {

    enum Suit {DIAMONDS, HEARTS, SPADES, CLUBS}

    enum Rank {
        ACE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING
    }

    private Suit suit;
    private Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

    /**
     * Returns the Crazy8s point value for this card.
     *
     * @return An integer representing this card's point value
     */
    public int getPointValue() {
        // Face cards return 10 pts
        if (rank.ordinal() >= Rank.JACK.ordinal()) {
            return 10;
        }

        if (rank == Rank.EIGHT) {
            return 50;
        }

        // Otherwise, return numeric value of card
        return rank.ordinal() + 1;
    }

    /**
     * Returns a list of the standard 52 cards in an unshuffled card deck.
     *
     * @return A list representing a standard deck
     */
    public static List<Card> getDeck() {
        List<Card> cardDeck = new ArrayList<>();

        for (Suit suit : EnumSet.allOf(Suit.class)) {
            for (Rank rank : EnumSet.allOf(Rank.class)) {
                cardDeck.add(new Card(suit, rank));
            }
        }

        return cardDeck;
    }
}
