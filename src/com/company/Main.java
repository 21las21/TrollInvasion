package com.company;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Game> games = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();
        while (true) {
            String line = scanner.nextLine();
            String[] input = line.split(":");
            if (input.length == 2) {
                String name = input[0];
                Player player = null;
                for (Player playerCheck : players)
                    if (playerCheck.name.equals(name))
                        player = playerCheck;
                if (player == null) {
                    line = input[1];
                    if (line.startsWith("createGame")) {
                        input = line.split(" ");
                        if (input.length == 2) {
                            String gameName = input[1];
                            boolean good = true;
                            for (Game game : games)
                                if (game.name.equals(gameName)) {
                                    good = false;
                                    break;
                                }
                            if (good) {
                                Game game = new Game();
                                games.add(game);
                                player = new Player(name, game);
                                game.name = gameName;
                                players.add(player);
                                game.acceptInput("+" + name);
                            }
                        }
                    } else if (line.startsWith("joinGame")) {
                        input = line.split(" ");
                        if (input.length == 2) {
                            String gameName = input[1];
                            for (Game game : games)
                                if (!game.isStarted && game.name.equals(gameName)) {
                                    player = new Player(name, game);
                                    players.add(player);
                                    game.acceptInput("+" + name);
                                    break;
                                }
                        }
                    } else if (line.equals("listGames")) {
                        for (Game game : games)
                            if (!game.isStarted)
                                System.out.println(name + ": gameList " + game.name + " " + game.players.size());
                    }
                } else {
                    for (Game game : games)
                        if (!game.isFinished && game.name.equals(player.game.name)) {
                            game.acceptInput(line);
                            break;
                        }
                }
            }
        }
    }
}