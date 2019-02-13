package com.example;

import java.util.Scanner;

public class CrazyEightGame {
    /**
     * Runs 10000 simulations of crazyeight game
     * @param args
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int numberOfPlayers = 4;
        /*
        while (numberOfPlayers == 0) {
            System.out.println("Please indicate the number of players between 0 and 10.");
            int userInput = input.nextInt();
            if (userInput > 0 && userInput < 10) {
                numberOfPlayers = userInput;
            } else {
                System.out.println("Wrong.");
            }
        }
        */
        int[] wins = new int[numberOfPlayers];

        for (int i = 0; i < 10000; i++) {
            GameEngine currentGame = new GameEngine(numberOfPlayers);
            int winner = currentGame.matchWinner.playerTurn.playerId;
            System.out.println(currentGame.matchWinner);
            wins[winner] += 1;
        }
        for (int i = 0; i < wins.length; i++) {
            System.out.println("Player " + i + " won " + wins[i] + " times");
        }
    }
}
