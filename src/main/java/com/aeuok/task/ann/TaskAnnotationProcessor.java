package com.aeuok.task.ann;

import com.google.auto.service.AutoService;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;
import com.sun.tools.javac.util.List;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.*;

import static com.aeuok.task.Constant.*;

/**
 * @author: CQ
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes("com.aeuok.task.ann.Task")
public class TaskAnnotationProcessor extends AbstractProcessor {
    private static JCTree.JCExpression returnMethodType;
    private static JCTree.JCExpression booleanType;
    private static JCTree.JCExpression stringType;
    private static JCTree.JCLiteral trueVal;
    private static JCTree.JCLiteral falseVal;
    private Messager messager;
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.WARNING, "【task-spring-boot-starter】可能与【Lombok】产生冲突导致编译失败..." + new Date());
        trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context);
        if (null == returnMethodType) {
            try {
                returnMethodType = treeMaker.Type((Type) (Class.forName(VOID).newInstance()));
            } catch (Exception e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }
        if (null == booleanType) {
            booleanType = memberAccess("java.lang.Boolean");
        }
        if (null == stringType) {
            stringType = memberAccess("java.lang.String");
        }
        if (null == trueVal) {
            trueVal = treeMaker.Literal(TypeTag.BOOLEAN, 1);
        }
        if (null == falseVal) {
            falseVal = treeMaker.Literal(TypeTag.BOOLEAN, 0);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> set = roundEnv.getElementsAnnotatedWith(Task.class);
        Map<Element, Integer> labeled = new HashMap<>(16);
        for (Element fieldElement : set) {
            if (!fieldElement.getKind().isField()) {
                continue;
            }
            Element typeElement = fieldElement.getEnclosingElement();
            JCTree jcTree = trees.getTree(typeElement);
            JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) trees.getTree(fieldElement);
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    super.visitClassDef(jcClassDecl);
                    if (!labeled.containsKey(typeElement)) {
                        jcClassDecl.mods.annotations = jcClassDecl.mods.annotations.prepend(
                                buildFlag(typeElement.asType().toString())
                        );
                        labeled.put(typeElement, TASK_FIELD_INJECT_START);
                    }
                    try {
                        Integer index = labeled.get(typeElement);
                        jcClassDecl.defs = jcClassDecl.defs.prepend(
                                generateInjectMethod(jcVariableDecl,
                                        fieldElement.getAnnotation(Task.class),
                                        index)
                        );
                        labeled.put(typeElement, ++index);
                    } catch (Exception e) {
                        messager.printMessage(Diagnostic.Kind.WARNING, e.getMessage());
                    }
                }
            });
        }
        return true;
    }

    /**
     * 用于获取被代理的原始类
     *
     * @param className 原始类名称
     * @return
     */
    private JCTree.JCAnnotation buildFlag(String className) {
        List<JCTree.JCExpression> list = List.of(
                treeMaker.Assign(
                        treeMaker.Ident(names.fromString("value")),
                        memberAccess(className + ".class")
                )
        );
        return treeMaker.Annotation(memberAccess(FLAG), list);
    }

    /**
     * 用于注入{@link com.aeuok.task.TaskContainer}
     *
     * @param jcVariableDecl
     * @param index
     * @return
     */
    private JCTree.JCMethodDecl generateInjectMethod(JCTree.JCVariableDecl jcVariableDecl, Task task, int index) {
        Name name = names.fromString(TASK_FIELD_INJECT_PREFIX + index);
        String thisField = "this." + jcVariableDecl.name.toString();
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(
                treeMaker.Exec(
                        treeMaker.Assign(
                                treeMaker.Select(
                                        treeMaker.Ident(names.fromString("this")),
                                        jcVariableDecl.getName()),
                                treeMaker.Ident(jcVariableDecl.getName()))))
                .append(
                        treeMaker.Exec(
                                treeMaker.Apply(
                                        List.of(booleanType),
                                        memberAccess(thisField + ".setTransactional"),
                                        List.of(task.transactional() ? trueVal : falseVal))))
                .append(
                        treeMaker.Exec(
                                treeMaker.Apply(
                                        List.of(booleanType),
                                        memberAccess(thisField + ".setShowInfo"),
                                        List.of(task.showInfo() ? trueVal : falseVal))))
                .append(
                        treeMaker.Exec(
                                treeMaker.Apply(
                                        List.of(booleanType),
                                        memberAccess(thisField + ".setWait"),
                                        List.of(task.waitTask() ? trueVal : falseVal))))
                .append(
                        treeMaker.Exec(
                                treeMaker.Apply(
                                        List.of(stringType),
                                        memberAccess(thisField + ".setTaskName"),
                                        List.of(treeMaker.Literal(task.value())))))
                .append(
                        treeMaker.Exec(
                                treeMaker.Apply(
                                        List.of(stringType),
                                        memberAccess(thisField + ".setTaskBeanName"),
                                        List.of(treeMaker.Literal(task.taskBeanName())))));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        JCTree.JCVariableDecl param = treeMaker.VarDef(
                treeMaker.Modifiers(Flags.PARAMETER, List.nil()),
                jcVariableDecl.name,
                jcVariableDecl.vartype,
                jcVariableDecl.nameexpr
        );
        List<JCTree.JCVariableDecl> parameters = List.of(param);
        return treeMaker.MethodDef(
                treeMaker.Modifiers(Flags.PUBLIC),
                name,
                returnMethodType,
                List.nil(),
                parameters,
                List.nil(),
                body,
                null
        );
    }

    /**
     * 调试使用
     *
     * @param message
     */
    private void debugger(String message) {
        if (DEBUG) {
            messager.printMessage(Diagnostic.Kind.NOTE, UUID.randomUUID().toString() + "---" + message);
        }
    }

    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(names.fromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, names.fromString(componentArray[i]));
        }
        return expr;
    }
}