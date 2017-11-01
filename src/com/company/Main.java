package com.company;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Starting server");
        ArrayList<Player> players = new ArrayList<>();
        int readyPlayers = 0;
        String read = scanner.nextLine();
        while (read != "Starting game") {
            String name = read.substring(1);

            if (read.startsWith("+") && (!players.contains(new Player(name, false)))) {
                players.add(new Player(name, false));
            }
            else if (read.startsWith("Ready") && players.contains(new Player(read.substring(6), false))) {
                readyPlayers++;
            }
            if (readyPlayers > 0 && players.size() == readyPlayers) {
                read = "Starting game";
                System.out.println(read);
                break;
            }
            read = scanner.nextLine();
        }
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i).Name);
        }

        System.out.println("Generating map");
        if (readyPlayers == 2) {
            System.out.println("## __ ## __ ## __ ##");
            System.out.println("__ ## __ 2B __ ## __");
            System.out.println("## __ ## __ ## __ ##");
            System.out.println("__ ## __ 2A __ ## __");
            System.out.println("## __ ## __ ## __ ##");
        }
        else
            System.out.println("Can not generate map");
    }
}
