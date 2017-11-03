package com.company;

public class Answer {
    Cell cell;
    boolean good;

    Cell[] step;
    int cost;

    public Answer(Cell cellIn, boolean goodIn) {
        cell = cellIn;
        good = goodIn;
    }

    public Answer(Cell[] stepIn, int costIn) {
        step = stepIn;
        cost = costIn;
    }
}
