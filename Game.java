package com.mycompany.game;
import java.util.*;
import java.io.*;
/**
 *
 * @author IDuran2026
 */

public class Game {

    static Scanner input = new Scanner(System.in);
    static HashMap<Integer, Room> rooms = new HashMap<>();
    static int currentRoom = 1;

    static Player player;

    static ArrayList<Item> allItems = new ArrayList<>();
    static ArrayList<Monster> monsters = new ArrayList<>();

    static boolean lichKey = false, draculaKey = false;
    static boolean riddleSolved = false;

    public static void main(String[] args) {

        loadRooms();
        loadItems();
        loadMonsters();

        if (rooms.isEmpty()) {
            System.out.println("No rooms loaded. Check rooms.txt location.");
            return;
        }

        intro();
        gameLoop();
    }

    // ================= INTRO =================
    static void intro() {
        System.out.println(
        "You stand at the edge of a dying world.\n" +
        "Villages burn. The air reeks of ash. Shadows coil in the sky above the ancient fortress of Castle Hyrule.\n" +
        "From its broken towers, something watches... something waiting.\n\n" +
        
        "You have been chosen to enter the castle and destroy the evil within.\n" +
        "No army is coming. No reinforcements.\n" +
        "Castle Hyrule awaits.\n\n" +
        "Controls: N E S W move | F fight | E explore | I inventory\n"
        );

        player = new Player();
    }

    // ================= GAME LOOP =================
    static void gameLoop() {

        while (true) {

            Room r = rooms.get(currentRoom);

            if (r == null) {
                System.out.println("ERROR: Room missing: " + currentRoom);
                return;
            }

            System.out.println("\n=== " + r.name + " ===");
            System.out.println(r.desc);

            specialEvents();

            System.out.println("\n[N/E/S/W] move | F fight | E explore | I inventory");
            String c = input.nextLine().toUpperCase();

            if (c.equals("I")) player.inventory.show(player);
            else if (c.equals("E")) explore();
            else if (c.equals("F")) combat(randomMonster());
            else move(c);
        }
    }

    // ================= MOVE =================
    static void move(String dir) {

        Room r = rooms.get(currentRoom);

        if (!r.next.containsKey(dir)) return;

        currentRoom = r.next.get(dir);

        if (Math.random() < 0.2) {
            combat(randomMonster());
        }

        // Hidden tunnel chance from room 2
        if (currentRoom == 2 && Math.random() < 0.10) {
            System.out.println("You discover a hidden tunnel...");
            currentRoom = 20;
        }
    }

    // ================= EVENTS =================
    static void specialEvents() {

        switch (currentRoom) {

            case 7:
                riddleDoor();
                break;

            case 21:
                System.out.println("A Stone Guardian rises!");
                combat(new Guardian());

                if (player.hp > 0) {
                    player.inventory.add(findItem("Excalibur"));
                }
                break;

            case 24:
                System.out.println("The Lich appears...");
                combat(new Lich());
                lichKey = true;
                break;

            case 30:
                System.out.println("Dracula rises...");
                combat(new Dracula());
                draculaKey = true;
                break;

            case 34:
                System.out.println("Smaug awakens...");
                combat(new Smaug());
                win();
                break;
                
            case 35:
                System.out.println("The dragon's hoard collapses ADD LATER...");
                combat(new Smaug());
                win();
                break;
        }
    }

    // ================= RIDDLE =================
    static void riddleDoor() {

        if (riddleSolved) return;

        System.out.println(
        "Riddle:\n" +
        "I speak without a mouth...\n" +
        "1 Shadow\n2 Echo\n3 Fire"
        );

        String ans = input.nextLine();

        if (ans.equals("2")) {
            System.out.println("Correct.");
            riddleSolved = true;
        } else {
            System.out.println("Wrong!");
            player.hp -= 20;
        }
    }

