/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.main;

/**
 *
 * @author IDuran2026
 */

import java.util.ArrayList;
import java.util.Collections;

// UML ON SCHOOLOGY

public class Main {

    public static void main(String[] args) {

        ArrayList<Task> tasks = new ArrayList<>();

        Task t1 = new Task("Finish Java Assignment");
        t1.setPriority(1);
        t1.setComplexity(5);

        Task t2 = new Task("Study for Physics Test");
        t2.setPriority(2);
        t2.setComplexity(8);

        Task t3 = new Task("Clean Room");
        t3.setPriority(1);
        t3.setComplexity(2);

        tasks.add(t1);
        tasks.add(t2);
        tasks.add(t3);

        // Sort by priority first, then complexity
        Collections.sort(tasks);

        System.out.println("Tasks ranked by Priority, then Complexity:");
        for (Task t : tasks) {
            System.out.println(t);
        }
    }
}

//------------------------------------------------------
// Priority Interface
//------------------------------------------------------
interface Priority {
    public void setPriority(int priority);
    public int getPriority();
}

//------------------------------------------------------
// Complexity Interface (from MiniQuiz idea)
//------------------------------------------------------
interface Complexity {
    public void setComplexity(int complexity);
    public int getComplexity();
}

//------------------------------------------------------
// Task Class
//------------------------------------------------------
class Task implements Priority, Complexity, Comparable<Task> {

    private final String name;
    private int priority;
    private int complexity;

    // Constructor
    public Task(String name) {
        // used 'this' since there were errors if I didn't
        // 
        this.name = name;
        priority = 1;
        complexity = 1;
    }

    // Priority methods
    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    // Complexity methods
    @Override
    public void setComplexity(int complexity) {
        // though I don't like using 'this', I'm not sure of another way to distinguish the pairs (complexity, priority, etc)
        this.complexity = complexity;
    }

    @Override
    public int getComplexity() {
        return complexity;
    }

    // Comparable method
    @Override
    public int compareTo(Task other) {
        // had to use this for the priority as well 
        if (this.priority != other.priority) {
            return this.priority - other.priority;
        } else {
            return this.complexity - other.complexity;
        }
    }

    @Override
    public String toString() {
        return "Task: " + name +
               " | Priority: " + priority +
               " | Complexity: " + complexity;
    }
}
