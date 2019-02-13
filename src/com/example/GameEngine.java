package com.example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameEngine {
    /**
     * pile of cards to draw from
     */
    public List<Card> drawPile;
    /**
     * pile of cards that are discarded
     */
    public List<Card> discardPile;

    /**
     * map containing players and cards they are holding
     */
    public Map<Player, List<Card>> players;
    /**
     * map containing players and their scores
     */
    public Map<Player, Integer> playerScores;

    /**
     * stores last player to check for legal moves
     */
    Player lastPlayed;
    /**
     * boolean that makes sure whether or not game is over
     */
    public boolean gameEnded;
    /**
     * boolean to make sure whether or not match is ended
     */
    public boolean matchEnded;
    /**
     * boolean to make sure whether or not someone made an illegal move
     */
    public boolean cheated;
    /**
     * stores winner of game
     */
    public Player gameWinner;
    /**
     * stores winner of match
     */
    public Player matchWinner;
    /**
     * stores list of player turns for the round
     */
    public List<PlayerTurn> opponentMoves;

    /**
     * constructor initializes the current match, sets all fields to default
     * @param numPlayers number of players in the round
     */
    public GameEngine(int numPlayers) {
        //initializing game to current number of players
        this.players = new HashMap<>();
        initializePlayers(numPlayers);
        initializePlayerScores();
        initializeNewGame();
        playMatch();
    }
    /**
     * PSEUDOCODE FOR ENTIRE GAME, USED TO MAP CLASSES
     * shuffle deck
     * TODO: deal 5 to each player, place rest into draw pile
     * TODO: take top (while 8, draw again) ==> do while loop
     * TODO: while pile not empty OR player win (discard all cards)
     *  TODO: for each player, 1 new turn for player
     *         TODO: 1 turn = discard own card if (matches suit or value) at top of pile
     *         TODO: OR discard 8 card, declaring suit for next player
     *         TODO: OR draw card
     */

    /**
     * starts a game (called within match), which gets a new deck, performs initial actions, and deals to players
     */
    public void initializeNewGame() {
        this.drawPile = Card.getDeck();
        this.discardPile = new ArrayList<>();
        this.opponentMoves = new ArrayList<>();
        shuffleDeck();
        dealToEachPlayer();
        drawTop();
    }

    /**
     * plays the game while the drawpile is not empty and while no winner
     */
    public void playGame() {
        initializeNewGame();
        while(drawPile.size() != 0 && !gameEnded) {
            playRound();
        }
    }

    /**
     * plays the match while match is ongoing (no one has >200 pts) and no one has cheated
     */
    public void playMatch() {
        while (!this.matchEnded && !this.cheated) {
            playGame();
        }
    }

    /**
     * shuffles the deck
     */
    public void shuffleDeck() {
        Collections.shuffle(this.drawPile);
    }

    /**
     * initial action in game, takes out card to start game
     */
    public void drawTop() {
        Card drawn = drawPile.remove(0);
        while(drawn.getRank().equals(Card.Rank.EIGHT)) {
            drawPile.add(drawn);
            shuffleDeck();
        }
        discardPile.add(drawn);
    }
    /**
     * plays a single round, resets player turns at the beginning, makes sure to check for legality of moves and
     * updates pts of players, checks for winner
     */
    public void playRound() {
        //completes actions for one round of game
        for (Map.Entry<Player, List<Card>> entry: players.entrySet()) {
            Player currentPlayer = entry.getKey();
            currentPlayer.reset();
            playerAction(currentPlayer);
            checkLegality(currentPlayer);
            updatePoints(currentPlayer);
            checkWinner(currentPlayer);
        }
        checkTies();
    }

    /**
     * deals 5 cards to each player
     */
    public void dealToEachPlayer() {
        //5 rounds of dealing cards
        for (int j = 0; j < 5; j++) {
            //each round of dealing cards, deal 1 card to player by removing from drawPile
            for(Map.Entry<Player, List<Card>> entry: players.entrySet()) {
                entry.getValue().add(drawPile.remove(0));
            }
        }
    }

    /**
     * gives player initial fields
     * @param numPlayers number of players to be initialized
     */
    public void initializePlayers(int numPlayers) {
        for (int i = 0; i < numPlayers; i++) {
            Player currentPlayer = new Player();
            //id'ing player, and opponents
            List<Integer> idList = IntStream.range(0, numPlayers).boxed().collect(Collectors.toList());

            //initialize player fields
            idList.remove(i);
            currentPlayer.init(i, idList);

            //add player to map with empty deck of cards
            players.put(currentPlayer, new ArrayList<>());
        }
    }

    /**
     * gives players initial scores
     */
    public void initializePlayerScores() {
        this.playerScores = new HashMap<>();
        for (Map.Entry<Player, List<Card>> entry: players.entrySet()) {
            playerScores.put(entry.getKey(), 0);
        }
    }

    /**
     * available in case functionality expanded to make console game
     */
    public void printPossibleDecisions() {
        //TODO: display what actions a player can take, take user input
    }


    /**
     * makes player move according to its strategy
     * @param current player whose turn it is currently
     */
    public void playerAction(Player current) {
        List<Card> playerCards = players.get(current);
        current.playerCards = playerCards;
        Card.Suit toMatch;
        if (lastPlayed == null) {
            toMatch = null;
        } else {
            toMatch = lastPlayed.playerTurn.declaredSuit;
        }

        //if you should draw a card, draw and recieve card
        if (current.shouldDrawCard(discardPile.get(0), toMatch) && drawPile.size() != 0) {
            Card drawn = drawPile.remove(0);
            current.receiveCard(drawn);
            current.playerTurn.playedCard = current.toPlay;
            current.playerTurn.drewACard = true;

        //if you should discard a card, discard
        } else {
            Card discarded = null;

            //if possible, discard an 8
            if (current.canDiscardEight) {
                current.playerTurn.declaredSuit = current.declareSuit();
                discarded = current.currentEight;
            }
            //otherwise, draw a matching card
            else {
                discarded = current.playCard();
            }
            discardPile.add(discarded);
            playerCards.remove(discarded);
        }
        lastPlayed = current;
    }

    /**
     * checks if there are any winners
     * @param current player that has just played and possibly won
     */
    public void checkWinner(Player current) {
        //if reach 200 pts, win match
        checkWinnerMatch(current);
        //if discard all, win game
        checkWinnerGame(current);

    }

    /**
     * checks if someone won the entire match
     * @param current player in question
     */
    public void checkWinnerMatch(Player current) {
        if (playerScores.get(current) >= 200) {
            this.matchWinner = current;
            this.gameEnded = true;
            this.matchEnded = true;
        }
    }

    /**
     * checks if someone won game
     * @param current player in question
     */
    public void checkWinnerGame(Player current) {
        //if this player's deck is empty, declare them as winner and end game
        if (players.get(current).size() == 0) {
            updateWinningPlayerScore(current);
            this.gameWinner = current;
            this.gameEnded = true;
        }
    }

    /**
     * checks if players have tied
     */
    public void checkTies() {
        //https://stackoverflow.com/questions/5911174/finding-key-associated-with-max-value-in-a-java-map
        if (drawPile.size() == 0 && this.gameWinner == null) {

            //find possible winners
            List<Player> listOfWinningPlayers = new ArrayList<>();
            Player possibleWinner = Collections.max(playerScores.entrySet(),
                    Comparator.comparingInt(Map.Entry::getValue)).getKey();
            int possibleWinnerPoints = playerScores.get(possibleWinner);

            //if any other player has the maximum number of points, there is a tie
            for (Map.Entry<Player, Integer> entry: playerScores.entrySet()) {
                if (playerScores.get(entry.getKey()) == possibleWinnerPoints) {
                    listOfWinningPlayers.add(entry.getKey());
                }
            }

            //if tied, list is longer than 1 element:
            if (listOfWinningPlayers.size() > 1) {
                updateAllPlayerScores();
            //if there is only one winner
            } else {
                updateWinningPlayerScore(possibleWinner);
            }
        }
    }

    /**
     * updates players scores based on the value of cards in their hand
     */
    public void updateAllPlayerScores() {
        for (Map.Entry<Player, Integer> entry: playerScores.entrySet()) {
            updateWinningPlayerScore(entry.getKey());
        }
    }

    /**
     * if a player has won, accumulate points and deduct from others
     * @param current
     */
    public void updateWinningPlayerScore(Player current) {
        //compute sum of all other scores and add (total - self + self = total)
        int totalScore = playerScores.values().stream().mapToInt(Integer::intValue).sum();
        playerScores.replace(current, totalScore);
    }

    /**
     * update the points of current player
     * @param current
     */
    public void updatePoints(Player current) {
        //calculate current player's points based on cards they have
        List<Card> playerCards = players.get(current);
        int score = 0;
        for (Card currentCard : playerCards) {
            score += currentCard.getPointValue();
        }
        playerScores.replace(current, score);
    }

    /**
     * check if someone has cheated, if so then end the match by declaring cheated to be true
     * @param current player in question
     */
    public void checkLegality(Player current) {
        //possible illegal moves:
        //not matching suit (but if card is 8, then ok)
        if (current.toPlay != null) {
            boolean correctSuit = lastPlayed.playerTurn.declaredSuit.equals(current.toPlay.getSuit());
            boolean correctRank = lastPlayed.playerTurn.playedCard.equals(current.toPlay.getRank());
            boolean drewEight = current.toPlay.getRank().equals(Card.Rank.EIGHT);
            if (!(correctSuit || correctRank) && !drewEight) {
                this.cheated = true;
            }
        }
    }
}
