package csc133;

import SlRenderer.slWindow;

import static csc133.spot.TOTAL_MINES;

public class Main {
    public static void main(String[] args) {
        slWindow my_win = slWindow.get();
        my_win.run(TOTAL_MINES);
    }
}