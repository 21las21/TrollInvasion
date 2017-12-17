package com.company;

import java.util.ArrayList;

class BadBot extends Player {
    BadBot(String name, boolean ready) {
        super(name, ready);
    }

    Cell select(Cell[][] cells, ArrayList<Player> players, int playerTurn, int index) {
        ArrayList<Cell> choices = allPlayerMoveableCells(cells, players, playerTurn);
        return choices.get(index);
    }

    Cell go(Cell[][] cells, ArrayList<Player> players, int playerTurn, int index, Cell goFromCell) {
        ArrayList<Cell> choices = allNearNotPlayerCells(cells, players, playerTurn, goFromCell);
        return choices.get(index);
    }

    Cell upgrade(Cell[][] cells, ArrayList<Player> players, int playerTurn, int index) {
        ArrayList<Cell> choices = allPlayerCells(cells, players, playerTurn);
        if (index >= choices.size())
            index = 0;
        return choices.get(index);
    }


    private ArrayList<Cell> allPlayerMoveableCells(Cell[][] cells, ArrayList<Player> players, int playerTurn) {
        ArrayList<Cell> allPlayerCells = new ArrayList<>();

        for (Cell[] cells1 : cells)
            for (Cell cell : cells1)
                if (cell != null && cell.player == players.get(playerTurn) && cell.unit > 1)
                    allPlayerCells.add(cell);
        return allPlayerCells;
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
