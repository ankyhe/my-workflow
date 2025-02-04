package com.gmail.at.ankyhe.wdl.parser.model.expression;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class LongExpression implements PrimitiveLiteralExpression {

    private long value;

    @Override
    public String toString() {
        return Long.toString(this.value);
    }
}
