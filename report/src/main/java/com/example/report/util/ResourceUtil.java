package com.example.report.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceUtil {
    
    public static String readTemplateFromResource(String resourcePath) {
        
        try (InputStream inputStream = ResourceUtil.class.getResourceAsStream(resourcePath)) {
            if (inputStream != null) {
                
                StringBuilder templateContent = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        templateContent.append(line).append("\n");
                    }
                }
                return templateContent.toString();
            } else {
                throw new IllegalArgumentException("Template resource not found: " + resourcePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading email template: " + resourcePath, e);
        }
    }
}
