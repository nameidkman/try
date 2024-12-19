/*
*
* this is the cool triangle which is made by intellj and i didnt make this at all
* but it still look cool and by the end of learning somewhat of the basics hopefully
* i can make something like this again because it would look really cool
* and i mean really cool
*
*/




import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.*;

public class Triangle3DExample {

    private long window;
    private int shaderProgram;
    private int vao;
    private int vbo;

    public void init() {
        // Step 1: Initialize GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Step 2: Set GLFW OpenGL version and profile
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Step 3: Create a window
        window = glfwCreateWindow(800, 600, "3D Triangle Example", 0, 0);
        if (window == 0) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Step 4: Set the OpenGL context and display the window
        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // Enable vertical sync
        glfwShowWindow(window);

        // Step 5: Initialize OpenGL capabilities
        GL.createCapabilities();

        // Step 6: Setup OpenGL (shaders, VAO/VBO)
        setupOpenGL();
    }

    public void setupOpenGL() {
        // Define triangle vertices (positions and colors)
        float[] vertices = {
                0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,  // Top vertex (Red)
                -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, // Bottom-left (Green)
                0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f  // Bottom-right (Blue)
        };

        // Create and bind VAO and VBO
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Setup vertex and fragment shaders
        String vertexShaderSource = "#version 330 core\n"
                + "layout(location = 0) in vec3 aPos;\n"
                + "layout(location = 1) in vec3 aColor;\n"
                + "uniform mat4 transform;\n"
                + "out vec3 ourColor;\n"
                + "void main() {\n"
                + "    gl_Position = transform * vec4(aPos, 1.0);\n"
                + "    ourColor = aColor;\n"
                + "}";

        String fragmentShaderSource = "#version 330 core\n"
                + "in vec3 ourColor;\n"
                + "out vec4 FragColor;\n"
                + "void main() {\n"
                + "    FragColor = vec4(ourColor, 1.0);\n"
                + "}";

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        checkShaderCompile(vertexShader);

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        checkShaderCompile(fragmentShader);

        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glUseProgram(shaderProgram);

        // Define how vertex data is interpreted
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Delete shaders after linking
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void loop() {
        // Transformation matrix for triangle
        float[] transform = new float[]{
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, -3.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        };

        int transformLocation = glGetUniformLocation(shaderProgram, "transform");

        // Main render loop
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear frame
            glEnable(GL_DEPTH_TEST); // Enable depth for 3D effect

            // Rotate triangle over time
            float angle = System.nanoTime() / 1_000_000_000.0f;
            transform[0] = (float) Math.cos(angle);
            transform[2] = (float) -Math.sin(angle);
            transform[8] = (float) Math.sin(angle);
            transform[10] = (float) Math.cos(angle);

            glUniformMatrix4fv(transformLocation, false, transform);

            // Draw triangle
            glDrawArrays(GL_TRIANGLES, 0, 3);

            // Update window
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void checkShaderCompile(int shaderId) {
        // Check and throw error if shader compilation fails
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader compilation failed: " + glGetShaderInfoLog(shaderId));
        }
    }

    public void cleanup() {
        // Release resources and terminate GLFW
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
        glDeleteProgram(shaderProgram);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public static void main(String[] args) {
        Triangle3DExample example = new Triangle3DExample();
        example.init();
        example.loop();
        example.cleanup();
    }
}
