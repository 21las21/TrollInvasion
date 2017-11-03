package com.company;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static void print(String command, Object... args) {
        StringBuilder line = new StringBuilder(command);
        for (Object arg : args) {
            line.append(' ').append(arg.toString());
        }
        System.out.println(line);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
//        System.out.println("Starting server");
        ArrayList<Player> players = new ArrayList<>();
        int readyPlayers = 0;
        String read = scanner.nextLine();
        while (!Objects.equals(read, "Starting game")) {
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
                    for (Player player1 : players) {
                        print("readyStatus", player1.name, player1.ready);
//                        System.out.println(player1.name + " " + (player1.ready ? "ready" : "not ready"));
                    }
                }
            } else if (read.toLowerCase().endsWith(":ready")) {
                String[] line = read.split(":");
                if (line[1].toLowerCase().equals("ready")) {
                    name = line[0];
                    for (Player player1 : players) {
                        if (player1.name.equals(name)) {
                            player = player1;
                            break;
                        }
                    }
                    if (player != null && !player.ready) {
                        player.ready = true;
                        readyPlayers++;
                        print("readyStatus", player.name, true);
//                        System.out.println(player.name + " ready");
                    }
                }
            }
            if (readyPlayers > 0 && players.size() == readyPlayers) {
//                read = "Starting game";
                print("gameStart");
//                System.out.println(read);
                break;
            }
            read = scanner.nextLine();
        }
//        System.out.println("Players:");
        for (int i = 0; i < players.size(); i++) {
            players.get(i).color = (char) ('A' + i);
            print("playerColor", players.get(i).name, players.get(i).color);
//            System.out.println(players.get(i).name + " " + players.get(i).color);
        }

