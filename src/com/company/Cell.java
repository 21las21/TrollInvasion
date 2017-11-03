package com.company;

import java.util.ArrayList;

public class Cell {
    Player player;
    int unit;
    int maxUnit = 8;
    int cellI;
    int cellJ;

    public Cell(int iIn, int jIn) {
        cellI = iIn;
        cellJ = jIn;
    }

    ArrayList<Cell> nearCells(Cell[][] cells, int mapI, int mapJ) {
        ArrayList<Cell> nearCells = new ArrayList<>();
        for (int i = 0; i < mapI; i++) {
            for (int j = 0; j < mapJ; j++) {
                int dI = Math.abs(cellI - i);
                int dJ = Math.abs(cellJ - j);
                if ((dI == 0 && dJ == 2) || (dI == 1 && dJ == 1)) {
                    nearCells.add(cells[i][j]);
                }
            }
        }
        return nearCells;
    }
}
