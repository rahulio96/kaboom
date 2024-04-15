package csc133;

import SlRenderer.slTilesManager;

import static csc133.spot.TOTAL_MINES;

public class Main {
    public static void main(String[] args) {
        slTilesManager my_tm = new slTilesManager(TOTAL_MINES);
        my_tm.printMineSweeperArray();
    }
}