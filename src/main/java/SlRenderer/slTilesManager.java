package SlRenderer;

import org.joml.Vector2i;

import javax.swing.*;
import java.util.Random;

import static csc133.spot.*;

public class slTilesManager {
    private float[] verticesArray;
    private int[] vertexIndicesArray;

    // 0 <-- gold & unexposed; GU, 1 <-- gold & exposed; GE,
    // 2 <-- mine & unexposed; MU, 3 <-- mine & exposed; ME.
    public static final int GU = 0;
    public static final int GE = 1;
    public static final int MU = 2;
    public static final int ME = 3;
    // Precomputed Texture Coordinates for the above states - tied to how textures are tiled:
                                       //umin, vmin, umax, vmax
    private static final float[] GUTC = {0.5f, 0.5f, 1.0f, 0.0f};
    private static final float[] MUTC = GUTC;
    private static final float[] GETC = {0.0f, 1.0f, 0.5f, 0.5f};
    private static final float[] METC = {0.5f, 1.0f, 1.0f, 0.5f};

    private int[] cellStatusArray;
    private int[] cellStats;  // {exposed gold - unexposed gold - mines} - in that order.
    // < 0 --> game in progress, 0 --> ended on gold, 1 --> ended on a mine, 2 --> game ended.
    private int boardDisplayStatus = -1;
    private int num_mines;

    float ZMIN = 0.0f; // z coordinate for all polygons

    public slTilesManager(int total_mines) {
        num_mines = total_mines;
        cellStatusArray = new int[NUM_POLY_COLS * NUM_POLY_ROWS];
        cellStats = new int[] {0, NUM_POLY_COLS * NUM_POLY_ROWS - total_mines, total_mines};

        for (int ii = 0; ii < cellStatusArray.length; ++ii) {
            cellStatusArray[ii] = GU;
        }

        int cur_mines = 0, cur_index = -1;
        Random my_rand = new Random();
        while (cur_mines < num_mines) {
            cur_index = my_rand.nextInt(cellStatusArray.length);
            if (cellStatusArray[cur_index] != MU) {
                cellStatusArray[cur_index] = MU;
                ++cur_mines;
            }
        }

        setVertexArray();
        setVertexIndicesArray();
    }  //  public slGeometryManager(int num_mines)

    // Call fillSquarecoordinates for each cell array
    private void setVertexArray() {
        verticesArray = new float[(NUM_POLY_ROWS * NUM_POLY_COLS) * vps * fpv];
        int index = 0;
        for (int row = 0; row < NUM_POLY_ROWS; row++) {
            for (int col = 0; col < NUM_POLY_COLS; col++) {
                fillSquareCoordinates(index, row, col, verticesArray);
                //alternateImages(row, col, 0); // TODO: REMOVE LATER, ONLY HERE FOR STEP 5
                index += (vps * fpv);
            }
        }

    }  // float[] setVertexArray(...)

    // Given a index, row, column, fill up the vertices of the square. "index" is the index
    // to the vert_array, a multiple of vps * fpv
    private void fillSquareCoordinates(int index, int row, int col, float[] vert_array) {
        // Counters used to tell when to insert colors and uv coordinates
        int color_counter = 0;
        int uv_counter = 0;

        float xmin = POLY_OFFSET + col * (POLY_LENGTH + POLY_PADDING);
        float ymax = WIN_HEIGHT - (POLY_OFFSET + row * (POLY_LENGTH + POLY_PADDING));
        float xmax = xmin + POLY_LENGTH, ymin = ymax - POLY_LENGTH;

        // Arrays with the correct orientation for xyz, colors, and uv
        float[] vs_colors = {0.0f, 0.0f, 1.0f, 1.0f};
        float umin = GUTC[0], vmin = GUTC[1], umax = GUTC[2], vmax = GUTC[3];
        float[] uv_coords = {umin,vmin, umax,vmin, umax,vmax, umin,vmax};

        float[] xyz_coords = {xmin,ymin,ZMIN, xmax,ymin,ZMIN, xmax,ymax,ZMIN, xmin,ymax,ZMIN};

        for (float xyz_coordinate : xyz_coords) {
            vert_array[index++] = xyz_coordinate;
            color_counter++;

            // Once x,y,z are in vert_array, add the colors and uv coordinates
            if (color_counter == 3) {
                color_counter = 0;
                for (float color : vs_colors) {
                    vert_array[index++] = color;
                }
                // Loop through uv array so each vertex gets a coordinate pair
                vert_array[index++] = uv_coords[uv_counter++];
                vert_array[index++] = uv_coords[uv_counter++];
            }
        }


    }  //  private void fillSquareCoordinates(int indx, int row, int col, float[] vert_array)

    public void setVertexIndicesArray() {
        vertexIndicesArray = new int[(NUM_POLY_ROWS * NUM_POLY_COLS) * ips];
        int index = 0;
        int v_index = 0;

        while (index < vertexIndicesArray.length) {
            vertexIndicesArray[index++] = v_index;
            vertexIndicesArray[index++] = v_index + 1;
            vertexIndicesArray[index++] = v_index + 2;
            vertexIndicesArray[index++] = v_index;
            vertexIndicesArray[index++] = v_index + 2;
            vertexIndicesArray[index++] = v_index + 3;

            v_index += vps;
        }
    }  //  public int[] setVertexIndicesArray(...)

