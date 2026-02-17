/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.vehicletest;

/**
 *
 * @author IDuran2026
 */

// ============================================================================
// SPEEDOMETER INTERFACE & VEHICLE BASE CLASS
// ============================================================================
interface Speedometer {
 public void setSpeed(double inSpeed);
 public double getSpeed();
}

class Vehicle implements Speedometer {
 protected String brandName = "";
 protected double speed = 0.0;
 protected int passengers = 0;
 protected double cargoWeight = 0.0;
 
 public Vehicle() { }
 public Vehicle(String inBrand, double inSpeed, int inPassengers, double inCargo) {
 brandName = inBrand; speed = inSpeed; passengers = inPassengers; cargoWeight = inCargo;
 }
 public String getBrand() { return brandName; }
 public void setBrandName(String inBrand) { brandName = inBrand; }
 public double getSpeed() { return speed; }
 public void setSpeed(double inSpeed) { speed = inSpeed; }
 public int getPassengers() { return passengers; }
 public void setPassengers(int inPassengers) { passengers = inPassengers; }
 public double getCargoWeight() { return cargoWeight; }
 public void setCargoWeight(double inCargoWeight) { cargoWeight = inCargoWeight; }
 
 public String toString() {
 return "Brand: \t\t\t" + getBrand() + "\nSpeed (mph): \t" + getSpeed() + 
 "\nPassengers: \t" + getPassengers() + "\nCargo (lbs): \t" + getCargoWeight() + "\n";
 }
}

// ============================================================================
// CAR CLASS & INHERITANCE
// ============================================================================
class Car extends Vehicle {
 protected int wheels = 4;
 protected String color = "White";
 protected boolean spoiler = false, stereo = false;
 protected double mpg = 0.0;
 protected String fuelType = "Gasoline";

 public Car(String inBrand, double inSpeed, int inPassengers, double inCargo, double inMPG) {
 super(inBrand, inSpeed, inPassengers, inCargo); mpg = inMPG;
 }
 public Car(String inBrand, double inSpeed, int inPassengers, double inCargo, double inMPG, String inColor) {
 this(inBrand, inSpeed, inPassengers, inCargo, inMPG); color = inColor;
 }
 public void setSpoiler(boolean inSpoiler) { spoiler = inSpoiler; }
 public boolean getSpoiler() { return spoiler; }
 public void setStereo(boolean inStereo) { stereo = inStereo; }
 public boolean getStereo() { return stereo; }
 
 @Override public double getSpeed() { return spoiler ? super.getSpeed() + 20 : super.getSpeed(); }
 public double getMPG() { return stereo ? mpg - (mpg / 10) : mpg; }
 public String toString() {
 return super.toString() + "Color: \t\t\t" + color + "\nFuel Type: \t\t" + fuelType + 
 "\nMPG: \t\t\t" + String.format("%.1f", getMPG()) + "\nWheels: \t\t" + wheels + "\n";
 }
}

// 1. SUV
class SUV extends Car {
 private double towingCapacity; private boolean is4WD; private double groundClearance; private boolean hasThirdRow;
 public SUV(String inBrand, double inSpeed, int inPassengers, double inCargo, double inMPG, String inColor,
 double towingCapacity, boolean is4WD, double groundClearance, boolean hasThirdRow) {
 super(inBrand, inSpeed, inPassengers, inCargo, inMPG, inColor);
 this.towingCapacity = towingCapacity; this.is4WD = is4WD; this.groundClearance = groundClearance;
 this.hasThirdRow = hasThirdRow; 
 }
 public SUV(String inBrand, double towingCapacity, boolean is4WD) {
 super(inBrand, 0, 5, 0, 22, "Silver");
 this.towingCapacity = towingCapacity; this.is4WD = is4WD; groundClearance = 8.5; hasThirdRow = false;
 }
 @Override public double getSpeed() { 
 double s = super.getSpeed(); if(s > 112) s = 112; return is4WD ? s * 0.95 : s;
 }
 @Override public String toString() {
 return "\n========== SUV ==========\n" + super.toString() + "Vehicle Type: \t\tSUV\n" +
 "Towing: \t\t" + towingCapacity + " lbs\n4WD: \t\t\t" + (is4WD ? "Yes" : "No") + 
 "\nGround Clearance: \t" + groundClearance + " in\n3rd Row: \t\t" + (hasThirdRow ? "Yes" : "No") + "\n";
 }
}

