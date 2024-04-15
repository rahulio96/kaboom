package csc133;

public class spot {
    public static int WIN_WIDTH = 900, WIN_HEIGHT = 900;
    public static String WINDOW_TITLE = "CSC 133";
    public static int NUM_POLY_ROWS = 9, NUM_POLY_COLS = 6; // TODO: Should be 9, 6
    public static int POLY_OFFSET = 40, POLY_LENGTH = 100, POLY_PADDING = 40;
    public static float FRUSTUM_LEFT = 0.0f, FRUSTUM_RIGHT = (float) WIN_WIDTH, FRUSTUM_BOTTOM = 0.0f,
            FRUSTUM_TOP = (float) WIN_HEIGHT, Z_NEAR = 0.0f, Z_FAR = 10.0f;
    public static int TOTAL_MINES = 4;
}
