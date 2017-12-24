package com.company;

import java.util.ArrayList;

class BadBot extends Player {
    private int index;

    BadBot(String name, boolean ready) {
        super(name, ready);
    }

    Cell select(Cell[][] cells, ArrayList<Player> players, int playerTurn) {
        ArrayList<Cell> choices = allPlayerMovableCells(cells, players, playerTurn);
        if (choices.size() == 0)
            return new Cell(-1, -1);
        if (index >= choices.size())
            index = 0;
        return choices.get(index);
    }

    Cell go(Cell[][] cells, ArrayList<Player> players, int playerTurn, Cell goFromCell) {
        ArrayList<Cell> choices = allNearNotPlayerCells(cells, players, playerTurn, goFromCell);
        if (index >= choices.size())
            resetIndex();
        return choices.get(index);
    }

    Cell upgrade(Cell[][] cells, ArrayList<Player> players, int playerTurn) {
        ArrayList<Cell> choices = allPlayerCells(cells, players, playerTurn);
        if (index >= choices.size())
            index = 0;
        return choices.get(index);
    }

    void resetIndex() {index = 0;}

    void nextIndex() {index++;}

    ArrayList<Cell> allPlayerMovableCells(Cell[][] cells, ArrayList<Player> players, int playerTurn) {
        ArrayList<Cell> allPlayerCells = new ArrayList<>();

        for (Cell[] cells1 : cells)
            for (Cell cell : cells1) {
                if (cell != null && cell.player == players.get(playerTurn) && cell.unit > 1) {
                    boolean canTurn = false;
                    check:
                    {
                        ArrayList<Cell> nearCells = cell.nearCells(cells);
                        for (Cell nearCell : nearCells)
                            if (nearCell != null && nearCell.player != players.get(playerTurn)) {
                                canTurn = true;
                                break check;
                            }
                    }
                    if (canTurn)
                        allPlayerCells.add(cell);
                }
            }
        return allPlayerCells;
    }

    ArrayList<Cell> allPlayerUpgradableCells(Cell[][] cells, ArrayList<Player> players, int playerTurn) {
        ArrayList<Cell> allUpgradableCells = new ArrayList<>();
        for (Cell[] cells1 : cells)
            for (Cell cell : cells1)
                if (cell != null && cell.player == players.get(playerTurn) && cell.unit < cell.maxUnit)
                    allUpgradableCells.add(cell);
        return allUpgradableCells;
    }

    private ArrayList<Cell> allPlayerCells(Cell[][] cells, ArrayList<Player> players, int playerTurn) {
        ArrayList<Cell> allPlayerCells = new ArrayList<>();

        for (Cell[] cells1 : cells)
            for (Cell cell : cells1)
                if (cell != null && cell.player == players.get(playerTurn))
                    allPlayerCells.add(cell);
        return allPlayerCells;
    }

    private ArrayList<Cell> allNearNotPlayerCells(Cell[][] cells, ArrayList<Player> players, int playerTurn, Cell goFromCell) {
        ArrayList<Cell> allNotPlayerCells = new ArrayList<>();

        for (Cell cell : goFromCell.nearCells(cells))
            if (cell != null && cell.player != players.get(playerTurn))
                allNotPlayerCells.add(cell);
        return allNotPlayerCells;
    }
}