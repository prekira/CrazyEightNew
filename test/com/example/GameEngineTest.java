package com.example;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class GameEngineTest {
    GameEngine testingEngine;

    @Before
    public void setUp() {
       testingEngine = new GameEngine(4);
    }

    @Test
    public void dealToEachPlayer() {
        assertTrue(testingEngine.players.values().size() == 4);
    }

    @Test
    public void initializePlayerScores() {
        assertTrue(testingEngine.playerScores.values().size() == 4);
    }

    @Test
    public void drawTop() {
        int size = testingEngine.drawPile.size();
        drawTop();
        assertTrue(testingEngine.drawPile.size() + 1 == size);
    }

    @Test
    public void initializeNewGame() {
        assertTrue(testingEngine.drawPile.size() == 52);
    }

    @Test
    public void shuffleDeck() {
        List<Card> originalPile = testingEngine.drawPile;
        shuffleDeck();
        assertTrue(!testingEngine.drawPile.equals(originalPile));
    }
}