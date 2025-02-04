package com.gmail.at.ankyhe.wdl.parser.model;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Data
@NoArgsConstructor
public abstract class AbstractInputOutput implements WorkflowElement {

    @NotEmpty
    protected List<@NotNull Declaration> declarations;

    @Override
    public String toString() {
        final String s =
            (this.declarations == null ? List.of() : this.declarations).stream().map(Objects::toString).collect(Collectors.joining(", "));
        return "%s (%s)".formatted(getClass().getSimpleName(), s);
    }
}
