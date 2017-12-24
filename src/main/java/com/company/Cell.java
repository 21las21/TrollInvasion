package com.company;

import java.util.ArrayList;
import java.util.Random;

class Cell {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        return cellI == cell.cellI && cellJ == cell.cellJ;
    }

    @Override
    public int hashCode() {
        int result = cellI;
        result = 31 * result + cellJ;
        return result;
    }

    Player player;
    int unit;
    int maxUnit = 8;
    int cellI;
    int cellJ;

    Cell(int iIn, int jIn) {
        cellI = iIn;
        cellJ = jIn;
    }

    ArrayList<Cell> nearCells(Cell[][] cells) {
        ArrayList<Cell> nearCells = new ArrayList<>();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                int dI = Math.abs(cellI - i);
                int dJ = Math.abs(cellJ - j);
                if (cells[i][j] != null && ((dI == 0 && dJ == 2) || (dI == 1 && dJ == 1))) {
                    nearCells.add(cells[i][j]);
                }
            }
        }
        return nearCells;
    }

    void cellAttack(Cell defendCell, ArrayList<Player> players, int playerTurn) {
        if (defendCell.player == null) {
            defendCell.player = players.get(playerTurn);
            defendCell.unit = unit - 1;
            unit = 1;
        } else {
            defendCell.unit = unit - defendCell.unit;
            if (Math.abs(defendCell.unit) > 1) {
                defendCell.player = (defendCell.unit > 1 ? players.get(playerTurn) : defendCell.player);
                defendCell.unit = Math.abs(defendCell.unit);
            } else {
                double capture = new Random().nextDouble();
                double chance;
                if (defendCell.unit == 0)
                    chance = 0.5;
                else if (defendCell.unit == 1)
                    chance = 0.25;
                else if (defendCell.unit == -1)
                    chance = 0.75;
                else
                    chance = 1;
                defendCell.unit = 1;
                defendCell.player = (capture > chance ? players.get(playerTurn) : defendCell.player);
            }
            unit = 1;
        }
//        Cell[] cellsAttack = new Cell[2];
//        cellsAttack[0] = cells[cellI][cellJ];
//        cellsAttack[1] = defendCell;
    }
}