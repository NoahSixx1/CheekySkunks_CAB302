package com.example.assignment1;


import io.github.ollama4j.OllamaAPI;

/**
 * Simple class to check that Ollama Server is running locally
 */
public class OllamaAPITest {


    private static final String HOST = "http://localhost:11434/";

    public OllamaAPITest() {
        OllamaAPI ollamaAPI = new OllamaAPI(HOST);
        if (ollamaAPI.ping()) {
            System.out.println("The Ollama Server is running: ");
        } else {
            System.out.println("The Ollama Server is currently not available ");
        }
    }
}