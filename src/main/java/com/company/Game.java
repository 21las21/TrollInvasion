package com.company;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

class Game {
    String name;
    String outline;
    boolean isFinished = false;
    boolean isStarted = false;
    ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Player> spectators = new ArrayList<>();
    private ArrayList<Player> playersRnd = new ArrayList<>();
    private ArrayList<Character> colors = new ArrayList<>();
    private StringBuilder line = new StringBuilder();
    private BadBot badBot = new BadBot("Bot", true);
    private Cell selectedCell = null;
    Cell[][] cells;
    private Map map;
    private int readyPlayers = 0;
    private int colorPlayers = 0;
    private int cellPhase = 0;
    private int playerTurn = 0;
    private int energy = 0;
//    private int indexBot = 0;
    private int mapI;
    private int mapJ;

    private void gameTurn(String playerName, int phase, char playerColor, Cell playerCell, int playerMode) {
        Player player = null;
        if (phase == 0) { //Player join
            for (Player player1 : players)
                if (player1.name.equals(playerName)) {
                    player = player1;
                    break;
                }
            if (player == null) {
                if (playerMode == 1) { //Player
                    player = new Player(playerName, false);
                    player.mode = 1;
                    players.add(player);
//                player.game = this;
//                print(outline, playerName + " join");
                } else if (playerMode == 0) { //Spectator
                    player = new Player(playerName, this);
                    player.mode = 0;
                    spectators.add(player);
                } else return;
                getOutline();
                if (playerMode == 0) {
                    print(playerName + ": gameEntered", name, "spectator");
                    print(outline, "spectatorJoin", playerName);
                } else {
                    print(playerName + ": gameEntered", name, "player");
                    for (Player player1 : players)
                        print(outline, "readyStatus", player1.name, player1.ready);
                }
            }
        } else if (phase == 1) { //Player left
            int index = 0;
            for (Player player1 : players) {
                if (player1.name.equals(playerName)) {
                    player = player1;
                    break;
                }
                index++;
            }
            if (player == null) {
                for (Player player1 : spectators) {
                    if (player1.name.equals(playerName)) {
                        player = player1;
                        break;
                    }
                    index++;
                }
                if (player == null)
                    return;
            }
            if (player.mode == 1) {
                readyPlayers--;
                print(outline + " gameLeft", player.name);
                if (!isStarted) {
                    players.remove(index);
                    player.game = null;
                    getOutline();
                }
//                if (line.length() > 0) {
//                    line.deleteCharAt(line.length() - 1).append(':');
//                    outline = line.toString();
//                }
                if (isStarted) {
                    player.game = null;
                    badBot.color = player.color;
                    players.set(index, badBot);
                    getOutline();
                    for (Cell[] cellRaw : cells)
                        for (Cell cellCheck : cellRaw)
                            if (cellCheck != null && cellCheck.player != null && cellCheck.player.equals(player))
                                cellCheck.player = badBot;
                    print(outline, "turn", players.get(playerTurn).name);
                    if (players.get(playerTurn) instanceof BadBot) {
//                        boolean canTurn = false;
//                        check:
//                        {
//                            for (Cell[] cells1 : cells)
//                                for (Cell cell : cells1) {
//                                    if (cell != null)
//                                        for (Cell cell1 : cell.nearCells(cells)) {
//                                            if (cell1 != null && cell1.player != players.get(playerTurn)) {
//                                                canTurn = true;
//                                                break check;
//                                            }
//                                        }
//                                }
//                        }
                        int canTurn = canTurn();
                        if (canTurn != -1) {
                            cellPhase = 0; //Select cell
                            if (players.get(playerTurn) instanceof BadBot) {
                                badBot.resetIndex();
                                Cell select = badBot.select(cells, players, playerTurn);
                                if (select.cellI == -1 && select.cellJ == -1)
                                    return;
                                acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                            }
                        } else {
                            energyCount();
                            cellPhase = 2; //Upgrade cell
                            print(outline, "upgradePhase");
                            print(players.get(playerTurn).name + ":", "energyLeft", energy);
                            if (players.get(playerTurn) instanceof BadBot) {
                                badBot.resetIndex();
                                Cell select = badBot.upgrade(cells, players, playerTurn);
                                acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                            }
                        }
                    }
                }
            } else if (player.mode == 0) {
                spectators.remove(index);
                print(outline + " gameLeft", player.name);
                player.game = null;
            }
        } else if (phase == 2) { //Player ready
            for (Player player1 : players) {
                if (player1.name.equals(playerName)) {
                    player = player1;
                    break;
                }
            }
            if (player != null && !player.ready) {
                player.ready = true;
                readyPlayers++;
                print(outline, "readyStatus", player.name, true);
                if (readyPlayers > 0 && players.size() == readyPlayers) {
                    isStarted = true;
                    Random random = new Random();
                    while (playersRnd.size() < players.size()) {
                        int rnd = random.nextInt(players.size());
                        Player player1 = players.get(rnd);
                        if (!playersRnd.contains(player1))
                            playersRnd.add(player1);
                    }
                    players = playersRnd;
                    print(outline, "gameStart");
                    BadBot badBot = new BadBot("Bot", true);
                    if (readyPlayers == 1)
                        players.add(0, badBot);
                    Iterator<Player> iterator = players.iterator();
                    for (int i = 0; colorPlayers < players.size(); i++) {
                        if (!colors.contains((char) ('A' + i)) && iterator.hasNext()) {
                            Player player1 = iterator.next();
                            if (player1.color == (char) 0) {
                                player1.color = (char) ('A' + i);
                                colorPlayers++;
                                colors.add(player1.color);
                            }
                        } else {
                            i++;
                        }
                    }
                    for (Player player1 : players) {
                        print(outline, "playerColor", player1.name, player1.color);
                    }
                    map = new Map();
                    cells = map.mapGenerate(players);
                    mapI = cells.length;
                    mapJ = cells[0].length;
                    map.mapPrint(this);
                    print(outline, "turn", players.get(playerTurn).name);
                    if (players.get(playerTurn) instanceof BadBot) {
//                        boolean canTurn = false;
//                        check:
//                        {
//                            for (Cell[] cells1 : cells)
//                                for (Cell cell : cells1) {
//                                    if (cell != null)
//                                        for (Cell cell1 : cell.nearCells(cells)) {
//                                            if (cell1 != null && cell1.player != players.get(playerTurn)) {
//                                                canTurn = true;
//                                                break check;
//                                            }
//                                        }
//                                }
//                        }
                        int canTurn = canTurn();
                        if (canTurn != -1) {
                            cellPhase = 0; //Select cell
                            if (players.get(playerTurn) instanceof BadBot) {
                                badBot.resetIndex();
                                Cell select = ((BadBot) players.get(playerTurn)).select(cells, players, playerTurn);
                                if (select.cellI == -1 && select.cellJ == -1)
                                    return;
                                acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                            }
                        } else {
                            energyCount();
                            cellPhase = 2; //Upgrade cell
                            print(outline, "upgradePhase");
                            print(players.get(playerTurn).name + ":", "energyLeft", energy);
                            if (players.get(playerTurn) instanceof BadBot) {
                                badBot.resetIndex();
                                Cell select = ((BadBot) players.get(playerTurn)).upgrade(cells, players, playerTurn);
                                acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                            }
                        }
                    }
                }
            }
        } else if (phase == 6) { //Player unready
            for (Player player1 : players)
                if (player1.name.equals(playerName)) {
                    player = player1;
                    break;
                }
            if (player != null && player.ready) {
                player.ready = false;
                readyPlayers--;
                print(outline, "readyStatus", playerName, false);
            }
        } else if (phase == 3) { //Player select color
            for (Player player1 : players) {
                if (player1.name.equals(playerName)) {
                    player = player1;
                    break;
                }
            }
            if (player != null && !colors.contains(playerColor)) {
                colorPlayers++;
                colors.add(playerColor);
                player.color = playerColor;
                print(outline,"playerColor", player.name, player.color);
            }
        } else if (phase == 4 && playerName.equals(players.get(playerTurn).name)) { //Skip phase
            if (cellPhase == 2) { //Upgrade cell
                if (playerTurn >= players.size() - 1)
                    playerTurn = 0;
                else
                    playerTurn++;
                print(outline, "turn", players.get(playerTurn).name);
                int canTurn = canTurn();
                if (canTurn == 1)
                    cellPhase = 0; //Select cell
                else {
                    cellPhase = 2; //Upgrade cell
                    energyCount();
                    print(outline, "upgradePhase");
                    print(outline, "energyLeft", energy);
                }
                if (players.get(playerTurn) instanceof BadBot) {
//                    boolean good = false, canTurn = false;
//                    ArrayList<Cell> nearCells;
//                    check:
//                    {
//                        for (int i = 0; i < mapI; i++)
//                            for (int j = 0; j < mapJ; j++)
//                                if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn) && cells[i][j].unit != 1) {
//                                    good = true;
//                                    nearCells = cells[i][j].nearCells(cells);
//                                    for (Cell cell : nearCells)
//                                        if (cell != null && cell.player != players.get(playerTurn)) {
//                                            canTurn = true;
//                                            break check;
//                                        }
//                                }
//                    }
//                    int canTurn = canTurn();
//                    if (canTurn != -1) {
                    if (cellPhase == 0) {
                        badBot.resetIndex();
                        Cell select = badBot.select(cells, players, playerTurn);
                        if (select.cellI == -1 && select.cellJ == -1)
                            return;
                        acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                    } else if (cellPhase == 2) {
//                    } else {
                        energyCount();
                        cellPhase = 2;
                        badBot.resetIndex();
                        Cell select = badBot.upgrade(cells, players, playerTurn);
                        acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                    }
//                    }
                }
            } else { //Select cell or Go cell
                energyCount();
                cellPhase = 2; //Upgrade cell
                print(outline, "upgradePhase");
                print(outline, "energyLeft", energy);
                if (players.get(playerTurn) instanceof BadBot) {
                    badBot.resetIndex();
                    Cell select = badBot.upgrade(cells, players, playerTurn);
                    acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                }
            }
        } else if (phase == 7) { //Hover over cell
//            if (playerName.equals(players.get(playerTurn).name))
            print(outline, "hover", playerName, playerCell.cellI, playerCell.cellJ);
        } else if (phase == 9) { //Hover none
            print(outline, "hover", playerName, "none");
        } else if (phase == 8 && cellPhase == 2) { //Full upgrade
            check:
            {
                if (!playerName.equals(players.get(playerTurn).name))
                    return;
                if (playerCell != null)
                    for (Cell[] cell1 : cells)
                        for (Cell cell : cell1)
                            if (cell != null && cell.cellI == playerCell.cellI && cell.cellJ == playerCell.cellJ) {
                                playerCell = cell;
                                break check;
                            }
                System.err.println("Invalid choice!");
                return;
            }
            selectedCell = playerCell;
            upgradePhase(true);
        } else if (phase == 5) {
            check:
            {
                if (!playerName.equals(players.get(playerTurn).name))
                    return;
                if (playerCell != null)
                    for (Cell[] cell1 : cells)
                        for (Cell cell : cell1)
                            if (cell != null && cell.cellI == playerCell.cellI && cell.cellJ == playerCell.cellJ) {
                                playerCell = cell;
                                break check;
                            }
                System.err.println("Invalid choice!");
                return;
            }
            if (cellPhase == 0) { //Select cell
                selectedCell = playerCell;
                selectPhase();
            }
            else if (cellPhase == 1) { //Go cell
                Cell memoryCell = selectedCell;
                selectedCell = playerCell;
                goPhase(memoryCell);
            }
            else if (cellPhase == 2) { //Upgrade cell
                selectedCell = playerCell;
                upgradePhase(false);
            }
        }
    }

