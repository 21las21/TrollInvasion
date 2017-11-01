package com.company;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Starting server");
        ArrayList<String> players = new ArrayList<>();
        ArrayList<String> readyPlayers = new ArrayList<>();
        String read = scanner.nextLine();
        while (read != "Starting game") {
            if (read.startsWith("+")) {
                players.add(read.substring(1));
            }
            else if (read.startsWith("Ready")) {
                readyPlayers.add(read.substring(1));
            }
            if (players.size() == readyPlayers.size()) {
                read = "Starting game";
                System.out.println(read);
                break;
            }
            read = scanner.nextLine();
        }
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i));
        }
    }
}
