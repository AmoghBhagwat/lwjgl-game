package com.amoghbhagwat.engine.light;

import org.joml.Vector3f;

public class PointLight {
    protected float intensity;
    private Vector3f color;
    private Vector3f position;
    private Attenuation attenuation;

    public PointLight(float intensity, Vector3f color, Vector3f position) {
        this.intensity = intensity;
        this.color = color;
        this.position = position;
        this.attenuation = new Attenuation(1, 0, 0);
    }

    public PointLight(float intensity, Vector3f color, Vector3f position, Attenuation attenuation) {
        this.intensity = intensity;
        this.color = color;
        this.position = position;
        this.attenuation = attenuation;
    }

    public PointLight(PointLight pointLight) {
        this(pointLight.getIntensity(), new Vector3f(pointLight.getColor()), new Vector3f(pointLight.getPosition()), pointLight.getAttenuation());
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Attenuation getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
    }

    public static class Attenuation {
        private float constant;
        private float linear;
        private float exponent;

        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        public float getConstant() {
            return constant;
        }

        public void setConstant(float constant) {
            this.constant = constant;
        }

        public float getLinear() {
            return linear;
        }

        public void setLinear(float linear) {
            this.linear = linear;
        }

        public float getExponent() {
            return exponent;
        }

        public void setExponent(float exponent) {
            this.exponent = exponent;
        }
    }
}
