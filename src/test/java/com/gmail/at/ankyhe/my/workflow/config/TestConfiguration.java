package com.gmail.at.ankyhe.my.workflow.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;

import com.gmail.at.ankyhe.my.workflow.annotation.Task;
import com.gmail.at.ankyhe.my.workflow.annotation.TaskService;

import lombok.extern.slf4j.Slf4j;

@Configuration
public class TestConfiguration {

    @Slf4j
    @TaskService
    public static class ExampleTaskServiceForTest {

        private final List<String> machines = List.of(
                "machine1",
                "machine2",
                "machine3",
                "machine4",
                "machine5",
                "machine6",
                "machine7",
                "machine8",
                "machine9",
                "machine10",
                "machine11",
                "machine12"
        );

        @Task
        public Object getRecentRegisteredMachines(final String period, final int limit) {
            log.info("Call getRecentMachines({}, {})", period, limit);
            final int usedLimit = Math.min(limit, this.machines.size());
            return this.machines.subList(0, usedLimit);
        }

        @Task
        public Object putMachinesToPool(final List<String> machineIds, final String poolId) {
            log.info("Call addMachinesToPool({}, {})", machineIds, poolId);
            return List.copyOf(machineIds);
        }
    }
}
