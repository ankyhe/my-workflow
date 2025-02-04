package com.gmail.at.ankyhe.wdl.parser.model.expression;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class IntExpression implements PrimitiveLiteralExpression {

    private int value;

    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
}
