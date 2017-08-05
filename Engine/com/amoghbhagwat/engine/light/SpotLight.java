package com.amoghbhagwat.engine.light;

import org.joml.Vector3f;

public class SpotLight {
    private PointLight pointLight;
    private Vector3f coneDirection;
    private float cutOffAngle;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle) {
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        calculateCutOffAngle(cutOffAngle);
    }

    public SpotLight(SpotLight spotLight) {
        this(spotLight.getPointLight(), spotLight.getConeDirection(), spotLight.getCutOffAngle());
    }

    public final void calculateCutOffAngle(float cutOffAngle) {
        this.setCutOffAngle((float) Math.cos(Math.toRadians(cutOffAngle)));
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float getCutOffAngle() {
        return cutOffAngle;
    }

    public void setCutOffAngle(float cutOffAngle) {
        this.cutOffAngle = cutOffAngle;
    }
}
