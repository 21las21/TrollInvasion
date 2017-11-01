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
            Player player = null;
            String name;
            if (read.startsWith("+")) {
                name = read.substring(1);
                for (Player player1 : players) {
                    if (player1.name.equals(name)) {
                        player = player1;
                        break;
                    }
                }
                if (player == null) {
                    player = new Player(name, false);
                    players.add(player);
                }
            }
            else if (read.startsWith("ready")){
                name = read.substring(6);
                for (Player player1 : players) {
                    if (player1.name.equals(name)) {
                        player = player1;
                        break;
                    }
                }
                if (player != null && player.ready == false) {
                    player.ready = true;
                    readyPlayers++;
                }
            }
            if (readyPlayers > 0 && players.size() == readyPlayers) {
                read = "Starting game";
                System.out.println(read);
                break;
            }
            read = scanner.nextLine();
        }
        for (int i = 0; i < players.size(); i++) {
            System.out.println(players.get(i).name);
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
