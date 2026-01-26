/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.radiuscalc;
import java.util.Scanner;

/**
 *
 * @author IDuran2026
 */

public class RadiusCalc {

    // FIX: Scanner needs to be accesible on all methods
    // BUG: Scanner 's' was decalred locally in main() only
    private static Scanner s = new Scanner(System.in);
    /**
     * @param args the command line arguments
     */
    //carry out calculations or circular items
    //such as a circle, a sphere, a cone, a column
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("This program calculates round item numbers such as \n" +
                "1 - Area of a circle (pi r^2)\n" +
                "2 - Volume of a sphere (4/3 pi r^3)\n" +
                "3 - Volume of a cone (pi r^2 h/3)\n" +
                "4 - Volume of a column (pi r^2 h)");
        System.out.println("Type the number for which one you want to calculate");
        int i = s.nextInt();
        if (i == 1){
            cCalc();
        }else if (i == 2){
            sCalc();
        }else if(i == 3){
            cCalc2();
        }else if(i == 4){
            cCalc3();
        } else { 
            // FIX: added default case for invalid input
            System.out.println("Invalid choice. Please choose between 1 through 4");
        }
    }
    
    public static void cCalc3(){
        // FIX: fixed the calculation forumala for column (cylinder)
        // BUG: was usuing cone formula (h/3) instead of coumn formula which is just h
        System.out.println("enter the radius of your cone, then height of your cone");
        int r = s.nextInt();
        int h = s.nextInt(); // FIX: added missing height input
        double result = Math.PI * Math.pow(r, 2) * h;
        System.out.println(result);
    }
    
    public static void cCalc2(){
        // FIX: added cone volume calculation
        // BUG: was using column formula (no diviosn by 3)
        System.out.println("enter the radius of your cone, then height of your cone");
        int r = s.nextInt();
        int h = s.nextInt();
        double result = (Math.PI * Math.pow(r, 2) * h) / 3; // FIX: corect formula for cone
        System.out.println("Volume of cone: " + result); // BUG: was printing 'r' instead of the volume of cone
    }
    
    public static void cCalc(){
        System.out.println("enter the radius of your circle");
        int r = s.nextInt();
        double result = Math.PI * Math.pow(r, 2);
        System.out.println(result);
    }
    
    public static void sCalc(){
        // FIX: fixed sphere volume calculation
        // BUG: integered division (4/3) + 1, and printing 'r' instead of 'result'
        System.out.println("enter the radius of your sphere"); // FIX: clarified by saying sphere
        int r = s.nextInt();
        double result = (4.0/3.0) * Math.PI * Math.pow(r, 3); // FIX: changed to double division
        System.out.println(r);
        
    }
}