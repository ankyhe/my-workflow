package com.gmail.at.ankyhe.wdl.parser.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class UnboundDeclaration implements Declaration {

    @NotNull
    private Type type;

    private String argumentName;

    @Override
    public String toString() {
        return "%s %s".formatted(this.type, this.argumentName);
    }
}
