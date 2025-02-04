package com.gmail.at.ankyhe.wdl.parser.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class Call implements InnerWorkflowElement {

    @NotBlank
    private String name;

    @Builder.Default
    private List<@NotNull CallInput> inputs = new ArrayList<>();

    @Override
    public String toString() {
        final String s = (this.inputs == null ? List.of() : this.inputs).stream().map(Objects::toString).collect(Collectors.joining(", "));

        return "Call: %s Inputs: [%s]".formatted(this.name, s);
    }
}
