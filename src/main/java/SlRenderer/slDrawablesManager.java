package SlRenderer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import javax.swing.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static SlRenderer.slTilesManager.*;
import static csc133.spot.*;
import static org.lwjgl.opengl.ARBVertexArrayObject.*;
import static org.lwjgl.opengl.GL20.*;

public class slDrawablesManager {

    private final Vector3f my_camera_location = new Vector3f(0, 0, 0.0f);
    private slCamera my_camera;
    private final slTilesManager board_manager = new slTilesManager(TOTAL_MINES);

    private final float [] vertexArray = board_manager.getVertexArray();
    private final int[] vertexIndexArray = board_manager.getVertexIndicesArray();
    private slShaderManager shader;
    private slTextureManager texture;
    private int vaoID, vboID, eboID;
    private final int vpoIndex = 0, vcoIndex = 1, vtoIndex = 2;

    // 3 points for position (x, y, z)
    // 4 points for color (r, g, b, a)
    // 2 points for uv (uv, uv)
    private int positionStride = 3, colorStride = 4, textureStride = 2,
            vertexStride = (positionStride + colorStride + textureStride) * Float.BYTES;


    public slDrawablesManager(int num_mines) {
        initRendering();


    }

    private void initRendering() {
        my_camera = new slCamera(new Vector3f(my_camera_location));
        my_camera.setOrthoProjection();

        shader = new slShaderManager("vs_texture_1.glsl", "fs_texture_1.glsl");
        shader.compile_shader();

        texture = new slTextureManager(System.getProperty("user.dir") + "/assets/textures/FourTextures.png");

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        // GL_STATIC_DRAW good for now; we can later change to dynamic vertices:
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(vertexIndexArray.length);
        elementBuffer.put(vertexIndexArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(vpoIndex, positionStride, GL_FLOAT, false, vertexStride, 0);
        glEnableVertexAttribArray(vpoIndex);

        glVertexAttribPointer(vcoIndex, colorStride, GL_FLOAT, false, vertexStride, positionStride * Float.BYTES);
        glEnableVertexAttribArray(vcoIndex);

        glVertexAttribPointer(vtoIndex, textureStride, GL_FLOAT, false, vertexStride, (colorStride + positionStride) * Float.BYTES);
        glEnableVertexAttribArray(vtoIndex);




    }  // void initRendering()

    public void update(int row, int col) {
        // first check if row, col >= 0 and if so, get the cell status. If status is GE --> call glDrawElements(...)
        // Else, update the polygon status change in the TilesManager, and get updated cell status.
        // If total Gold tiles == 0, expose the entire board
        // if the vertex data changed; needs updating to GPU that the vertices have changed --> need to call:
        //    glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);
        //}

        shader.set_shader_program();
        texture.bind_texture();

        shader.loadMatrix4f("uProjMatrix", my_camera.getProjectionMatrix());
        shader.loadMatrix4f("uViewMatrix", my_camera.getViewMatrix());

        glBindVertexArray(vaoID);
        if (row != -1 && col != -1 && board_manager.getCellStatus(row, col) != GE) {
            System.out.println("\nCLICKED ROW: " + row + " COL: " + col);

            if (board_manager.getCellStatus(row, col) == GU || board_manager.getCellStatus(row, col) == MU) {
                board_manager.updateForPolygonStatusChange(row, col, true);
                glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);
            }
            board_manager.printMineSweeperArray();
        }

        glEnableVertexAttribArray(vpoIndex);
        glEnableVertexAttribArray(vcoIndex);
        glEnableVertexAttribArray(vtoIndex);

        glDrawElements(GL_TRIANGLES, vertexIndexArray.length, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(vpoIndex);
        glDisableVertexAttribArray(vcoIndex);
        glDisableVertexAttribArray(vtoIndex);

        glBindVertexArray(0);
        shader.detach_shader();
        texture.unbind_texture();
    }  //  public void update(int row, int col)

}
