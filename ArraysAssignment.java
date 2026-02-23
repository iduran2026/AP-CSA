/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.arraysassignment;
import java.util.Arrays;
import java.util.Random;
/**
 * 
 *
 * @author IDuran2026
 */

public class ArraysAssignment {
    // Shuffling method to change the order randomly
    public static void shuffleArray(String[] array) {
        Random rand = new Random();
        // Go through array
        for (int i = 0; i < array.length; i++) {
            // Set random index
            int randomIndexToSwap = rand.nextInt(array.length);
            // Set a temp variable
            String temp = array[randomIndexToSwap];
            // Set the target array element
            array[randomIndexToSwap] = array[i];
            // current element to the temp variable value
            array[i] = temp;
        }
    }

    public static void main(String[] args) {
        // Create array with all 7 days of the week
        String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", 
                             "Friday", "Saturday", "Sunday"};

        System.out.println("Original week days:");
        // Print the days of the week out one day per row
        for (String day : weekDays) {
            System.out.println(day);
        }

        System.out.println(); 

        // Resize the array to 5, and copy just the weekdays to it

        String[] resizedWeekDays = new String[5];
        System.arraycopy(weekDays, 0, resizedWeekDays, 0, 5);
        System.out.println("Weekdays only (Monday-Friday):");
        // Print the days again with one day per row
        for (String day : resizedWeekDays) {
            System.out.println(day);
        }
        System.out.println();

        // Bonus below
        System.out.println("Shuffled weekdays:");
        shuffleArray(resizedWeekDays);
        // Print the shuffled days
        for (String day : resizedWeekDays) {
            System.out.println(day);
        }
        System.out.println();

        // Shuffle 7-day array
        System.out.println("Shuffled all 7 days:");
        shuffleArray(weekDays);
        // Print the shuffled days
        for (String day : weekDays) {
            System.out.println(day);
        }
    }
}