// 2. SportsCar
class SportsCar extends Car {
 private double hp, zeroToSixty, topSpeed; private boolean hasTurbo, isConvertible;
 public SportsCar(String inBrand, double inSpeed, int inPassengers, double inCargo, double inMPG, 
 String inColor, double hp, double zeroToSixty, boolean hasTurbo, boolean isConvertible) {
 super(inBrand, inSpeed, inPassengers, inCargo, inMPG, inColor);
 this.hp = hp; this.zeroToSixty = zeroToSixty; this.hasTurbo = hasTurbo; this.isConvertible = isConvertible;
 this.spoiler = true; topSpeed = Math.min(100 + (hp/10) + (hasTurbo?10:0) - (isConvertible?5:0), 230);
 }
 public SportsCar(String inBrand, double hp) {
 super(inBrand, 0, 2, 0, 15, "Red"); this.hp = hp; zeroToSixty = 1000/(hp*0.5);
 hasTurbo = hp > 500; isConvertible = false; spoiler = true; topSpeed = Math.min(100 + (hp/10) + (hasTurbo?10:0), 230);
 }
 @Override public double getSpeed() { 
 double s = super.getSpeed(); return s == 0 ? topSpeed : s + (spoiler && hasTurbo ? 30 : 20);
 }
 @Override public String toString() {
 return "\n========== SPORTS CAR ==========\n" + super.toString() + 
 "HP: \t\t\t" + String.format("%.0f", hp) + "\n0-60: \t\t\t" + zeroToSixty + "s\nTop Speed: \t\t" + 
 String.format("%.0f", topSpeed) + " mph\nTurbo: \t\t\t" + (hasTurbo ? "Yes" : "No") + "\n";
 }
}

// 3. ElectricCar
class ElectricCar extends Car {
 private double batteryKwh, rangeMiles, mpge;
 public ElectricCar(String inBrand, double inSpeed, int inPassengers, double inCargo, String inColor,
 double batteryKwh, double rangeMiles, boolean isDualMotor) {
 super(inBrand, inSpeed, inPassengers, inCargo, 0, inColor);
 this.batteryKwh = batteryKwh; this.rangeMiles = rangeMiles; 
 this.mpge = (rangeMiles * 33.7) / batteryKwh; this.mpg = mpge * 0.3; this.fuelType = "Electric";
 }
 public ElectricCar(String inBrand, double rangeMiles) {
 super(inBrand, 0, 5, 0, 0, "White");
 this.batteryKwh = rangeMiles / 4; this.rangeMiles = rangeMiles;
 this.mpge = (rangeMiles * 33.7) / batteryKwh; this.mpg = mpge * 0.3; this.fuelType = "Electric";
 }
 @Override public String toString() {
 return "\n========== ELECTRIC CAR ==========\n" + "Brand: \t\t\t" + getBrand() + 
 "\nSpeed: \t\t\t" + getSpeed() + " mph\nRange: \t\t\t" + rangeMiles + " miles\nBattery: \t\t" + 
 batteryKwh + " kWh\nMPGe: \t\t\t" + String.format("%.1f", mpge) + "\nFuel: \t\t\tElectric\n";
 }
}

// 4. PickupTruck
class PickupTruck extends Car {
 private double payload, towing; private boolean isDiesel;
 public PickupTruck(String inBrand, double inSpeed, int inPassengers, double inCargo, double inMPG,
 String inColor, double payload, double towing, boolean isDiesel) {
 super(inBrand, inSpeed, inPassengers, inCargo, inMPG, inColor);
 this.payload = payload; this.towing = towing; this.isDiesel = isDiesel;
 this.fuelType = isDiesel ? "Diesel" : "Gasoline";
 }
 public PickupTruck(String inBrand, double towing) {
 super(inBrand, 0, 5, 0, 18, "Black"); this.payload = 2000; this.towing = towing; this.isDiesel = false;
 }
 @Override public double getSpeed() { 
 double s = super.getSpeed(); if(towing > 5000) s *= 0.9; return Math.min(s, 105);
 }
 @Override public String toString() {
 return "\n========== PICKUP TRUCK ==========\n" + super.toString() + 
 "Payload: \t\t" + payload + " lbs\nTowing: \t\t" + towing + " lbs\nFuel: \t\t\t" + fuelType + "\n";
 }
}

