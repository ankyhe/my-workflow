version 1.2

task get_recent_machines {
  input {
    String period
    Int limit
  }

  output {
    # get recent added machines
    Array[String] machine_ids
  }
}

task add_machines_to_pool {
  input {
    Array[String] machine_ids
    String pool_id
  }
  output {
   Array[String] added_machines
  }
}

workflow AddRecentMachinesToPool {
  input {
    String period = "7d"  # 假设获取最近7天内添加的机器
    String pool_id = "desktop_pool_1"  # 目标桌面池ID
  }

  # 调用 get_recent_machines 任务，获取最近添加的机器
  call get_recent_machines {
    input:
      period = period,
      limit = 10
  }

  # 从获取的机器列表中提取前10台机器
  Array[String] top_10_machines = get_recent_machines.machine_ids[0:10]

  # 调用 add_machines_to_pool 任务，将前10台机器加入指定桌面池
  call add_machines_to_pool {
    input:
      machine_ids = top_10_machines,
      pool_id = pool_id
  }

  # 输出添加到桌面池的机器ID列表
  output {
    Array[String] added_machines = add_machines_to_pool.added_machines
  }
}
