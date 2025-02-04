package com.gmail.at.ankyhe.my.workflow.service;

import java.util.Map;

public interface LlmChatService {

    /**
     * Retrieve WDL string from LLM model.
     *
     * @param input The input string.
     * @param context The context of conversation/chat.
     *
     * @return the wdl document
     */
    String toWdlString(String input, Map<String, Object> context);
}