    // For part 5 in the rubric (alternate images for first row)
    public void alternateImages(int cur_row, int cur_col, int row) {
        float umin = -1f, vmin = -1f, umax = -1f, vmax = -1f;
        if (cur_row == row && cur_col % 2 == 0) {
            umin = GETC[0];
            vmin = GETC[1];
            umax = GETC[2];
            vmax = GETC[3];
        } else if (cur_row == row) {
            umin = 0.5f;
            vmin = 0.5f;
            umax = 0.0f;
            vmax = 0.0f;
        }
        if (umin != -1f) {
            int xyz_color_offset = 7;
            int index = ((row * NUM_POLY_COLS + cur_col) * vps * fpv) + xyz_color_offset;
            float[] uv_coords = {umin,vmin, umax,vmin, umax,vmax, umin,vmax};
            int uv_index = 0;
            for (int i = 0; i < vps; i++) {
                verticesArray[index++] = uv_coords[uv_index++];
                verticesArray[index++] = uv_coords[uv_index++];
                index += xyz_color_offset;
            }

        }

    }

    // TODO: MODIFY THIS TO MAKE IT SOMEWHAT MATCH ABOVE CODE!
    public void updateForPolygonStatusChange(int row, int col, boolean printStats) {
        //locate the index to the verticesArray:
        int fps = vps * fpv; //Floats Per Square
        int first_offset = 7; // first texture coord offset;
        int tc_offset = 8;  // subsequent texture coord offsets;
        int indx = (row * NUM_POLY_COLS + col) * fps + first_offset;
        float umin = GETC[0], vmin = GETC[1], umax = GETC[2], vmax = GETC[3];
        int old_state = cellStatusArray[row * NUM_POLY_COLS + col];
        int new_state = -1;
        if (old_state == GU ) {
            new_state = GE;
            ++cellStats[0];
            --cellStats[1];
            if (printStats && boardDisplayStatus < 0) {
                printStats();
            }
            // tex coords set to this by default - no need to update
        } else if (old_state == MU) {
            new_state = ME;
        } else {
            return;
        }

        if (new_state == ME) {
            umin = METC[0]; umax = METC[1]; vmin = METC[2]; vmax = METC[3];
        }
        cellStatusArray[row * NUM_POLY_COLS + col] = new_state;

        verticesArray[indx++] = umin;
        verticesArray[indx] = vmin;
        indx += tc_offset;
        verticesArray[indx++] = umax;
        verticesArray[indx] = vmin;
        indx += tc_offset;
        verticesArray[indx++] = umax;
        verticesArray[indx] = vmax;
        indx += tc_offset;
        verticesArray[indx++] = umin;
        verticesArray[indx] = vmax;
    }

    private void printStats() {
        
    }

    // status can be GE, ME, GU, MU
    public void setCellStatus(int row, int col, int status) {
        



    }  //  public void setCellStatus(int row, int col, int status)

    public int getCellStatus(int row, int col) {
        return cellStatusArray[row * NUM_POLY_COLS + col];
    }

    public void updateStatusArrayToDisplayAll() {
        




    }

    public float[] getVertexArray() {
        return verticesArray;
    }

    public int[] getVertexIndicesArray() {
        return vertexIndicesArray;
    }

    public void printMineSweeperArray() {
        // Keep track of num of rows, new line if = num rows
        int row_count = 0;

        for (int i = 0; i < cellStatusArray.length; i++) {
            if (row_count >= NUM_POLY_ROWS) {
                row_count = 0;
                System.out.println();
            }
            System.out.print(cellStatusArray[i]);
            row_count++;
        }


    }  //  public void printMineSweeperArray()

    public int[] getCellStats() {
        return cellStats;
    }

    // returns (-1, -1) if a cell was not selected; else computes the row and column 
    // corresponding to the window coordinates and returns the updated retVec
    public static Vector2i getRowColFromXY(float xpos, float ypos) {
        Vector2i retVec = new Vector2i(-1, -1);
        int col = (int) ((xpos - POLY_OFFSET) / (POLY_LENGTH + POLY_PADDING));
        int row = (int) ((ypos - POLY_OFFSET) / (POLY_LENGTH + POLY_PADDING));

        float xmink = POLY_OFFSET + col * (POLY_LENGTH + POLY_PADDING);
        float xmaxk = xmink + POLY_LENGTH;
        float ymink = POLY_OFFSET + row * (POLY_LENGTH + POLY_PADDING);
        float ymaxk = ymink + POLY_LENGTH;

        if (xpos >= xmink && xpos <= xmaxk && ypos >= ymink && ypos <= ymaxk) {
            return retVec.set(NUM_POLY_ROWS-1-row, col);
        } else {
            return retVec;
        }


    }

}  //  public class slGeometryManager