    static void print(String command, Object... args) {
        StringBuilder line = new StringBuilder(command);
        for (Object arg : args) {
            line.append(' ').append(arg.toString());
        }
        System.out.println(line);
    }

    void acceptInput(String input) {
        char color = 0;
        String player = null;
        Cell cell = null;
        int phase = -1;
        int playerMode = -1;
        if (input.startsWith("+")) {
            String[] playermode = input.substring(1).split(" ");
            player = playermode[0];
            String mode = playermode[1];
            if (mode.equals("spectator"))
                playerMode = 0; //Spectator
            else if (!this.isStarted && mode.equals("player"))
                playerMode = 1; //Player
            phase = 0; //Player join
        }
        else if (input.startsWith("-")) {
            player = input.substring(1);
            phase = 1; //Player left
        }
        else if (input.equals("gameFinish")) {
            isFinished = true;

        }
        else {
            String[] inputs = input.split(":");
            if (inputs.length == 2) {
                player = inputs[0];
                if (!this.isStarted && inputs[1].toLowerCase().equals("ready")) {
                    phase = 2; //Player ready
                } else if (!this.isStarted && inputs[1].toLowerCase().startsWith("selectcolor")) {
                    inputs = inputs[1].split(" ");
                    if (inputs.length == 2) {
                        phase = 3; //Player select color
                        color = inputs[1].charAt(0);
                    }
                } else if (isStarted && inputs[1].toLowerCase().equals("next phase")) {
                    phase = 4; //Skip phase
//                } else if (inputs[1].equals("leaveGame")) {
//                    phase = 1; //Player left
                } else if (!isStarted && inputs[1].toLowerCase().equals("unready")) {
                    phase = 6; //Player unready
                } else if (isStarted && inputs[1].startsWith("hover")) {
                    input = inputs[1].split(" ")[1];
                    if (input.equals("none")) {
                        phase = 9; //Hover none
                    } else {
                        try {
                            cell = getCellFromMessage(inputs[1].substring("hover ".length())).cell;
                        } catch (Exception e) {
                            return;
                        }
                        if (cell != null)
                            phase = 7; //Hover over cell
                    }
                } else if (isStarted && inputs[1].startsWith("fullUp")) {
                    try {
                        cell = getCellFromMessage(inputs[1].substring("fullUp ".length())).cell;
                    } catch (Exception e) {
                        return;
                    }
                    if (cell != null)
                        phase = 8; //Full upgrade
                } else {
                    try {
//                        cell = getCellFromMessage(input).cell;
                        cell = getCellFromMessage(inputs[1]).cell;
                    }
                    catch (Exception e) {
                        return;
                    }
                    if (cell != null)
                        phase = 5; //Cell phase
                }
            }
        }
        gameTurn(player, phase, color, cell, playerMode);
    }

