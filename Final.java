package com.mycompany.game;

import java.util.*;
import java.io.*;
import javax.sound.sampled.*;
/**
 *
 * @author IDuran2026
 */

public class Game {

    // ================= Sound System =================
    static HashMap<String, Clip> soundClips = new HashMap<>();
    static boolean soundEnabled = true;
    static float volume = 0.7f;
    
    static void loadSounds() {
        String[] soundNames = {
            "intro", "door_open", "monster_roar", "boss_defeated",
            "sword_clash", "fireball", "victory", "death"
        };
        
        for(String name : soundNames) {
            try {
                File soundFile = new File("src/main/sounds/" + name + ".wav");
                if(soundFile.exists()) {
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    soundClips.put(name, clip);
                    System.out.println("Loaded sound: " + name);
                } else {
                    System.out.println("Sound not found: " + name + ".wav (game will run without it)");
                }
            } catch(Exception e) {
                // Sound file missing or format issue - game continues without it
            }
        }
    }
    
    static void playSound(String name) {
        if(!soundEnabled) return;
        
        Clip clip = soundClips.get(name);
        if(clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    
    static void playSoundLoop(String name) {
        if(!soundEnabled) return;
        
        Clip clip = soundClips.get(name);
        if(clip != null) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    static void stopSound(String name) {
        Clip clip = soundClips.get(name);
        if(clip != null) {
            clip.stop();
        }
    }
    
    static void toggleSound() {
        soundEnabled = !soundEnabled;
        System.out.println("Sound: " + (soundEnabled ? "ON" : "OFF"));
    }

    // ================= globalls =================
    static Scanner input = new Scanner(System.in);
    
    static HashMap<Integer, Room> rooms = new HashMap<>();
    static int currentRoom = 1;
    
    static Player player = new Player();
    
    static ArrayList<Item> allItems = new ArrayList<>();
    static ArrayList<Monster> monsterPool = new ArrayList<>();
    
    // falsg 
    static boolean riddleSolved = false;
    static boolean vecnaKey = false;
    static boolean strahdKey = false;
    
    // triple T gate keeys 
    static boolean hayKey = false;
    static boolean corpseKey = false;
    static boolean storageKey = false;
    
    // boss death flags 
    static boolean blackKnightDead = false;
    static boolean guardianDead = false;
    static boolean lordSothDead = false;
    static boolean vecnaDead = false;
    static boolean strahdDead = false;
    static boolean demogorgonDead = false;
    static boolean smaugDead = false;
    static boolean tiamatDead = false;
    
    // room events triggered 
    static boolean dragonForeshadowDone = false;
    static boolean libraryFireDone = false;
    static boolean cryptFireDone = false;
    
    // EXCALIBUR pickup
    static boolean excaliburTaken = false;
    
    // TIAMAT secret boss
    static boolean dragonHeartObtained = false;
    static boolean ritualActivated = false;
    
    // Explore Chances (30% nothing, 50% potion, 20% weapon/armor)
    static final double EXPLORE_FIND_CHANCE = 0.70;
    static final double EXPLORE_POTION_CHANCE = 0.50;
    static final double EXPLORE_EQUIP_CHANCE = 0.20;
    
    // hints
    static int hintsUsed = 0;
    static final int MAX_FREE_HINTS = 3;
    static int playerGold = 0;
    
    // ================= main stuff =================
    public static void main(String[] args) {
        loadRooms();
        loadItems();
        loadMonsters();
        loadSounds();
        
        intro();
        gameLoop();
    }
    
    // ================= intro =================
    static void intro() {
        playSound("intro");
        
        System.out.println("==========================================");
        System.out.println("       CASTLE HYRULE: DRAGON'S FALL       ");
        System.out.println("==========================================");
        System.out.println();
        System.out.println("The world behind you burns.");
        System.out.println("Smoke rises from a hundred fallen kingdoms.");
        System.out.println("Before you stands Castle Hyrule — once a beacon of hope,");
        System.out.println("now a bastion of darkness and flame.");
        System.out.println();
        System.out.println("You are the last warrior brave enough to enter.");
        System.out.println("If you fail, the darkness consumes everything.");
        System.out.println();
        
        System.out.println("Choose your class:");
        System.out.println("1 Fighter (Tank - 150 HP, 25 MP)");
        System.out.println("2 Wizard (High Damage - 110 HP, 55 MP)");
        System.out.println("3 Rogue (Crit + Dodge - 135 HP, 35 MP)");
        System.out.print("> ");
        
        String c = input.nextLine();
        
        if(c.equals("1")) {
            player.playerClass = "Fighter";
            player.hp = 150;
            player.maxHp = 150;
            player.mp = 25;
            player.maxMp = 25;
        }
        else if(c.equals("2")) {
            player.playerClass = "Wizard";
            player.hp = 110;
            player.maxHp = 110;
            player.mp = 55;
            player.maxMp = 55;
        }
        else {
            player.playerClass = "Rogue";
            player.hp = 135;
            player.maxHp = 135;
            player.mp = 35;
            player.maxMp = 35;
        }
        
        System.out.println("\nYou are a " + player.playerClass + ".");
        System.out.println("HP: " + player.hp + " | MP: " + player.mp);
        System.out.println("\nThe Castle Gates loom before you...");
        System.out.println("Type H for help, ? for hints, M for sound, or start exploring!");
    }
    
    // ================= game loop =================
    static void gameLoop() {
        while(true) {
            Room r = rooms.get(currentRoom);
            
            if(r == null) {
                System.out.println("Room error. Ending game safely.");
                return;
            }
            
            System.out.println("\n==============================");
            System.out.println("== " + r.name + " ==");
            System.out.println("==============================");
            System.out.println(r.desc);
            
            // Apply passive regen each turn
            player.applyPassiveRegen();
            
            // trigger room events
            specialEvents();
            
            // Check if player died from room event
            if(player.hp <= 0) {
                die();
                return;
            }
            
            // controls
            System.out.println("\n" + r.getAvailableDirections());
            System.out.print("> ");
            
            String cmd = input.nextLine().toUpperCase();
            
            if(cmd.equals("I")) player.inventory.show(player);
            else if(cmd.equals("X")) explore();
            else if(cmd.equals("T")) fightRandom();
            else if(cmd.equals("H")) showHelp();
            else if(cmd.equals("?")) giveHint();
            else if(cmd.equals("M")) toggleSound();
            else move(cmd);
        }
    }
    
    // ================= Help Menu =================
    static void showHelp() {
        System.out.println("\n=== COMMANDS ===");
        System.out.println("[F]orward / [B]ack / [L]eft / [R]ight - Move");
        System.out.println("X - Explore the area for items");
        System.out.println("T - Hunt for monsters to fight");
        System.out.println("I - Open inventory (use/equip items)");
        System.out.println("H - Show this help menu");
        System.out.println("? - Get a hint (free hints: " + (MAX_FREE_HINTS - hintsUsed) + " remaining, then costs 10 HP)");
        System.out.println("M - Toggle sound (currently: " + (soundEnabled ? "ON" : "OFF") + ")");
        System.out.println();
        System.out.println("In combat:");
        System.out.println("  1 - Attack     2 - Ability     3 - Dodge");
        System.out.println("  4 - Use Potion 5 - Flee (non-boss only)");
        System.out.println();
        System.out.println("Gold: " + playerGold + " | Level: " + player.level);
    }
    
    // ================= Hinys System =================
    static void giveHint() {
        hintsUsed++;
        
        if(hintsUsed > MAX_FREE_HINTS) {
            System.out.println("\n*** The spirits demand a sacrifice for guidance... ***");
            System.out.println("You feel a sharp pain as 10 HP is drained from you!");
            player.hp -= 10;
            if(player.hp <= 0) {
                die();
                return;
            }
        } else {
            System.out.println("\n*** A whisper of guidance reaches your ears... ***");
        }
        
        System.out.println("=== HINT (" + hintsUsed + ") ===");
        
        if(currentRoom == 7 && !riddleSolved) {
            System.out.println("The riddle speaks of something that returns when called.");
            System.out.println("Shout into a canyon, and what answers back?");
        }
        else if(currentRoom == 16 && !(hayKey && corpseKey && storageKey)) {
            System.out.println("Three keys seal the Triple Gate:");
            System.out.println("- Search where food is stored (Hay Storage)");
            System.out.println("- Search where supplies are kept (Storage Vault)");
            System.out.println("- Search where death was delivered (Execution Chamber)");
        }
        else if(smaugDead && dragonHeartObtained && !ritualActivated) {
            System.out.println("The Dragon Heart pulses with ancient power...");
            System.out.println("Legends speak of a Ritual Chamber deep in the castle");
            System.out.println("where dragon essence can be awakened into something greater.");
        }
        else if(currentRoom == 35 && smaugDead && dragonHeartObtained && !ritualActivated) {
            System.out.println("The Hoard of the Dragon contains a secret passage.");
            System.out.println("Search beyond the treasure... the Ritual Chamber awaits.");
        }
        else if(player.hp < 30) {
            System.out.println("You're badly wounded! Use potions from your inventory (press I).");
            System.out.println("Or explore (X) to find more healing items.");
        }
        else if(monsterPool.size() < 3 && !smaugDead) {
            System.out.println("The castle feels empty. Monsters respawn as you move");
            System.out.println("through different areas. Keep exploring!");
        }
        else if(!guardianDead && currentRoom == 2) {
            System.out.println("The ground here feels unstable...");
            System.out.println("Perhaps exploring the Castle Gates more carefully");
            System.out.println("could reveal a hidden path downward.");
        }
        else if(!guardianDead && currentRoom >= 15 && !excaliburTaken) {
            System.out.println("Legends tell of Excalibur, a sword sealed in crystal.");
            System.out.println("It lies hidden beneath the castle, guarded by stone.");
            System.out.println("Try returning to the Castle Gates and searching carefully.");
        }
        else {
            System.out.println("Explore every room. Press X to search for items.");
            System.out.println("Defeat bosses to unlock gates and gain powerful gear.");
            System.out.println("Check your inventory (I) to equip better weapons and armor.");
        }
        System.out.println();
    }
    
    // ================= helpers =================
    static void explore() {
        System.out.println("\nYou search the area carefully...");
        
        if(Math.random() > EXPLORE_FIND_CHANCE) {
            System.out.println("You found nothing of interest.");
            return;
        }
        
        double roll = Math.random();
        
        if(roll < EXPLORE_POTION_CHANCE) {
            if(Math.random() < 0.6) {
                player.inventory.add(new HealthPotion());
                System.out.println("You found a Health Potion!");
            } else {
                player.inventory.add(new ManaPotion());
                System.out.println("You found a Mana Potion!");
            }
        }
        else if(roll < EXPLORE_POTION_CHANCE + EXPLORE_EQUIP_CHANCE) {
            double equipRoll = Math.random();
            if(equipRoll < 0.5) {
                String[] basicWeapons = {"Rusty Sword", "Iron Sword", "Silver Longsword", "Shadow Dagger", "War Hammer", "Frostreaver"};
                String chosen = basicWeapons[(int)(Math.random() * basicWeapons.length)];
                Item found = findItem(chosen);
                if(found != null) {
                    player.inventory.add(found);
                    System.out.println("You found: " + found.name + " (Damage: " + ((Weapon)found).damage + ")!");
                }
            } else {
                String[] basicArmors = {"Leather Armor", "Chain Armor", "Shadow Cloak", "Plate Armor"};
                String chosen = basicArmors[(int)(Math.random() * basicArmors.length)];
                Item found = findItem(chosen);
                if(found != null) {
                    player.inventory.add(found);
                    System.out.println("You found: " + found.name + " (Defense: " + ((Armor)found).reduction + ")!");
                }
            }
        }
        else {
            if(Math.random() < 0.7) {
                if(Math.random() < 0.5) {
                    player.inventory.add(new GreaterHealthPotion());
                    System.out.println("You found a Greater Health Potion!");
                } else {
                    player.inventory.add(new GreaterManaPotion());
                    System.out.println("You found a Greater Mana Potion!");
                }
            } else {
                player.inventory.add(new HolyElixir());
                System.out.println("You found a Holy Elixir!");
            }
        }
    }
    
    static void bossDrop(String name) {
        playSound("boss_defeated");
        
        System.out.println("\n==========================================");
        System.out.println("   " + name.toUpperCase() + " HAS BEEN DEFEATED!   ");
        System.out.println("==========================================");
        
        if(name.equals("Black Knight")) {
            if(Math.random() < 0.6) {
                System.out.println("\nThe Black Knight's blade clatters to the stone!");
                System.out.println(">>> Knight Blade (Damage: 16) <<<");
                player.inventory.add(findItem("Knight Blade"));
            }
            if(Math.random() < 0.5) {
                System.out.println("\nHis blackened armor plates fall loose!");
                System.out.println(">>> Knight's Plate (Defense: 11) <<<");
                player.inventory.add(findItem("Knight's Plate"));
            }
            player.inventory.add(new HealthPotion());
            playerGold += 50;
            System.out.println("Gold found: 50 (Total: " + playerGold + ")");
            blackKnightDead = true;
        } 
        else if(name.equals("Stone Guardian")) {
            if(Math.random() < 0.5) {
                System.out.println("\nThe Guardian's core solidifies into a massive shield!");
                System.out.println(">>> Stone Aegis (Defense: 13) <<<");
                player.inventory.add(findItem("Stone Aegis"));
            }
            player.inventory.add(new HealthPotion());
            player.inventory.add(new HealthPotion());
            playerGold += 75;
            System.out.println("Gold found: 75 (Total: " + playerGold + ")");
            guardianDead = true;
        }
        else if(name.equals("Lord Soth")) {
            if(Math.random() < 0.5) {
                System.out.println("\nThe death knight's flaming sword remains burning!");
                System.out.println(">>> Flamebrand (Damage: 24) <<<");
                player.inventory.add(findItem("Flamebrand"));
            }
            if(Math.random() < 0.4) {
                System.out.println("\nHis shadowy cloak detaches and floats down!");
                System.out.println(">>> Shadow Cloak (Defense: 7) <<<");
                player.inventory.add(findItem("Shadow Cloak"));
            }
            player.inventory.add(new ManaPotion());
            playerGold += 100;
            System.out.println("Gold found: 100 (Total: " + playerGold + ")");
            lordSothDead = true;
        }
        else if(name.equals("Vecna")) {
            if(Math.random() < 0.5) {
                System.out.println("\nThe Archlich's mummified hand detaches, crackling with power!");
                System.out.println(">>> Hand of Vecna (Damage: 28) <<<");
                System.out.println("    (Lifesteals 5 HP on hit!)");
                player.inventory.add(new Weapon("Hand of Vecna", 28));
            }
            player.inventory.add(new GreaterHealthPotion());
            player.inventory.add(new GreaterManaPotion());
            playerGold += 200;
            System.out.println("Gold found: 200 (Total: " + playerGold + ")");
            vecnaDead = true;
        }
        else if(name.equals("Count Strahd")) {
            if(Math.random() < 0.5) {
                System.out.println("\nStrahd's ancient dragon-scale armor remains intact!");
                System.out.println(">>> Dragon Scale Armor (Defense: 15) <<<");
                player.inventory.add(findItem("Dragon Scale Armor"));
            }
            player.inventory.add(new HolyElixir());
            playerGold += 250;
            System.out.println("Gold found: 250 (Total: " + playerGold + ")");
            strahdDead = true;
        }
        else if(name.equals("Demogorgon")) {
            if(Math.random() < 0.5) {
                System.out.println("\nThe demon's twin whips crackle with abyssal energy!");
                System.out.println(">>> Demogorgon's Twin Whips (Damage: 35) <<<");
                player.inventory.add(findItem("Demogorgon's Twin Whips"));
            }
            player.inventory.add(new GreaterHealthPotion());
            player.inventory.add(new GreaterManaPotion());
            playerGold += 300;
            System.out.println("Gold found: 300 (Total: " + playerGold + ")");
            demogorgonDead = true;
        }
        else if(name.equals("Smaug")) {
            if(Math.random() < 0.5) {
                System.out.println("\nSmaug's scales gleam with unbreakable dragon power!");
                System.out.println(">>> Smaug's Scalemail (Defense: 20) <<<");
                player.inventory.add(findItem("Smaug's Scalemail"));
            }
            System.out.println("\n*** A pulsing Dragon Heart floats from Smaug's chest! ***");
            System.out.println("*** Use this in the Ritual Chamber for a secret... ***");
            dragonHeartObtained = true;
            player.inventory.add(new HolyElixir());
            player.inventory.add(new HolyElixir());
            playerGold += 500;
            System.out.println("Gold found: 500 (Total: " + playerGold + ")");
            smaugDead = true;
        }
        else if(name.equals("Tiamat")) {
            System.out.println("\nTiamat's five heads dissolve into elemental energy!");
            System.out.println("The Dragon Queen is no more. Her essence scatters");
            System.out.println("across the planes, never to reform.");
            System.out.println("\nNo items drop from a god's corpse.");
            System.out.println("Victory itself is the only reward.");
            playerGold += 1000;
            System.out.println("Gold found: 1000 (Total: " + playerGold + ")");
            tiamatDead = true;
            trueEnding();
        }
    }
    
    static void riddleDoor() {
        if(riddleSolved) {
            System.out.println("The stone door stands open, its crystal eye dark.");
            return;
        }
        
        System.out.println("\nA massive stone door with a glowing crystal eye blocks your path.");
        System.out.println("A spectral voice echoes through the chamber:");
        System.out.println();
        System.out.println("\"I speak without a mouth and hear without ears.");
        System.out.println("I have no body, but I come alive with wind.");
        System.out.println("Born of a call, I die in silence.");
        System.out.println("What am I?\"");
        System.out.println();
        System.out.print("Your answer: ");
        
        String answer = input.nextLine().toLowerCase().trim();
        
        if(answer.contains("echo")) {
            playSound("door_open");
            System.out.println();
            System.out.println("The crystal eye glows brilliant green!");
            System.out.println("A deep grinding sound fills the chamber as the");
            System.out.println("stone door slowly slides open, revealing the path forward.");
            riddleSolved = true;
        } else {
            System.out.println();
            System.out.println("The eye flares blood-red! WRONG ANSWER!");
            System.out.println("A bolt of arcane energy strikes you!");
            player.hp -= 10;
            System.out.println("(-10 HP | HP: " + player.hp + "/" + player.maxHp + ")");
            if(player.hp <= 0) die();
        }
    }
    
    static void fightRandom() {
        if(monsterPool.isEmpty()) {
            System.out.println("\nThe area is quiet... no monsters nearby.");
            System.out.println("(Monsters repopulate as you explore new areas.)");
            return;
        }
        
        int index = (int)(Math.random() * monsterPool.size());
        Monster m = monsterPool.get(index);
        monsterPool.remove(index);
        
        System.out.println("\nA wild " + m.name + " appears from the shadows!");
        playSound("monster_roar");
        combat(m, false);
    }
    
    // ================= MOVEMENT =================
    static void move(String dir) {
        Room r = rooms.get(currentRoom);
        
        if(r == null) return;
        
        if(!r.next.containsKey(dir)) {
            System.out.println("You can't go that way.");
            return;
        }
        
        if(currentRoom == 16 && dir.equals("F") && !(hayKey && corpseKey && storageKey)) {
            System.out.println("\nThree locks remain sealed. You need all three keys.");
            System.out.println("(Keys found: " + (hayKey ? "[Golden Key] " : "") + (corpseKey ? "[Iron Key] " : "") + (storageKey ? "[Silver Key] " : "") + ")");
            return;
        }
        
        if(currentRoom == 27 && dir.equals("F") && !vecnaKey) {
            System.out.println("\nArcane energy crackles violently, blocking your path.");
            System.out.println("The Archlich Vecna's magic must be undone first.");
            return;
        }
        
        if(currentRoom == 32 && dir.equals("F") && !strahdKey) {
            System.out.println("\nA crimson barrier of blood magic pulses before you.");
            System.out.println("Count Strahd's power still holds this gate.");
            return;
        }
        
        if(currentRoom == 7 && dir.equals("F") && !riddleSolved) {
            System.out.println("\nThe massive stone door won't budge. The crystal eye stares at you, waiting.");
            return;
        }
        
        if(currentRoom == 34 && (dir.equals("L") || dir.equals("R")) && !smaugDead) {
            System.out.println("\nA massive dragon blocks the way deeper into the lair.");
            System.out.println("You must defeat Smaug first.");
            return;
        }
        
        if(currentRoom == 36 && dir.equals("F") && !dragonHeartObtained) {
            System.out.println("\nThe altar pulses with dark energy, but something is missing.");
            System.out.println("You need a powerful draconic artifact to activate this ritual.");
            return;
        }
        
        currentRoom = r.next.get(dir);
        
        if(currentRoom == 2 && Math.random() < 0.10 && guardianDead) {
            System.out.println("\n*** The ground collapses beneath you! ***");
            System.out.println("You tumble down into a hidden stone passage...");
            currentRoom = 20;
        }
        
        if(monsterPool.size() < 5 && Math.random() < 0.3) {
            repopulateMonsters();
        }
    }
    
    static void repopulateMonsters() {
        String[] types = {"Goblin", "Skeleton", "Orc", "Vampire", "Wraith", "Troll", "Death Knight"};
        String chosen = types[(int)(Math.random() * types.length)];
        
        switch(chosen) {
            case "Goblin": monsterPool.add(new Goblin()); break;
            case "Skeleton": monsterPool.add(new Skeleton()); break;
            case "Orc": monsterPool.add(new Orc()); break;
            case "Vampire": monsterPool.add(new Vampire()); break;
            case "Wraith": monsterPool.add(new Wraith()); break;
            case "Troll": monsterPool.add(new Troll()); break;
            case "Death Knight": monsterPool.add(new DeathKnight()); break;
        }
    }
   
    // ================= Room Events =================
    static void specialEvents() {
        switch(currentRoom) {
            case 3:
                if(!blackKnightDead) {
                    playSound("monster_roar");
                    System.out.println("\n==============================");
                    System.out.println("The wind dies. The bridge falls silent.");
                    System.out.println("A massive figure in blackened steel armor");
                    System.out.println("crashes down onto the stone before you.");
                    System.out.println("\"None shall pass while I still stand.\"");
                    System.out.println("The Black Knight raises his enormous blade.");
                    System.out.println("==============================");
                    System.out.print("Press Enter to fight...");
                    input.nextLine();
                    combat(new BlackKnight(), true);
                }
                break;
            
            case 7:
                if(!riddleSolved) {
                    riddleDoor();
                }
                break;
            
            case 5:
                if(!hayKey) {
                    System.out.println("\nYou dig through the moldy hay and find a gleaming golden key!");
                    hayKey = true;
                }
                break;
            
            case 11:
                if(!storageKey) {
                    System.out.println("\nYou search the vault and find a silver key among the supplies!");
                    storageKey = true;
                    player.inventory.add(new HealthPotion());
                    System.out.println("You also find a Health Potion!");
                }
                break;
            
            case 14:
                if(!corpseKey) {
                    System.out.println("\nYou pry the iron key from the corpse's cold, stiff fingers.");
                    corpseKey = true;
                }
                break;
            
            case 20:
                if(!guardianDead) {
                    playSound("monster_roar");
                    System.out.println("\n==============================");
                    System.out.println("The tunnel opens into a vast underground chamber.");
                    System.out.println("A towering construct of living stone stirs,");
                    System.out.println("its rune-carved body grinding as it awakens.");
                    System.out.println("\"WORTHY OR NOT, ALL WHO ENTER ARE JUDGED.\"");
                    System.out.println("The Stone Guardian raises fists of granite.");
                    System.out.println("==============================");
                    System.out.print("Press Enter to fight...");
                    input.nextLine();
                    combat(new Guardian(), true);
                }
                break;
            
            case 21:
                if(guardianDead && !excaliburTaken) {
                    System.out.println("\n==============================");
                    System.out.println("With the Guardian defeated, the crystal pedestal");
                    System.out.println("glows with a warm, inviting light.");
                    System.out.println("The legendary sword Excalibur is sealed within.");
                    System.out.println("You grasp the hilt... the stone releases its grip.");
                    System.out.println("==============================");
                    System.out.println("\n=== YOU HAVE OBTAINED EXCALIBUR! ===");
                    System.out.println("Damage: 50 | The blade hums with divine power.");
                    player.inventory.add(findItem("Excalibur"));
                    excaliburTaken = true;
                }
                break;
            
            case 18:
                if(!lordSothDead) {
                    playSound("monster_roar");
                    System.out.println("\n==============================");
                    System.out.println("The torches flicker and die, plunging the hall into darkness.");
                    System.out.println("Hoofbeats echo from nowhere and everywhere at once.");
                    System.out.println("A towering death knight on a spectral steed emerges,");
                    System.out.println("his armor blackened by centuries of hellfire.");
                    System.out.println("\"I AM LORD SOTH. YOUR SOUL IS FORFEIT.\"");
                    System.out.println("Flames erupt along his cursed blade.");
                    System.out.println("==============================");
                    System.out.print("Press Enter to fight...");
                    input.nextLine();
                    combat(new LordSoth(), true);
                }
                break;
            
            case 19:
                if(!dragonForeshadowDone) {
                    System.out.println("\nA massive shadow sweeps across the shattered ceiling.");
                    System.out.println("Whatever casts it is enormous — far larger than");
                    System.out.println("anything you've faced. The ground trembles.");
                    System.out.println("Somewhere above, Smaug circles... waiting.");
                    dragonForeshadowDone = true;
                }
                break;
            
            case 23:
                if(!libraryFireDone) {
                    System.out.println("\n==============================");
                    System.out.println("Without warning, dragon fire crashes through the window!");
                    System.out.println("Flames engulf the nearby shelves. You're caught in the blast!");
                    player.hp -= 15;
                    System.out.println("(-15 HP | HP: " + player.hp + "/" + player.maxHp + ")");
                    System.out.println("==============================");
                    if(player.hp <= 0) die();
                    libraryFireDone = true;
                }
                break;
            
            case 28:
                if(!cryptFireDone) {
                    System.out.println("\n==============================");
                    System.out.println("Ancient fire traps erupt from the walls!");
                    System.out.println("Magical flames sear your flesh!");
                    player.hp -= 15;
                    System.out.println("(-15 HP | HP: " + player.hp + "/" + player.maxHp + ")");
                    System.out.println("==============================");
                    if(player.hp <= 0) die();
                    cryptFireDone = true;
                }
                break;
            
            case 26:
                if(!vecnaDead) {
                    playSound("monster_roar");
                    System.out.println("\n==============================");
                    System.out.println("Frost crawls up the stone walls in spiraling patterns.");
                    System.out.println("A skeletal figure rises from a throne of frozen souls,");
                    System.out.println("a crown of ice and shadow hovering above its skull.");
                    System.out.println("One hand is missing, replaced by a crackling void.");
                    System.out.println();
                    System.out.println("\"YOU DARE DISTURB THE ARCHLICH VECNA?\"");
                    System.out.println("\"I HAVE UNRAVELED THE SECRETS OF DEATH ITSELF.\"");
                    System.out.println("\"LET ME SHARE THEM WITH YOU... ETERNALLY.\"");
                    System.out.println("Vecna extends his remaining hand, dark magic swirling.");
                    System.out.println("==============================");
                    System.out.print("Press Enter to fight...");
                    input.nextLine();
                    combat(new Vecna(), true);
                    vecnaKey = true;
                }
                break;
            
            case 31:
                if(!strahdDead) {
                    playSound("monster_roar");
                    System.out.println("\n==============================");
                    System.out.println("A noble figure sits upon a throne of obsidian and bone.");
                    System.out.println("His eyes burn like dying suns, ancient and terrible.");
                    System.out.println("He rises gracefully, a goblet of crimson liquid in hand.");
                    System.out.println();
                    System.out.println("\"I am Count Strahd von Zarovich.\"");
                    System.out.println("\"I have ruled this land for centuries beyond count.\"");
                    System.out.println("\"You are... a welcomed diversion from eternity.\"");
                    System.out.println("Strahd sets down his goblet and smiles — a predator's smile.");
                    System.out.println("==============================");
                    System.out.print("Press Enter to fight...");
                    input.nextLine();
                    combat(new CountStrahd(), true);
                    strahdKey = true;
                }
                break;
            
            case 33:
                if(!demogorgonDead) {
                    playSound("monster_roar");
                    System.out.println("\n==============================");
                    System.out.println("The lava below churns violently, sending gouts of molten");
                    System.out.println("rock splashing against the narrow stone bridge.");
                    System.out.println("From the inferno rises a towering demon with two");
                    System.out.println("slavering baboon heads and twin tentacle arms.");
                    System.out.println();
                    System.out.println("\"I AM THE DEMOGORGON! PRINCE OF DEMONS!\"");
                    System.out.println("\"YOUR MIND AND BODY SHALL BREAK BEFORE ME!\"");
                    System.out.println("Both heads fix their insane gaze upon you.");
                    System.out.println("==============================");
                    System.out.print("Press Enter to fight...");
                    input.nextLine();
                    combat(new Demogorgon(), true);
                }
                break;
            
            case 34:
                if(!smaugDead) {
                    playSound("dragon_roar");
                    System.out.println("\n==============================");
                    System.out.println("Mountains of gold coins and jeweled treasures");
                    System.out.println("stretch as far as the eye can see.");
                    System.out.println("The gold shifts... something enormous uncoils beneath it.");
                    System.out.println("Two eyes like furnaces snap open in the darkness.");
                    System.out.println();
                    System.out.println("\"WELL, THIEF. I SMELL YOU.");
                    System.out.println("I HEAR YOUR BREATH. I FEEL YOUR AIR.");
                    System.out.println("WHERE IS YOUR TREASURE NOW?\"");
                    System.out.println("Smaug the Terrible rises from his hoard, fire brewing");
                    System.out.println("deep in his furnace-like chest.");
                    System.out.println("==============================");
                    System.out.print("Press Enter to fight...");
                    input.nextLine();
                    combat(new Smaug(), true);
                }
                else if(smaugDead && !dragonHeartObtained) {
                    System.out.println("\nThe great dragon's corpse lies sprawled across the gold.");
                    System.out.println("You already searched the body and found nothing more.");
                    System.out.println("Perhaps there are other paths to explore...");
                }
                else if(smaugDead && dragonHeartObtained && !ritualActivated) {
                    System.out.println("\nSmaug's corpse remains, but the Dragon Heart is yours.");
                    System.out.println("The deeper passages of the lair beckon...");
                }
                break;
            
            case 35:
                if(smaugDead && !ritualActivated) {
                    System.out.println("\nThe dragon's treasure chamber stretches before you.");
                    System.out.println("Gold and jewels glitter in the dim light.");
                    if(dragonHeartObtained) {
                        System.out.println("The Dragon Heart pulses warmly in your possession.");
                        System.out.println("You sense a dark presence deeper within...");
                    }
                }
                else if(ritualActivated) {
                    System.out.println("\nThe treasure chamber lies quiet. The ritual has been completed.");
                    System.out.println("Whatever happened here has already transpired.");
                }
                break;
            
            case 36:
                if(!ritualActivated && dragonHeartObtained && !tiamatDead) {
                    System.out.println("\n==============================");
                    System.out.println("Ancient runes circle a stone altar at the chamber's center.");
                    System.out.println("Dark energy pulses through the carvings in rhythm");
                    System.out.println("with the Dragon Heart in your possession.");
                    System.out.println();
                    System.out.println("The altar seems to call to the Heart...");
                    System.out.println("Do you place the Dragon Heart on the altar?");
                    System.out.println("==============================");
                    System.out.print("[Y]es or [N]o: ");
                    
                    String choice = input.nextLine().toUpperCase();
                    
                    if(choice.equals("Y")) {
                        System.out.println("\n==============================");
                        System.out.println("You place the Dragon Heart upon the stone altar.");
                        System.out.println("The runes flare with blinding crimson light!");
                        System.out.println("The chamber shakes violently as dark energy surges");
                        System.out.println("through the castle's very foundations.");
                        System.out.println();
                        System.out.println("A portal tears open before you — swirling darkness");
                        System.out.println("that pulls you through space itself.");
                        System.out.println();
                        System.out.println("You find yourself back in Smaug's Lair...");
                        System.out.println("but something is terribly wrong.");
                        System.out.println("==============================");
                        ritualActivated = true;
                        currentRoom = 37;
                        
                        System.out.println("\n==============================");
                        System.out.println("Smaug's massive corpse begins to float upward,");
                        System.out.println("surrounded by a vortex of draconic energy.");
                        System.out.println("The body twists and contorts, bones cracking,");
                        System.out.println("scales splitting and reforming into something... more.");
                        System.out.println();
                        System.out.println("One head becomes two. Two becomes five.");
                        System.out.println("Each one a different chromatic color —");
                        System.out.println("white, black, green, blue, and red.");
                        System.out.println();
                        playSound("monster_roar");
                        System.out.println("\"I AM TIAMAT, THE FIVE-HEADED QUEEN!\"");
                        System.out.println("\"YOU THOUGHT TO STEAL MY HEART?\"");
                        System.out.println("\"NOW WITNESS TRUE DRACONIC FURY!\"");
                        System.out.println("==============================");
                        System.out.print("Press Enter to face Tiamat...");
                        input.nextLine();
                        combat(new Tiamat(), true);
                    } else {
                        System.out.println("\nYou step back from the altar.");
                        System.out.println("Perhaps you are not ready for what lies beyond...");
                    }
                }
                else if(!dragonHeartObtained) {
                    System.out.println("\nThe altar pulses weakly. It seems to hunger for");
                    System.out.println("something of great draconic power.");
                }
                break;
            
            case 37:
                if(ritualActivated && !tiamatDead) {
                    System.out.println("\nThe remnants of the ritual still crackle in the air.");
                    System.out.println("Tiamat's presence lingers, waiting...");
                }
                break;
            
            case 38:
                if(tiamatDead) {
                    System.out.println("\nThe chamber lies in ruins, scorched by dragon fire.");
                    System.out.println("Tiamat's essence has been scattered to the winds.");
                    System.out.println("The nightmare is finally over.");
                }
                break;
        }
    }
    
    // ================= combat =================
    static void combat(Monster m, boolean boss) {
        System.out.println("\n=== COMBAT: " + m.name + " ===");
        System.out.println(m.name + " HP: " + m.hp);
        System.out.println("==============================");
        
        while(m.hp > 0 && player.hp > 0) {
            player.applyEffects();
            m.applyEffects();
            
            if(player.hp <= 0) {
                die();
                return;
            }
            if(m.hp <= 0) break;
            
            if(player.isStunned()) {
                System.out.println("\n--- Your Turn ---");
                System.out.println("You are stunned and cannot act!");
            }
            else {
                boolean weakened = false;
                if(m instanceof Demogorgon && ((Demogorgon)m).insanityGazeActive) {
                    weakened = true;
                    System.out.println("\n(Demogorgon's Insanity Gaze clouds your mind! -50% damage)");
                }
                
                System.out.println("\n--- Your Turn ---");
                System.out.println("HP: " + player.hp + "/" + player.maxHp + " | MP: " + player.mp + "/" + player.maxMp);
                System.out.println("Enemy: " + m.name + " | HP: " + m.hp);
                
                System.out.println(
                    "1 Attack\n" +
                    "2 Ability\n" +
                    "3 Dodge\n" +
                    "4 Use Potion\n" +
                    "5 Flee"
                );
                System.out.print("> ");
                
                String c = input.nextLine();
                
                if(c.equals("1")) {
                    playSound("sword_clash");
                    if(m instanceof CountStrahd && ((CountStrahd)m).mistForm) {
                        System.out.println("Your attack passes through Count Strahd's mist form!");
                    } else {
                        int dmg = player.getDamage();
                        if(weakened) dmg /= 2;
                        m.hp -= dmg;
                        System.out.println("You deal " + dmg + " damage!");
                        
                        if(player.weapon.name.equals("Hand of Vecna")) {
                            int heal = 5;
                            player.hp = Math.min(player.hp + heal, player.maxHp);
                            System.out.println("Hand of Vecna drains life! (+5 HP)");
                        }
                    }
                }
                else if(c.equals("2")) {
                    if(weakened) {
                        System.out.println("(Demogorgon's Insanity Gaze weakens your ability!)");
                    }
                    player.useAbility(m, weakened);
                }
                else if(c.equals("3")) {
                    if(player.playerClass.equals("Rogue") && player.rogueDodgeCounter >= 3) {
                        System.out.println("Rogue passive: Auto-dodge!");
                        player.rogueDodgeCounter = 0;
                        if(m.hp > 0) {
                            System.out.println("\n--- " + m.name + "'s Turn ---");
                            System.out.println("Your dodge was perfect! No damage taken!");
                        }
                        continue;
                    }
                    
                    double dodge = player.dodgeChance;
                    
                    if(player.playerClass.equals("Rogue")) {
                        dodge = 0.7;
                        player.rogueDodgeCounter++;
                    }
                    
                    if(Math.random() < dodge) {
                        System.out.println("You dodge the attack!");
                        if(m.hp > 0) {
                            System.out.println("\n--- " + m.name + "'s Turn ---");
                            System.out.println(m.name + " attacks but you evade!");
                        }
                        continue;
                    }
                    
                    System.out.println("Dodge failed!");
                }
                else if(c.equals("4")) {
                    player.inventory.usePotionInCombat(player);
                }
                else if(c.equals("5")) {
                    if(boss) {
                        System.out.println("There is no escape from this fight!");
                    }
                    else if(Math.random() < 0.5) {
                        System.out.println("You flee from the battle!");
                        return;
                    }
                    else {
                        System.out.println("Escape failed!");
                    }
                }
            }
            
            if(m.hp <= 0) break;
            
            if(m.stunned) {
                System.out.println("\n--- " + m.name + "'s Turn ---");
                System.out.println(m.name + " is stunned and cannot act!");
                m.stunned = false;
                continue;
            }
            
            System.out.println("\n--- " + m.name + "'s Turn ---");
            
            if(m instanceof Demogorgon && !m.stunned) {
                ((Demogorgon)m).chooseAttack();
            }
            
            System.out.println(m.attackText());
            
            int raw = m.attack();
            
            int damage;
            
            if(m instanceof Demogorgon && ((Demogorgon)m).tentacleAttack) {
                damage = raw;
                ((Demogorgon)m).tentacleAttack = false;
            }
            else if(m instanceof Demogorgon && raw > 30) {
                damage = raw;
            } else {
                damage = raw - player.armor;
            }
            
            if(damage < 1) damage = 1;
            
            player.hp -= damage;
            System.out.println("You take " + damage + " damage! (HP: " + player.hp + "/" + player.maxHp + ")");
            
            if(player.hp <= 0) die();
        }
        
        if(m.hp <= 0) {
            System.out.println("\n==========================================");
            System.out.println("   " + m.name + " HAS BEEN DEFEATED!   ");
            System.out.println("==========================================");
            
            if(boss) {
                bossDrop(m.name);
                player.gainExp(bossExpAmount(m.name));
            } else {
                int expGain = 25 + (m.maxHp / 10);
                player.gainExp(expGain);
                playerGold += (int)(Math.random() * 20) + 5;
                System.out.println("Gold found! (Total: " + playerGold + ")");
            }
        }
    }
    
    static int bossExpAmount(String name) {
        switch(name) {
            case "Black Knight": return 75;
            case "Stone Guardian": return 100;
            case "Lord Soth": return 125;
            case "Vecna": return 200;
            case "Count Strahd": return 250;
            case "Demogorgon": return 300;
            case "Smaug": return 500;
            case "Tiamat": return 1000;
            default: return 50;
        }
    }
    
    // ================= Stat Effects =================
    enum StatusEffectType {
        BURN, STUN, REGEN, WEAKENED
    }
    
    static class StatusEffect {
        StatusEffectType type;
        int duration;
        
        StatusEffect(StatusEffectType t, int d) {
            type = t;
            duration = d;
        }
    }
    
    // ================= Char. base =================
    static class Character {
        int hp = 100;
        int maxHp = 100;
        int mp = 25;
        int maxMp = 25;
        int level = 1;
        int exp = 0;
        int armor = 0;
        
        void gainExp(int amount) {
            exp += amount;
            System.out.println("Gained " + amount + " EXP! (" + exp + "/" + (level * 100) + ")");
            while(exp >= level * 100) {
                exp -= level * 100;
                level++;
                onLevelUp();
            }
        }
        
        void onLevelUp() {
            System.out.println("\n=== LEVEL UP! Level " + level + " ===");
        }
    }
    
    // ================= player =================
    static class Player extends Character {
        // all chars. starts with rusty sword
        Weapon weapon = new Weapon("Rusty Sword", 6);
        Inventory inventory = new Inventory();
        ArrayList<StatusEffect> effects = new ArrayList<>();
        
        String playerClass = "Fighter";
        
        double dodgeChance = 0.5;
        double critChance = 0.1;
        
        int rogueDodgeCounter = 0;
        
        int getDamage() {
            int base = weapon.damage + (level * 2);
            
            boolean weakened = false;
            for(StatusEffect e : effects) {
                if(e.type == StatusEffectType.WEAKENED) {
                    weakened = true;
                    break;
                }
            }
            if(weakened) base /= 2;
            
            if(playerClass.equals("Rogue") && Math.random() < critChance) {
                System.out.println("CRITICAL HIT!");
                return base * 2;
            }
            
            if(playerClass.equals("Wizard")) {
                return base + 5;
            }
            
            return base;
        }
        
        void applyPassiveRegen() {
            if(level >= 3) {
                if(playerClass.equals("Fighter")) {
                    int regen = 5;
                    if(hp < maxHp) {
                        hp = Math.min(hp + regen, maxHp);
                        System.out.println("(Fighter passive: Regenerated 5 HP)");
                    }
                }
                else if(playerClass.equals("Wizard")) {
                    int regen = 5;
                    if(mp < maxMp) {
                        mp = Math.min(mp + regen, maxMp);
                        System.out.println("(Wizard passive: Regenerated 5 MP)");
                    }
                }
            }
        }
        
        void applyEffects() {
            Iterator<StatusEffect> it = effects.iterator();
            
            while(it.hasNext()) {
                StatusEffect e = it.next();
                
                if(e.type == StatusEffectType.BURN) {
                    hp -= 5;
                    System.out.println("You are burning! (-5 HP)");
                }
                else if(e.type == StatusEffectType.REGEN) {
                    hp = Math.min(hp + 10, maxHp);
                    System.out.println("Holy light heals you! (+10 HP)");
                }
                
                e.duration--;
                
                if(e.duration <= 0) {
                    if(e.type == StatusEffectType.WEAKENED) {
                        System.out.println("(Your mind clears! Damage restored.)");
                    }
                    it.remove();
                }
            }
        }
        
        void addEffect(StatusEffectType type, int duration) {
            for(StatusEffect e : effects) {
                if(e.type == type) {
                    e.duration = Math.max(e.duration, duration);
                    return;
                }
            }
            
            effects.add(new StatusEffect(type, duration));
        }
        
        boolean isStunned() {
            for(StatusEffect e : effects) {
                if(e.type == StatusEffectType.STUN) {
                    return true;
                }
            }
            return false;
        }
        
        void useAbility(Monster m, boolean weakened) {
            int mpCost;
            
            if(level >= 5) mpCost = 25;
            else if(level >= 4) mpCost = 15;
            else if(level >= 2) mpCost = 10;
            else mpCost = 5;
            
            if(mp < mpCost) {
                System.out.println("Not enough mana! (Need " + mpCost + " MP, have " + mp + " MP)");
                return;
            }
            
            mp -= mpCost;
            
            if(playerClass.equals("Fighter")) {
                if(level >= 5) {
                    System.out.println("ULTIMATE: DIVINE JUDGMENT!");
                    System.out.println("A massive ethereal sword crashes down from the heavens!");
                    int dmg = weakened ? 40 : 80;
                    m.hp -= dmg;
                    m.effects.add(new StatusEffect(StatusEffectType.STUN, 2));
                    System.out.println("Dealt " + dmg + " damage! Enemy stunned for 2 turns!");
                }
                else if(level >= 4) {
                    System.out.println("Mighty Cleave!");
                    int dmg = weakened ? 20 : 40;
                    m.hp -= dmg;
                    if(Math.random() < 0.4) {
                        m.effects.add(new StatusEffect(StatusEffectType.STUN, 1));
                        System.out.println("Enemy stunned!");
                    }
                    System.out.println("Dealt " + dmg + " damage!");
                }
                else if(level >= 2) {
                    System.out.println("Shield Bash!");
                    int dmg = weakened ? 10 : 20;
                    m.hp -= dmg;
                    if(Math.random() < 0.3) {
                        m.effects.add(new StatusEffect(StatusEffectType.STUN, 1));
                        System.out.println("Enemy stunned!");
                    }
                    System.out.println("Dealt " + dmg + " damage!");
                }
                else {
                    System.out.println("Power Strike!");
                    int dmg = weakened ? 7 : 15;
                    m.hp -= dmg;
                    System.out.println("Dealt " + dmg + " damage!");
                }
            }
            else if(playerClass.equals("Wizard")) {
                playSound("fireball");
                if(level >= 5) {
                    System.out.println("ULTIMATE: METEOR STORM!");
                    System.out.println("The sky opens as burning meteors rain down!");
                    int dmg = weakened ? 45 : 90;
                    m.hp -= dmg;
                    m.effects.add(new StatusEffect(StatusEffectType.BURN, 5));
                    System.out.println("Dealt " + dmg + " damage! Enemy burns for 5 turns!");
                }
                else if(level >= 4) {
                    System.out.println("Lightning Bolt!");
                    int dmg = weakened ? 22 : 45;
                    m.hp -= dmg;
                    if(Math.random() < 0.4) {
                        m.effects.add(new StatusEffect(StatusEffectType.STUN, 1));
                        System.out.println("Enemy paralyzed!");
                    }
                    System.out.println("Dealt " + dmg + " damage!");
                }
                else if(level >= 2) {
                    System.out.println("Fireball!");
                    int dmg = weakened ? 12 : 25;
                    m.hp -= dmg;
                    m.effects.add(new StatusEffect(StatusEffectType.BURN, 3));
                    System.out.println("Dealt " + dmg + " damage! Enemy burns for 3 turns!");
                }
                else {
                    System.out.println("Arcane Missile!");
                    int dmg = weakened ? 9 : 18;
                    m.hp -= dmg;
                    System.out.println("Dealt " + dmg + " damage!");
                }
            }
            else if(playerClass.equals("Rogue")) {
                if(level >= 5) {
                    System.out.println("ULTIMATE: DEATH MARK!");
                    System.out.println("You vanish, striking from every shadow at once!");
                    int dmg = weakened ? 35 : 70;
                    m.hp -= dmg;
                    if(Math.random() < 0.8) {
                        int extraDmg = weakened ? 20 : 40;
                        m.hp -= extraDmg;
                        System.out.println("Fatal strike! Additional " + extraDmg + " damage!");
                    }
                    System.out.println("Dealt " + dmg + " damage!");
                }
                else if(level >= 4) {
                    System.out.println("Shadow Dance!");
                    int dmg = weakened ? 15 : 30;
                    m.hp -= dmg;
                    if(Math.random() < 0.5) {
                        int extra = weakened ? 10 : 20;
                        m.hp -= extra;
                        System.out.println("Double strike! Additional " + extra + " damage!");
                    }
                    System.out.println("Dealt " + dmg + " damage!");
                }
                else if(level >= 2) {
                    System.out.println("Shadow Strike!");
                    int dmg = weakened ? 7 : 15;
                    m.hp -= dmg;
                    if(Math.random() < 0.5) {
                        int bleed = weakened ? 7 : 15;
                        m.hp -= bleed;
                        System.out.println("Bleeding wound! Additional " + bleed + " damage!");
                    }
                    System.out.println("Dealt " + dmg + " damage!");
                }
                else {
                    System.out.println("Quick Stab!");
                    int dmg = weakened ? 6 : 12;
                    m.hp -= dmg;
                    if(Math.random() < 0.4) {
                        int extra = weakened ? 4 : 8;
                        m.hp -= extra;
                        System.out.println("Second stab! Additional " + extra + " damage!");
                    }
                    System.out.println("Dealt " + dmg + " damage!");
                }
            }
            
            System.out.println("(MP: " + mp + "/" + maxMp + ")");
        }
        
        @Override
        void onLevelUp() {
            System.out.println("\n=== LEVEL UP! Level " + level + " ===");
            
            maxHp += 20;
            hp = maxHp;
            maxMp += 10;
            mp = maxMp;
            
            if(playerClass.equals("Fighter")) {
                maxHp += 10;
                hp = maxHp;
                System.out.println("Fighter bonus: +10 Max HP");
            }
            else if(playerClass.equals("Wizard")) {
                maxMp += 10;
                mp = maxMp;
                System.out.println("Wizard bonus: +10 Max MP");
            }
            else if(playerClass.equals("Rogue")) {
                critChance += 0.05;
                System.out.println("Rogue bonus: +5% Crit Chance (now " + (int)(critChance * 100) + "%)");
            }
            
            if(level == 3) {
                System.out.println("\n=== CLASS PASSIVE UNLOCKED! ===");
                if(playerClass.equals("Fighter")) {
                    System.out.println("IRON CONSTITUTION: Regenerate 5 HP per turn.");
                }
                else if(playerClass.equals("Wizard")) {
                    System.out.println("ARCANE MEDITATION: Regenerate 5 MP per turn.");
                }
                else if(playerClass.equals("Rogue")) {
                    System.out.println("ELUSIVE: Free automatic dodge every 3 turns.");
                }
            }
            
            if(level == 5) {
                System.out.println("\n=== ULTIMATE ABILITY UNLOCKED! ===");
                if(playerClass.equals("Fighter")) {
                    System.out.println("DIVINE JUDGMENT (25 MP): 80 damage + 2 turn stun.");
                }
                else if(playerClass.equals("Wizard")) {
                    System.out.println("METEOR STORM (25 MP): 90 damage + 5 turn burn.");
                }
                else if(playerClass.equals("Rogue")) {
                    System.out.println("DEATH MARK (25 MP): 70 damage + 80% chance 40 extra.");
                }
            }
            
            System.out.println("HP: " + hp + "/" + maxHp + " | MP: " + mp + "/" + maxMp);
        }
    }
    
    // ================= Potion Types =================
    static class Item { String name; String description = ""; }
    
    static class Weapon extends Item {
        int damage;
        Weapon(String n, int d) { name = n; damage = d; description = "Damage: " + d; }
    }
    
    static class Armor extends Item {
        int reduction;
        Armor(String n, int r) { name = n; reduction = r; description = "Defense: " + r; }
    }
    
    static class Potion extends Item {
        int healAmount = 25;
        Potion() { name = "Health Potion"; description = "Restores 25 HP"; }
    }
    
    static class HealthPotion extends Potion {
        HealthPotion() { name = "Health Potion"; healAmount = 25; description = "Restores 25 HP"; }
    }
    
    static class ManaPotion extends Item {
        int manaAmount = 25;
        ManaPotion() { name = "Mana Potion"; description = "Restores 25 MP"; }
    }
    
    static class GreaterHealthPotion extends Potion {
        GreaterHealthPotion() { name = "Greater Health Potion"; healAmount = 50; description = "Restores 50 HP"; }
    }
    
    static class GreaterManaPotion extends Item {
        int manaAmount = 50;
        GreaterManaPotion() { name = "Greater Mana Potion"; description = "Restores 50 MP"; }
    }
    
    static class HolyElixir extends Item {
        HolyElixir() { name = "Holy Elixir"; description = "Fully restores HP & MP"; }
    }
    
    // ================= Base Monster =================
    static class Monster {
        String name;
        int hp;
        int maxHp;
        ArrayList<StatusEffect> effects = new ArrayList<>();
        boolean stunned = false;
        
        int attack() { return 5 + (int)(Math.random() * 8); }
        
        void applyEffects() {
            stunned = false;
            Iterator<StatusEffect> it = effects.iterator();
            while(it.hasNext()) {
                StatusEffect e = it.next();
                if(e.type == StatusEffectType.BURN) {
                    hp -= 5;
                    System.out.println(name + " burns for 5 damage!");
                }
                if(e.type == StatusEffectType.STUN) stunned = true;
                e.duration--;
                if(e.duration <= 0) it.remove();
            }
        }
        
        String attackText() { return name + " attacks!"; }
    }
    
    // ================= newbie monsters =================
    static class Goblin extends Monster {
        Goblin() { name = "Goblin"; hp = 40; maxHp = 40; }
        @Override String attackText() { return "Goblin swings wildly with a rusty dagger!"; }
        @Override int attack() { return 3 + (int)(Math.random() * 8); }
    }
    
    static class Skeleton extends Monster {
        Skeleton() { name = "Skeleton"; hp = 60; maxHp = 60; }
        @Override String attackText() { return "Skeleton thrusts a bone spear at you!"; }
        @Override int attack() { return 4 + (int)(Math.random() * 10); }
    }
    
    static class Orc extends Monster {
        Orc() { name = "Orc"; hp = 90; maxHp = 90; }
        @Override String attackText() { return "Orc cleaves down with a massive axe!"; }
        @Override int attack() { return 6 + (int)(Math.random() * 14); }
    }
    
    static class Vampire extends Monster {
        Vampire() { name = "Vampire"; hp = 110; maxHp = 110; }
        @Override String attackText() { return "Vampire lunges with supernatural speed!"; }
        @Override int attack() {
            if(Math.random() < 0.3) {
                System.out.println("Vampire sinks fangs into your neck!");
                int drain = 8;
                hp = Math.min(hp + drain, maxHp);
                System.out.println("Vampire drains " + drain + " HP from you!");
                return 12;
            }
            return 8 + (int)(Math.random() * 10);
        }
    }
    
    static class Wraith extends Monster {
        Wraith() { name = "Wraith"; hp = 75; maxHp = 75; }
        @Override String attackText() { return "Wraith phases through your body, chilling your soul!"; }
        @Override int attack() {
            if(Math.random() < 0.35) {
                System.out.println("Wraith's touch passes through your armor!");
                return 15;
            }
            return 10 + (int)(Math.random() * 8);
        }
    }
    
    static class Troll extends Monster {
        Troll() { name = "Troll"; hp = 150; maxHp = 150; }
        @Override String attackText() { return "Troll smashes the ground, sending debris flying!"; }
        @Override int attack() {
            int regen = 5;
            hp = Math.min(hp + regen, maxHp);
            System.out.println("Troll regenerates " + regen + " HP!");
            return 12 + (int)(Math.random() * 8);
        }
    }
    
    static class DeathKnight extends Monster {
        boolean darkAura = false;
        DeathKnight() { name = "Death Knight"; hp = 130; maxHp = 130; }
        @Override String attackText() {
            if(darkAura) return "Death Knight channels dark energy through his cursed blade!";
            return "Death Knight swings his cursed blade!";
        }
        @Override int attack() {
            if(!darkAura && Math.random() < 0.3) {
                darkAura = true;
                System.out.println("Death Knight surrounds himself with a dark aura! Damage increased!");
                return 15;
            }
            if(darkAura) return 20 + (int)(Math.random() * 10);
            return 15 + (int)(Math.random() * 10);
        }
    }
    
    // ================= MINI-BOSSES =================
    static class BlackKnight extends Monster {
        BlackKnight() { name = "Black Knight"; hp = 180; maxHp = 180; }
        @Override String attackText() { return "The Black Knight brings his enormous sword down!"; }
        @Override int attack() {
            if(Math.random() < 0.3) { System.out.println("HEAVY SLAM! The ground shakes beneath you!"); return 35; }
            return 15 + (int)(Math.random() * 10);
        }
    }
    
    static class Guardian extends Monster {
        boolean shieldUp = false;
        Guardian() { name = "Stone Guardian"; hp = 250; maxHp = 250; }
        @Override String attackText() { return "The Guardian's stone fists crash toward you!"; }
        @Override int attack() {
            if(Math.random() < 0.4 && !shieldUp) {
                shieldUp = true;
                System.out.println("Guardian raises its shield! Damage reduced!");
                return 5;
            }
            shieldUp = false;
            return 12 + (int)(Math.random() * 8);
        }
    }
    
    static class LordSoth extends Monster {
        LordSoth() { name = "Lord Soth"; hp = 220; maxHp = 220; }
        @Override String attackText() { return "Lord Soth charges on his spectral steed, flaming sword raised!"; }
        @Override int attack() {
            if(Math.random() < 0.25) {
                System.out.println("CATACLYSMIC FIRE! Lord Soth unleashes hellfire!");
                Game.player.effects.add(new StatusEffect(StatusEffectType.BURN, 3));
                Game.player.effects.add(new StatusEffect(StatusEffectType.STUN, 1));
                return 30;
            }
            return 18 + (int)(Math.random() * 10);
        }
    }
    
    // ================= BOSSES =================
    static class Vecna extends Monster {
        int mana = 0;
        Vecna() { name = "Vecna"; hp = 350; maxHp = 350; }
        @Override int attack() {
            Game.player.mp -= 5;
            if(Game.player.mp < 0) Game.player.mp = 0;
            mana += 5;
            System.out.println("Vecna drains 5 MP from you!");
            if(mana >= 20) { mana = 0; System.out.println("FINGER OF DEATH! A necrotic beam strikes your soul!"); return 50; }
            return 12 + (int)(Math.random() * 10);
        }
        @Override String attackText() { return "Vecna whispers forbidden secrets of death..."; }
    }
    
    static class CountStrahd extends Monster {
        boolean mistForm = false;
        CountStrahd() { name = "Count Strahd"; hp = 400; maxHp = 400; }
        @Override int attack() {
            int regen = 10;
            hp = Math.min(hp + regen, maxHp);
            System.out.println("Count Strahd regenerates " + regen + " HP!");
            if(Math.random() < 0.3) { mistForm = true; System.out.println("Count Strahd transforms into mist! Physical attacks will miss!"); }
            else { mistForm = false; }
            return 18 + (int)(Math.random() * 15);
        }
        @Override String attackText() { return "Count Strahd strikes with blinding vampiric speed!"; }
    }
    
    static class Demogorgon extends Monster {
        boolean tentacleAttack = false;
        boolean insanityGazeActive = false;
        Demogorgon() { name = "Demogorgon"; hp = 500; maxHp = 500; }
        void chooseAttack() {
            double roll = Math.random();
            if(roll < 0.3) { tentacleAttack = true; System.out.println("DEMOGORGON'S TWIN TENTACLES lash out!"); }
            else if(roll < 0.55) { insanityGazeActive = true; System.out.println("INSANITY GAZE! The right head stares into your soul!"); Game.player.addEffect(StatusEffectType.WEAKENED, 2); System.out.println("Your attack damage is halved for 2 turns!"); }
            else if(roll < 0.8) { System.out.println("BEGUILING GAZE! The left head mesmerizes you!"); Game.player.addEffect(StatusEffectType.STUN, 1); }
        }
        @Override int attack() {
            if(tentacleAttack) { int dmg = 25 + (int)(Math.random() * 15); return dmg * 2; }
            insanityGazeActive = false;
            return 20 + (int)(Math.random() * 15);
        }
        @Override String attackText() { return "The Demogorgon radiates pure chaos and madness!"; }
    }
    
    static class Smaug extends Monster {
        boolean charging = false;
        boolean phase2 = false;
        Smaug() { name = "Smaug"; hp = 600; maxHp = 600; }
        @Override int attack() {
            if(!phase2 && hp <= 300) { phase2 = true; System.out.println("\n=== SMAUG ENTERS PHASE 2 ==="); System.out.println("\"YOU WILL BURN LIKE ALL THE REST!\""); System.out.println("Smaug's fury intensifies! The hoard melts around him!"); }
            if(phase2) {
                Game.player.effects.add(new StatusEffect(StatusEffectType.BURN, 3));
                System.out.println("The inferno around Smaug burns you!");
                if(!charging && Math.random() < 0.4) { charging = true; System.out.println("Smaug inhales deeply, chest glowing white-hot..."); return 0; }
                if(charging) { charging = false; System.out.println("INFERNO BREATH! A wall of white-hot fire engulfs you!"); return 65; }
                return 35;
            }
            if(!charging && Math.random() < 0.25) { charging = true; System.out.println("Smaug's chest glows orange as he inhales..."); return 0; }
            if(charging) { charging = false; System.out.println("FIRE BREATH! Flames erupt from Smaug's maw!"); return 50; }
            return 25;
        }
        @Override String attackText() { return phase2 ? "Smaug thrashes violently, destroying everything around him!" : "Smaug's massive tail sweeps across the treasure hoard!"; }
    }
    
    // ================= TIAMAT “SECRET” BOSS =================
    static class Tiamat extends Monster {
        int currentHead = 0;
        String[] headNames = {"White", "Black", "Green", "Blue", "Red"};
        boolean phase2 = false;
        boolean charging = false;
        Tiamat() { name = "Tiamat"; hp = 1000; maxHp = 1000; }
        @Override int attack() {
            if(!phase2 && hp <= 500) { phase2 = true; System.out.println("\n=== TIAMAT ENTERS PHASE 2 ==="); System.out.println("\"MY FULL POWER AWAKENS! ALL ELEMENTS OBEY ME!\""); System.out.println("All five heads roar in unison! The chamber begins to collapse!"); }
            currentHead = (currentHead + 1) % 5;
            String headName = headNames[currentHead];
            if(phase2 && !charging && Math.random() < 0.3) { charging = true; System.out.println("Tiamat's five heads inhale together... gathering catastrophic energy!"); return 0; }
            if(charging) { charging = false; System.out.println("CHROMATIC CATACLYSM! All five breath weapons fire at once!"); Game.player.effects.add(new StatusEffect(StatusEffectType.BURN, 5)); Game.player.effects.add(new StatusEffect(StatusEffectType.STUN, 2)); return 80; }
            switch(headName) {
                case "White": System.out.println("The WHITE head breathes freezing frost!"); Game.player.effects.add(new StatusEffect(StatusEffectType.STUN, 1)); return 30;
                case "Black": System.out.println("The BLACK head spews corrosive acid!"); Game.player.armor = Math.max(0, Game.player.armor - 2); System.out.println("Your armor is corroded! (-2 Defense)"); return 35;
                case "Green": System.out.println("The GREEN head exhales poisonous gas!"); Game.player.effects.add(new StatusEffect(StatusEffectType.BURN, 3)); return 28;
                case "Blue": System.out.println("The BLUE head unleashes chain lightning!"); return 40;
                case "Red": System.out.println("The RED head breathes searing fire!"); Game.player.effects.add(new StatusEffect(StatusEffectType.BURN, 4)); return 45;
            }
            return 30;
        }
        @Override String attackText() { return "Tiamat's five heads roar in terrible harmony!"; }
    }
    
    // ================= Inventory =================
    static class Inventory {
        ArrayList<Item> items = new ArrayList<>();
        
        void add(Item i) { if(i == null) return; items.add(i); System.out.println("Picked up: " + i.name); }
        
        void show(Player p) {
            if(items.isEmpty()) { System.out.println("\n=== INVENTORY ===\nYour inventory is empty."); return; }
            System.out.println("\n=== INVENTORY ===");
            System.out.println("Gold: " + playerGold);
            System.out.println("HP: " + p.hp + "/" + p.maxHp + " | MP: " + p.mp + "/" + p.maxMp);
            System.out.println("Weapon: " + p.weapon.name + " (Damage: " + p.weapon.damage + ")");
            System.out.println("Armor: " + p.armor + " defense");
            System.out.println("------------------------------");
            for(int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                String extra = "";
                if(item instanceof Weapon) { extra = " [Damage: " + ((Weapon)item).damage + "]"; if(item.name.equals("Excalibur")) extra += " *** LEGENDARY ***"; if(item.name.equals("Hand of Vecna")) extra += " [Lifesteals 5 HP]"; }
                else if(item instanceof Armor) { extra = " [Defense: " + ((Armor)item).reduction + "]"; if(item.name.equals("Smaug's Scalemail")) extra += " *** LEGENDARY ***"; }
                else if(item instanceof GreaterHealthPotion) extra = " [Restores 50 HP]";
                else if(item instanceof HealthPotion || item instanceof Potion) extra = " [Restores 25 HP]";
                else if(item instanceof GreaterManaPotion) extra = " [Restores 50 MP]";
                else if(item instanceof ManaPotion) extra = " [Restores 25 MP]";
                else if(item instanceof HolyElixir) extra = " [Fully restores HP & MP] *** RARE ***";
                System.out.println((i+1) + ". " + item.name + extra);
            }
            System.out.println("\nSelect item number to use/equip, or X to close:");
            System.out.print("> ");
            String s = Game.input.nextLine();
            if(s.equalsIgnoreCase("x")) return;
            try {
                int index = Integer.parseInt(s) - 1;
                if(index < 0 || index >= items.size()) { System.out.println("Invalid selection."); return; }
                Item item = items.get(index);
                if(item instanceof Weapon) {
                    Weapon oldWeapon = p.weapon; p.weapon = (Weapon)item; items.remove(index);
                    if(oldWeapon != null && !oldWeapon.name.equals("Rusty Sword")) { items.add(oldWeapon); System.out.println("Unequipped: " + oldWeapon.name); }
                    System.out.println("Equipped: " + p.weapon.name + " (Damage: " + p.weapon.damage + ")");
                    if(p.weapon.name.equals("Excalibur")) System.out.println("*** The legendary blade hums with divine power! ***");
                }
                else if(item instanceof Armor) { int oldArmor = p.armor; p.armor = ((Armor)item).reduction; items.remove(index); System.out.println("Equipped: " + item.name + " (Armor " + oldArmor + " -> " + p.armor + ")"); }
                else if(item instanceof GreaterHealthPotion) { int before = p.hp; p.hp = Math.min(p.hp + 50, p.maxHp); System.out.println("Used Greater Health Potion! +" + (p.hp - before) + " HP (HP: " + p.hp + "/" + p.maxHp + ")"); items.remove(index); }
                else if(item instanceof HealthPotion || item instanceof Potion) { int before = p.hp; p.hp = Math.min(p.hp + 25, p.maxHp); System.out.println("Used Health Potion! +" + (p.hp - before) + " HP (HP: " + p.hp + "/" + p.maxHp + ")"); items.remove(index); }
                else if(item instanceof GreaterManaPotion) { int before = p.mp; p.mp = Math.min(p.mp + 50, p.maxMp); System.out.println("Used Greater Mana Potion! +" + (p.mp - before) + " MP (MP: " + p.mp + "/" + p.maxMp + ")"); items.remove(index); }
                else if(item instanceof ManaPotion) { int before = p.mp; p.mp = Math.min(p.mp + 25, p.maxMp); System.out.println("Used Mana Potion! +" + (p.mp - before) + " MP (MP: " + p.mp + "/" + p.maxMp + ")"); items.remove(index); }
                else if(item instanceof HolyElixir) { p.hp = p.maxHp; p.mp = p.maxMp; System.out.println("Used Holy Elixir! HP and MP fully restored!"); System.out.println("(HP: " + p.hp + "/" + p.maxHp + " | MP: " + p.mp + "/" + p.maxMp + ")"); items.remove(index); }
            } catch(NumberFormatException e) { System.out.println("Invalid input."); }
        }
        
        void usePotionInCombat(Player p) {
            ArrayList<Integer> potionIndexes = new ArrayList<>();
            System.out.println("\n=== USE POTION ===");
            System.out.println("HP: " + p.hp + "/" + p.maxHp + " | MP: " + p.mp + "/" + p.maxMp);
            System.out.println("------------------------------");
            int displayNum = 1;
            for(int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                if(item instanceof Potion || item instanceof HealthPotion || item instanceof ManaPotion || item instanceof HolyElixir) {
                    String desc = "";
                    if(item instanceof GreaterHealthPotion) desc = " - Restores 50 HP";
                    else if(item instanceof HealthPotion || item instanceof Potion) desc = " - Restores 25 HP";
                    else if(item instanceof GreaterManaPotion) desc = " - Restores 50 MP";
                    else if(item instanceof ManaPotion) desc = " - Restores 25 MP";
                    else if(item instanceof HolyElixir) desc = " - Fully restores HP & MP";
                    System.out.println(displayNum + ". " + item.name + desc);
                    potionIndexes.add(i); displayNum++;
                }
            }
            if(potionIndexes.isEmpty()) { System.out.println("No potions available!"); return; }
            System.out.println("------------------------------");
            System.out.println("Select potion number, or X to cancel:");
            System.out.print("> ");
            String s = Game.input.nextLine();
            if(s.equalsIgnoreCase("x")) return;
            try {
                int choice = Integer.parseInt(s) - 1;
                if(choice < 0 || choice >= potionIndexes.size()) { System.out.println("Invalid selection."); return; }
                int actualIndex = potionIndexes.get(choice);
                Item item = items.get(actualIndex);
                if(item instanceof GreaterHealthPotion) { int before = p.hp; p.hp = Math.min(p.hp + 50, p.maxHp); System.out.println("Used Greater Health Potion! +" + (p.hp - before) + " HP (HP: " + p.hp + "/" + p.maxHp + ")"); items.remove(actualIndex); }
                else if(item instanceof HealthPotion || item instanceof Potion) { int before = p.hp; p.hp = Math.min(p.hp + 25, p.maxHp); System.out.println("Used Health Potion! +" + (p.hp - before) + " HP (HP: " + p.hp + "/" + p.maxHp + ")"); items.remove(actualIndex); }
                else if(item instanceof GreaterManaPotion) { int before = p.mp; p.mp = Math.min(p.mp + 50, p.maxMp); System.out.println("Used Greater Mana Potion! +" + (p.mp - before) + " MP (MP: " + p.mp + "/" + p.maxMp + ")"); items.remove(actualIndex); }
                else if(item instanceof ManaPotion) { int before = p.mp; p.mp = Math.min(p.mp + 25, p.maxMp); System.out.println("Used Mana Potion! +" + (p.mp - before) + " MP (MP: " + p.mp + "/" + p.maxMp + ")"); items.remove(actualIndex); }
                else if(item instanceof HolyElixir) { p.hp = p.maxHp; p.mp = p.maxMp; System.out.println("Used Holy Elixir! HP and MP fully restored!"); System.out.println("(HP: " + p.hp + "/" + p.maxHp + " | MP: " + p.mp + "/" + p.maxMp + ")"); items.remove(actualIndex); }
            } catch(NumberFormatException e) { System.out.println("Invalid input."); }
        }
        
        boolean usePotion(Player p) {
            for(int i = 0; i < items.size(); i++) {
                if(items.get(i) instanceof Potion || items.get(i) instanceof HealthPotion) {
                    int before = p.hp; p.hp = Math.min(p.hp + 25, p.maxHp);
                    System.out.println("Used Health Potion! +" + (p.hp - before) + " HP (HP: " + p.hp + "/" + p.maxHp + ")");
                    items.remove(i); return true;
                }
            }
            return false;
        }
    }
    
    // ================= Room =================
    static class Room {
        String name, desc;
        HashMap<String, Integer> next = new HashMap<>();
        boolean eventTriggered = false;
        Room(String n, String d) { name = n; desc = d; }
        String getAvailableDirections() {
            StringBuilder sb = new StringBuilder();
            for(String dir : next.keySet()) {
                switch(dir) { case "F": sb.append("[F]orward "); break; case "B": sb.append("[B]ack "); break; case "L": sb.append("[L]eft "); break; case "R": sb.append("[R]ight "); break; }
            }
            sb.append("| X Explore | T Fight | I Inventory | H Help | ? Hint | M Sound");
            return sb.toString();
        }
    }
    
    // ================= Data loading =================
    static void loadRooms() {
        try {
            Scanner sc = new Scanner(new File("src/main/rooms.txt"));
            while(sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if(line.isEmpty()) continue;
                String[] p = line.split("\\|");
                Room r = new Room(p[1], p[2]);
                String[] dirs = p[3].split(",");
                String[] nums = p[4].split(",");
                for(int i = 0; i < dirs.length; i++) { r.next.put(dirs[i].trim(), Integer.parseInt(nums[i].trim())); }
                rooms.put(Integer.parseInt(p[0]), r);
            }
            sc.close();
            System.out.println("Loaded " + rooms.size() + " rooms.");
        } catch(Exception e) { System.out.println("ERROR: rooms.txt missing or could not be read."); System.exit(1); }
    }
    
    static void loadItems() {
        try {
            Scanner sc = new Scanner(new File("src/main/items.txt"));
            while(sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if(line.isEmpty()) continue;
                String[] p = line.split("\\|");
                String itemName = p[0].trim();
                String itemType = p[1].trim();
                if(itemType.equals("weapon")) allItems.add(new Weapon(itemName, Integer.parseInt(p[2].trim())));
                else if(itemType.equals("armor")) allItems.add(new Armor(itemName, Integer.parseInt(p[2].trim())));
                else if(itemType.equals("potion")) {
                    if(itemName.contains("Greater Health")) allItems.add(new GreaterHealthPotion());
                    else if(itemName.contains("Greater Mana")) allItems.add(new GreaterManaPotion());
                    else if(itemName.contains("Holy Elixir")) allItems.add(new HolyElixir());
                    else if(itemName.contains("Mana")) allItems.add(new ManaPotion());
                    else allItems.add(new HealthPotion());
                }
            }
            sc.close();
            System.out.println("Loaded " + allItems.size() + " items.");
        } catch(Exception e) { System.out.println("ERROR: items.txt missing or could not be read."); System.exit(1); }
    }
    
    static void loadMonsters() {
        try {
            Scanner sc = new Scanner(new File("src/main/monsters.txt"));
            while(sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if(line.isEmpty()) continue;
                String[] p = line.split("\\|");
                String name = p[0].trim();
                if(name.equals("Goblin")) { for(int i = 0; i < 2; i++) monsterPool.add(new Goblin()); }
                else if(name.equals("Skeleton")) { for(int i = 0; i < 2; i++) monsterPool.add(new Skeleton()); }
                else if(name.equals("Orc")) { for(int i = 0; i < 2; i++) monsterPool.add(new Orc()); }
                else if(name.equals("Vampire")) { for(int i = 0; i < 2; i++) monsterPool.add(new Vampire()); }
                else if(name.equals("Wraith")) { for(int i = 0; i < 2; i++) monsterPool.add(new Wraith()); }
                else if(name.equals("Troll")) { monsterPool.add(new Troll()); }
                else if(name.equals("Death Knight")) { monsterPool.add(new DeathKnight()); }
            }
            sc.close();
            System.out.println("Loaded " + monsterPool.size() + " monsters into pool.");
        } catch(Exception e) { System.out.println("monsters.txt missing, loading defaults..."); for(int i = 0; i < 3; i++) monsterPool.add(new Goblin()); for(int i = 0; i < 3; i++) monsterPool.add(new Skeleton()); for(int i = 0; i < 2; i++) monsterPool.add(new Orc()); for(int i = 0; i < 2; i++) monsterPool.add(new Vampire()); for(int i = 0; i < 2; i++) monsterPool.add(new Wraith()); monsterPool.add(new Troll()); monsterPool.add(new DeathKnight()); System.out.println("Loaded " + monsterPool.size() + " default monsters."); }
    }
    
    // ================= utility =================
    static void die() {
        playSound("death");
        System.out.println("\n==========================================");
        System.out.println("              YOU HAVE DIED                ");
        System.out.println("==========================================");
        System.out.println("Final Level: " + player.level + " | Gold: " + playerGold);
        System.out.println("==========================================");
        System.exit(0);
    }
    
    static void win() {
        System.out.println("\n==========================================");
        System.out.println("          SMAUG THE TERRIBLE IS SLAIN       ");
        System.out.println("==========================================");
        System.out.println("Hint: Explore the deeper passages of the Dragon Hoard.");
        currentRoom = 35;
    }
    
    static void trueEnding() {
        playSound("victory");
        System.out.println("\n==========================================");
        System.out.println("       TIAMAT, THE DRAGON QUEEN, IS DEAD    ");
        System.out.println("==========================================");
        int bossesDefeated = (blackKnightDead?1:0) + (guardianDead?1:0) + (lordSothDead?1:0) + (vecnaDead?1:0) + (strahdDead?1:0) + (demogorgonDead?1:0) + (smaugDead?1:0) + (tiamatDead?1:0);
        System.out.println("Bosses Defeated: " + bossesDefeated + "/8");
        if(bossesDefeated == 8) System.out.println("Rank: TRUE DRAGON SLAYER");
        else if(bossesDefeated >= 7) System.out.println("Rank: DRAGON SLAYER");
        else if(bossesDefeated >= 5) System.out.println("Rank: HERO OF HYRULE");
        else System.out.println("Rank: BRAVE ADVENTURER");
        System.out.println("==========================================");
        System.out.println("         THANK YOU FOR PLAYING!            ");
        System.out.println("==========================================");
        System.exit(0);
    }
    
    static Item findItem(String n) {
        for(Item i : allItems) if(i.name.equalsIgnoreCase(n)) return i;
        return null;
    }
}

//items.txt below
Rusty Sword|weapon|6
Iron Sword|weapon|10
Silver Longsword|weapon|18
Shadow Dagger|weapon|12
War Hammer|weapon|20
Knight Blade|weapon|16
Flamebrand|weapon|24
Excalibur|weapon|50
Demogorgon's Twin Whips|weapon|35
Frostreaver|weapon|28
Dragon King's Crown|armor|18
Leather Armor|armor|2
Chain Armor|armor|5
Shadow Cloak|armor|7
Knight's Plate|armor|11
Plate Armor|armor|9
Stone Aegis|armor|13
Dragon Scale Armor|armor|15
Smaug's Scalemail|armor|20
Potion|potion|25
Potion|potion|25
Potion|potion|25
Potion|potion|25
Potion|potion|25
Greater Health Potion|potion|50
Greater Health Potion|potion|50
Greater Mana Potion|potion|50
Greater Mana Potion|potion|50
Holy Elixir|potion|100


// monsters.txt below
Goblin|40|3|8
Skeleton|60|4|10
Orc|90|6|14
Vampire|110|8|16
Wraith|75|10|5
Troll|150|12|6
Death Knight|130|15|10

// rooms.txt below
1|Forest Camp|You crouch among the blackened stumps of what was once a thriving woodland. Smoke curls through the air like ghostly serpents, stinging your eyes. Ahead, the silhouette of Castle Hyrule looms against a blood-red sky.|F|2
2|Castle Gates|The massive iron gates of Hyrule lie twisted and half-melted, blackened by dragon fire. Scorch marks streak the ancient stone walls like claw marks. High above, something shifts in the shadows of a crumbling tower.|F,B|3,1
3|Drawbridge|The stone bridge spans a bottomless chasm, cracked and groaning under its own weight. Wind screams through the gap. Scattered weapons and shattered shields litter the stones here.|F,B|4,2
4|Courtyard|A vast open courtyard littered with shattered statues and charred banners stretches before you. Toppled fountains weep stagnant black water. Paths branch in four directions through crumbling archways.|F,L,R,B|7,5,6,3
5|Hay Storage|The air here is thick with the smell of moldy straw and something metallic. Old hay piles shift unnaturally in the corner. A faint golden glint catches your eye from deep within.|F|4
6|Stables|Row after row of broken stalls line the walls, the wood splintered as if great claws raked through them. Rusted horseshoes and shattered carts litter the floor.|F|4
7|Riddle Door|A massive stone door dominates the far wall, its surface carved with intricate runes that pulse with a faint blue light. A crystal eye stares directly at you, unblinking.|F,B|8,4
8|Crossroads Hall|The grand hallway splits in multiple directions, each path disappearing into impenetrable darkness. Distant echoes bounce through the corridors. Torches flicker weakly in their sconces.|F,L,R,B|9,12,10,7
9|Barracks|Rows of armored figures stand at attention along both walls. They are empty suits of armor — old, rusted, ceremonial. Torn banners hang from the ceiling, their symbols long faded.|F,B|10,8
10|Armory|Weapons of every description hang on the walls — swords, axes, maces — all untouched by rust or time. The air is deathly still. Racks of blades and shields line the chamber.|F,B|11,9
11|Storage Vault|Crushed barrels and rusted crates are stacked in precarious towers. Amidst the wreckage, a single untouched chest sits on a pedestal, surrounded by a faint shimmer of preserved magic.|F,B|12,10
12|Dungeon Entry|The temperature drops sharply as you descend worn stone steps. Iron chains hang from the ceiling, some still holding things that twitch feebly. The air tastes of copper and despair.|F,B|13,11
13|Prison Cells|Endless rows of cells stretch into the darkness, their bars bent outward from the inside. Torn clothing and old bones are scattered across the floor. Graffiti warns of horrors deeper within.|F,B|14,12
14|Execution Chamber|A blood-stained chopping block squats in the center beneath a rusted blade. Skeletons are chained to the walls. One corpse near the block still clutches something in its bony fingers.|F,B|15,13
15|Mess Hall|Long wooden tables stretch the length of the hall, overturned and shattered. Plates of food long rotted to black sludge still sit at some places. The echo of screams bounces off the walls.|F,B|16,14
16|Triple Gate|Three enormous locks seal a massive stone door carved with the image of a three-headed dragon. Each lock glows faintly — gold, silver, crimson. Ancient mechanisms hum behind the stone.|F,B|17,15
17|Dark Hallway|The passage narrows to a claustrophobic tunnel where your torch seems to dim. The air is frigid. Something flutters overhead in the blackness — leathery wings, then silence.|F,B|18,16
18|Upper Hall|The castle's second floor opens into a grand hallway lined with tattered tapestries. The heroes in the images all have their eyes burned out. Footsteps echo strangely here.|F,B|19,17
19|Dragon Shadow Hall|The ceiling is open to the sky through a shattered dome, revealing churning storm clouds. Scorch marks streak every surface. The entire room vibrates faintly with a deep, rhythmic sound.|F,B|23,18
20|Hidden Tunnel|The floor collapsed, dropping you into a narrow stone passage smelling of ancient earth. The walls are covered in glowing moss that pulses like a heartbeat.|F,B|21,2
21|Guardian Chamber|A circular chamber dominated by a massive crystal pedestal. Runes cover every surface, glowing faintly blue. The air hums with power, and the pedestal seems to wait.|F,B|22,20
22|Excalibur Stone|A legendary sword is driven deep into a crystal outcrop. Its blade shines with pure silver light. Runes along the hilt spell a name: EXCALIBUR.|F,B|21,2
23|Library Entrance|Towering bookshelves rise into the shadows above, packed with ancient tomes. The books seem to lean toward you as you pass, their pages rustling without a breeze.|F,B|24,19
24|Library Depths|Deeper in the library, the shelves seem to shift when you aren't looking. Books lie open on reading tables, their ink still wet after centuries.|F,B|25,23
25|Alchemy Lab|Glass beakers bubble with liquids that glow in impossible colors. Something in a jar on the shelf still moves. A notebook lies open on a desk, its ink still fresh.|F,B|26,24
26|Lich Study|The temperature plummets to freezing. Frost crawls up the walls. A desk made of bones dominates the room, covered in ancient scrolls. A frozen throne sits against the far wall.|F,B|27,25
27|Lich Gate|A wall of crackling arcane energy blocks the passage, a barrier of swirling blue and purple lightning. Ghostly faces press against it from the other side, screaming silently.|F,B|28,26
28|Crypt Entrance|Cold air carrying the heavy scent of decay washes over you. Stone sarcophagi line the walls, their lids cracked or shattered. Death hangs in the air thickly.|F,B|29,27
29|Crypt Depths|The dead do not rest here. Bones are scattered across the floor in deliberate patterns. The walls moan with the voices of those buried within. Something shifts in the shadows.|F,B|30,28
30|Vampire Nest|Blood stains every surface. It drips from the ceiling and pools on the floor. Torn velvet curtains hang from the walls, and an ornate mirror lies shattered in the corner.|F,B|31,29
31|Throne of Blood|A throne carved from obsidian and bone dominates this chamber. Torn family portraits line the walls, their subjects' eyes gouged out. An empty goblet rests on the throne's arm.|F,B|32,30
32|Crimson Gate|A barrier of pulsing blood magic blocks the way, a wall of liquid crimson writhing with trapped souls. The gate beats like a heart, each pulse shaking the floor.|F,B|33,31
33|Lava Bridge|A narrow stone bridge spans a river of molten rock, the heat searing your lungs. Fire bubbles pop lazily below. The far side is barely visible through the heat haze.|F,B|34,32
34|Dragon Hoard|Mountains of gold coins and jeweled treasures stretch as far as the eye can see. The treasure gleams in the glow of heat from somewhere deep within. The gold shifts.|F,L,R,B|35,36,35,33
35|Hoard of the Dragon|The dragon's treasure chamber lies quiet now. Mountains of gold surround a massive corpse. A passage leads deeper into darkness, while another leads back.|F,B|36,34
36|Ritual Chamber|Ancient runes circle a stone altar at the center of this chamber. Dark energy pulses through the carvings. The altar seems to wait for an offering.|B|35
37|Smaug's Lair|The great wyrm's corpse lies sprawled across mountains of gold. As you approach with the Dragon Heart, the body begins to float upward, twisting and transforming.|F|38
38|Tiamat's Ascension|The chamber shakes as five heads emerge from the transforming corpse. Tiamat, the five-headed dragon goddess, has been reborn. Her heads roar in unison.|B|37

