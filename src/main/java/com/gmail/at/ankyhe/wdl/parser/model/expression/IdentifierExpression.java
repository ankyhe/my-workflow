package com.gmail.at.ankyhe.wdl.parser.model.expression;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class IdentifierExpression implements PrimitiveLiteralExpression {

    @NotBlank
    private String identifierName;

    @Override
    public String toString() {
        return this.identifierName;
    }
}
