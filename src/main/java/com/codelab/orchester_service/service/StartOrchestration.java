package com.codelab.orchester_service.service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class StartOrchestration {

    private final KubernetesClient client;

    public StartOrchestration() {
        this.client = new KubernetesClientBuilder().build();
    }

    public String startOrchestrationFromYml(String id, String language) {
        String namespace = "default";
        String replId = id;

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("static/ser.yaml")) {
            if (is == null) {
                return "YAML file not found.";
            }

            String rawYaml = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            rawYaml = rawYaml.replaceAll("service_name", replId);

            System.out.println(rawYaml);

            // Load and apply resources
            List<HasMetadata> resources = client.load(new ByteArrayInputStream(rawYaml.getBytes())).items();

            System.out.println(resources);

            for (HasMetadata resource : resources) {
                if (resource == null) {
                    System.out.println("Skipping null resource (possibly empty YAML section).");
                    continue;
                }
                if (resource.getMetadata() == null || resource.getKind() == null) {
                    System.out.println("Skipping resource with missing metadata or kind.");
                    continue;
                }

                client.resource(resource).inNamespace(namespace).createOrReplace();
                System.out.println("Applied " + resource.getKind() + ": " + resource.getMetadata().getName());
            }


            return "Resources created successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to create resources: " + e.getMessage();
        }
    }

    public String stopAndDeleteResources(String id) {
        String namespace = "default";

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("static/ser.yaml")) {
            if (is == null) {
                return "YAML file not found.";
            }

            String rawYaml = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            List<HasMetadata> resources = client.load(new ByteArrayInputStream(rawYaml.getBytes())).items();

            if (resources == null || resources.isEmpty()) {
                return "No resources found in the YAML.";
            }

            for (HasMetadata resource : resources) {
                if (resource == null || resource.getMetadata() == null || resource.getKind() == null) {
                    System.out.println("Skipping null or invalid resource.");
                    continue;
                }

                String resourceNamespace = resource.getMetadata().getNamespace();
                if (resourceNamespace == null || resourceNamespace.isEmpty()) {
                    resourceNamespace = namespace;
                }

                List<StatusDetails> deleted = client.resource(resource).inNamespace(resourceNamespace).delete();
                System.out.println("Deleted " + resource.getKind() + ": " + resource.getMetadata().getName() + " = " + deleted);
            }

            return "All resources attempted to be deleted.";

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to delete resources: " + e.getMessage();
        }
    }

}


