#version 330

in vec2 outTextureCoord;
in vec3 mvVertexNormal;
in vec3 mvVertexPosition;

out vec4 fragmentColor;

struct Attenuation {
    float constant;
    float linear;
    float exponent;
};

struct PointLight {
    vec3 color;
    vec3 position;
    float intensity;
    Attenuation attenuation;
};

struct DirectionalLight {
    vec3 color;
    vec3 direction;
    float intensity;
};

struct Material {
    vec4 diffuse;
    vec4 ambient;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

uniform sampler2D textureSampler;
uniform vec3 ambientLight;
uniform float specularPower;
uniform Material material;
uniform PointLight pointLight;
uniform DirectionalLight directionalLight;

vec4 ambientC;
vec4 diffuseC;
vec4 speculrC;

void setUpColors(Material material, vec2 textCoord) {
    if (material.hasTexture == 1)
    {
        ambientC = texture(textureSampler, textCoord);
        diffuseC = ambientC;
        speculrC = ambientC;
    }
    else
    {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        speculrC = material.specular;
    }
}

vec4 calculateLightColor(vec3 lightColor, float lightIntensity, vec3 position, vec3 toLightDirection, vec3 normal) {
    vec4 diffuseColor = vec4(0, 0, 0, 0);
    vec4 specularColor = vec4(0, 0, 0, 0);

    // diffuse light
    float diffuseFactor = max(dot(normal, toLightDirection), 0.0);
    diffuseColor = diffuseC * vec4(lightColor, 1.0) * lightIntensity * diffuseFactor;

    // specular light
    vec3 cameraDirection = normalize(-position);
    vec3 fromLightDirection = -toLightDirection;
    vec3 reflectedLight = normalize(reflect(fromLightDirection, normal));
    float specularFactor = max(dot(cameraDirection, reflectedLight), 0.0);
    specularFactor = pow(specularFactor, specularPower);
    specularColor = lightIntensity * specularFactor * material.reflectance * vec4(lightColor, 1.0);

    return diffuseColor + specularColor;
}

vec4 calculatePointLight(PointLight light, vec3 position, vec3 normal) {
    vec3 lightDirection = light.position - position;
    vec3 toLightDirection = normalize(lightDirection);
    vec4 lightColor = calculateLightColor(light.color, light.intensity, position, toLightDirection, normal);

    // apply attenuation
    float distance = length(lightDirection);
    float attenuationInverse = light.attenuation.constant + light.attenuation.linear * distance + light.attenuation.exponent * distance * distance;

    return lightColor / attenuationInverse;
}

vec4 calculateDirectionalLight(DirectionalLight light, vec3 position, vec3 normal) {
    return calculateLightColor(light.color, light.intensity, position, normalize(light.direction), normal);
}

void main() {
    setUpColors(material, outTextureCoord);

    vec4 diffuseSpecularComp = calculateDirectionalLight(directionalLight, mvVertexPosition, mvVertexNormal);
    diffuseSpecularComp += calculatePointLight(pointLight, mvVertexPosition, mvVertexNormal);

    fragmentColor = ambientC * vec4(ambientLight, 1.0) + diffuseSpecularComp;
}

