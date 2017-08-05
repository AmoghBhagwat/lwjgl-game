package com.amoghbhagwat.game;

import com.amoghbhagwat.engine.Utils;
import com.amoghbhagwat.engine.Window;
import com.amoghbhagwat.engine.graph.ShaderProgram;
import com.amoghbhagwat.engine.light.DirectionalLight;
import com.amoghbhagwat.engine.light.PointLight;
import com.amoghbhagwat.game.entities.Camera;
import com.amoghbhagwat.game.entities.GameItem;
import com.amoghbhagwat.game.utils.Transformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glEnable;

public class Renderer {
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.0f;

    private ShaderProgram shaderProgram;
    private Transformation transformation;

    private float specularPower;

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init(Window window) throws Exception {
        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.glsl"));
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.glsl"));
        shaderProgram.link();

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");
        shaderProgram.createUniform("textureSampler");
        shaderProgram.createMaterialUniform("material");
        shaderProgram.createUniform("specularPower");
        shaderProgram.createUniform("ambientLight");
        shaderProgram.createPointLightUniform("pointLight");
        shaderProgram.createDirectionalLightUniform("directionalLight");

        glEnable(GL_DEPTH_TEST);
    }

    public void render(Window window, List<GameItem> gameItems, Camera camera, Vector3f ambientLight, PointLight pointLight, DirectionalLight directionalLight) {
        clear();

        if (window.isResized()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // update projection matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // update view matrix
        Matrix4f viewMatrix = transformation.getViewMatrix(camera);
        shaderProgram.setUniform("textureSampler", 0);

        shaderProgram.setUniform("ambientLight", ambientLight);
        shaderProgram.setUniform("specularPower", specularPower);

        // update point light uniforms
        PointLight currentPointLight = new PointLight(pointLight);
        Vector3f lightPosition = currentPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPosition, 1.0f);
        aux.mul(viewMatrix);
        lightPosition.x = aux.x;
        lightPosition.y = aux.y;
        lightPosition.z = aux.z;
        shaderProgram.setUniform("pointLight", currentPointLight);

        // update directional light uniforms
        DirectionalLight currentDirectionalLight = new DirectionalLight(directionalLight);
        Vector4f direction = new Vector4f(currentDirectionalLight.getDirection(), 0);
        direction.mul(viewMatrix);
        currentDirectionalLight.setDirection(new Vector3f(direction.x, direction.y, direction.z));
        shaderProgram.setUniform("directionalLight", currentDirectionalLight);

        for (GameItem gameItem : gameItems) {
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);

            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            shaderProgram.setUniform("material", gameItem.getMesh().getMaterial());

            gameItem.getMesh().render();
        }

        shaderProgram.unbind();
    }

    public void clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanUp() {
        if (shaderProgram != null) {
            shaderProgram.cleanUp();
        }
    }
}
