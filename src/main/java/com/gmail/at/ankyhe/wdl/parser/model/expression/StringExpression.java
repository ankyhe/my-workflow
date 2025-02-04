package com.gmail.at.ankyhe.wdl.parser.model.expression;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class StringExpression implements PrimitiveLiteralExpression {

    @NotNull
    private String value;

    @Override
    public String toString() {
        return this.value;
    }
}
