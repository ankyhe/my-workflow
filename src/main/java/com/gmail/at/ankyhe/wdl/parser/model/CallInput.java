package com.gmail.at.ankyhe.wdl.parser.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.gmail.at.ankyhe.wdl.parser.model.expression.Expression;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class CallInput {

    private String name;

    private Expression expression;

    @Override
    public String toString() {
        return "%s = %s".formatted(this.name, this.expression);
    }
}
