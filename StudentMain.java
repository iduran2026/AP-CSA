/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.studentmain;

/**
 *
 * @author IDuran2026
 */


// Main Driver Class
public class StudentMain {
    public static void main(String args[]) {
        // Test creating students
        Student st1 = new Student("Bob", "Smith");
        Student st2 = new Student("Alice", "Johnson");
        
        // Set test scores
        st1.setTestScore(1, 85);
        st1.setTestScore(2, 90);
        st1.setTestScore(3, 78);
        
        st2.setTestScore(1, 92);
        st2.setTestScore(2, 88);
        st2.setTestScore(3, 95);
        
        // Display student details
        System.out.println(st1);
        System.out.println(st2);
        
        // Get individual scores
        System.out.println("Bob's Course 1 score: " + st1.getTestScore(1));
        System.out.println("Bob's average: " + st1.getAverage());
        System.out.println("Alice's average: " + st2.getAverage());
        
        // Test empty constructor
        Student st3 = new Student();
        st3.setFirstName("Charlie");
        st3.setLastName("Brown");
        st3.setTestScore(1, 70);
        st3.setTestScore(2, 75);
        st3.setTestScore(3, 80);
        
        System.out.println("\n" + st3);
        System.out.println("Charlie's average: " + st3.getAverage());
    }
}

// Student Class
class Student {
    private String firstName, lastName;
    private Course course1, course2, course3;
    
    // Empty constructor
    public Student() {
        firstName = "";
        lastName = "";
        course1 = new Course();
        course2 = new Course();
        course3 = new Course();
    }
    
    // Constructor with name only
    public Student(String first, String last) {
        firstName = first;
        lastName = last;
        course1 = new Course();
        course2 = new Course();
        course3 = new Course();
    }
    
    // Full constructor with courses
    public Student(String first, String last, Course c1, Course c2, Course c3) {
        firstName = first;
        lastName = last;
        course1 = c1;
        course2 = c2;
        course3 = c3;
    }
    
    // Set test score for specific course (1, 2, or 3)
    public void setTestScore(int courseNumber, int score) {
        switch(courseNumber) {
            case 1:
                course1.setScore(score);
                break;
            case 2:
                course2.setScore(score);
                break;
            case 3:
                course3.setScore(score);
                break;
            default:
                System.out.println("Invalid course number. Use 1, 2, or 3.");
        }
    }
    
    // Get test score for specific course
    public int getTestScore(int courseNumber) {
        switch(courseNumber) {
            case 1:
                return course1.getScore();
            case 2:
                return course2.getScore();
            case 3:
                return course3.getScore();
            default:
                System.out.println("Invalid course number. Use 1, 2, or 3.");
                return -1;
        }
    }
    
    // Calculate average of all 3 courses
    public double getAverage() {
        double sum = course1.getScore() + course2.getScore() + course3.getScore();
        return sum / 3.0;
    }
    
    // Getters and setters
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    // Enhanced toString
    public String toString() {
        String result = "Student: " + firstName + " " + lastName + "\n";
        result += "  Course 1 Score: " + course1.getScore() + "\n";
        result += "  Course 2 Score: " + course2.getScore() + "\n";
        result += "  Course 3 Score: " + course3.getScore() + "\n";
        result += "  Average Score: " + String.format("%.2f", getAverage()) + "\n";
        return result;
    }
}

// Course Class
class Course {
    private int score;
    
    // Default constructor - score initialized to 0
    public Course() {
        score = 0;
    }
    
    // Constructor with initial score
    public Course(int initialScore) {
        score = initialScore;
    }
    
    // Set score with validation
    public void setScore(int inScore) {
        if (inScore >= 0 && inScore <= 100) {
            score = inScore;
        } else {
            System.out.println("Invalid score. Must be between 0 and 100.");
        }
    }
    
    // Get score
    public int getScore() {
        return score;
    }
    
    // toString for Course
    public String toString() {
        return "Score: " + score;
    }
}