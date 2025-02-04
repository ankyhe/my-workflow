package com.gmail.at.ankyhe.wdl.parser.listener;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.commons.lang3.StringUtils;
import com.gmail.at.ankyhe.wdl.parser.WdlParser;
import com.gmail.at.ankyhe.wdl.parser.exception.WdlParserException;
import com.gmail.at.ankyhe.wdl.parser.model.AnyType;
import com.gmail.at.ankyhe.wdl.parser.model.ArrayType;
import com.gmail.at.ankyhe.wdl.parser.model.BoundDeclaration;
import com.gmail.at.ankyhe.wdl.parser.model.Call;
import com.gmail.at.ankyhe.wdl.parser.model.CallInput;
import com.gmail.at.ankyhe.wdl.parser.model.Declaration;
import com.gmail.at.ankyhe.wdl.parser.model.Input;
import com.gmail.at.ankyhe.wdl.parser.model.IntType;
import com.gmail.at.ankyhe.wdl.parser.model.Output;
import com.gmail.at.ankyhe.wdl.parser.model.StringType;
import com.gmail.at.ankyhe.wdl.parser.model.Task;
import com.gmail.at.ankyhe.wdl.parser.model.Type;
import com.gmail.at.ankyhe.wdl.parser.model.UnboundDeclaration;
import com.gmail.at.ankyhe.wdl.parser.model.Workflow;
import com.gmail.at.ankyhe.wdl.parser.model.expression.DoubleExpression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.Expression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.ExpressionCore;
import com.gmail.at.ankyhe.wdl.parser.model.expression.GetNameExpression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.IdentifierExpression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.IntExpression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.LongExpression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.RangeExpression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.StringExpression;

public class MyWdlParserListener extends WdlParserDefaultListener {

    @Getter
    private String version;

    @Getter
    private final List<Task> tasks = new ArrayList<>();

    @Getter
    private final List<Workflow> workflows = new ArrayList<>();

    private Type type;

    private Task task;

    private Call call;

    private Workflow workflow;

    private Expression expression;

    private RangeExpression rangeExpression;

    private List<Declaration> declarations;

    @Override
    public void enterVersion(final WdlParser.VersionContext ctx) {
        this.version = ctx.RELEASE_VERSION() == null ? null : ctx.RELEASE_VERSION().getText();

        super.enterVersion(ctx);
    }

    @Override
    public void enterType_base(final WdlParser.Type_baseContext ctx) {
        if (ctx.STRING() != null) {
            this.type = StringType.TYPE;
        } else if (ctx.INT() != null) {
            this.type = IntType.TYPE;
        } else {
            this.type = AnyType.TYPE;
        }
        super.enterType_base(ctx);
    }

    @Override
    public void exitArray_type(final WdlParser.Array_typeContext ctx) {
        this.type = new ArrayType(this.type);
        super.exitArray_type(ctx);
    }

    @Override
    public void exitUnbound_decls(final WdlParser.Unbound_declsContext ctx) {
        final String identifier = ctx.Identifier().getText();
        final UnboundDeclaration unboundDeclaration = UnboundDeclaration.builder().type(this.type).argumentName(identifier).build();
        if (this.declarations == null) {
            throw new WdlParserException("Failed to settle the unbound declaration: %s".formatted(ctx.getText()));
        }

        this.declarations.add(unboundDeclaration);

        super.exitUnbound_decls(ctx);
    }

    @Override
    public void exitBound_decls(final WdlParser.Bound_declsContext ctx) {
        final String identifier = ctx.Identifier().getText();
        final BoundDeclaration boundDeclaration = BoundDeclaration.builder()
            .type(this.type)
            .argumentName(identifier)
            .expression(this.expression)
            .build();

        if (this.declarations != null) {
            this.declarations.add(boundDeclaration);
        } else if (this.workflow != null) {
            this.workflow.getWorkflowElements().add(boundDeclaration);
        } else {
            throw new WdlParserException("Failed to settle the bound declaration: %s".formatted(ctx.getText()));
        }

        super.exitBound_decls(ctx);
    }

    @Override
    public void enterTask(final WdlParser.TaskContext ctx) {
        final String taskName = ctx.Identifier().getText();
        this.task = Task.builder().name(taskName).build();

        super.enterTask(ctx);
    }

    @Override
    public void enterTask_input(final WdlParser.Task_inputContext ctx) {
        this.declarations = new ArrayList<>();

        super.enterTask_input(ctx);
    }

    @Override
    public void exitTask_input(final WdlParser.Task_inputContext ctx) {
        final Input input = Input.builder().declarations(this.declarations).build();
        this.task.setInput(input);
        this.declarations = null;

        super.exitTask_input(ctx);
    }

    @Override
    public void enterTask_output(final WdlParser.Task_outputContext ctx) {
        this.declarations = new ArrayList<>();

        super.enterTask_output(ctx);
    }

    @Override
    public void exitTask_output(final WdlParser.Task_outputContext ctx) {
        final Output output = Output.builder().declarations(this.declarations).build();
        this.task.setOutput(output);
        this.declarations = null;

        super.exitTask_output(ctx);
    }

