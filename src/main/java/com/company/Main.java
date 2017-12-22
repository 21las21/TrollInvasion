package com.company;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Game> games = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<Player> lobbyPlayers = new ArrayList<>();
        while (true) {
            String line = scanner.nextLine();
            if (line.startsWith("+")) {
                String name = line.substring(1);
                boolean good = true;
                for (Player player : lobbyPlayers)
                    if (player.name.equals(name)) {
                        good = false;
                        break;
                    }
                if (good) {
                    Player player = new Player(name, null);
                    lobbyPlayers.add(player);
                }
            }
            String[] input = line.split(":");
            if (input.length == 2) {
                String name = input[0];
                Player player = null;
                for (Player playerCheck : players)
                    if (playerCheck.name.equals(name))
                        player = playerCheck;
                if (player == null) {
                    for (Player playerCheck : lobbyPlayers)
                        if (playerCheck.name.equals(name))
                            player = playerCheck;
                    if (player != null) {
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
//                                player = new Player(name, game);
                                    player.game = game;
                                    game.name = gameName;
                                    players.add(player);
                                    lobbyPlayers.remove(player);
//                                    StringBuilder playersLine = new StringBuilder();
//                                    for (Player player1 : lobbyPlayers)
//                                        playersLine.append(player1.name).append(',');
//                                    if (playersLine.length() > 0) {
//                                        playersLine.deleteCharAt(playersLine.length() - 1).append(':');
//                                        System.out.println(playersLine.toString() + " gameList " + game.name + " " + (game.players.size() + 1) + " notStarted");
//                                    }
                                    game.acceptInput("+" + name + " player");
                                }
                            }
                        } else if (line.startsWith("joinGame")) {
                            input = line.split(" ");
                            if (input.length == 3) {
                                String gameName = input[1];
                                for (Game game : games)
                                    if (!game.isStarted && game.name.equals(gameName)) {
//                                        player = new Player(name, game);
                                        player.game = game;
                                        if (line.endsWith(" spectator")) {
                                            players.add(player);
                                            lobbyPlayers.remove(player);
                                            game.acceptInput("+" + name + " spectator");
                                        } else if (line.endsWith(" player")) {
                                            players.add(player);
                                            lobbyPlayers.remove(player);
                                            game.acceptInput("+" + name + " player");
                                        }
                                        break;
                                    }
                            }
                        } else if (line.equals("listGames")) {
                            for (Game game : games)
                                if (!game.isStarted)
                                    System.out.println(name + ": gameList " + game.name + " " + game.players.size() + " notStarted");
                                else
                                    System.out.println(name + ": gameList " + game.name + " " + game.players.size() + " started");
                        } else if (input[1].equals("-"))
                            lobbyPlayers.remove(player);
                    }
                } else {
                    if (input[1].equals("leaveGame")) {
                        players.remove(player);
                        lobbyPlayers.add(player);
                        ArrayList<Player> gamePlayers = new ArrayList<>();
                        for (Player player1 : player.game.players)
                            if (!(player1 instanceof BadBot))
                                gamePlayers.add(player1);
                        remove:
                        {
                            if (gamePlayers.size() == 1) {
                                games.remove(player.game);
                                player.game = null;
                                player.mode = -1;
                                System.out.println(name + ": gameLeft " + name);
                                break remove;
                            }
                            player.game.acceptInput('-' + player.name);
                        }

                    } else {
                        for (Game game : games) {
                            if (!game.isFinished && game.name.equals(player.game.name)) {
//                                boolean change = false;
//                                if (!game.isStarted)
//                                    change = true;
                                game.acceptInput(line);
//                                if (!game.isStarted)
//                                    change = false;
//                                if (change)
//                                    System.out.println(name + ": gameList " + game.name + " " + game.players.size() + " started");
                                if (game.isFinished) {
                                    games.remove(game);
                                }
                                break;
                            }
                        }

                    }
                }
            }
        }
    }
}