package com.company;

class Answer {
    Cell cell;

    Cell[] step;
    int cost;

    Answer(Cell cellIn) {
        cell = cellIn;
    }

    Answer(Cell[] stepIn, int costIn) {
        step = stepIn;
        cost = costIn;
    }
}