// ============================================================================
// BOAT CLASS & INHERITANCE
// ============================================================================
class Boat extends Vehicle {
 protected double length; protected int engines; protected String hullType;
 public Boat(String inBrand, double inSpeed, int inPassengers, double inCargo,
 double length, int engines, String hullType) {
 super(inBrand, inSpeed, inPassengers, inCargo);
 this.length = length; this.engines = engines; this.hullType = hullType;
 }
 public Boat(String inBrand, double length) {
 super(inBrand, 0, 6, 0); this.length = length; this.engines = 1; this.hullType = "V-hull";
 }
 @Override public double getSpeed() { 
 double s = super.getSpeed(); return s == 0 ? Math.sqrt(length) * 5 : s;
 }
 public String toString() {
 return "\n========== BOAT ==========\n" + super.toString() + 
 "Length: \t\t" + length + " ft\nEngines: \t\t" + engines + "\nHull: \t\t\t" + hullType + "\n";
 }
}

// 1. Speedboat
class Speedboat extends Boat {
 private double hp; private boolean isJet;
 public Speedboat(String inBrand, double inSpeed, int inPassengers, double inCargo,
 double length, int engines, String hullType, double hp, boolean isJet) {
 super(inBrand, inSpeed, inPassengers, inCargo, length, engines, hullType);
 this.hp = hp; this.isJet = isJet;
 }
 public Speedboat(String inBrand, double hp) {
 super(inBrand, 0, 4, 0, 20, 1, "Deep V"); this.hp = hp; this.isJet = false;
 }
 @Override public double getSpeed() { 
 double s = super.getSpeed(); return s == 0 ? hp / 8 : s;
 }
 @Override public String toString() {
 return "\n========== SPEEDBOAT ==========\n" + super.toString() + 
 "HP: \t\t\t" + hp + "\nJet Drive: \t\t" + (isJet ? "Yes" : "No") + "\n";
 }
}

// 2. Sailboat
class Sailboat extends Boat {
 private double sailArea; private boolean hasMotor;
 public Sailboat(String inBrand, double inSpeed, int inPassengers, double inCargo,
 double length, int engines, String hullType, double sailArea, boolean hasMotor) {
 super(inBrand, inSpeed, inPassengers, inCargo, length, engines, hullType);
 this.sailArea = sailArea; this.hasMotor = hasMotor;
 }
 public Sailboat(String inBrand, double length) {
 super(inBrand, 0, 4, 0, length, 0, "Monohull");
 this.sailArea = length * 10; this.hasMotor = true;
 }
 @Override public double getSpeed() { return !hasMotor ? Math.sqrt(sailArea) * 0.5 : super.getSpeed(); }
 @Override public String toString() {
 return "\n========== SAILBOAT ==========\n" + super.toString() + 
 "Sail Area: \t\t" + sailArea + " sq ft\nAux Motor: \t\t" + (hasMotor ? "Yes" : "No") + "\n";
 }
}

// ============================================================================
// AIRPLANE CLASS & INHERITANCe
// ============================================================================
class Airplane extends Vehicle {
 protected double wingspan; protected int engines; protected double range;
 public Airplane(String inBrand, double inSpeed, int inPassengers, double inCargo,
 double wingspan, int engines, double range) {
 super(inBrand, inSpeed, inPassengers, inCargo);
 this.wingspan = wingspan; this.engines = engines; this.range = range;
 }
 public Airplane(String inBrand, double wingspan) {
 super(inBrand, 0, 150, 0); this.wingspan = wingspan; this.engines = 2; this.range = wingspan * 500;
 }
 @Override public double getSpeed() { double s = super.getSpeed(); return s == 0 ? 500 : s; }
 public String toString() {
 return "\n========== AIRPLANE ==========\n" + super.toString() + 
 "Wingspan: \t\t" + wingspan + " ft\nEngines: \t\t" + engines + "\nRange: \t\t\t" + range + " miles\n";
 }
}

