package com.example;

import java.util.List;

/*
 * ========================================
 * You should not need to modify this file.
 * ========================================
 */

/**
 * A contract for how a Crazy8's player will interact with a Crazy8's game engine.
 */
public interface PlayerStrategy {

    /**
     * Gives the player their assigned id, as well as a list of the opponents' assigned ids.
     *
     * @param playerId The id for this player
     * @param opponentIds A list of ids for this player's opponents
     */
    void init(int playerId, List<Integer> opponentIds);

    /**
     * Called at the very beginning of the game to deal the player their initial cards.
     *
     * @param cards The initial list of cards dealt to this player
     */
    void receiveInitialCards(List<Card> cards);

    /**
     * Called to check whether the player wants to draw this turn. Gives this player the top card of
     * the discard pile at the beginning of their turn, as well as an optional suit for the pile in
     * case a "8" was played, and the suit was changed.
     *
     * By having this return true, the game engine will then call receiveCard() for this player.
     * Otherwise, playCard() will be called.
     *
     * @param topPileCard The card currently at the top of the pile
     * @param pileSuit The suit that the pile was changed to as the result of an "8" being played.
     * Will be null if no "8" was played.
     * @return whether or not the player wants to draw
     */
    boolean shouldDrawCard(Card topPileCard, Card.Suit pileSuit);

    /**
     * Called when this player has chosen to draw a card from the deck.
     *
     * @param drawnCard The card that this player has drawn
     */
    void receiveCard(Card drawnCard);

    /**
     * Called when this player is ready to play a card (will not be called if this player drew on
     * their turn).
     *
     * This will end this player's turn.
     *
     * @return The card this player wishes to put on top of the pile
     */
    Card playCard();

    /**
     * Called if this player decided to play a "8" card. This player should then return the
     * Card.Suit enum that it wishes to set for the discard pile.
     */
    Card.Suit declareSuit();

    /**
     * Called at the very beginning of this player's turn to give it context of what its opponents
     * chose to do on each of their turns.
     *
     * @param opponentActions A list of what the opponents did on each of their turns
     */
    void processOpponentActions(List<PlayerTurn> opponentActions);

    /**
     * Called when the game is being reset for the next round.
     */
    void reset();
}
