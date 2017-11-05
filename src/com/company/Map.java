package com.company;

import java.util.ArrayList;
import java.util.Random;

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

    public Cell[][] mapGenerate(ArrayList<Player> players) {
        Random random = new Random();
        int mapI = (5 + random.nextInt(10) / 2) * players.size();
        int mapJ = (5 + random.nextInt(10) / 2) * players.size();
        Cell[][] cells = new Cell[mapI][mapJ];
        for (int i = 0; i < mapI; i++)
            for (int j = 0; j < mapJ; j++)
                cells[i][j] = new Cell(i, j);
        int cellI = random.nextInt(5) - 3 + mapI / 2;
        int cellJ = random.nextInt(5) - 3 + mapJ / 2;
        cells[cellI][cellJ].unit = 1;
//        cells[playerI][playerJ] = new Cell(playerI, playerJ);
//        Cell playerCell = cells[playerI][playerJ];
//        cells[cellI][cellJ] = new Cell(cellI, cellJ);
//                    switch (random.nextInt(5)) {
//                case 0:
//                    if (cellJ - 2 >= 0) {
//                        cellJ -= 2;
//                        if (cells[cellI][cellJ] == null)
//                            cells[cellI][cellJ] = new Cell(cellI, cellJ);
//                    } else {
//                        i--;
//                    }
//                    break;
//                case 1:
//                    if (cellI - 1 >= 0 && cellJ - 1 >= 0) {
//                        cellI--;
//                        cellJ--;
//                        if (cells[cellI][cellJ] == null)
//                            cells[cellI][cellJ] = new Cell(cellI, cellJ);
//                    } else {
//                        i--;
//                    }
//                    break;
//                case 2:
//                    if (cellI - 1 >= 0 && cellJ + 2 <= mapJ) {
//                        cellI--;
//                        cellJ++;
//                        if (cells[cellI][cellJ] == null)
//                            cells[cellI][cellJ] = new Cell(cellI, cellJ);
//                    } else {
//                        i--;
//                    }
//                    break;
//                case 3:
//                    if (cellJ + 3 <= mapI) {
//                        cellJ += 2;
//                        if (cells[cellI][cellJ] == null)
//                            cells[cellI][cellJ] = new Cell(cellI, cellJ);
//                    } else {
//                        i--;
//                    }
//                    break;
//                case 4:
//                    if (cellI + 2 <= mapI && cellJ + 2 <= mapJ) {
//                        cellI++;
//                        cellJ++;
//                        if (cells[cellI][cellJ] == null)
//                            cells[cellI][cellJ] = new Cell(cellI, cellJ);
//                    } else {
//                        i--;
//                    }
//                    break;
//                case 5:
//                    if (cellI + 2 <= mapI && cellJ - 1 >= 0) {
//                        cellI++;
//                        cellJ--;
//                        if (cells[cellI][cellJ] == null)
//                            cells[cellI][cellJ] = new Cell(cellI, cellJ);
//                    } else {
//                        i--;
//                    }
//                    break;
//            }
        for (int i = 0; i < mapI * mapJ; i++)
            for (Cell[] cells1 : cells)
                for (Cell cell : cells1)
                    if (cell.unit == i)
                        for (Cell cell1 : cell.nearCells(cells))
                            if (random.nextFloat() < 0.15)
                                cell1.unit = i + 1;
        for (int i = 0; i < mapI; i++) {
            for (int j = 0; j < mapJ; j++) {
                if ((i + j) % 2 != 0 || cells[i][j].unit == 0)
                    cells[i][j] = null;
                else
                    cells[i][j].unit = 0;
            }
        }
        int playersOnMap = 0;
        setPlayers:
        {
            while (playersOnMap < players.size()) {
                int playerI = random.nextInt(mapI);
                int playerJ = random.nextInt(mapJ);
                Cell cellCheck = cells[playerI][playerJ];
                safeCheck:
                {
                    if (cellCheck != null && cellCheck.player == null)
                        for (Cell cell : cellCheck.nearCells(cells))
                            if (cell != null && cell.player == null) {
                                cells[playerI][playerJ].player = players.get(playersOnMap);
                                if (players.size() == 2 && playersOnMap == 1)
                                    cells[playerI][playerJ].unit = 3;
                                else
                                    cells[playerI][playerJ].unit = 2;
                                playersOnMap++;
                                if (playersOnMap >= players.size())
                                    break setPlayers;
                                else
                                    break safeCheck;
                            }
                }
            }
        }
        return cells;
    }
}