//        System.out.println("Generating map");
        if (readyPlayers != 2) {
            throw new RuntimeException("Can not generate map");
        }

        int mapI = 7;
        int mapJ = 7;
        Cell[][] cells = new Cell[mapI][mapJ];
        for (int i = 0; i < mapI; i++) {
            for (int j = 0; j < mapJ; j++) {
                if ((i + j) % 2 == 0) {
                    cells[i][j] = new Cell(i, j);
                }
            }
        }
        cells[1][3].player = players.get(0);
        cells[1][3].unit = 2;
        cells[5][3].player = players.get(1);
        cells[5][3].unit = 3;

        //Map building
        mapPrint(cells);
        //Game loop start
        int playerTurn = 0;
        while (true) {
            //First phase start
            first:
            {
                while (true) {
                    print("turn", players.get(playerTurn).name);
//                    System.out.print(players.get(playerTurn).name);
//                    System.out.println("'s turn");
                    ArrayList<Cell> nearCells;
                    int cordI = 0;
                    int cordJ = 0;
                    Cell selectedCell;
                    boolean good = false;
                    boolean canTurn = false;
                    check:
                    {
                        for (int i = 0; i < mapI; i++) {
                            for (int j = 0; j < mapJ; j++) {
                                if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn) && cells[i][j].unit != 1) {
                                    good = true;
                                    nearCells = cells[i][j].nearCells(cells, mapI, mapJ);
                                    for (Cell cell : nearCells) {
                                        if (cell != null && cell.player != players.get(playerTurn)) {
                                            canTurn = true;
                                            break check;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!good || !canTurn) {
                        break first;
                    }
                    while (true) {
//                        String[] select;
                        String line;
                        while (true) {
                            line = scanner.nextLine();
                            Answer answer = getCellFromMessage(line, players.get(playerTurn), cells);
                            if (answer != null) {
                                if (answer.cell == null) {
                                    if (answer.good)
                                        //Skip phase
                                        break first;
                                    else
                                        System.err.println("Invalid input!");
                                } else {
                                    selectedCell = answer.cell;
                                    break;
                                }
                            }
                        }
                        if (selectedCell.player != players.get(playerTurn) || selectedCell.unit < 2) {
                            System.err.println("Invalid choice!");
                        } else {
                            print("selectCell", cordI, cordJ);
//                            System.out.println("Selected cell: " + cordI + " " + cordJ);
                            break;
                        }
                    }
                    nearCells = selectedCell.nearCells(cells, mapI, mapJ);
                    Cell goCell;
                    while (true) {
//                        String[] go;
                        String line;
//                        int goI;
//                        int goJ;
                        while (true) {
                            line = scanner.nextLine();
                            Answer answer = getCellFromMessage(line, players.get(playerTurn), cells);
                            if (answer != null) {
                                if (answer.cell == null) {
                                    if (answer.good)
                                        //Skip phase
                                        break first;
                                    else
                                        System.err.println("Invalid input!");
                                } else {
                                    goCell = answer.cell;
                                    break;
                                }
                            }
                        }
                        if (!nearCells.contains(goCell) || (goCell.player != null && goCell.player.equals(players.get(playerTurn)))) {
                            System.err.println("Invalid choice!");
                        } else {
                            break;
                        }
                    }
                    if (goCell.player == null) {
                        goCell.player = players.get(playerTurn);
                        goCell.unit = selectedCell.unit - 1;
                        selectedCell.unit = 1;
                    } else {
                        goCell.unit = selectedCell.unit - goCell.unit;
                        if (Math.abs(goCell.unit) > 1) {
                            goCell.player = (goCell.unit > 1 ? players.get(playerTurn) : goCell.player);
                            goCell.unit = Math.abs(goCell.unit);
                        }
                        else {
                            double capture = new Random().nextDouble();
                            double chance;
                            if (goCell.unit == 0)
                                chance = 0.5;
                            else if (goCell.unit == 1)
                                chance = 0.25;
                            else if (goCell.unit == -1)
                                chance = 0.75;
                            else
                                chance = 1;
                            goCell.unit = 1;
                            goCell.player = (capture > chance ? players.get(playerTurn) : goCell.player);
                        }
                        selectedCell.unit = 1;
                    }
                    boolean enemyExist = false;
                    check:
                    {
                        for (int i = 0; i < mapI; i++) {
                            for (int j = 0; j < mapJ; j++) {
                                if (cells[i][j] != null && cells[i][j].player != null && cells[i][j].player != players.get(playerTurn)) {
                                    enemyExist = true;
                                    break check;
                                }
                            }
                        }
                    }
                    mapPrint(cells);
                    if (!enemyExist) {
                        print("gameFinish", players.get(playerTurn).name);
//                        System.out.println(players.get(playerTurn).name + " " + "won!");
                        return;
                    }
                }
            }
            //First phase end

            print("upgradePhase");
//            System.out.println("Upgrade phase");

            //Second phase start
            int energy = 0;
            for (int i = 0; i < mapI; i++) {
                for (int j = 0; j < mapJ; j++) {
                    if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn)) {
                        energy++;
                    }
                }
            }
            print("energyLeft", energy);
//            System.out.println("You have " + energy + " energy left");
            Cell upgradeCell;
//            boolean good;
            second:
            {
                while (true) {
//                    String[] upgrade;
                    String line;
//                    int upI;
//                    int upJ;
                    while (true) {
                        line = scanner.nextLine();
                        Answer answer = getCellFromMessage(line, players.get(playerTurn), cells);
                        if (answer != null) {
                            if (answer.cell == null) {
                                if (answer.good)
                                    //Skip phase
                                    break second;
                                else
                                    System.err.println("Invalid input!");
                            } else {
                                upgradeCell = answer.cell;
                                break;
                            }
                        }
                    }
                    if (upgradeCell.player != players.get(playerTurn) || upgradeCell.unit + 1 > upgradeCell.maxUnit) {
                        System.err.println("Invalid choice!");
                    } else {
                        energy--;
                        upgradeCell.unit++;
                        mapPrint(cells);
                        print("energyLeft", energy);
//                        System.out.println(energy + " energy left");
                    }
                    if (energy == 0) {
                        break;
                    }
                }
            }

            playerTurn++;
            if (playerTurn >= players.size()) {
                playerTurn = 0;
            }
        }
    }

    private static void mapPrint(Cell[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            Cell[] cells1 = cells[i];
            StringBuilder line = new StringBuilder();
            for (Cell cell : cells1) {
                if (cell == null) {
                    line.append("__|");
                } else if (cell.player == null) {
                    line.append("##|");
                } else {
                    line.append(cell.unit);
                    line.append(cell.player.color);
                    line.append("|");
                }
            }
            print("mapLine", i, line.substring(0, line.length() - 1));
        }
    }
    
    private static Answer getCellFromMessage(String message, Player player, Cell[][] cells) {
        boolean good = true;
        String[] messages;
        Cell cell = null;
        int numberI;
        int numberJ;
        if (message.startsWith(player.name + ":")) {
            messages = message.split(":");
            if (messages.length < 2) {
                return new Answer(null, false);
            }
            message = messages[1];
            if (message.toLowerCase().equals("next phase")) {
                return new Answer(null, true);
            }
            messages = message.split(" ");
            try {
                numberI = Integer.parseInt(messages[0]);
                numberJ = Integer.parseInt(messages[1]);
                cell = cells[numberI][numberJ];
            } catch (Exception exc) {
                good = false;
            }
        }
        else
            return null;
        return new Answer(cell, good);
    }
}