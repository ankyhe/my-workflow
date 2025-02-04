package com.gmail.at.ankyhe.my.workflow.service;

import com.google.common.base.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Validator;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import com.gmail.at.ankyhe.my.workflow.annotation.Task;
import com.gmail.at.ankyhe.my.workflow.annotation.TaskService;
import com.gmail.at.ankyhe.my.workflow.exception.WdlInvokingException;
import com.gmail.at.ankyhe.wdl.parser.SimpleWdlParser;
import com.gmail.at.ankyhe.wdl.parser.model.AnyType;
import com.gmail.at.ankyhe.wdl.parser.model.BoundDeclaration;
import com.gmail.at.ankyhe.wdl.parser.model.Call;
import com.gmail.at.ankyhe.wdl.parser.model.CallInput;
import com.gmail.at.ankyhe.wdl.parser.model.Declaration;
import com.gmail.at.ankyhe.wdl.parser.model.Input;
import com.gmail.at.ankyhe.wdl.parser.model.IntType;
import com.gmail.at.ankyhe.wdl.parser.model.LongType;
import com.gmail.at.ankyhe.wdl.parser.model.Output;
import com.gmail.at.ankyhe.wdl.parser.model.StringType;
import com.gmail.at.ankyhe.wdl.parser.model.Type;
import com.gmail.at.ankyhe.wdl.parser.model.WdlDocument;
import com.gmail.at.ankyhe.wdl.parser.model.Workflow;
import com.gmail.at.ankyhe.wdl.parser.model.WorkflowElement;
import com.gmail.at.ankyhe.wdl.parser.model.expression.DoubleExpression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.Expression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.ExpressionCore;
import com.gmail.at.ankyhe.wdl.parser.model.expression.GetNameExpression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.IdentifierExpression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.IntExpression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.LongExpression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.RangeExpression;
import com.gmail.at.ankyhe.wdl.parser.model.expression.StringExpression;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorkflowService {

    public record WorkflowContextValue(String name, Type type, Object value) {}

    private final ApplicationContext applicationContext;

    private final Validator validator;

    private Map<String, Pair<Object, Method>> taskMethods;

    @Autowired
    public WorkflowService(final ApplicationContext applicationContext, final Validator validator) {
        this.applicationContext = applicationContext;
        this.validator = validator;
    }

    public String runWdl(final String str) {
        final SimpleWdlParser simpleWdlParser = new SimpleWdlParser();
        final WdlDocument wdlDocument = simpleWdlParser.parse(str);
        Preconditions.checkArgument(validator.validate(wdlDocument).isEmpty(), "wdlDocument is valid");

        final Map<String, WorkflowService.WorkflowContextValue> workflowContext = new HashMap<>();
        final List<String> keys = this.runWdl(wdlDocument, workflowContext);

        if (CollectionUtils.isEmpty(keys)) {
            return Strings.EMPTY;
        }
        if (keys.size() == 1) {
            final Object value = workflowContext.get(keys.get(0)).value();
            if (value != null) {
                return value.toString();
            }
            return Strings.EMPTY;
        }

        return keys
                .stream()
                .map(k -> {
                    final Object value = workflowContext.get(k).value();
                    if (value != null) {
                        return value.toString();
                    }
                    return Strings.EMPTY;
                })
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(","));
    }

    public List<String> runWdl(final WdlDocument wdlDocument, final Map<String, WorkflowContextValue> workflowContext) {
        Objects.requireNonNull(wdlDocument, "wdlDocument should not be null");

        final List<com.gmail.at.ankyhe.wdl.parser.model.Task> tasks = wdlDocument.getTasks();

        final List<Workflow> workflows = wdlDocument.getWorkflows();
        if (CollectionUtils.isEmpty(workflows)) {
            log.warn("There is no workflows, no run needed");
            return List.of();
        }

        // Currently, it supports only 1 workflow.
        final Workflow workflow = workflows.get(0);
        workflow.getWorkflowElements().forEach(w -> invokeWorkflowElement(w, tasks, workflowContext));
        log.info("The workflowContext is {}", workflowContext);

        if (CollectionUtils.isEmpty(workflow.getOutputs())) {
            return List.of();
        }

        final Output output = workflow.getOutputs().get(0);
        return output.getDeclarations().stream().map(Declaration::getArgumentName).toList();
    }

    private void invokeWorkflowElement(
            final WorkflowElement workflowElement,
            final List<com.gmail.at.ankyhe.wdl.parser.model.Task> tasks,
            final Map<String, WorkflowContextValue> workflowContext
    ) {
        if (workflowElement instanceof Input) {
            invokeInput((Input) workflowElement, workflowContext);
        } else if (workflowElement instanceof Call) {
            invokeCall((Call) workflowElement, tasks, workflowContext);
        } else if (workflowElement instanceof BoundDeclaration) {
            invokeBoundDeclaration((BoundDeclaration) workflowElement, workflowContext);
        } else if (workflowElement instanceof Output) {
            invokeWorkflowOutput((Output) workflowElement, workflowContext);
        } else {
            log.debug("invoke{}: {}", workflowElement.getClass().getSimpleName(), workflowElement);
        }
    }

    private void invokeInput(final Input input, Map<String, WorkflowContextValue> workflowContext) {
        log.debug("invokeInput: {}", input);

        input
                .getDeclarations()
                .stream()
                .filter(d -> d instanceof BoundDeclaration)
                .map(d -> (BoundDeclaration) d)
                .forEach(b -> invokeBoundDeclaration(b, workflowContext));
    }

    private void invokeWorkflowOutput(final Output output, Map<String, WorkflowContextValue> workflowContext) {
        log.debug("invokeOutput: {}", output);

        output
                .getDeclarations()
                .stream()
                .filter(d -> d instanceof BoundDeclaration)
                .map(d -> (BoundDeclaration) d)
                .forEach(b -> invokeBoundDeclaration(b, workflowContext));
    }

    /*
     * The key is the @Task value.  If the @Task value is blank, it is sneak of method name.
     * Object is the bean which is marked as @TaskService, Method is the method.
     */
    @PostConstruct
    void getAllTaskMethods() {
        final Map<String, List<Pair<Object, Method>>> result = new HashMap<>();

        final Map<String, Object> taskServices = this.applicationContext.getBeansWithAnnotation(TaskService.class);
        taskServices.forEach((serviceName, taskServiceObject) -> {
            Map<String, List<Method>> methods = getAllTaskMethods(taskServiceObject);
            methods.forEach((methodName, methodList) -> {
                final List<Pair<Object, Method>> pairs = methodList.stream().map(m -> Pair.of(taskServiceObject, m)).toList();
                result.computeIfAbsent(methodName, k -> new ArrayList<>()).addAll(pairs);
            });
        });

        final List<Map.Entry<String, List<Pair<Object, Method>>>> invalidMethods = result
                .entrySet()
                .stream()
                .filter(e -> e.getValue().size() > 1)
                .toList();
        if (!CollectionUtils.isEmpty(invalidMethods)) {
            final String errorMessage = invalidMethods
                    .stream()
                    .map(elem -> {
                        final String value = elem
                                .getValue()
                                .stream()
                                .map(p -> "%s.%s".formatted(p.getKey().getClass().getName(), p.getValue()))
                                .collect(Collectors.joining(", "));
                        return "%s exists more than one mapping @Task methods: [%s]".formatted(elem.getKey(), value);
                    })
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Failed to initialize all @Task methods due to %s".formatted(errorMessage));
        }

        final Map<String, Pair<Object, Method>> tmp = new HashMap<>();
        result.forEach((name, pairs) -> tmp.put(name, pairs.get(0)));
        this.taskMethods = Map.copyOf(tmp);
    }

    private Map<String, List<Method>> getAllTaskMethods(final Object taskServiceObject) {
        final Method[] methods = ReflectionUtils.getDeclaredMethods(taskServiceObject.getClass());
        final Map<String, List<Method>> ret = new HashMap<>();

        for (final Method m : methods) {
            final Annotation[] annotations = m.getDeclaredAnnotations();
            for (final Annotation annotation : annotations) {
                if (annotation instanceof Task task) {
                    String value = task.value();
                    if (StringUtils.isBlank(value)) {
                        value = camelToSnake(m.getName());
                    }
                    ret.computeIfAbsent(value, k -> new ArrayList<>()).add(m);
                    break;
                }
            }
        }

        return ret;
    }

    private void invokeCall(
            final Call call,
            final List<com.gmail.at.ankyhe.wdl.parser.model.Task> tasks,
            final Map<String, WorkflowContextValue> workflowContext
    ) {
        log.debug("invokeCall: {}", call);

        final String callMethodName = call.getName();

        final Pair<Object, Method> pair = this.taskMethods.get(callMethodName);

        if (pair == null) {
            throw new WdlInvokingException("Failed to invokeCall: %s due to there is no this task".formatted(call));
        }

        final List<CallInput> callInputs = call.getInputs();
        final List<Object> parameters = callInputs
                .stream()
                .map(input -> invokeExpression(input.getExpression(), workflowContext))
                .map(Pair::getRight)
                .toList();

        final com.gmail.at.ankyhe.wdl.parser.model.Task theTask = tasks
                .stream()
                .filter(t -> t.getName().equals(call.getName()))
                .findFirst()
                .orElseThrow(() -> new WdlInvokingException("Failed to find the task of the call: %s".formatted(call)));

        final Output output = theTask.getOutput();
        final List<Declaration> declarations = output.getDeclarations();

        // TODO: support multiple output
        if (declarations != null && declarations.size() >= 2) {
            throw new WdlInvokingException("Currently we don't support multiple outputs fo the task: %s".formatted(theTask));
        }

        if (CollectionUtils.isEmpty(declarations)) {
            safeInvokeMethod(call, pair.getRight(), pair.getLeft(), parameters.toArray());
        } else {
            final Object object = safeInvokeMethod(call, pair.getRight(), pair.getLeft(), parameters.toArray());
            final Declaration declaration = declarations.get(0);
            final String fullName = "%s.%s".formatted(theTask.getName(), declaration.getArgumentName());
            workflowContext.put(fullName, new WorkflowContextValue(fullName, declaration.getType(), object));
        }
    }

    private Object safeInvokeMethod(final Call call, final Method method, final Object object, final Object... parameters) {
        try {
            return method.invoke(object, parameters);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new WdlInvokingException("Failed to call: %s".formatted(call), ex);
        }
    }

    private void invokeBoundDeclaration(final BoundDeclaration boundDeclaration, final Map<String, WorkflowContextValue> workflowContext) {
        log.debug("invokeBoundDeclaration: {}", boundDeclaration);

        final String name = boundDeclaration.getArgumentName();
        final Type type = boundDeclaration.getType();
        final Expression expression = boundDeclaration.getExpression();
        final Pair<Type, Object> returnValue = invokeExpression(expression, workflowContext);
        //TODO: check returnValue.getLeft and type later.
        workflowContext.put(name, new WorkflowContextValue(name, type, returnValue.getRight()));
    }

    private Pair<Type, Object> invokeExpression(final Expression expression, final Map<String, WorkflowContextValue> workflowContext) {
        if (expression instanceof ExpressionCore) {
            return invokeExpressionCore((ExpressionCore) expression, workflowContext);
        }

        throw new WdlInvokingException("Failed to invoke expression: %s".formatted(expression));
    }

    private Pair<Type, Object> invokeExpressionCore(
            final ExpressionCore expressionCore,
            final Map<String, WorkflowContextValue> workflowContext
    ) {
        // Primitive
        if (expressionCore instanceof IdentifierExpression) {
            return invokeExpressionCore((IdentifierExpression) expressionCore, workflowContext);
        }
        if (expressionCore instanceof IntExpression) {
            return invokeExpressionCore((IntExpression) expressionCore, workflowContext);
        }
        if (expressionCore instanceof LongExpression) {
            return invokeExpressionCore((LongExpression) expressionCore, workflowContext);
        }
        if (expressionCore instanceof DoubleExpression) {
            return invokeExpressionCore((DoubleExpression) expressionCore, workflowContext);
        }
        if (expressionCore instanceof StringExpression) {
            return invokeExpressionCore((StringExpression) expressionCore, workflowContext);
        }

        if (expressionCore instanceof GetNameExpression) {
            return invokeExpressionCore((GetNameExpression) expressionCore, workflowContext);
        }

        // Range
        if (expressionCore instanceof RangeExpression) {
            return invokeExpressionCore((RangeExpression) expressionCore, workflowContext);
        }

        throw new WdlInvokingException("Failed to invoke expressionCore: %s".formatted(expressionCore));
    }

    private Pair<Type, Object> invokeExpressionCore(
            final IntExpression intExpression,
            final Map<String, WorkflowContextValue> workflowContext
    ) {
        return Pair.of(IntType.TYPE, intExpression.getValue());
    }

    private Pair<Type, Object> invokeExpressionCore(
            final LongExpression longExpression,
            final Map<String, WorkflowContextValue> workflowContext
    ) {
        return Pair.of(LongType.TYPE, longExpression.getValue());
    }

    private Pair<Type, Object> invokeExpressionCore(
            final DoubleExpression doubleExpression,
            final Map<String, WorkflowContextValue> workflowContext
    ) {
        return Pair.of(AnyType.TYPE, doubleExpression.getValue());
    }

    private Pair<Type, Object> invokeExpressionCore(
            final StringExpression stringExpression,
            final Map<String, WorkflowContextValue> workflowContext
    ) {
        return Pair.of(StringType.TYPE, stringExpression.getValue());
    }

    private Pair<Type, Object> invokeExpressionCore(
            final IdentifierExpression identifierExpression,
            final Map<String, WorkflowContextValue> workflowContext
    ) {
        final String identifierName = identifierExpression.getIdentifierName();
        final WorkflowContextValue contextValue = workflowContext.get(identifierName);
        if (contextValue == null) {
            throw new WdlInvokingException("There is no value of identifier: %s".formatted(identifierName));
        }

        return Pair.of(contextValue.type(), contextValue.value());
    }

    // FIXME: it seems this method duplicates many of identifierExpression
    private Pair<Type, Object> invokeExpressionCore(
            final GetNameExpression getNameExpression,
            final Map<String, WorkflowContextValue> workflowContext
    ) {
        final String fullName = getNameExpression.fullName();

        final WorkflowContextValue contextValue = workflowContext.get(fullName);
        if (contextValue == null) {
            throw new WdlInvokingException("There is no value of GetNameExpression: %s".formatted(fullName));
        }

        return Pair.of(contextValue.type(), contextValue.value());
    }

    private Pair<Type, Object> invokeExpressionCore(
            final RangeExpression rangeExpression,
            final Map<String, WorkflowContextValue> workflowContext
    ) {
        final ExpressionCore expressionCore = rangeExpression.getExpressionCore();
        final Pair<Type, Object> value = invokeExpressionCore(expressionCore, workflowContext);

        final Pair<Type, Object> begin = invokeExpression(rangeExpression.getBegin(), workflowContext);
        final Pair<Type, Object> end = invokeExpression(rangeExpression.getEnd(), workflowContext);

        final Object v = value.getValue();
        if (!(v instanceof List<?> list)) {
            throw new WdlInvokingException("rangeExpression: %s is not valid".formatted(rangeExpression));
        }

        if (!(begin.getRight() instanceof Integer) || !(end.getRight() instanceof Integer)) {
            throw new WdlInvokingException("rangeExpression: %s is not valid".formatted(rangeExpression));
        }

        final int beginIdx = (Integer) begin.getRight();
        int endIdx = (Integer) end.getRight();

        /*
         * In LLM application, we always say something like: please add latest 10 registered machines into pool A.
         * But sometimes there is no 10 registered machines.  We'd better safely understand it as: add latest maximum
         * 10 registered machines into pool A.
         */
        endIdx = Math.min(endIdx, list.size());

        return Pair.of(value.getLeft(), list.subList(beginIdx, endIdx));
    }

    //NonPublicForTest
    static String camelToSnake(final String str) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

}
