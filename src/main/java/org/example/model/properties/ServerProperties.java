package org.example.model.properties;

import java.io.FileInputStream;
import java.util.Properties;

public class ServerProperties {

    protected static FileInputStream fileInputStream;
    protected static Properties PROPERTIES;

    //Статической инициализации класса
    static {
        try {
            fileInputStream = new FileInputStream("..//config//config.properties");
            PROPERTIES = new Properties();
            PROPERTIES.load(fileInputStream);
        } catch (Exception e) {
            System.out.println("Properties error: " + e.getMessage());
        } finally {
            if (fileInputStream != null)
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                    System.out.println("Properties error: " + e.getMessage());
                }
        }
    }

    public static String getProperty(String key) {
        return PROPERTIES.getProperty(key); }
}
