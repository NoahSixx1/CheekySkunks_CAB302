package com.example.assignment1;



import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.utils.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * OllamaSyncResponse class allows questions to be sent to LLMs in a synchronous way.
 * This API corresponds to the completion API.
 * Need to choose the LLM that is available locally
 */
public class OllamaSyncResponse {

    static final String LLAMA3 = "llama3.1";
    private static final int HTTP_OK = 200;
    static final String host = "http://localhost:11434/";    private String prompt;
    private OllamaAPI ollamaAPI;
    private static Logger logger = LoggerFactory.getLogger(OllamaSyncResponse.class);

    /**
     * Syncs prompt for ollama query
     * @param prompt prompt for query
     */
    public OllamaSyncResponse(String prompt) {
        this.prompt = prompt;
        ollamaAPI = new OllamaAPI(host);
    }

    /**
     * Uses the Ollama4j API to generate a response to the prompt sent
     * @return String representing the response from LLM as long as HTTP response is 200 ok and response not null
     * @throws OllamaBaseException
     * @throws IOException
     * @throws InterruptedException
     */
    public String ollamaResponse() throws OllamaBaseException, IOException, InterruptedException {
        OllamaAPI ollamaAPI = new OllamaAPI(host);
        ollamaAPI.setRequestTimeoutSeconds(120);

        OllamaResult result =
                ollamaAPI.generate(LLAMA3, prompt,false, new OptionsBuilder().build());
        if (result.getHttpStatusCode()==HTTP_OK) {
            String response;
            if (result != null) {
                logger.info("A valid response is returned");
                return result.getResponse() + "\n \uD83D\uDC50\uD83C\uDFFB";

            }
        }
        logger.warn("NO VALID RESPONSE!!!!");
        return null;
    }


}