    @Override
    public void exitTask(final WdlParser.TaskContext ctx) {
        this.tasks.add(this.task);
        this.task = null;

        super.exitTask(ctx);
    }

    @Override
    public void enterCall(final WdlParser.CallContext ctx) {
        this.call = Call.builder().name(ctx.call_name().getText()).build();

        super.enterCall(ctx);
    }

    @Override
    public void exitCall_input(final WdlParser.Call_inputContext ctx) {
        final CallInput input = CallInput.builder().name(ctx.Identifier().getText()).expression(this.expression).build();
        this.call.getInputs().add(input);
        this.expression = null;

        super.exitCall_input(ctx);
    }

    @Override
    public void exitCall(final WdlParser.CallContext ctx) {
        this.workflow.getWorkflowElements().add(this.call);
        this.call = null;

        super.exitCall(ctx);
    }

    @Override
    public void enterWorkflow(final WdlParser.WorkflowContext ctx) {
        this.workflow = Workflow.builder().name(ctx.Identifier().getText()).build();

        super.enterWorkflow(ctx);
    }

    @Override
    public void enterWorkflow_input(final WdlParser.Workflow_inputContext ctx) {
        this.declarations = new ArrayList<>();

        super.enterWorkflow_input(ctx);
    }

    @Override
    public void exitWorkflow_input(final WdlParser.Workflow_inputContext ctx) {
        final Input input = Input.builder().declarations(this.declarations).build();
        this.workflow.getWorkflowElements().add(input);
        this.declarations = null;

        super.exitWorkflow_input(ctx);
    }

    @Override
    public void enterWorkflow_output(final WdlParser.Workflow_outputContext ctx) {
        this.declarations = new ArrayList<>();

        super.enterWorkflow_output(ctx);
    }

    @Override
    public void exitWorkflow_output(final WdlParser.Workflow_outputContext ctx) {
        final Output output = Output.builder().declarations(this.declarations).build();
        this.workflow.getWorkflowElements().add(output);
        this.declarations = null;

        super.exitWorkflow_output(ctx);
    }

    @Override
    public void exitWorkflow(final WdlParser.WorkflowContext ctx) {
        this.workflows.add(this.workflow);
        this.workflow = null;

        super.exitWorkflow(ctx);
    }

    @Override
    public void exitPrimitives(final WdlParser.PrimitivesContext ctx) {
        if (ctx.primitive_literal().number() != null) {
            final String s = ctx.primitive_literal().number().getText();
            if (StringUtils.contains(s, ".")) {
                final double value = Double.parseDouble(s);
                this.expression = DoubleExpression.builder().value(value).build();
            } else {
                final long value = Long.parseLong(s);
                if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
                    this.expression = IntExpression.builder().value((int) value).build();
                } else {
                    this.expression = LongExpression.builder().value((int) value).build();
                }
            }
        } else if (ctx.primitive_literal().string() != null) {
            final String s = ctx.primitive_literal().string().getText();
            this.expression = StringExpression.builder().value(s).build();
        } else if (ctx.primitive_literal().Identifier() != null) {
            this.expression = IdentifierExpression.builder().identifierName(ctx.primitive_literal().Identifier().getText()).build();
        } else {
            throw new WdlParserException("Failed to parse primitive: %s in current version".formatted(ctx.getText()));
        }

        super.exitPrimitives(ctx);
    }

    @Override
    public void exitGet_name(final WdlParser.Get_nameContext ctx) {
        if (!(this.expression instanceof ExpressionCore)) {
            throw new WdlParserException("Failed to parse get_name expr_core: %s".formatted(ctx.getText()));
        }

        final IdentifierExpression identifier = IdentifierExpression.builder().identifierName(ctx.Identifier().getText()).build();

        this.expression = GetNameExpression.builder().scope((ExpressionCore) this.expression).identifier(identifier).build();

        super.exitGet_name(ctx);
    }

    @Override
    public void enterRange(final WdlParser.RangeContext ctx) {
        this.rangeExpression = new RangeExpression();

        super.enterRange(ctx);
    }

    @Override
    public void exitRange(final WdlParser.RangeContext ctx) {
        this.expression = this.rangeExpression;
        this.rangeExpression = null;

        super.exitRange(ctx);
    }

    @Override
    public void exitExpr(final WdlParser.ExprContext ctx) {
        if (ctx.parent instanceof final WdlParser.RangeContext parent) {
            if (parent.expr(0).getRuleContext() == ctx) {
                this.rangeExpression.setBegin(this.expression);
            } else if (parent.expr(1).getRuleContext() == ctx) {
                this.rangeExpression.setEnd(this.expression);
            } else {
                throw new WdlParserException("Failed to parse expression as expr_core: %s".formatted(ctx.getText()));
            }
        }

        super.exitExpr(ctx);
    }

    @Override
    protected void exitExpr_core(final ParserRuleContext ctx) {
        if (ctx.parent instanceof final WdlParser.RangeContext parent) {
            if (parent.expr_core().getRuleContext() == ctx) {
                if (!(this.expression instanceof ExpressionCore)) {
                    throw new WdlParserException("Failed to parse expression as expr_core: %s".formatted(ctx.getText()));
                }
                this.rangeExpression.setExpressionCore((ExpressionCore) this.expression);
            }
        }
    }
}
