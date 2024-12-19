import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class learningLWJGL {

    public static void main(String [] a){


        /*
         =====================================================================================================================================
         GLFW.glfwCreateWindow(); in this there are main 5 parameter which are use
         there is width for the window the height for it and then the title for it
         after that there is the monitor and that just means that which monitor it is going to go on
         and usually it is going to be zero as it is just going to go on the primary monitor
         at last there is the share and that means that would you like to share (texture, buffer and other stuff)with the opengl or not
         =====================================================================================================================================
        */

        if(!GLFW.glfwInit()){
            System.err.println("GLFW initialization failed");

        }

        /*
         this is hints which we have to set up
         first of all what are the hint they
         hint are used to configure how the window and opengl context is going to behave
         this is going to let you allow and customize settings for opengl
         for setting up the two major and then the minor hint


         ======================================================================================
         basically there are two major and then the minor version which there are for opengl
         these are used for using function which are inside core or inside some other dependencies
         ======================================================================================
        */

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);


        /*
        ======================================================================================
         the profile is for the context which can be either be the core profile or the compatibility profile
         the core profile -
            this is usually recommended for the application which are only need for the non-deprecated feature
         the compatibility profile -
            this is for accessing the older opengl function which might have been removed from the newer version
            of opengl
        ======================================================================================
         */
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);

        long window = GLFW.glfwCreateWindow(800, 600, "Hello World", 0, 0);
        if(window == 0){
            System.err.println("GLFW window creation failed");
        }


        // this is to make the contex window which is there
        GLFW.glfwMakeContextCurrent(window);
        
        /*
         ======================================================================================
         This is here for enabling v-sync.
         So what is v-sync? 
         V-sync is basically a visual artifact where two parts of the frame are displayed together.
         This usually happens when the graphics card outputs a new frame before the monitor finishes
         displaying the previous one.

         By setting the monitor to 1 what we are going is that we are tell the GLFW to reset the buffer once per monitor
         ======================================================================================
         */

        GLFW.glfwSwapInterval(1);
        // this is just to show the window
        GLFW.glfwShowWindow(window);

        // this is for initialize opengl
        GL.createCapabilities();

        // this is the main rendering loop for the window
        while(!GLFW.glfwWindowShouldClose(window)){

            // this is for the things like the input for which comes in through the user
            // or it is for the window resizing which there is.
            GLFW.glfwPollEvents();

           /*
            this is for setting up the color which is need
            it take in 4 parameters which are the red, green, blue and the alpha which is there
            and at the end this are all going to be represented inside 0.0f
            and the end which is going ot be 1.0f
           */
            GL11.glClearColor(0.45f, 0.3f, 0.5f, 1.0f);

            // this is clearing the GL_COLOR_BUFFER_BIT what basically this is a constant inside the class GL11
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            // this is just switching the buffers
            GLFW.glfwSwapBuffers(window);
        }
        // this is just destroying the window which is made
        GLFW.glfwDestroyWindow(window);

        // terminating everything.
        GLFW.glfwTerminate();


    }


}