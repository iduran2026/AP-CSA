/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.contactmanager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

/**
 *
 * @author IDuran2026
 */

public class ContactManager {
    
    private static ArrayList<Contact> contacts = new ArrayList<>();
    private static final String CONTACTS_FILE = "Contacts/contacts.csv";
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=== Contact Information Manager ===");
        
        // Load existing contacts from CSV file
        loadContactsFromFile();
        
        // Main program loop
        boolean running = true;
        while (running) {
            displayMenu();
            String choice = scanner.nextLine().trim().toLowerCase();
            
            switch (choice) {
                case "a":
                    addContact();
                    break;
                case "e":
                    listByEmail();
                    break;
                case "y":
                    listByGraduationYear();
                    break;
                case "n":
                    listByName();
                    break;
                case "q":
                    running = false;
                    System.out.println("Thank you for using Contact Manager. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please choose a valid option (a, e, y, n, or q).");
            }
        }
        
        scanner.close();
    }
    
    private static void displayMenu() {
        System.out.println("\n=== Contact Manager Menu ===");
        System.out.println("a - Add new contact");
        System.out.println("e - List contacts by email address");
        System.out.println("y - List contacts by graduation year");
        System.out.println("n - List contacts by name");
        System.out.println("q - Quit");
        System.out.print("Enter your choice: ");
    }
    
    private static void loadContactsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CONTACTS_FILE))) {
            String line;
            boolean fileEmpty = true;
            
            // Skip header line if it exists
            String header = reader.readLine();
            if (header != null && header.contains("Name")) {
                fileEmpty = false;
            } else if (header != null) {
                // If no header, process this line as data
                Contact contact = parseContactFromLine(header);
                if (contact != null) {
                    contacts.add(contact);
                    fileEmpty = false;
                }
            }
            
            // Read remaining lines
            while ((line = reader.readLine()) != null) {
                Contact contact = parseContactFromLine(line);
                if (contact != null) {
                    contacts.add(contact);
                    fileEmpty = false;
                }
            }
            
            if (!fileEmpty) {
                System.out.println("Loaded " + contacts.size() + " contacts from file.");
            } else {
                System.out.println("No existing contacts found. Starting with empty contact list.");
            }
            
        } catch (IOException e) {
            System.out.println("No existing contacts file found. Starting with empty contact list.");
        }
    }
    
    private static Contact parseContactFromLine(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 4) {
            return new Contact(parts[0].trim(), parts[1].trim(), 
                              parts[2].trim(), parts[3].trim());
        }
        return null;
    }
    
    private static void addContact() {
        System.out.println("\n=== Add New Contact ===");
        
        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Enter email address: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("Enter graduation year: ");
        String graduationYear = scanner.nextLine().trim();
        
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        // Create new contact and add to ArrayList
        Contact newContact = new Contact(name, email, graduationYear, username);
        contacts.add(newContact);
        
        // Save to file
        saveContactsToFile();
        
        System.out.println("Contact added successfully!");
    }
    
    private static void saveContactsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONTACTS_FILE))) {
            // Write header
            writer.write("Name,Email,Graduation Year,Username");
            writer.newLine();
            
            // Write all contacts
            for (Contact contact : contacts) {
                writer.write(contact.toCSVString());
                writer.newLine();
            }
            
        } catch (IOException e) {
            System.err.println("Error saving contacts to file: " + e.getMessage());
        }
    }
    
    private static void listByName() {
        if (contacts.isEmpty()) {
            System.out.println("\nNo contacts to display. Please add some contacts first.");
            return;
        }
        
        // Create a copy of the contacts list and sort by name
        ArrayList<Contact> sortedContacts = new ArrayList<>(contacts);
        sortedContacts.sort(Comparator.comparing(Contact::getName));
        
        System.out.println("\n=== Contacts Sorted by Name ===");
        for (int i = 0; i < sortedContacts.size(); i++) {
            Contact contact = sortedContacts.get(i);
            System.out.println((i + 1) + ". " + contact.getName() + 
                             " (Username: " + contact.getUsername() + ")");
        }
    }
    
    private static void listByEmail() {
        if (contacts.isEmpty()) {
            System.out.println("\nNo contacts to display. Please add some contacts first.");
            return;
        }
        
        // Create a copy of the contacts list and sort by email
        ArrayList<Contact> sortedContacts = new ArrayList<>(contacts);
        sortedContacts.sort(Comparator.comparing(Contact::getEmail));
        
        System.out.println("\n=== Contacts Sorted by Email ===");
        for (int i = 0; i < sortedContacts.size(); i++) {
            Contact contact = sortedContacts.get(i);
            System.out.println((i + 1) + ". " + contact.getEmail() + 
                             " (" + contact.getName() + ")");
        }
    }
    
    private static void listByGraduationYear() {
        if (contacts.isEmpty()) {
            System.out.println("\nNo contacts to display. Please add some contacts first.");
            return;
        }
        
        // Create a copy of the contacts list and sort by graduation year
        ArrayList<Contact> sortedContacts = new ArrayList<>(contacts);
        sortedContacts.sort(Comparator.comparing(Contact::getGraduationYear));
        
        System.out.println("\n=== All Contact Data Sorted by Graduation Year ===");
        for (int i = 0; i < sortedContacts.size(); i++) {
            Contact contact = sortedContacts.get(i);
            System.out.println("\nContact #" + (i + 1));
            System.out.println("  Name: " + contact.getName());
            System.out.println("  Email: " + contact.getEmail());
            System.out.println("  Graduation Year: " + contact.getGraduationYear());
            System.out.println("  Username: " + contact.getUsername());
            System.out.println("------------------------");
        }
    }
}

// Contact class to store individual contact information
class Contact {
    private String name;
    private String email;
    private String graduationYear;
    private String username;
    
    // Constructor
    public Contact(String name, String email, String graduationYear, String username) {
        this.name = name;
        this.email = email;
        this.graduationYear = graduationYear;
        this.username = username;
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getGraduationYear() {
        return graduationYear;
    }
    
    public String getUsername() {
        return username;
    }
    
    // Convert to CSV string for file storage
    public String toCSVString() {
        return name + "," + email + "," + graduationYear + "," + username;
    }
    
    @Override
    public String toString() {
        return "Name: " + name + ", Email: " + email + 
               ", Grad Year: " + graduationYear + ", Username: " + username;
    }
}
