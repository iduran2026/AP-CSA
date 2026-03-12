/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.pascalstriangle;
import java.util.Scanner;
/**
 *
 * @author IDuran2026
 */


public class PascalsTriangle {

    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        System.out.println("Enter the row number (N) for Pascal's Triangle:");
        int n = scan.nextInt();

        int[] row = getRow(n);

        System.out.println("Row " + n + " of Pascal's Triangle:");

        for (int num : row) {
            System.out.print(num + " ");
        }

        System.out.println();
    }

    /**
     * Recursive method that returns the Nth row of Pascal's Triangle
     */
    public static int[] getRow(int n) {

        // Base case
        if (n == 0) {
            int[] baseRow = {1};
            return baseRow;
        }

        // Recursive call to get previous row
        int[] prevRow = getRow(n - 1);

        // Create current row
        int[] currentRow = new int[n + 1];

        // First and last values are always 1
        currentRow[0] = 1;
        currentRow[n] = 1;

        // Fill middle values
        for (int i = 1; i < n; i++) {
            currentRow[i] = prevRow[i - 1] + prevRow[i];
        }

        return currentRow;
    }
}


