package com.amoghbhagwat.game;

import com.amoghbhagwat.engine.GameLogic;
import com.amoghbhagwat.engine.Window;
import com.amoghbhagwat.engine.graph.Mesh;
import com.amoghbhagwat.engine.light.DirectionalLight;
import com.amoghbhagwat.engine.light.PointLight;
import com.amoghbhagwat.engine.light.SpotLight;
import com.amoghbhagwat.engine.models.Material;
import com.amoghbhagwat.engine.models.OBJLoader;
import com.amoghbhagwat.game.entities.Camera;
import com.amoghbhagwat.game.entities.GameItem;
import com.amoghbhagwat.game.entities.Texture;
import com.amoghbhagwat.game.utils.MouseInput;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements GameLogic {
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private static final float CAMERA_POS_STEP = 0.5f;

    private final Renderer renderer;
    private final Vector3f cameraInc;
    private final Camera camera;

    private List<GameItem> gameItems = new ArrayList<>();

    private Vector3f ambientLight;
    private PointLight pointLight;
    private SpotLight spotLight;
    private DirectionalLight directionalLight;

    private float lightAngle;

    private float spotAngle = 0;
    private float spotInc = 1;

    public DummyGame() {
        renderer = new Renderer();
        cameraInc = new Vector3f();
        camera = new Camera(new Vector3f(), new Vector3f());
        lightAngle = -90;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);

        float reflectance = 1f;

        Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("/textures/grassblock.png");
        Material material = new Material(reflectance, texture);
        mesh.setMaterial(material);

        GameItem gameItem = new GameItem(mesh);
        gameItem.setScale(1.5f);
        gameItem.setPosition(1, -1, 1);

        gameItems.add(gameItem);

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

        // Point Light
        Vector3f lightColor = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 0, 1);
        float lightIntensity = 0f;
        pointLight = new PointLight(lightIntensity, lightColor, lightPosition);
        PointLight.Attenuation attenuation = new PointLight.Attenuation(0, 0, 1);
        pointLight.setAttenuation(attenuation);

        // Spot Light
        lightPosition = new Vector3f(0.0f, 0.0f, 10f);
        pointLight = new PointLight(lightIntensity, new Vector3f(1, 1, 1), lightPosition);
        attenuation = new PointLight.Attenuation(0.0f, 0.0f, 0.02f);
        pointLight.setAttenuation(attenuation);
        Vector3f coneDirection = new Vector3f(0, 0, -1);
        float cutOffAngle = (float) Math.cos(Math.toRadians(140));
        spotLight = new SpotLight(pointLight, coneDirection, cutOffAngle);

        // Directional Light
        lightPosition = new Vector3f(-1, 0, 0);
        lightColor = new Vector3f(1, 1, 1);
        directionalLight = new DirectionalLight(lightColor, lightPosition, 1.0f);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotationVector = mouseInput.getDisplayVector();
            camera.moveRotation(rotationVector.x * MOUSE_SENSITIVITY, rotationVector.y * MOUSE_SENSITIVITY, 0);
        }

        // Update spot light direction
        spotAngle += spotInc * 0.05f;
        if (spotAngle > 2) {
            spotInc = -1;
        } else if (spotAngle < -2) {
            spotInc = 1;
        }
        double spotAngleRadians = Math.toRadians(spotAngle);
        Vector3f coneDirection = spotLight.getConeDirection();
        coneDirection.y = (float) Math.sin(spotAngleRadians);

        // Update directional light direction, intensity and color
        lightAngle += 1.1f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 360) {
                lightAngle = -90;
            }
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }

        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, gameItems, camera, ambientLight, pointLight, spotLight, directionalLight);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        for (GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}