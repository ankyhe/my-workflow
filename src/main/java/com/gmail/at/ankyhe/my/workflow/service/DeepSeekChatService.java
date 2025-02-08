package com.gmail.at.ankyhe.my.workflow.service;

import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gmail.at.ankyhe.my.workflow.util.JsonUtils;
import com.gmail.at.ankyhe.my.workflow.util.ResourceHelper;
import com.google.common.base.Preconditions;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeepSeekChatService implements LlmChatService {

    private static final String API_KEY_NOT_CONFIGURED_PROPERLY_MESSAGE = "DeepSeek api key to chat service is not configured properly";

    private static final String WDL_TASK_DEFINITION_PREFIX = "已经在WDL中定义了以下任务:";

    private static final String COMMAND_PREFIX = "请把下面这句话转成WDL:\n";

    private static final String WDL_PREFIX = "```wdl";

    private String wdlPromptPrefix;

    @Value("${llm.deepSeek-api-url:https://api.deepseek.com/chat/completions}")
    private String deepSeekApiUrl;

    // This file: deepseek.wdl.prompt.prefix will be generated automatically from @TaskService and @Task
    @Value("${llm.deepSeek-prompt-prefix-file:llm/deepseek.wdl.prompt.prefix}")
    private String deepSeekPromptPrefixFile;

    @Value("${llm.deepSeek-api-key:#{null}}")
    private String deepSeekApiKey;

    private final RestTemplate restTemplate;

    @Autowired
    public DeepSeekChatService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    void init() {
        this.wdlPromptPrefix = ResourceHelper.stringFromResourceFile(this.deepSeekPromptPrefixFile);
    }

    @Override
    public String toWdlString(final String userInput, final Map<String, Object> context) {
        Preconditions.checkArgument(StringUtils.isNotBlank(userInput), "userInput should not be blank");

        final String input = COMMAND_PREFIX + userInput;
        if (StringUtils.isBlank(this.deepSeekApiKey)) {
            return API_KEY_NOT_CONFIGURED_PROPERLY_MESSAGE;
        }

        final HttpHeaders headers = prepareHttpHeaders();
        final List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", WDL_TASK_DEFINITION_PREFIX + this.wdlPromptPrefix),
                Map.of("role", "user", "content", input)
        );

        final Map<String, Object> body = Map.of(
                "model",
                "deepseek-chat",
                "messages",
                messages,
                "max_tokens",
                4096,
                "response_format",
                Map.of("type", "text")
        );

        final HttpEntity<String> request = new HttpEntity<>(JsonUtils.toString(body), headers);

        final HttpEntity<String> response = this.restTemplate.postForEntity(this.deepSeekApiUrl, request, String.class);
        log.debug("Response body is {}", response.getBody());

        final String documentStr = extractWdlFromResponseBody(response.getBody());

        return """
                version 1.2
                %s
                %s""".formatted(this.wdlPromptPrefix, documentStr);
    }

    private HttpHeaders prepareHttpHeaders() {
        final HttpHeaders headers = new HttpHeaders();

        headers.put("Content-Type", List.of("application/json"));
        headers.put("Accept", List.of("application/json"));
        headers.put("Authorization", List.of("Bearer %s".formatted(this.deepSeekApiKey)));

        return headers;
    }

    private String extractWdlFromResponseBody(String body) {
        if (body == null) {
            return "";
        }

        body = StringUtils.replace(body, "version 1.0", "");
        body = StringUtils.replace(body, "\\\\\"", "\"");
        body = StringUtils.replace(body, "\\\\", "\\");
        body = StringUtils.replace(body, "\\\"", "\"");
        body = StringUtils.replace(body, "\\n", "\n");

        int begin = body.indexOf(WDL_PREFIX);
        if (begin == -1) {
            return "";
        }

        begin += WDL_PREFIX.length();

        final int end = body.indexOf("```", begin);
        if (end == -1) {
            return body.substring(begin);
        }

        return body.substring(begin, end);
    }

}
