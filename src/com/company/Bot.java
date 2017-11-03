package com.company;

import java.util.ArrayList;

public class Bot extends Player {
    public Bot(String name, boolean ready) {
        super(name, ready);
    }

    ArrayList<Cell> turn(Cell[][] cells, ArrayList<Player> players, int playerTurnIn) {
//        ArrayList<Cell> outputs = new ArrayList<>();
//        ArrayList<Integer> controlCells = new ArrayList<>();
//        ArrayList<ArrayList<Cell[]>> cellsStep = new ArrayList<>();
//        ArrayList<Cell[]> step;
//        step = step(cells, players, playerTurnIn);
//        int stepsCheck = 2;
//        for (int i = 0; i < stepsCheck; i++) {
//            for (int i1 = 1; i1 < players.size(); i1++) {
//                while (step != null) {
////                    Cell[] cellsAttack = cellsStep[0].cellAttack(cells, cellsStep[1], players, playerTurnIn);
////                    cellsStep[0] = cellsAttack[0];
////                    cellsStep[1] = cellsAttack[1];
//                    cellsStep.add(step);
//                    step = step(cells, players, playerTurnIn);
//                }
//                playerTurnIn++;
//                if (playerTurnIn > players.size())
//                    playerTurnIn = 0;
//            }
//        }
//        return outputs;
        ArrayList<Cell> turn = new ArrayList<>();
        int depth = 2;
        Cell[] step = minimax(depth, cells, players, playerTurnIn, false).step;
        turn.add(step[0]);
        turn.add(step[1]);
//        ArrayList<Cell> upgrade = upgrade(cells, players, playerTurnIn);
//        turn.addAll(upgrade);
        return turn;
    }

//    ArrayList<Cell> upgrade(Cell[][] cells, ArrayList<Player> players, int playerTurn) {
//        ArrayList<Cell> allCells = allPlayerCells(cells, players, playerTurn);
//
//
//    }

    ArrayList<Cell[]> allSteps(Cell[][] cells, ArrayList<Player> players, int playerTurn) {
//        Cell[] outputs = new Cell[2];
//        ArrayList<Cell> startCells = new ArrayList<>();
//        ArrayList<Cell[]> choices = new ArrayList<>();
//
//        for (Cell[] cell1 : cells) {
//            for (int j = 0; j < cells[0].length; j++) {
//                Cell cellCheck = cell1[j];
//                if (cellCheck != null && cellCheck.player instanceof Bot && cellCheck.player == players.get(playerTurn) && cellCheck.unit > 1) {
//                    startCells.add(cellCheck);
//                }
//            }
//        }
//        for (Cell cell : startCells) {
//            outputs[0] = cell;
//            for (Cell nearCell : cell.nearCells(cells)) {
//                outputs[1] = nearCell;
////                Cell[] cellsAttack = cell.cellAttack(cells, nearCell, players, playerTurn);
////                cell = cellsAttack[0];
////                nearCell = cellsAttack[1];
//                choices.add(outputs);
//            }
//        }
////        Iterator<Cell[]> iterator = choices.iterator();
//        return choices;
        ArrayList<Cell> checkCells;
        ArrayList<Cell[]> allSteps = new ArrayList<>();

        checkCells = allPlayerCells(cells, players, playerTurn);
        for (Cell cell : checkCells)
            for (Cell cell1 : cell.nearCells(cells)) {
                Cell[] step = new Cell[2];
                step[0] = cell;
                step[1] = cell1;
                allSteps.add(step);
            }
        return allSteps;
    }

    ArrayList<Cell> allPlayerCells(Cell[][] cells, ArrayList<Player> players, int playerTurn) {
        ArrayList<Cell> allPlayerCells = new ArrayList<>();

        for (Cell[] cells1 : cells)
            for (Cell cell : cells1)
                if (cell != null && cell.player == players.get(playerTurn) && cell.unit > 1)
                    allPlayerCells.add(cell);
        return allPlayerCells;
    }

//    int bestCost(Cell[][] cells, ArrayList<Player> players, int playerTurn, int depth, boolean isMaximising) {
//        Cell[][] fakeCells = cells.clone();
//        ArrayList<Cell[]> allSteps = allSteps(cells, players, playerTurn);
//        return 0;
//        while (true) {
//            if (depth == 0) {
//                break;
//            } else {
//                int bestCost = Integer.MAX_VALUE;
//                Cell[] bestMove = new Cell[2];
//                for (int i = 0; i < allSteps.size() - 1; i++) {
//                    Cell[] step = allSteps.get(i);
//                    Cell[] cellAttack = step[0].cellAttack(cells, step[1], players, playerTurn);
//                    fakeCells[step[0].cellI][step[0].cellJ] = cellAttack[0];
////                fakeCells[step[0].cellI][step[0].cellJ].player = cellAttack[0].player;
//                    fakeCells[step[1].cellI][step[1].cellJ] = cellAttack[1];
////                fakeCells[step[1].cellI][step[1].cellJ].player = cellAttack[1].player;
//                    bestCost = Math.min(bestCost, bestCost(cells, players, playerTurn, depth - 1));
//                }
//            }
//        }
//    }

    Answer minimax(int depth, Cell[][] cells, ArrayList<Player> players, int playerTurn, boolean isMaximisingPlayer) {
        Cell[] step = new Cell[2];
        int bestMove = 0;
        if (depth == 0) {
            return new Answer(step, bestMove);
        }
        Cell[][] fakeCells = cells.clone();
        ArrayList<Cell[]> newGameMoves = allSteps(cells, players, playerTurn);
        if (isMaximisingPlayer) {
            bestMove = Integer.MIN_VALUE;
            for (int i = 0; i < newGameMoves.size() - 1; i++) {
                step = newGameMoves.get(i);
                step[0].cellAttack(cells, step[1], players, playerTurn);
                bestMove = Math.max(bestMove, minimax(depth - 1, cells, players, playerTurn, !isMaximisingPlayer).cost);
                cells = fakeCells;
            }
            return new Answer(step, bestMove);
        } else {
            bestMove = Integer.MAX_VALUE;
            for (int i = 0; i < newGameMoves.size() - 1; i++) {
                 step = newGameMoves.get(i);
                step[0].cellAttack(cells, step[1], players, playerTurn);
                bestMove = Math.max(bestMove, minimax(depth - 1, cells, players, playerTurn, !isMaximisingPlayer).cost);
                cells = fakeCells;
            }
            return new Answer(step, bestMove);
        }
    };
}