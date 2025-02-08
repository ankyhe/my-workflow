package com.gmail.at.ankyhe.my.workflow.service;

import java.util.List;

import com.gmail.at.ankyhe.my.workflow.annotation.Task;
import com.gmail.at.ankyhe.my.workflow.annotation.TaskService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@TaskService
public class LlmTaskService {

    @Task
    public Object getRecentMachines(final String period, final int limit) {
        log.info("Call getRecentMachines({}, {})", period, limit);
        return List.of("vm_1", "vm_2", "vm_3", "vm_4", "vm_5");
    }

    @Task
    public Object addMachinesToPool(final List<String> machineIds, final String poolId) {
        log.info("Call addMachinesToPool({}, {})", machineIds, poolId);
        return List.copyOf(machineIds);
    }
}
