package com.gmail.at.ankyhe.my.workflow.model;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class CommandOutput implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String output;
}
