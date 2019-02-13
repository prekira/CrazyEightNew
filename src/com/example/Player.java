package com.example;

import java.util.List;

public class Player implements PlayerStrategy {
    public static final int EIGHT_CARD = 8;
    PlayerTurn playerTurn;
    List<Integer> opponentIds;
    //iffy
    List<Card> playerCards;
    List<PlayerTurn> opponentActions;
    Card.Suit suitToMatch;
    Card cardToMatch;
    Card toPlay;
    boolean canDiscardEight;
    Card currentEight;
    Card.Suit desiredSuit;
    //todo, fix opponent actions

    //Card toMatch; //include only if opponent actions should be processed here
    //Card.Suit toMatchSuit; //include only if opponent actions should be processed here
    /**
     * Gives the player their assigned id, as well as a list of the opponents' assigned ids.
     *
     * @param playerId The id for this player
     * @param opponentIds A list of ids for this player's opponents
     */
    public void init(int playerId, List<Integer> opponentIds) {
        this.playerTurn = new PlayerTurn();
        this.playerTurn.playerId = playerId;
        this.opponentIds = opponentIds;
    }

    /**
     * Called at the very beginning of the game to deal the player their initial cards.
     *
     * @param cards The initial list of cards dealt to this player
     */
    public void receiveInitialCards(List<Card> cards) {
        this.playerCards = cards;
    }

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
    public boolean shouldDrawCard(Card topPileCard, Card.Suit pileSuit) {
        //check own list if matches suit/Rank of top card, ==> discard
        Card.Suit topSuit = topPileCard.getSuit();
        Card.Rank topRank = topPileCard.getRank();
        Card toPlay = null;
        for(int i = 0; i < playerCards.size(); i++) {
            Card currentCard = playerCards.get(i);
            //both cases return false if you can discard card
            //check if topPileCard matches any cards in deck
            if ((currentCard.getSuit().equals(topSuit) || currentCard.getRank().equals(topRank)) && pileSuit != null) {
                toPlay = currentCard;
                return false;
            }
            //check if opponent declared suit is found in cards
            if (topRank.equals(Card.Rank.EIGHT) && currentCard.getSuit().equals(pileSuit)) {
                toPlay = currentCard;
                return false;
            }
            if (currentCard.getRank().equals(Card.Rank.EIGHT)) {
                this.canDiscardEight = true;
                this.currentEight = currentCard;
            }
        }
        //sets card to be played to be most recent card that applies
        this.toPlay = toPlay;
        //if cannot discard
        return true;
    }

    //public boolean shouldDiscardEight() {
        //if ()
    //}

    /**
     * Called when this player has chosen to draw a card from the deck.
     *
     * @param drawnCard The card that this player has drawn
     */
    public void receiveCard(Card drawnCard) {
        playerCards.add(drawnCard);
    }

    /**
     * Called when this player is ready to play a card (will not be called if this player drew on
     * their turn).
     *
     * This will end this player's turn.
     *
     * @return The card this player wishes to put on top of the pile
     */
    public Card playCard() {
        this.playerCards.add(0, this.toPlay);
        return this.toPlay;
    }

    /**
     * Called if this player decided to play a "8" card. This player should then return the
     * Card.Suit enum that it wishes to set for the discard pile.
     */
    public Card.Suit declareSuit() {
        this.playerTurn.declaredSuit = desiredSuit;
        return this.desiredSuit;
    }
    public Card.Suit randomSuit() {
        Card.Suit[] setOfSuits = {Card.Suit.SPADES, Card.Suit.CLUBS, Card.Suit.DIAMONDS, Card.Suit.HEARTS};
        int index = (int)(Math.random() * 4);
        return setOfSuits[index];
    }

    /**
     * Called at the very beginning of this player's turn to give it context of what its opponents
     * chose to do on each of their turns.
     *
     * @param opponentActions A list of what the opponents did on each of their turns
     */
    public void processOpponentActions(List<PlayerTurn> opponentActions) {
        this.opponentActions = opponentActions;
         //include only if opponent actions should be processed here
        Card topCard = null;
        Card.Suit declaredSuit = null;
        //see
        for (int i = 0; i < opponentActions.size(); i++) {
            if (opponentActions.get(i).playedCard != null) {
                topCard = opponentActions.get(i).playedCard;
            }
            if (opponentActions.get(i).declaredSuit != null) {
                declaredSuit = opponentActions.get(i).declaredSuit;
            }
        }
        this.suitToMatch = declaredSuit;
        this.cardToMatch = topCard;
    }

    /**
     * Called when the game is being reset for the next round.
     */
    public void reset() {
        int tempId = this.playerTurn.playerId;
        this.playerTurn = new PlayerTurn();
        init(tempId, opponentIds);
        this.desiredSuit = randomSuit();
    }
    public String toString() {
        return "Player " + this.playerTurn.playerId;
    }
}
