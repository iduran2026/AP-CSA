/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.readwriteantfiles;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Scanner;
/**
 *
 * @author IDuran2026
 */

public class ReadWriteAntFiles {

    public static void main(String[] args) {
        // contacts
        String contactsFilePath = "Contacts/contacts.txt";
        
        // info 
        String name = "Isaac";
        String email = "iduran2026@cchsdons.com";
        String graduationYear = "2026";
        String username = "IDuran2026";
        
        // comma
        String contactData = name + "," + email + "," + graduationYear + "," + username;
        
        // bonus
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Contact Information Manager ===");
        System.out.println("Do you want to enter contact information manually? (yes/no): ");
        String userChoice = scanner.nextLine();
        
        if (userChoice.equalsIgnoreCase("yes")) {
            System.out.println("Enter your name: ");
            name = scanner.nextLine();
            
            System.out.println("Enter your email adress: ");
            email = scanner.nextLine();
            
            System.out.println("Enter your graduation year: ");
            graduationYear = scanner.nextLine();
            
            System.out.println("Enter your username: ");
            username = scanner.nextLine();
            
            contactData = name + "," + email + "," + graduationYear + "," + username;
            System.out.println("\nContact information saved from user input!");
        } else { 
            System.out.println("\nUsing contact information:");
            System.out.println("Name: " + name);
            System.out.println("Email: " + email);
            System.out.println("Graduation Year: " + graduationYear);
            System.out.println("Username: " + username);
        }
        
        // write contact info to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(contactsFilePath, true))) {
            // add date
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter shortFormatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.US);
            String formattedDateTime = now.format(shortFormatter);
            
            // write the with date
            writer.append("[" + formattedDateTime + "] " + contactData + "\n");
            System.out.println("\nSuccessfully wrote contact information to: " + contactsFilePath);
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
        
        // read and display all contacts from file
        System.out.println("\n=== Reading Contacts from File ===");
        try (BufferedReader reader = new BufferedReader(new FileReader(contactsFilePath))) {
            String line;
            int lineNumber = 1;
            boolean fileEmpty = true;
            
            System.out.println("\nAll contacts in the file:");
            System.out.println("------------------------");
            
            while ((line = reader.readLine()) != null) {
                fileEmpty = false;
                // show the contact information 
                if (line.contains("[")) {
                    // split date from contact 
                    int timestampEnd = line.indexOf("] ");
                    if (timestampEnd != -1) {
                        String timestamp = line.substring(0, timestampEnd + 1);
                        String contactInfo = line.substring(timestampEnd + 2);
                        String[] contactParts = contactInfo.split(",");
                        
                        System.out.println("Contact #" + lineNumber + " (" + timestamp + ")");
                        if (contactParts.length >= 4) {
                            System.out.println("  Name: " + contactParts[0]);
                            System.out.println("  Email: " + contactParts[1]);
                            System.out.println("  Graduation Year: " + contactParts[2]);
                            System.out.println("  Username: " + contactParts[3]);
                        } else {
                            System.out.println("  Raw data: " + contactInfo);
                        }
                        System.out.println("------------------------");
                    } else {
                        System.out.println("Contact #" + lineNumber + ": " + line);
                    }
                } else {
                    System.out.println("Contact #" + lineNumber + ": " + line);
                }
                lineNumber++;
            }
            
            if (fileEmpty) {
                System.out.println("The contacts file is currently empty.");
            }
            
        } catch (IOException e) {
            System.err.println("An error occurred while reading from the file: " + e.getMessage());
        }
        
        scanner.close();
    }
}