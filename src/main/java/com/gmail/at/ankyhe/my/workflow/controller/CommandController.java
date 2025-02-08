package com.gmail.at.ankyhe.my.workflow.controller;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.gmail.at.ankyhe.my.workflow.model.CommandInput;
import com.gmail.at.ankyhe.my.workflow.model.CommandOutput;
import com.gmail.at.ankyhe.my.workflow.service.LlmService;

@RestController
public class CommandController {

    private final LlmService llmService;

    @Autowired
    public CommandController(final LlmService llmService) {
        this.llmService = llmService;
    }

    @PostMapping("/api/v1/commands")
    public ResponseEntity<CommandOutput> runCommand(@Validated @NotNull @RequestBody final CommandInput commandInput) {
        final String output = this.llmService.chat(commandInput.getInput());

        final CommandOutput commandOutput = CommandOutput.builder()
                .output(output)
                .build();
        return ResponseEntity.ok(commandOutput);
    }
}
