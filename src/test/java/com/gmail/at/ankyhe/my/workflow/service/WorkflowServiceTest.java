package com.gmail.at.ankyhe.my.workflow.service;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.gmail.at.ankyhe.my.workflow.IntegrationTest;
import com.gmail.at.ankyhe.my.workflow.util.ResourceHelper;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@IntegrationTest
public class WorkflowServiceTest {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private Validator validator;

    @Test
    public void testRunWdl() {
        final String s = ResourceHelper.stringFromResourceFile("wdl/test1.wdl");
        final String output = this.workflowService.runWdl(s);

        final List<String> expectedList = new ArrayList<>();
        for (int i = 1; i <= 10; ++i) {
            expectedList.add("machine" + i);
        }
        assertThat(output, is(expectedList.toString()));
    }

    @Test
    public void testCamelToSnake() {
        assertThat(WorkflowService.camelToSnake("GetRecentMachines"), is("get_recent_machines"));
        assertThat(WorkflowService.camelToSnake("getRecentMachines"), is("get_recent_machines"));
        assertThat(WorkflowService.camelToSnake("get_recent_machines"), is("get_recent_machines"));
    }
}
