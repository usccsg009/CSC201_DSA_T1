package com.example.csc201_dsa_t1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InventoryManagementSystem {
    private static Map<String, Item> inventory = new HashMap<>();
    private static List<Sale> salesHistory = new ArrayList<>();
    private static double totalProfit = 0.0;

    public static void main(String[] args) {
        String filename = "commands.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                processCommand(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        calculateAndPrintTotalProfit();
    }

    private static void processCommand(String command) {
        String[] parts = command.split(" ");
        String action = parts[0];
        switch (action) {
            case "ADD":
                String itemName = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                double price = Double.parseDouble(parts[3]);
                addItemToInventory(itemName, quantity, price);
                break;
            case "SELL":
                itemName = parts[1];
                quantity = Integer.parseInt(parts[2]);
                price = Double.parseDouble(parts[3]);
                sellItem(itemName, quantity, price);
                break;
            case "WRITEOFF":
                itemName = parts[1];
                quantity = Integer.parseInt(parts[2]);
                writeOffItem(itemName, quantity);
                break;
            case "DONATE":
                itemName = parts[1];
                quantity = Integer.parseInt(parts[2]);
                donateItem(itemName, quantity);
                break;
            case "RETURN":
                itemName = parts[1];
                quantity = Integer.parseInt(parts[2]);
                price = Double.parseDouble(parts[3]);
                returnItem(itemName, quantity, price);
                break;
            case "CHECK":
                outputInventory();
                break;
            case "PROFIT":
                calculateAndPrintTotalProfit();
                break;
            default:
                System.out.println("Invalid command: " + command);
        }
    }

    private static void addItemToInventory(String itemName, int quantity, double price) {
        if (inventory.containsKey(itemName)) {
            Item item = inventory.get(itemName);
            item.quantity += quantity;
            item.totalCost += quantity * price;
        } else {
            inventory.put(itemName, new Item(quantity, quantity * price));
        }
    }

    private static void sellItem(String itemName, int quantity, double price) {
        if (inventory.containsKey(itemName) && inventory.get(itemName).quantity >= quantity) {
            inventory.get(itemName).quantity -= quantity;
            salesHistory.add(new Sale(itemName, quantity, price));
            totalProfit += quantity * (price - inventory.get(itemName).totalCost / quantity);
        } else {
            System.out.println("SELL Error: Insufficient quantity of " + itemName + " in inventory.");
        }
    }

    private static void writeOffItem(String itemName, int quantity) {
        if (inventory.containsKey(itemName) && inventory.get(itemName).quantity >= quantity) {
            inventory.get(itemName).quantity -= quantity;
            totalProfit -= quantity * (inventory.get(itemName).totalCost / inventory.get(itemName).quantity);
        } else {
            System.out.println("WRITEOFF Error: Insufficient quantity of " + itemName + " in inventory.");
        }
    }

    private static void donateItem(String itemName, int quantity) {
        if (inventory.containsKey(itemName) && inventory.get(itemName).quantity >= quantity) {
            inventory.get(itemName).quantity -= quantity;
        } else {
            System.out.println("DONATE Error: Insufficient quantity of " + itemName + " in inventory.");
        }
    }

    private static void returnItem(String itemName, int quantity, double price) {
        for (int i = salesHistory.size() - 1; i >= 0; i--) {
            Sale sale = salesHistory.get(i);
            if (sale.itemName.equals(itemName) && sale.price == price) {
                if (sale.quantity >= quantity) {
                    salesHistory.remove(i);
                    totalProfit -= quantity * (price - sale.totalCost / quantity);
                    break;
                } else {
                    System.out.println("RETURN Error: Not enough items to return.");
                }
            }
        }
    }

    private static void outputInventory() {
        for (Map.Entry<String, Item> entry : inventory.entrySet()) {
            System.out.println("Item: " + entry.getKey() + ", Quantity: " + entry.getValue().quantity);
        }
    }

    private static void calculateAndPrintTotalProfit() {
        System.out.println("Profit/Loss: " + totalProfit);
    }

    static class Item {
        int quantity;
        double totalCost;

        Item(int quantity, double totalCost) {
            this.quantity = quantity;
            this.totalCost = totalCost;
        }
    }

    static class Sale {
        String itemName;
        int quantity;
        double price;
        double totalCost;

        Sale(String itemName, int quantity, double price) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
            this.totalCost = quantity * price;
        }
    }
}
