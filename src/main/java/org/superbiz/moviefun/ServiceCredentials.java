package org.superbiz.moviefun;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

public class ServiceCredentials {

    private final String vcapServices;

    public ServiceCredentials() {
        this.vcapServices = "{\"user-provided\": [{\"credentials\": {\"access_key_id\":  \"${MINIO_ACCESS_KEY:?missing}\", \"bucket\": \"moviefun\", \"secret_access_key\": \"${MINIO_SECRET_ACCESS_KEY:?missing}\", \"endpoint\": \"http://127.0.0.1:9000\"}, \"name\": \"photo-storage\"}]}";
    }

    public String getCredential(String serviceName, String serviceType, String credentialKey) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode root;

        try {
            root = objectMapper.readTree(vcapServices);
        } catch (IOException e) {
            throw new IllegalStateException("No VCAP_SERVICES found", e);
        }

        JsonNode services = root.path(serviceType);

        for (JsonNode service : services) {
            if (Objects.equals(service.get("name").asText(), serviceName)) {
                return service.get("credentials").get(credentialKey).asText();
            }
        }

        throw new IllegalStateException("No "+ serviceName + " found in VCAP_SERVICES");
    }
}
