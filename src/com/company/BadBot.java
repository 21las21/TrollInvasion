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

    Cell go(Cell[][] cells, ArrayList<Player> players, int playerTurn, int index, Cell goFromCell) {
        ArrayList<Cell> choices = allNearNotPlayerCells(cells, players, playerTurn, goFromCell);
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


    ArrayList<Cell> allNearNotPlayerCells(Cell[][] cells, ArrayList<Player> players, int playerTurn, Cell goFromCell) {
        ArrayList<Cell> allNotPlayerCells = new ArrayList<>();

        for (Cell cell : goFromCell.nearCells(cells))
            if (cell != null && cell.player != players.get(playerTurn))
                allNotPlayerCells.add(cell);
        return allNotPlayerCells;
    }
}
