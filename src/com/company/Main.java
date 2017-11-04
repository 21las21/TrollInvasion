package com.company;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
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
        ArrayList<Character> colors = new ArrayList<>();
        ArrayList<Cell> outputs = new ArrayList<>();
        Cell output = null;
        int readyPlayers = 0;
        int colorPlayers = 0;
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
            else {
                String[] line = read.split(":");
                if (line.length == 2 && line[1].toLowerCase().startsWith("selectcolor")) {
                    name = line[0];
                    for (Player player1 : players) {
                        if (player1.name.equals(name)) {
                            player = player1;
                            break;
                        }
                    }
                    line = line[1].split(" ");
                    if (line.length == 2) {
                        char color = line[1].charAt(0);
                        if (!colors.contains(color)) {
                            colorPlayers++;
                            colors.add(color);
                            player.color = color;
                            print("playerColor", player.name, player.color);
                        }
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
        Bot bot;
        BadBot badBot = null;
        if (readyPlayers == 1) {
//            bot = new Bot("Bot", true);
            badBot = new BadBot("BadBot", true);
//            players.add(bot);
            players.add(badBot);
//            players.get(players.size() - 1).color = (char) ('A' + players.size() - 1);
//            print("playerColor", "Bot", players.get(players.size() - 1).color);
        }
        Iterator<Player> iterator = players.iterator();
//        System.out.println("Players:");
        for (int i = 0; colorPlayers < players.size();) {
            if (!colors.contains((char)('A' + i)) && iterator.hasNext()) {
                Player player = iterator.next();
                if (player.color == (char)0) {
                    player.color = (char) ('A' + i);
                    colorPlayers++;
                    colors.add(player.color);
                }
            }
            else {
                i++;
            }
//            System.out.println(players.get(i).name + " " + players.get(i).color);
        }
        for (int i = 0; i < players.size(); i++) {
            print("playerColor", players.get(i).name, players.get(i).color);
        }

//        System.out.println("Generating map");
        if (readyPlayers > 2) {
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
//                    int cordI = 0;
//                    int cordJ = 0;
                    Cell selectedCell;
                    boolean good = false;
                    boolean canTurn = false;
                    check:
                    {
                        for (int i = 0; i < mapI; i++) {
                            for (int j = 0; j < mapJ; j++) {
                                if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn) && cells[i][j].unit != 1) {
                                    good = true;
                                    nearCells = cells[i][j].nearCells(cells);
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
//                    outputs = new ArrayList<>();
//                    if (players.get(playerTurn) instanceof Bot) {
//                        outputs = ((Bot)players.get(playerTurn)).turn(cells, players, playerTurn);
//                    }
                    int index = 0;
                    while (true) {
//                        String[] select;
                        String line;
                        while (true) {
//                            if (players.get(playerTurn) instanceof Bot)
//                                line = String.valueOf(outputs.get(0).cellI + outputs.get(0).cellJ);
                            if (players.get(playerTurn) instanceof BadBot) {
                                selectedCell = ((BadBot)players.get(playerTurn)).select(cells, players, playerTurn, index);
                                index++;
                                break;
                            }
                            else {
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
                        }
                        canTurn = false;
                        nearCells = selectedCell.nearCells(cells);
                        for (Cell cell : nearCells) {
                            if (cell != null && cell.player != players.get(playerTurn)) {
                                canTurn = true;
                            }
                        }
                        if (!canTurn || selectedCell.player != players.get(playerTurn) || selectedCell.unit < 2) {
                            if (!(players.get(playerTurn) instanceof BadBot))
                                System.err.println("Invalid choice!");
                        } else {
                            print("selectCell", selectedCell.cellI, selectedCell.cellJ);
//                            System.out.println("Selected cell: " + cordI + " " + cordJ);
                            break;
                        }
                    }
//                    nearCells = selectedCell.nearCells(cells);
                    Cell goCell;
                    index = 0;
                    while (true) {
//                        String[] go;
                        String line;
//                        int goI;
//                        int goJ;
                        while (true) {
//                            if (players.get(playerTurn) instanceof Bot)
//                                line = String.valueOf(outputs.get(1).cellI + outputs.get(1).cellJ);
                            if (players.get(playerTurn) instanceof BadBot) {
                                goCell = ((BadBot)players.get(playerTurn)).go(cells, players, playerTurn, index);
                                index++;
                                break;
                            }
                            else {
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
                        }
                        if (!nearCells.contains(goCell) || (goCell.player != null && goCell.player.equals(players.get(playerTurn)))) {
                            if (!(players.get(playerTurn) instanceof BadBot))
                                System.err.println("Invalid choice!");
                        } else {
                            break;
                        }
                    }
                    Cell[] cellsAttack = selectedCell.cellAttack(cells, goCell, players, playerTurn);
                    selectedCell = cellsAttack[0];
                    goCell = cellsAttack[1];
//                    if (goCell.player == null) {
//                        goCell.player = players.get(playerTurn);
//                        goCell.unit = selectedCell.unit - 1;
//                        selectedCell.unit = 1;
//                    } else {
//                        goCell.unit = selectedCell.unit - goCell.unit;
//                        if (Math.abs(goCell.unit) > 1) {
//                            goCell.player = (goCell.unit > 1 ? players.get(playerTurn) : goCell.player);
//                            goCell.unit = Math.abs(goCell.unit);
//                        }
//                        else {
//                            double capture = new Random().nextDouble();
//                            double chance;
//                            if (goCell.unit == 0)
//                                chance = 0.5;
//                            else if (goCell.unit == 1)
//                                chance = 0.25;
//                            else if (goCell.unit == -1)
//                                chance = 0.75;
//                            else
//                                chance = 1;
//                            goCell.unit = 1;
//                            goCell.player = (capture > chance ? players.get(playerTurn) : goCell.player);
//                        }
//                        selectedCell.unit = 1;
//                    }
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
            int index = 0;
//            boolean good;
            second:
            {
                while (true) {
//                    String[] upgrade;
                    String line;
//                    int upI;
//                    int upJ;
                    while (true) {
//                        if (players.get(playerTurn) instanceof Bot)
//                            line = String.valueOf(outputs.get(2).cellI + outputs.get(2).cellJ);
                        if (players.get(playerTurn) instanceof BadBot) {
                            upgradeCell = ((BadBot)players.get(playerTurn)).upgrade(cells, players, playerTurn, index);
                            index++;
                            if (index >= badBot.allPlayerCells(cells, players, playerTurn).size())
                                index = 0;
                            break;
                        }
                        else {
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
                    }
                    if (upgradeCell.player != players.get(playerTurn) || upgradeCell.unit + 1 > upgradeCell.maxUnit) {
                        if (!(players.get(playerTurn) instanceof BadBot))
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
            } catch (Exception ignored) {
            }
        }
        else
            return null;
        if (cell != null) {
            return new Answer(cell, true);
        }
        else {
            return new Answer(null, false);
        }
    }
}