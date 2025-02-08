package com.gmail.at.ankyhe.my.workflow.service;

import java.util.List;

import com.gmail.at.ankyhe.my.workflow.annotation.Task;
import com.gmail.at.ankyhe.my.workflow.annotation.TaskService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@TaskService
public class ExampleTaskService {

    @Task
    public Object getRecentMachines(final String period, final int limit) {
        log.info("Call getRecentMachines({}, {})", period, limit);
        final List<String> ret = List.of("vm_1", "vm_2", "vm_3", "vm_4", "vm_5");
        return ret.subList(0, Math.min(ret.size(), limit));
    }

    @Task
    public Object addMachinesToPool(final List<String> machineIds, final String poolId) {
        log.info("Call addMachinesToPool({}, {})", machineIds, poolId);
        return List.copyOf(machineIds);
    }
}
