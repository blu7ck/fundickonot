package com.blu4ck.fundickonot.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigService {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = ConfigService.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("⚠️ config.properties bulunamadı!");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("⚠️ config.properties okunamadı!", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}