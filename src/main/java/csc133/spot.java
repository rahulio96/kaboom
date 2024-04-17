package csc133;

public class spot {
    // public static int WIN_WIDTH = 900, WIN_HEIGHT = 900;
    public static String WINDOW_TITLE = "CSC 133";
    public static int vps = 4, ips = 6, fpv = 9;
    public static int NUM_POLY_ROWS = 9, NUM_POLY_COLS = 6; // TODO: Should be 9, 6
    public static int POLY_OFFSET = 30, POLY_LENGTH = 70, POLY_PADDING = 30; // TODO: Should be 40, 100, 40

    public static int WIN_WIDTH = POLY_OFFSET * 2 + NUM_POLY_COLS * (POLY_LENGTH + POLY_PADDING) - POLY_PADDING,
            WIN_HEIGHT = POLY_OFFSET * 2 + NUM_POLY_ROWS * (POLY_LENGTH + POLY_PADDING) - POLY_PADDING;

    public static float FRUSTUM_LEFT = 0.0f, FRUSTUM_RIGHT = (float) WIN_WIDTH, FRUSTUM_BOTTOM = 0.0f,
            FRUSTUM_TOP = (float) WIN_HEIGHT, Z_NEAR = 0.0f, Z_FAR = 10.0f;
    public static int TOTAL_MINES = 4;
    public static void print_legalese() {
        System.out.println("Legal Stuff");
    }
}
