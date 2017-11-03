package com.company;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BadBot extends Player {
    public BadBot(String name, boolean ready) {
        super(name, ready);
    }

    Cell select(Cell[][] cells, ArrayList<Player> players, int playerTurn, int index) {
        ArrayList<Cell> choices = allPlayerMoveableCells(cells, players, playerTurn);
        Cell select = choices.get(index);
        return select;
    }

    Cell go(Cell[][] cells, ArrayList<Player> players, int playerTurn, int index) {
        ArrayList<Cell> choices = allNotPlayerCells(cells, players, playerTurn);
        Cell go = choices.get(index);
        return go;
    }

    Cell upgrade(Cell[][] cells, ArrayList<Player> players, int playerTurn, int index) {
        ArrayList<Cell> choices = allPlayerCells(cells, players, playerTurn);
        Cell upgrade = choices.get(index);
        return upgrade;
    }


    ArrayList<Cell> allPlayerMoveableCells(Cell[][] cells, ArrayList<Player> players, int playerTurn) {
        ArrayList<Cell> allPlayerCells = new ArrayList<>();

        for (Cell[] cells1 : cells)
            for (Cell cell : cells1)
                if (cell != null && cell.player == players.get(playerTurn) && cell.unit > 1)
                    allPlayerCells.add(cell);
        return allPlayerCells;
    }


    ArrayList<Cell> allPlayerCells(Cell[][] cells, ArrayList<Player> players, int playerTurn) {
        ArrayList<Cell> allPlayerCells = new ArrayList<>();

        for (Cell[] cells1 : cells)
            for (Cell cell : cells1)
                if (cell != null && cell.player == players.get(playerTurn))
                    allPlayerCells.add(cell);
        return allPlayerCells;
    }


    ArrayList<Cell> allNotPlayerCells(Cell[][] cells, ArrayList<Player> players, int playerTurn) {
        ArrayList<Cell> allNotPlayerCells = new ArrayList<>();

        for (Cell[] cells1 : cells)
            for (Cell cell : cells1)
                if (cell != null && cell.player != players.get(playerTurn))
                    allNotPlayerCells.add(cell);
        return allNotPlayerCells;
    }
}
