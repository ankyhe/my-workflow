package com.gmail.at.ankyhe.my.workflow.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.HttpStatusCode;
import org.springframework.beans.factory.annotation.Autowired;

import com.gmail.at.ankyhe.my.workflow.IntegrationTest;
import com.gmail.at.ankyhe.my.workflow.util.ResourceHelper;

import lombok.extern.slf4j.Slf4j;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;

@IntegrationTest
@Slf4j
public class LlmServiceTest {

    @Autowired
    private LlmService llmService;

    private ClientAndServer deepSeekMockServer;

    @BeforeEach
    public void startServer() {
        this.deepSeekMockServer = startClientAndServer(13000);
        setUpDeepseekMockServer();
    }

    @AfterEach
    public void stopServer() {
        this.deepSeekMockServer.stop();
    }

    private void setUpDeepseekMockServer() {
        this.deepSeekMockServer.when(HttpRequest.request().withMethod("POST").withPath("/chat/completions")).respond(
                HttpResponse.response()
                        .withStatusCode(HttpStatusCode.OK_200.code())
                        .withBody(ResourceHelper.stringFromResourceFile("mock-server/deepseek.mockserver.response.body"))
        );
    }

    @Test
    void testChat() {
        final String input = "请把最近注册的10台机器加入到资源池子A";
        final String output = this.llmService.chat(input);
        log.info("output is {}", output);
        assertThat(output, not(emptyOrNullString()));
    }
}

