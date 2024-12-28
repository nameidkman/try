/*

All that this is to be able to make sure i can make something using the lwjgl
so i am making a triangle and then i am going to make a triangle which is animated

this for learning purpose so i dont really know all that much right now
the basic thing which i want to try to do is make something 3d today

*/

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.glfw.GLFW.*;

public class MakingTriangleAnimation {

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
        /*
        ================================================================================================
        The vertices contain the position and the color of the triangle in 3D space, as well as RGB values.
        The layout for this is:
            x, y, z, r, g, b
        We want the triangle to be at the center. This is why the vertex at (0.0f, 0.5f, 0.0f) is at the top of the triangle,
        and the other two vertices are symmetrically placed relative to the center. This creates the 2D appearance
        when viewed from above, but it is still in 3D space because the z-coordinate is 0.

        ================================================================================================
        */
        float[] vertices = {
                0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,  // Top vertex (Red)
                0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f,  // Bottom-right (Blue)
                -0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f  // Bottom-left (Green)
        };

        /*
        ================================================================================================
        VAO (Vertex Array Object) is used to store the state of the vertex, such as the layout.
        By creating a VAO, we can store the settings, and then bind it later to use in the OpenGL context.
        ================================================================================================
        */
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // VBO (Vertex Buffer Object) stores the actual vertex data in GPU memory
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);  // Upload the data to GPU

        /*
        Vertex Shader:
        This shader determines how to modify the 3D object and apply transformations like rotation.
        We pass a uniform matrix (transform) to the shader for applying transformations.
        The output color is also passed to the fragment shader.
        */
        String vertexShaderSource = "#version 330 core\n" +
                "layout(location = 0) in vec3 aPos;\n" +  // Input position
                "layout(location = 1) in vec3 aColor;\n" +  // Input color
                "uniform mat4 transform;\n" +  // Transformation matrix for rotating/transforming the object
                "out vec3 outColor;\n" +  // Output color to fragment shader
                "void main() {\n" +
                "    gl_Position = transform * vec4(aPos, 1.0);\n" +  // Apply transformation
                "    outColor = aColor;\n" +  // Pass the color to the fragment shader
                "}";

        // Fragment Shader:
        // This shader modifies the pixel colors on the screen.
        String fragmentShaderSource = "#version 330 core\n" +
                "in vec3 outColor;\n" +  // Input from vertex shader
                "out vec4 FragColor;\n" +  // Output color
                "void main() {\n" +
                "    FragColor = vec4(outColor, 1.0);\n" +  // Set the pixel color with alpha = 1 (opaque)
                "}";

        // Compile and link shaders
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        checkShaderCompile(vertexShader);

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        checkShaderCompile(fragmentShader);

        // Link shaders to create the shader program
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        glUseProgram(shaderProgram);

        // Set the vertex attributes (position and color) layout
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Delete shaders after linking
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void loop() {
        /*
        ================================================================================================
        Here we define the transformation matrix, which will rotate the triangle.
        The matrix is 4x4 because we're working with 3D transformations. Initially, the triangle is placed in front
        of the camera with no rotation.
        ================================================================================================
        */
        float[] transform = new float[]{
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, -3.0f,  // Move the object slightly forward in 3D space
                0.0f, 0.0f, 0.0f, 1.0f
        };

        int transformLocation = glGetUniformLocation(shaderProgram, "transform");

        // Main rendering loop
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);  // Clear the screen and depth buffer
            glEnable(GL_DEPTH_TEST);  // Enable depth testing for 3D rendering

            // Update rotation angle based on time
            float angle = System.nanoTime() / 1_000_000_000.0f;
            transform[0] = (float) Math.cos(angle);
            transform[1] = (float) -Math.sin(angle);
            transform[8] = (float) Math.sin(angle);
            transform[10] = (float) Math.cos(angle);

            // Pass the updated transformation matrix to the shader
            glUniformMatrix4fv(transformLocation, false, transform);

            // Render the triangle
            glDrawArrays(GL_TRIANGLES, 0, 3);
            glfwSwapBuffers(window);  // Swap buffers to display the new frame
            glfwPollEvents();  // Poll for window events (e.g., input)
        }
    }

    private void checkShaderCompile(int shaderId) {
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Shader compilation failed: " + glGetShaderInfoLog(shaderId));
        }
    }

    public void cleanUp() {
        // Clean up OpenGL resources
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
        glDeleteProgram(shaderProgram);
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public static void main(String[] args) {
        MakingTriangleAnimation main = new MakingTriangleAnimation();
        main.init();
        main.loop();
        main.cleanUp();
    }
}
