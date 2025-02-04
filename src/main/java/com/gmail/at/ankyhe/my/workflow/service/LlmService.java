package com.gmail.at.ankyhe.my.workflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LlmService {

    private final LlmChatService llmChatService;

    private final WorkflowService workflowService;

    @Autowired
    public LlmService(final LlmChatService llmChatService, final WorkflowService workflowService) {
        this.llmChatService = llmChatService;
        this.workflowService = workflowService;
    }

    public String chat(final String input) {
        final String wdlString = this.llmChatService.toWdlString(input, null);
        log.debug("wdlString is {}", wdlString);
        final String output = this.workflowService.runWdl(wdlString);
        log.debug("output is {}", output);
        return output;
    }
}
