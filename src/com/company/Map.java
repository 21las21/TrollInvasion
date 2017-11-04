package com.company;

import java.util.ArrayList;

import static com.company.Main.print;

public class Map {

    public void mapPrint(Cell[][] cells) {
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

    public Cell[][] map1Cells(ArrayList<Player> players) {
//        height = 17;
//        width = 17;
        Cell[][] cells = new Cell[17][17];
        String[] lines = new String[17];
        lines[0] = "__|__|__|__|__|__|__|##|__|##|__|__|__|__|__|__|__";
        lines[1] = "__|__|__|__|##|__|##|__|##|__|__|__|__|__|__|__|__";
        lines[2] = "__|__|__|##|__|##|__|##|__|##|__|__|__|__|__|__|__";
        lines[3] = "__|__|__|__|##|__|##|__|##|__|##|__|##|__|__|__|__";
        lines[4] = "__|__|__|__|__|##|__|##|__|##|__|##|__|##|__|__|__";
        lines[5] = "__|__|##|__|##|__|##|__|##|__|2A|__|##|__|##|__|##";
        lines[6] = "__|__|__|##|__|##|__|##|__|##|__|__|__|__|__|__|__";
        lines[7] = "##|__|##|__|##|__|##|__|##|__|##|__|##|__|__|__|__";
        lines[8] = "__|##|__|##|__|__|__|__|__|##|__|##|__|__|__|__|__";
        lines[9] = "##|__|##|__|##|__|##|__|##|__|##|__|##|__|__|__|__";
        lines[10] = "__|__|__|##|__|##|__|##|__|##|__|__|__|__|__|__|__";
        lines[11] = "__|__|##|__|##|__|##|__|##|__|3B|__|##|__|##|__|##";
        lines[12] = "__|__|__|__|__|##|__|##|__|##|__|##|__|##|__|__|__";
        lines[13] = "__|__|__|__|##|__|##|__|##|__|##|__|##|__|__|__|__";
        lines[14] = "__|__|__|##|__|##|__|##|__|##|__|__|__|__|__|__|__";
        lines[15] = "__|__|__|__|##|__|##|__|##|__|__|__|__|__|__|__|__";
        lines[16] = "__|__|__|__|__|__|__|##|__|##|__|__|__|__|__|__|__";

        for (int i = 0; i < lines.length; i++) {
            String[] line = lines[i].split("|");
            ArrayList<String> trueLine = new ArrayList<>();
            for (int i1 = 0; i1 < line.length; i1 += 3) {
                trueLine.add(line[i1] + line[i1 + 1]);
            }
            for (int j = 0; j < trueLine.size(); j++) {
                if (trueLine.get(j).equals("__")) {
                    cells[i][j] = null;
                } else if (trueLine.get(j).equals("##")) {
                    cells[i][j] = new Cell(i, j);
                } else {
                    Cell cell = new Cell(i, j);
//                    StringBuilder stringBuilder = new StringBuilder();
//                    stringBuilder
                    if (trueLine.get(j).charAt(1) == 'A')
                        cell.player = players.get(0);
                    else
                        cell.player = players.get(1);
                    cell.unit = Integer.parseInt(trueLine.get(j).charAt(0) + "");
                    cells[i][j] = cell;
                }
            }
        }
        return cells;
    }
}