// 1. CommercialJet
class CommercialJet extends Airplane {
 private String airline; private int seats;
 public CommercialJet(String inBrand, double inSpeed, int inPassengers, double inCargo,
 double wingspan, int engines, double range, String airline, int seats) {
 super(inBrand, inSpeed, inPassengers, inCargo, wingspan, engines, range);
 this.airline = airline; this.seats = seats;
 }
 public CommercialJet(String inBrand, String airline) {
 super(inBrand, 0, 180, 50000, 120, 2, 6500); this.airline = airline; this.seats = 180;
 }
 @Override public double getSpeed() { return 560; }
 @Override public String toString() {
 return "\n========== COMMERCIAL JET ==========\n" + super.toString() + 
 "Airline: \t\t" + airline + "\nSeats: \t\t\t" + seats + "\n";
 }
}

// 2. PrivateJet
class PrivateJet extends Airplane {
 private String owner; private boolean hasBed;
 public PrivateJet(String inBrand, double inSpeed, int inPassengers, double inCargo,
 double wingspan, int engines, double range, String owner, boolean hasBed) {
 super(inBrand, inSpeed, inPassengers, inCargo, wingspan, engines, range);
 this.owner = owner; this.hasBed = hasBed;
 }
 public PrivateJet(String inBrand, String owner) {
 super(inBrand, 0, 12, 1000, 60, 2, 4000); this.owner = owner; this.hasBed = true;
 }
 @Override public double getSpeed() { return 620; }
 @Override public String toString() {
 return "\n========== PRIVATE JET ==========\n" + super.toString() + 
 "Owner: \t\t\t" + owner + "\nBedroom: \t\t" + (hasBed ? "Yes" : "No") + "\n";
 }
}

// ============================================================================
// MOTORCYCLE CLASS & INHERITANCE
// ============================================================================
class Motorcycle extends Vehicle {
 protected double engineCC; protected String style;
 public Motorcycle(String inBrand, double inSpeed, int inPassengers, double inCargo,
 double engineCC, String style) {
 super(inBrand, inSpeed, inPassengers, inCargo);
 this.engineCC = engineCC; this.style = style;
 }
 public Motorcycle(String inBrand, double engineCC) {
 super(inBrand, 0, 1, 0); this.engineCC = engineCC; this.style = "Sport";
 }
 @Override public double getSpeed() { double s = super.getSpeed(); return s == 0 ? engineCC / 12 : s; }
 public void wheelie() { System.out.println("Wheelie! " + engineCC + "cc power!"); }
 public String toString() {
 return "\n========== MOTORCYCLE ==========\n" + super.toString() + 
 "Engine: \t\t" + engineCC + "cc\nStyle: \t\t\t" + style + "\nWheels: \t\t2\n";
 }
}

// 1. Cruiser
class Cruiser extends Motorcycle {
 private double seatHeight;
 public Cruiser(String inBrand, double inSpeed, int inPassengers, double inCargo,
 double engineCC, double seatHeight) {
 super(inBrand, inSpeed, inPassengers, inCargo, engineCC, "Cruiser");
 this.seatHeight = seatHeight;
 }
 public Cruiser(String inBrand, double engineCC) {
 super(inBrand, 0, 2, 50, engineCC, "Cruiser"); this.seatHeight = 26.5;
 }
 @Override public double getSpeed() { return super.getSpeed() * 0.8; }
 @Override public String toString() {
 return "\n========== CRUISER ==========\n" + super.toString() + 
 "Seat Height: \t\t" + seatHeight + " in\n";
 }
}

// 2. SportBike
class SportBike extends Motorcycle {
 private double zeroToSixty;
 public SportBike(String inBrand, double inSpeed, int inPassengers, double inCargo,
 double engineCC, double zeroToSixty) {
 super(inBrand, inSpeed, inPassengers, inCargo, engineCC, "Sport");
 this.zeroToSixty = zeroToSixty;
 }
 public SportBike(String inBrand, double engineCC) {
 super(inBrand, 0, 1, 0, engineCC, "Sport");
 this.zeroToSixty = 1000 / (engineCC * 0.8);
 }
 @Override public double getSpeed() { return super.getSpeed() * 1.3; }
 @Override public String toString() {
 return "\n========== SPORT BIKE ==========\n" + super.toString() + 
 "0-60: \t\t\t" + String.format("%.1f", zeroToSixty) + "s\n";
 }
}

