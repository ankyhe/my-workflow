package com.gmail.at.ankyhe.wdl.parser.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.gmail.at.ankyhe.wdl.parser.model.expression.Expression;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class BoundDeclaration implements Declaration, InnerWorkflowElement {

    @NotBlank
    private Type type;

    @NotBlank
    private String argumentName;

    @NotNull
    private Expression expression;

    @Override
    public String toString() {
        return "%s %s=%s".formatted(this.type, this.argumentName, this.expression);
    }
}
