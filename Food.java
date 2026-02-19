/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.food;

/**
 *
 * @author IDuran2026
 */


// ========== Abstract Base Class ==========
abstract class FoodBase {
    private String name;
    private int calories; // per 100g

public FoodBase(String name, int calories) {
    this.name = name;
    this.calories = calories;
    }

public String getName() {
    return name;
    }

public int getCalories() {
    return calories;
    }

public abstract String taste();

public void eat() {
    System.out.println("Eating " + name + "... It tastes " + taste() + ".");
}

@Override
public String toString() {
    return name + " (" + calories + " kcal/100g)";
    }
}

// ========== Interface ==========
interface Cookable {
    void cook();
}

// ========== Meat Hierarchy ==========
abstract class Meat extends FoodBase implements Cookable {
    private String animalSource;

    public Meat(String name, int calories, String animalSource) {
    super(name, calories);
    this.animalSource = animalSource;
    }

public String getAnimalSource() {
    return animalSource;
    }

@Override
public void cook() {
    System.out.println("Cooking " + getName() + " from " + animalSource + ".");
    }
}

abstract class RedMeat extends Meat {
    public RedMeat(String name, int calories, String animalSource) {
    super(name, calories, animalSource);
    }

@Override
public String taste() {
    return "rich and savory";
    }
}

class Beef extends RedMeat {
    public Beef() {
    super("Beef", 250, "Cow");
    }
}

class Lamb extends RedMeat {
    public Lamb() {
    super("Lamb", 294, "Sheep");
    }
}

abstract class Poultry extends Meat {
    public Poultry(String name, int calories, String animalSource) {
    super(name, calories, animalSource);
}

@Override
public String taste() {
    return "mild and tender";
    }
}

class Chicken extends Poultry {
    public Chicken() {
    super("Chicken", 165, "Chicken");
    }
}

class Turkey extends Poultry {
    public Turkey() {
    super("Turkey", 135, "Turkey");
    }
}

// ========== Vegetable Hierarchy ==========
abstract class Vegetable extends FoodBase {
private boolean isRoot;

public Vegetable(String name, int calories, boolean isRoot) {
    super(name, calories);
    this.isRoot = isRoot;
    }

public boolean isRoot() {
    return isRoot;
    }

@Override
public String taste() {
    return "earthy and fresh";
    }
}

abstract class LeafyGreen extends Vegetable {
    public LeafyGreen(String name, int calories) {
    super(name, calories, false);
}

@Override
public String taste() {
    return "mild and slightly bitter";
}
}

class Spinach extends LeafyGreen {
public Spinach() {
super("Spinach", 23);
}
}

class Lettuce extends LeafyGreen {
public Lettuce() {
super("Lettuce", 15);
}
}

abstract class RootVegetable extends Vegetable {
    public RootVegetable(String name, int calories) {
    super(name, calories, true);
}

@Override
public String taste() {
    return "sweet and starchy";
    }
}

class Carrot extends RootVegetable {
    public Carrot() {
    super("Carrot", 41);
    }
}

class Potato extends RootVegetable {
    public Potato() {
    super("Potato", 77);
    }
}

// ========== Fruit Hierarchy ==========
class Fruit extends FoodBase {
    public Fruit(String name, int calories) {
    super(name, calories);
}

@Override
public String taste() {
    return "sweet or tangy";
    }
}

class Apple extends Fruit {
    public Apple() {
    super("Apple", 52);
}

@Override
public String taste() {
    return "sweet and crisp";
    }
}

class Banana extends Fruit {
    public Banana() {
super("Banana", 89);
}

@Override
public String taste() {
    return "creamy and sweet";
    }
}

// ========== Public Class with main ==========
public class Food {
    public static void main(String[] args) {
    FoodBase[] foods = {
    new Beef(),
    new Chicken(),
    new Spinach(),
    new Carrot(),
    new Apple(),
    new Banana()
    };

System.out.println("=== Food Tasting Menu ===\n");
for (FoodBase f : foods) {
System.out.println(f);
f.eat();
if (f instanceof Cookable) {
    ((Cookable) f).cook();
}
    System.out.println();
}

System.out.println("=== All items are FoodBase ===");
    for (FoodBase f : foods) {
    System.out.println(f.getName() + " is a " + f.getClass().getSimpleName());
        }
    }
}
