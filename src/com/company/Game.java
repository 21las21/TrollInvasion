package com.company;

import java.util.ArrayList;
import java.util.Iterator;

class Game {
    String name;
    String outline;
    boolean isFinished = false;
    boolean isStarted = false;
    ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Character> colors = new ArrayList<>();
    private StringBuilder line = new StringBuilder();
    private Cell selectedCell = null;
    Cell[][] cells;
    private Map map;
    private int readyPlayers = 0;
    private int colorPlayers = 0;
    private int cellPhase = 0;
    private int playerTurn = 0;
    private int energy = 0;
    private int indexBot = 0;
    private int mapI;
    private int mapJ;

    private void gameTurn(String playerName, int phase, char playerColor, Cell playerCell) {
        Player player = null;
        if (phase == 0) { //Player join
            for (Player player1 : players)
                if (player1.name.equals(playerName)) {
                    player = player1;
                    break;
                }
            if (player == null) {
                player = new Player(playerName, false);
                players.add(player);
//                player.game = this;
                line.delete(0, line.length());
                for (Player player1 : players) {
                    print(playerName + ":", "readyStatus", player1.name, player1.ready);
                    line.append(player1.name).append(',');
                }
                line.deleteCharAt(line.length() - 1).append(':');
                outline = line.toString();
                print(outline + " gameEntered", name);
//                print(outline, playerName + " join");
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
            if (player != null) {
                print(outline + " gameLeft", player.name);
                line = new StringBuilder();
                for (Player player1 : players) {
                    line.append(player1.name).append(',');
                }
                line.deleteCharAt(line.length() - 1).append(':');
                outline = line.toString();
            }
            if (!isStarted) {
                if (player != null) {
                    players.remove(index);
                    player.game = null;
                }
            } else if (player != null) {
                player.game = null;
                BadBot badBot = new BadBot("Bot", true);
                badBot.color = player.color;
                players.set(index, badBot);
                for (Cell[] cellRaw : cells)
                    for (Cell cellCheck : cellRaw)
                        if (cellCheck != null && cellCheck.player != null && cellCheck.player.equals(player))
                            cellCheck.player = badBot;
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
                print(outline,"readyStatus", player.name, true);
                if (readyPlayers > 0 && players.size() == readyPlayers) {
                    isStarted = true;
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
                        print(outline,"playerColor", player1.name, player1.color);
                    }
                    map = new Map();
                    cells = map.mapGenerate(players);
                    mapI = cells.length;
                    mapJ = cells[0].length;
                    map.mapPrint(this);
                    print(outline,"turn", players.get(playerTurn).name);
                    if (players.get(playerTurn) instanceof BadBot) {
                        boolean canTurn = false;
                        check:
                        {
                            for (Cell[] cells1 : cells)
                                for (Cell cell : cells1) {
                                if (cell != null)
                                    for (Cell cell1 : cell.nearCells(cells)) {
                                        if (cell1 != null && cell1.player != players.get(playerTurn)) {
                                            canTurn = true;
                                            break check;
                                        }
                                    }
                                }
                        }
                        if (canTurn) {
                            cellPhase = 0; //Select cell
                            if (players.get(playerTurn) instanceof BadBot) {
                                indexBot = 0;
                                Cell select = ((BadBot) players.get(playerTurn)).select(cells, players, playerTurn, indexBot);
                                acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                            }
                        } else {
                            energy = 0;
                            for (int i = 0; i < mapI; i++) {
                                for (int j = 0; j < mapJ; j++) {
                                    if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn)) {
                                        energy++;
                                    }
                                }
                            }
                            cellPhase = 2; //Upgrade cell
                            print(outline,"upgradePhase");
                            print(players.get(playerTurn).name + ":", "energyLeft", energy);
                            if (players.get(playerTurn) instanceof BadBot) {
                                indexBot = 0;
                                Cell select = ((BadBot) players.get(playerTurn)).upgrade(cells, players, playerTurn, indexBot);
                                acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                            }
                        }
                    }
                }
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
                cellPhase = 0; //Select cell
                if (players.get(playerTurn) instanceof BadBot) {
                    boolean good = false, canTurn = false;
                    ArrayList<Cell> nearCells;
                    check:
                    {
                        for (int i = 0; i < mapI; i++)
                            for (int j = 0; j < mapJ; j++)
                                if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn) && cells[i][j].unit != 1) {
                                    good = true;
                                    nearCells = cells[i][j].nearCells(cells);
                                    for (Cell cell : nearCells)
                                        if (cell != null && cell.player != players.get(playerTurn)) {
                                            canTurn = true;
                                            break check;
                                        }
                                }
                    }
                    if (good && canTurn) {
                        indexBot = 0;
                        Cell select = ((BadBot) players.get(playerTurn)).select(cells, players, playerTurn, indexBot);
                        acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                    }
                    else {
                        energy = 0;
                        for (int i = 0; i < mapI; i++)
                            for (int j = 0; j < mapJ; j++)
                                if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn))
                                    energy++;
                        cellPhase = 2;
                        indexBot = 0;
                        Cell select = ((BadBot) players.get(playerTurn)).upgrade(cells, players, playerTurn, indexBot);
                        acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                    }
                }
            }
            else { //Select cell or Go cell
                energy = 0;
                for (int i = 0; i < mapI; i++)
                    for (int j = 0; j < mapJ; j++)
                        if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn))
                            energy++;
                cellPhase = 2; //Upgrade cell
                print(outline, "upgradePhase");
                print(players.get(playerTurn).name + ":", "energyLeft", energy);
                if (players.get(playerTurn) instanceof BadBot) {
                    indexBot = 0;
                    Cell select = ((BadBot) players.get(playerTurn)).upgrade(cells, players, playerTurn, indexBot);
                    acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                }
            }
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
            Cell goCell, upgradeCell;
            boolean canTurn = false, good = false;
            ArrayList<Cell> nearCells;
            if (cellPhase == 0) { //Select cell
                canTurn = false;
                selectedCell = playerCell;
                nearCells = selectedCell.nearCells(cells);
                for (Cell cell1 : nearCells) {
                    if (cell1 != null && cell1.player != players.get(playerTurn)) {
                        canTurn = true;
                    }
                }
                if (!canTurn || selectedCell.player != players.get(playerTurn) || selectedCell.unit < 2) {
                    if (!(players.get(playerTurn) instanceof BadBot))
                        System.err.println("Invalid choice!");
                    else {
                        indexBot++;
                        Cell select = ((BadBot) players.get(playerTurn)).select(cells, players, playerTurn, indexBot);
                        acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                    }
                } else {
                    print(outline,"selectCell", selectedCell.cellI, selectedCell.cellJ);
                    cellPhase++;
                    if (players.get(playerTurn) instanceof BadBot) {
                        indexBot = 0;
                        Cell select = ((BadBot) players.get(playerTurn)).go(cells, players, playerTurn, indexBot, selectedCell);
                        acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                    }
                }
            }
            else if (cellPhase == 1) { //Go cell
                goCell = playerCell;
                nearCells = selectedCell.nearCells(cells);
                if (goCell.player != null && goCell.player.equals(players.get(playerTurn))) {
                    cellPhase = 0;
                    acceptInput(goCell.player.name + ":" + goCell.cellI + " " + goCell.cellJ);
                } else if (!nearCells.contains(goCell)) {
                    if (!(players.get(playerTurn) instanceof BadBot))
                        System.err.println("Invalid choice!");
                    else {
                        indexBot++;
                        Cell select = ((BadBot) players.get(playerTurn)).go(cells, players, playerTurn, indexBot, selectedCell);
                        acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                    }
                } else {
                    selectedCell.cellAttack(goCell, players, playerTurn);
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
                    check:
                    {
                        for (int i = 0; i < mapI; i++)
                            for (int j = 0; j < mapJ; j++)
                                if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn) && cells[i][j].unit != 1) {
                                    good = true;
                                    nearCells = cells[i][j].nearCells(cells);
                                    for (Cell cell : nearCells)
                                        if (cell != null && cell.player != players.get(playerTurn)) {
                                            canTurn = true;
                                            break check;
                                        }
                                }
                    }
                    if (!good || !canTurn) {
                        energy = 0;
                        for (int i = 0; i < mapI; i++)
                            for (int j = 0; j < mapJ; j++)
                                if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn))
                                    energy++;
                        print(outline,"upgradePhase");
                        print(players.get(playerTurn).name + ":","energyLeft", energy);
                        cellPhase++;
                        if (players.get(playerTurn) instanceof BadBot) {
                            indexBot = 0;
                            Cell select = ((BadBot) players.get(playerTurn)).upgrade(cells, players, playerTurn, indexBot);
                            acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                        }
                    } else {
                        cellPhase--;
                        if (players.get(playerTurn) instanceof BadBot) {
                            indexBot = 0;
                            Cell select = ((BadBot) players.get(playerTurn)).select(cells, players, playerTurn, indexBot);
                            acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                        }
                        if (goCell.unit > 1) {
                            acceptInput(goCell.player.name + ":" + goCell.cellI + " " + goCell.cellJ);
                        }
                    }
                }
            }
            else if (cellPhase == 2) { //Upgrade cell
                upgradeCell = playerCell;
                if (upgradeCell.player != players.get(playerTurn) || upgradeCell.unit == upgradeCell.maxUnit) {
                    if (!(players.get(playerTurn) instanceof BadBot)) {
                        System.err.println("Invalid choice!");
                        return;
                    }
                    else {
                        indexBot++;
                        Cell select = ((BadBot) players.get(playerTurn)).upgrade(cells, players, playerTurn, indexBot);
                        acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                    }
                } else {
                    energy--;
                    upgradeCell.unit++;
                    map.mapPrint(this);
                    print(players.get(playerTurn).name + ":","energyLeft", energy);
                }
                if (energy <= 0) {
                    if (playerTurn >= players.size() - 1)
                        playerTurn = 0;
                    else
                        playerTurn++;
                    print(outline,"turn", players.get(playerTurn).name);
                    cellPhase = 0;
                    if (players.get(playerTurn) instanceof BadBot) {
                        check:
                        {
                            for (int i = 0; i < mapI; i++)
                                for (int j = 0; j < mapJ; j++)
                                    if (cells[i][j] != null && cells[i][j].player == players.get(playerTurn) && cells[i][j].unit != 1) {
                                        good = true;
                                        nearCells = cells[i][j].nearCells(cells);
                                        for (Cell cell : nearCells)
                                            if (cell != null && cell.player != players.get(playerTurn)) {
                                                canTurn = true;
                                                break check;
                                            }
                                    }
                        }
                        if (good && canTurn) {
                            indexBot = 0;
                            Cell select = ((BadBot) players.get(playerTurn)).select(cells, players, playerTurn, indexBot);
                            acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                        }
                        else {
                            cellPhase = 2;
                            indexBot = 0;
                            Cell select = ((BadBot) players.get(playerTurn)).upgrade(cells, players, playerTurn, indexBot);
                            acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                        }
                    }
                } else if (players.get(playerTurn) instanceof BadBot) {
                    indexBot++;
                    Cell select = ((BadBot) players.get(playerTurn)).upgrade(cells, players, playerTurn, indexBot);
                    acceptInput("Bot:" + select.cellI + " " + select.cellJ);
                }
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
        if (!this.isStarted && input.startsWith("+")) {
            player = input.substring(1);
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
                } else if (!this.isStarted && inputs[1].toLowerCase().startsWith("selectColor")) {
                    inputs = inputs[1].split(" ");
                    if (inputs.length == 2) {
                        phase = 3; //Player select color
                        color = inputs[1].charAt(0);
                    }
                } else if (isStarted && inputs[1].toLowerCase().equals("next phase")) {
                    phase = 4; //Skip phase
//                } else if (inputs[1].equals("leaveGame")) {
//                    phase = 1; //Player left
                } else {
                    try {
                        cell = getCellFromMessage(input).cell;
                    }
                    catch (Exception e) {
                        return;
                    }
                    if (cell != null)
                        phase = 5; //Cell phase
                }
            }
        }
        gameTurn(player, phase, color, cell);
    }

    private static Answer getCellFromMessage(String message) {
        String[] messages = message.split(":");
        Cell cell = null;
        if (messages.length == 2) {
            messages = messages[1].split(" ");
            if (messages.length == 2) {
                cell = new Cell(Integer.parseInt(messages[0]), Integer.parseInt(messages[1]));
            }
        }
        if (cell != null) {
            return new Answer(cell);
        }
        else {
            return new Answer(null);
        }
    }
}
