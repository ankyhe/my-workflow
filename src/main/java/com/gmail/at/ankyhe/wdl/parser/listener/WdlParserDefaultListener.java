package com.gmail.at.ankyhe.wdl.parser.listener;

import org.antlr.v4.runtime.ParserRuleContext;
import com.gmail.at.ankyhe.wdl.parser.WdlParser;
import com.gmail.at.ankyhe.wdl.parser.WdlParserBaseListener;

public class WdlParserDefaultListener extends WdlParserBaseListener {

    @Override
    public void exitApply(final WdlParser.ApplyContext ctx) {
        super.exitApply(ctx);
        exitExpr_core(ctx);
    }

    @Override
    public void exitArray_literal(final WdlParser.Array_literalContext ctx) {
        super.exitArray_literal(ctx);
        exitExpr_core(ctx);
    }

    @Override
    public void exitGet_name(final WdlParser.Get_nameContext ctx) {
        super.exitGet_name(ctx);
        exitExpr_core(ctx);
    }

    @Override
    public void exitPrimitive_literal(final WdlParser.Primitive_literalContext ctx) {
        super.exitPrimitive_literal(ctx);
        exitExpr_core(ctx);
    }

    /*
     * Below is the expr_core in parser file:
    ```g4
    expr_core
	: Identifier LPAREN (expr (COMMA expr)*)? RPAREN #apply
	| LBRACK (expr (COMMA expr)*)* RBRACK #array_literal
	| LPAREN expr COMMA expr RPAREN #pair_literal
	| LBRACE (expr COLON expr (COMMA expr COLON expr)*)* RBRACE #map_literal
	| Identifier LBRACE (Identifier COLON expr (COMMA Identifier COLON expr)*)* RBRACE #struct_literal
	| IF expr THEN expr ELSE expr #ifthenelse
    | LPAREN expr RPAREN #expression_group
	| expr_core LBRACK expr RBRACK #at
	| expr_core LBRACK expr COLON expr RBRACK #range
    | expr_core DOT Identifier #get_name
    | NOT expr #negate
    | (PLUS | MINUS) expr #unirarysigned
	| primitive_literal #primitives
	| Identifier #left_name
    ;
    ```
    It should call exitExpr_core in all these exit_foo, such as exit_apply.  In PoC, I just add several cases.
    */

    protected void exitExpr_core(final ParserRuleContext ctx) {}
}
