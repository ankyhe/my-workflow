package com.gmail.at.ankyhe.wdl.parser.model.expression;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.gmail.at.ankyhe.wdl.parser.exception.WdlParserException;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class GetNameExpression implements ExpressionCore {

    @NotNull
    private ExpressionCore scope;

    @NotNull
    private IdentifierExpression identifier;

    public String fullName() {
        if (this.scope instanceof IdentifierExpression) {
            final String scopeStr = ((IdentifierExpression) this.scope).getIdentifierName();
            return "%s.%s".formatted(scopeStr, this.identifier.getIdentifierName());
        }
        if (this.scope instanceof GetNameExpression) {
            final String scopeStr = ((GetNameExpression) this.scope).fullName();
            return "%s.%s".formatted(scopeStr, this.identifier.getIdentifierName());
        }
        throw new WdlParserException(
            ("The scope: %s of GetNameExpression: %s is not valid.  " +
                "The scope is neither IdentifierExpression nor GetNameExpression").formatted(this.scope, this)
        );
    }

    @AssertTrue
    public boolean isValid() {
        return (this.scope instanceof IdentifierExpression) || (this.scope instanceof GetNameExpression);
    }

    @Override
    public String toString() {
        return "%s.%s".formatted(this.scope, this.identifier);
    }
}
