package com.gmail.at.ankyhe.wdl.parser.model.expression;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class RangeExpression implements ExpressionCore {

    @NotNull
    private ExpressionCore expressionCore;

    /*
     * Inclusive
     */
    @NotNull
    private Expression begin;

    /*
     * Exclusive
     */
    @NotNull
    private Expression end;

    @AssertTrue
    public boolean isValid() {
        return (this.begin instanceof IntExpression) && (this.end instanceof IntExpression);
    }

    @Override
    public String toString() {
        return "%s[%s:%s]".formatted(this.expressionCore, this.begin, this.end);
    }
}