    private static Answer getCellFromMessage(String message) {
        String[] messages;
        Cell cell = null;
        messages = message.split(" ");
        if (messages.length == 2) {
            cell = new Cell(Integer.parseInt(messages[0]), Integer.parseInt(messages[1]));
        }
        if (cell != null) {
            return new Answer(cell);
        }
        else {
            return new Answer(null);
        }
    }

    private void selectPhase() {
        boolean canTurn = false;
        Cell memoryCell = selectedCell;
        if (selectedCell.unit < 2)
            selectedCell = memoryCell;
        ArrayList<Cell> nearCells = selectedCell.nearCells(cells);
        for (Cell cell1 : nearCells) {
            if (cell1 != null && cell1.player != players.get(playerTurn)) {
                canTurn = true;
            }
        }
        if (!canTurn || selectedCell.player != players.get(playerTurn) || selectedCell.unit < 2) {
            if (!(players.get(playerTurn) instanceof BadBot))
                System.err.println("Invalid choice!");
            else {
                badBot.nextIndex();
                Cell select = badBot.select(cells, players, playerTurn);
                if (select.cellI == -1 && select.cellJ == -1)
                    return;
                acceptInput("Bot:" + select.cellI + " " + select.cellJ);
            }
        } else {
            print(outline,"selectCell", selectedCell.cellI, selectedCell.cellJ);
            cellPhase++;
            if (players.get(playerTurn) instanceof BadBot) {
                badBot.resetIndex();
                Cell select = badBot.go(cells, players, playerTurn, selectedCell);
                acceptInput("Bot:" + select.cellI + " " + select.cellJ);
            }
        }
    }