// ============================================================================
// HOVERCRAFT CLASS -
// ============================================================================
class Hovercraft extends Vehicle {
 private double fanDiameter; private int liftFans; private boolean amphibious;
 public Hovercraft(String inBrand, double inSpeed, int inPassengers, double inCargo,
 double fanDiameter, int liftFans, boolean amphibious) {
 super(inBrand, inSpeed, inPassengers, inCargo);
 this.fanDiameter = fanDiameter; this.liftFans = liftFans; this.amphibious = amphibious;
 }
 public Hovercraft(String inBrand, double fanDiameter) {
 super(inBrand, 0, 12, 2000); this.fanDiameter = fanDiameter; this.liftFans = 2; this.amphibious = true;
 }
 @Override public double getSpeed() { 
 double s = super.getSpeed(); return s == 0 ? fanDiameter * 6 : s;
 }
 public void hover() { System.out.println("Hovering on air cushion!"); }
 public String toString() {
 return "\n========== HOVERCRAFT ==========\n" + super.toString() + 
 "Fan Diameter: \t\t" + fanDiameter + " ft\nLift Fans: \t\t" + liftFans + 
 "\nAmphibious: \t\t" + (amphibious ? "Yes" : "No") + "\n" +
 "**Can travel on land, water, ice, and mud!**\n";
 }
}

// ============================================================================
// TEST CLASS - DEMONSTRATES ALL VEHICLES
// ============================================================================
class VehicleTest {
 public static void main(String[] args) {
 System.out.println("=========================================");
 System.out.println(" VEHICLE INHERITANCE DEMO");
 System.out.println("=========================================\n");
 
 // Test CAR INHERITANCE (4 classes)
 SUV mySUV = new SUV("Toyota", 70, 5, 250, 24, "Blue", 5000, true, 9.2, true);
 System.out.println(mySUV);
 
 SportsCar mySportsCar = new SportsCar("Porsche", 0, 2, 0, 18, "Yellow", 640, 2.7, true, false);
 System.out.println(mySportsCar);
 
 ElectricCar myEV = new ElectricCar("Tesla", 0, 5, 0, "Red", 100, 396, true);
 System.out.println(myEV);
 
 PickupTruck myTruck = new PickupTruck("Ford", 75, 5, 800, 20, "Black", 2000, 8000, true);
 System.out.println(myTruck);
 
 // Test BOAT INHERITANCE (2 classes)
 Speedboat mySpeedboat = new Speedboat("Sea-Doo", 0, 3, 100, 18, 1, "Deep V", 300, false);
 System.out.println(mySpeedboat);
 
 Sailboat mySailboat = new Sailboat("Hunter", 0, 6, 200, 32, 0, "Monohull", 320, true);
 System.out.println(mySailboat);
 
 // Test AIRPLANE INHERITANCE (2 classes)
 CommercialJet myJet = new CommercialJet("Boeing", 0, 180, 50000, 120, 2, 6500, "Delta", 180);
 System.out.println(myJet);
 
 PrivateJet myPrivateJet = new PrivateJet("Gulfstream", 0, 12, 1000, 60, 2, 4000, "Elon Musk", true);
 System.out.println(myPrivateJet);
 
 // Test MOTORCYCLE INHERITANCE (2 classes)
 Cruiser myCruiser = new Cruiser("Harley", 0, 2, 30, 1200, 26.5);
 System.out.println(myCruiser);
 
 SportBike mySportBike = new SportBike("Yamaha", 0, 1, 0, 1000, 3.1);
 System.out.println(mySportBike);
 
 // Test IMPRESSIVE VEHICLE - HOVERCRAFT
 Hovercraft myHovercraft = new Hovercraft("Hovercraft Industries", 45, 12, 2000, 8, 2, true);
 System.out.println(myHovercraft);
 
 System.out.println("=========================================");
 System.out.println(" DEMONSTRATING METHODS");
 System.out.println("=========================================\n");
 
 
 myHovercraft.hover();
 mySportBike.wheelie();
 }
}