/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.deckofcardsassignment;
import java.util.Random;

/**
 *
 * @author IDuran2026
 */

public class DeckOfCardsAssignment {

public static void main(String[] args) {
// Create a 2D String array called deckOfCards with 4 suits and 13 ranks
String[][] deck = new String[4][13];

// suit names
String[] suits = {"Diamonds", "Hearts", "Clubs", "Spades"};
// Rank names
String[] ranks = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};

// Fill the deck with cards
for (int suit = 0; suit < 4; suit++) {
    for (int rank = 0; rank < 13; rank++) {
        deck[suit][rank] = ranks[rank] + " of " + suits[suit];
    }
}

// Shuffle the deck
shuffleDeck(deck);

// Deal 4 hands of 5 cards each
System.out.println("Dealing 4 hands of 5 cards:");
System.out.println("===========================\n");

// Track dealt cards to avoid duplicates
boolean[][] dealt = new boolean[4][13];
Random rand = new Random();

// Create array for 4 hands
String[][] hands = new String[4][5];

// Deal cards to each hand
for (int card = 0; card < 5; card++) {
    for (int player = 0; player < 4; player++) {
        int suit, rank;

// Find a card that hasn't been dealt yet
   do {
        suit = rand.nextInt(4);
rank = rand.nextInt(13);
} while (dealt[suit][rank]);

// Mark as dealt and assign to hand
dealt[suit][rank] = true;
    hands[player][card] = deck[suit][rank];
    }
}

// Display each player's hand
for (int player = 0; player < 4; player++) {
    System.out.println("Player " + (player + 1) + "'s hand:");
    for (int card = 0; card < 5; card++) {
        System.out.println(" " + hands[player][card]);
    }
    System.out.println();
    }
}

// Shuffling method 
public static void shuffleDeck(String[][] deck) {
Random rand = new Random();

for (int suit = 0; suit < 4; suit++) {
    for (int i = 0; i < 13; i++) {
        int randomSuit = rand.nextInt(4);
        int randomRank = rand.nextInt(13);

// Swap cards
String temp = deck[suit][i];
    deck[suit][i] = deck[randomSuit][randomRank];
        deck[randomSuit][randomRank] = temp;
        }
        }
    }
}
