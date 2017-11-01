package com.company;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Starting server");
        ArrayList<Player> players = new ArrayList<>();
        int readyPlayers = 0;
        String read = scanner.nextLine();
        while (!Objects.equals(read, "Starting game")) {
            Player player = null;
            String name = null;
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
            else if (read.toLowerCase().startsWith("ready")){
                name = read.substring(6);
                for (Player player1 : players) {
                    if (player1.name.equals(name)) {
                        player = player1;
                        break;
                    }
                }
                if (player != null && !player.ready) {
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
        for (Player player : players) {
            System.out.println(player.name);
        }

        System.out.println("Generating map");
        if (readyPlayers != 2) {
            System.out.println("Can not generate map");
            return;
        }

        Cell[][] cells = new Cell[5][7];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                if ((i + j) % 2 == 0) {
                    cells[i][j] = new Cell();
                }
            }
        }
        cells[1][3].player = players.get(0);
        cells[1][3].unit = 2;
        cells[3][3].player = players.get(1);
        cells[3][3].unit = 3;

        //Map building
        mapBuild(cells);
        //Game loop start
        int playerTurn = 0;
        while (true) {
            //First phase start
            while (true) {
                System.out.print(players.get(playerTurn).name);
                System.out.println("'s turn");
                int cordI;
                int cordJ;
                Cell selectedCell;
                boolean good = false;
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 7; j++) {
                        if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn) && cells[i][j].unit != 1) {
                            good = true;
                        }
                    }
                }
                if (!good) {
                    break;
                }
                while (true) {
                    String[] select = null;
                    String line;
                    while (true) {
                        line = scanner.nextLine();
                        if (line.startsWith(players.get(playerTurn).name + ":")) {
                            break;
                        }
                    }
                    select = line.substring(2).split(" ");
                    cordI = Integer.parseInt(select[0]);
                    cordJ = Integer.parseInt(select[1]);
                    selectedCell = cells[cordI][cordJ];
                    if (selectedCell.player != players.get(playerTurn) || selectedCell.unit < 2) {
                        System.out.println("Invalid choice!");
                    } else {
                        break;
                    }
                }
                ArrayList<Cell> nearCells = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 7; j++) {
                        int dI = Math.abs(cordI - i);
                        int dJ = Math.abs(cordJ - j);
                        if ((dI == 2 && dJ == 0) || (dI == 1 && dJ == 1)) {
                            nearCells.add(cells[i][j]);
                        }
                    }
                }
                Cell goCell;
                while (true) {
                    String[] go;
                    String line;
                    while (true) {
                        line = scanner.nextLine();
                        if (line.startsWith(players.get(playerTurn).name + ":")) {
                            break;
                        }
                    }
                    go = line.substring(2).split(" ");
                    goCell = cells[Integer.parseInt(go[0])][Integer.parseInt(go[1])];
                    if (!nearCells.contains(goCell)) {
                        System.out.println("Invalid choice!");
                    } else {
                        break;
                    }
                }
                if (goCell.player == null) {
                    goCell.player = players.get(playerTurn);
                    goCell.unit = selectedCell.unit - 1;
                    selectedCell.unit = 1;
                } else {
                    if (selectedCell.unit > goCell.unit) {
                        goCell.player = players.get(playerTurn);
                    }
                    goCell.unit = Math.abs(selectedCell.unit - goCell.unit);
                    if (goCell.unit == 0) {
                        goCell.unit = 1;
                    }
                    selectedCell.unit = 1;
                }
                mapBuild(cells);
            }
            //First phase end

            System.out.println("Upgrade phase");
            //mapBuild(cells);

            //Second phase start
            int energy = 0;
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 7; j++) {
                    if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn)) {
                        energy++;
                    }
                }
            }
            System.out.println("You have " + energy + " energy left");
            Cell upgradeCell;
            while (true) {
                String[] upgrade;
                String line;
                while (true) {
                    line = scanner.nextLine();
                    if (line.startsWith(players.get(playerTurn).name + ":")) {
                        break;
                    }
                }
                upgrade = line.substring(2).split(" ");
                upgradeCell = cells[Integer.parseInt(upgrade[0])][Integer.parseInt(upgrade[1])];
                if (upgradeCell.player != players.get(playerTurn)) {
                    System.out.println("Invalid choice!");
                }
                else {
                    energy--;
                    upgradeCell.unit++;
                    mapBuild(cells);
                    System.out.println(energy + " energy left");
                }
                if (energy == 0) {
                    break;
                }
            }

            playerTurn++;
            if (playerTurn >= players.size()) {
                playerTurn = 0;
            }
        }
    }

    public static void mapBuild(Cell[][] cells) {
        for (Cell[] cells1 : cells) {
            for (Cell cell : cells1) {
                if (cell == null) {
                    System.out.print("__ ");
                } else if (cell.player == null) {
                    System.out.print("## ");
                } else {
                    System.out.print(cell.unit);
                    System.out.print(cell.player.name);
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}