    private void goPhase(Cell startCell) {
        Cell goCell = selectedCell;
        ArrayList<Cell> nearCells = startCell.nearCells(cells);
        boolean mine = (goCell.player != null && goCell.player.equals(players.get(playerTurn)));
        if (mine && goCell.unit > 1) {
            cellPhase = 0;
            acceptInput(goCell.player.name + ":" + goCell.cellI + " " + goCell.cellJ);
        } else if (!nearCells.contains(goCell) || mine) {
            if (!(players.get(playerTurn) instanceof BadBot)) {
                System.err.println("Invalid choice!");
                selectedCell = startCell;
            }
            else {
                badBot.nextIndex();
                selectedCell = startCell;
                Cell select = badBot.go(cells, players, playerTurn, startCell);
                acceptInput("Bot:" + select.cellI + " " + select.cellJ);
            }
        } else {
            startCell.cellAttack(goCell, players, playerTurn);
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
            map.mapPrint(this);
            if (!enemyExist) {
                print(outline,"gameFinish", players.get(playerTurn).name);
                acceptInput("gameFinish");
                return;
            }
//            boolean good = false;
//            boolean canTurn = false;
//            check:
//            {
//                for (int i = 0; i < mapI; i++)
//                    for (int j = 0; j < mapJ; j++)
//                        if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn) && cells[i][j].unit != 1) {
//                            good = true;
//                            nearCells = cells[i][j].nearCells(cells);
//                            for (Cell cell : nearCells)
//                                if (cell != null && cell.player != players.get(playerTurn)) {
//                                    canTurn = true;
//                                    break check;
//                                }
//                        }
//            }
            int canTurn = canTurn();
            if (canTurn != 1) {
                energyCount();
                print(outline,"upgradePhase");
//                        print(players.get(playerTurn).name + ":","energyLeft", energy);
                print(outline, "energyLeft", energy);
                cellPhase++;
                if (players.get(playerTurn) instanceof BadBot) {
                    badBot.resetIndex();
                    Cell select = badBot.upgrade(cells, players, playerTurn);
                    acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                }
            } else {
                cellPhase--;
                if (players.get(playerTurn) instanceof BadBot) {
                    badBot.resetIndex();
                    Cell select = badBot.select(cells, players, playerTurn);
                    if (select.cellI == -1 && select.cellJ == -1)
                        return;
                    acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                } else {
                    if (goCell.unit > 1) {
                        acceptInput(goCell.player.name + ":" + goCell.cellI + " " + goCell.cellJ);
                    } else if (goCell.unit == 1) {
                        print(outline, "deselectCell");
                    }
                }
            }
        }
    }