    // ================= COMBAT =================
    static void combat(Monster m) {

        System.out.println("A " + m.name + " appears!");

        while (m.hp > 0 && player.hp > 0) {

            System.out.println("HP: " + player.hp);

            String c = input.nextLine().toUpperCase();

            if (c.equals("F")) m.hp -= 10;
            else if (c.equals("D")) continue;

            if (m.hp > 0) player.hp -= m.attack();
        }

        if (player.hp <= 0) {
            System.out.println("YOU DIED");
            System.exit(0);
        }

        System.out.println("Enemy defeated!");
    }

    static void win() {
        System.out.println("YOU WIN!");
        System.exit(0);
    }

    // ================= EXPLORE =================
    static void explore() {
        if (Math.random() < 0.5) {
            Item i = getRandomItem();
            player.inventory.add(i);
        } else {
            System.out.println("Nothing found.");
        }
    }

    static Monster randomMonster() {
        return monsters.get((int)(Math.random() * monsters.size()));
    }

    static Item getRandomItem() {
        return allItems.get((int)(Math.random() * allItems.size()));
    }

    static Item findItem(String name) {
        for (Item i : allItems)
            if (i.name.equals(name)) return i;
        return null;
    }

    // ================= LOADERS =================
    static void loadRooms() {
        try {
            Scanner sc = new Scanner(new File("src/main/rooms.txt"));
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split("\\|");

                Room r = new Room(p[1], p[2]);

                String[] d = p[3].split(",");
                String[] n = p[4].split(",");

                for (int i = 0; i < d.length; i++) {
                    r.next.put(d[i].toUpperCase(), Integer.parseInt(n[i]));
                }

                rooms.put(Integer.parseInt(p[0]), r);
            }
        } catch (Exception e) {
            System.out.println("rooms.txt missing");
        }
    }

    static void loadItems() {
        try {
            Scanner sc = new Scanner(new File("src/main/items.txt"));
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split("\\|");

                if (p[1].equals("weapon"))
                    allItems.add(new Weapon(p[0], Integer.parseInt(p[2])));
                else
                    allItems.add(new Armor(p[0], Integer.parseInt(p[2])));
            }
        } catch (Exception e) {}
    }

    static void loadMonsters() {
        try {
            Scanner sc = new Scanner(new File("src/main/monsters.txt"));
            while (sc.hasNextLine()) {
                String[] p = sc.nextLine().split("\\|");
                monsters.add(new Monster(p[0], Integer.parseInt(p[1])));
            }
        } catch (Exception e) {}
    }
}

// ================= CLASSES =================

class Player {
    int hp = 100;
    Inventory inventory = new Inventory();
}

class Inventory {
    ArrayList<Item> items = new ArrayList<>();

    void add(Item i) {
        items.add(i);
        System.out.println("Picked up " + i.name);
    }

    void show(Player p) {
        System.out.println("Inventory:");
        for (Item i : items)
            System.out.println("- " + i.name);
    }
}

class Room {
    String name, desc;
    HashMap<String, Integer> next = new HashMap<>();

    Room(String n, String d) {
        name = n;
        desc = d;
    }
}

class Item {
    String name;
    Item(String n) { name = n; }
}

class Weapon extends Item {
    int dmg;
    Weapon(String n, int d) { super(n); dmg = d; }
}

class Armor extends Item {
    int def;
    Armor(String n, int d) { super(n); def = d; }
}

class Monster {
    String name;
    int hp;

    Monster(String n, int h) {
        name = n;
        hp = h;
    }

    int attack() {
        return (int)(Math.random() * 10) + 5;
    }
}

class Guardian extends Monster {
    Guardian() { super("Stone Guardian", 120); }
}

class Lich extends Monster {
    Lich() { super("Lich", 200); }
}

class Dracula extends Monster {
    Dracula() { super("Dracula", 250); }
}

class Smaug extends Monster {
    Smaug() { super("Smaug", 400); }

    @Override
    int attack() {
        if (Math.random() < 0.3) {
            System.out.println("FIRE BREATH");
            return 30;
        }
        return 15;
    }
}