    private void upgradePhase(boolean full) {
        if (selectedCell.player != players.get(playerTurn) || selectedCell.unit == selectedCell.maxUnit) {
            if (!(players.get(playerTurn) instanceof BadBot))
                System.err.println("Invalid choice!");
            else {
                badBot.nextIndex();
//                selectedCell = ((BadBot) players.get(playerTurn)).upgrade(cells, players, playerTurn);
                selectedCell = badBot.upgrade(cells, players, playerTurn);
                acceptInput("Bot:" + selectedCell.cellI + " " + selectedCell.cellJ);
            }
        } else {
            if (full) {
                int energyNeed = selectedCell.maxUnit - selectedCell.unit;
                if (energyNeed > energy)
                    energyNeed = energy;
                energy -= energyNeed;
                selectedCell.unit += energyNeed;
            } else {
                energy--;
                selectedCell.unit++;
            }
            map.mapPrint(this);
            print(outline, "energyLeft", energy);
        }

        if (energy <= 0) {
            if (playerTurn >= players.size() - 1)
                playerTurn = 0;
            else
                playerTurn++;
            print(outline,"turn", players.get(playerTurn).name);
            cellPhase = 0;
//            boolean canTurn = false;
//            check:
//            {
//                for (int i = 0; i < mapI; i++)
//                    for (int j = 0; j < mapJ; j++)
//                        if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn) && cells[i][j].unit != 1) {
//                            ArrayList<Cell> nearCells = cells[i][j].nearCells(cells);
//                            for (Cell cell : nearCells)
//                                if (cell != null && cell.player != players.get(playerTurn)) {
//                                    canTurn = true;
//                                    break check;
//                                }
//                        }
//            }
            int canTurn = canTurn();
            if (players.get(playerTurn) instanceof BadBot) {
                if (canTurn == 1) {
                    badBot.resetIndex();
//                    selectedCell = ((BadBot) players.get(playerTurn)).select(cells, players, playerTurn);
                    selectedCell = badBot.select(cells, players, playerTurn);
                    if (selectedCell.cellI == -1 && selectedCell.cellJ == -1)
                        return;
                    acceptInput("Bot:" + selectedCell.cellI + " " + selectedCell.cellJ);
                } else {
                    cellPhase = 2;
                    badBot.resetIndex();
                    energyCount();
//                    selectedCell = ((BadBot) players.get(playerTurn)).upgrade(cells, players, playerTurn);
                    selectedCell = badBot.upgrade(cells, players, playerTurn);
                    acceptInput("Bot:" + selectedCell.cellI + " " + selectedCell.cellJ);
                }
            }
            else if (canTurn != 1) {
                cellPhase = 2;
                energyCount();
                print(outline, "upgradePhase");
                print(outline, "energyLeft", energy);
            }
        } else if (players.get(playerTurn) instanceof BadBot) {
            badBot.nextIndex();
//            selectedCell = ((BadBot) players.get(playerTurn)).upgrade(cells, players, playerTurn);
            selectedCell = badBot.upgrade(cells, players, playerTurn);
            acceptInput("Bot:" + selectedCell.cellI + " " + selectedCell.cellJ);
        }
    }

    private int canTurn() {
        int canTurn = -1;
        check:
        {
            for (int i = 0; i < mapI; i++)
                for (int j = 0; j < mapJ; j++)
                    if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn) && cells[i][j].unit != 1) {
                        canTurn = 0;
                        ArrayList<Cell> nearCells = cells[i][j].nearCells(cells);
                        for (Cell cell : nearCells)
                            if (cell != null && cell.player != players.get(playerTurn)) {
                                canTurn = 1;
                                break check;
                            }
                    }
        }
        return canTurn;
    }

    private void energyCount() {
        energy = 0;
        for (int i = 0; i < mapI; i++)
            for (int j = 0; j < mapJ; j++)
                if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn))
                    energy++;
    }

    private void getOutline() {
//        String outline;
        line.delete(0, line.length());
        for (Player player1 : players)
            if (!(player1 instanceof BadBot))
                line.append(player1.name).append(',');
        for (Player player1 : spectators)
            line.append(player1.name).append(',');
        line.deleteCharAt(line.length() - 1).append(':');
        outline = line.toString();
    }
}