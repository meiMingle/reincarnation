/*
 * Copyright (C) 2023 The REINCARNATION Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package reincarnation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import kiss.I;
import reincarnation.JavaMethodDecompiler.TryCatchFinally;

public class Debugger {

    /** The processing environment. */
    static boolean whileDebug = false;

    /** The processing environment. */
    private static final boolean whileTest;

    // initialization
    static {
        boolean flag = false;

        for (StackTraceElement element : new Error().getStackTrace()) {
            if (element.getClassName().startsWith("org.junit.")) {
                flag = true;
                break;
            }
        }
        whileTest = flag;
    }

    /** The instance holder per thread. */
    private static final ThreadLocal<Debugger> local = ThreadLocal.withInitial(() -> new Debugger());

    /**
     * Retrieve the current debbuger.
     * 
     * @return
     */
    public static Debugger current() {
        return local.get();
    }

    /** The debugging state. */
    boolean enable;

    /** The debugging state. */
    boolean enableForcibly;

    /** The use flag. */
    private boolean used;

    /** Can i use getDominator safely? */
    private boolean dominatorSafe;

    /** The buffered IO. */
    private StringBuilder buffer;

    /**
     * Hide constructor.
     */
    private Debugger() {
    }

    /**
     * Replace system IO.
     * 
     * @return
     */
    public StringBuilder replaceOutput() {
        return this.buffer = new StringBuilder();
    }

    /**
     * Check whether the current context enable debugger or not.
     * 
     * @return
     */
    public boolean isEnable() {
        return isEnable(getExecutable());
    }

    /**
     * Check whether the specified context enable debugger or not.
     * 
     * @return
     */
    public boolean isEnable(Executable executable) {
        boolean enable = this.enable || this.enableForcibly;

        if (enable && !used) {
            used = true;
            printInfo(false);
        }
        return enable;
    }

    /**
     * Print method info as header like.
     */
    private void printInfo(boolean safe) {
        current().dominatorSafe = safe;

        if (isEnable()) {
            String text = linkableMethodInfo(true);
            int base = (120 - text.length()) / 2;

            print("//" + "-".repeat(base) + " " + linkableMethodInfo(true) + " " + "-".repeat(base) + "//");
        }
    }

    /**
     * Show linkable method info in console.
     * 
     * @return
     */
    private String linkableMethodInfo(boolean head) {
        String methodName;

        if (whileTest) {
            String testClassName = computeTestClassName(getTarget());
            String testMethodName = computeTestMethodName(testClassName);

            methodName = testMethodName == null ? getMethodName() : testMethodName;
        } else {
            methodName = getMethodName();
        }

        if (!methodName.startsWith("<")) {
            methodName = "#".concat(methodName);
        }

        Class clazz = getTarget();

        if (clazz.isAnonymousClass()) {
            clazz = clazz.getEnclosingClass();
        }
        return clazz.getSimpleName() + methodName + " (" + clazz.getSimpleName() + ".java:" + (head ? getMethodLine() : getLine()) + ")";
    }

    /**
     * @param message
     */
    public void print(Object message) {
        if (isEnable()) {
            if (buffer == null) {
                System.out.println(message);
            } else {
                buffer.append(message).append("\r\n");
            }
        }
    }

    /**
     * Dump node tree.
     * 
     * @param nodes
     */
    public void print(List<Node> nodes) {
        if (isEnable()) {
            print(format(nodes));
        }
    }

    /**
     * Print the current processing method info.
     */
    public void printMethod() {
        if (isEnable()) {
            Executable m = getExecutable();
            String name = m instanceof Constructor ? "Constructor" : m instanceof Method ? "Method " + m.getName() : "StaticInitializer";

            Class clazz = getTarget();
            if (clazz.isAnonymousClass()) {
                clazz = clazz.getEnclosingClass();
            }

            StringJoiner joiner = new StringJoiner(", ", "(", ")");
            for (Parameter p : m.getParameters()) {
                Class<?> type = p.getType();
                if (type != clazz) joiner.add(type.getSimpleName() + " " + p.getName());
            }

            print(Printable.stain(name + joiner, "21") + " (" + clazz.getSimpleName() + ".java:" + getLine() + ")");
        }
    }

    /**
     * Dump node tree if it was chabged.
     * 
     * @param nodes
     * @param label
     * @return
     */
    public Printable diff(List<Node> nodes, String label) {
        if (isEnable()) {
            String before = format(nodes);

            return () -> {
                String after = format(nodes);

                if (!before.equals(after)) {
                    Deque<String> befores = I.collect(ArrayDeque.class, before.split("\r\n"));
                    Deque<String> afters = I.collect(ArrayDeque.class, after.split("\r\n"));

                    // delete same line from head and tail
                    int erased = erase(befores.iterator(), afters.iterator());
                    erased += erase(befores.descendingIterator(), afters.descendingIterator());

                    print(label + (erased <= 0 ? " (show full nodes)" : " (show the changed nodes only - hide " + erased + " nodes)"));
                    print(Printable.stain(befores.stream().collect(Collectors.joining("\r\n")), "9") + "\r\n" + Printable
                            .stain(afters.stream().collect(Collectors.joining("\r\n")), "78") + "\r\n");
                }
            };
        } else {
            return () -> {
            };
        }
    }

    /**
     * Erase same lines.
     * 
     * @param before
     * @param after
     */
    private int erase(Iterator<String> before, Iterator<String> after) {
        int count = 0;
        while (after.hasNext() && before.hasNext()) {
            if (!before.next().equals(after.next())) continue;
            before.remove();
            after.remove();
            count++;
        }
        return count;
    }

    /**
     * Print node's diff.
     */
    interface Printable extends AutoCloseable {
        /**
         * {@inheritDoc}
         */
        @Override
        void close();

        /**
         * Colorize your text.
         * 
         * @param code
         * @param colorCode
         * @return
         */
        private static String stain(String text, String colorCode) {
            return "[38;5;" + colorCode + "m" + text + "[0m";
        }
    }

    /**
     * Compute actual test class name.
     * 
     * @param clazz
     * @return
     */
    private String computeTestClassName(Class clazz) {
        String name = clazz.getName();

        int index = name.indexOf('$');

        if (index != -1) {
            name = name.substring(0, index);
        }
        return name;
    }

    /**
     * Compute actual test method name.
     * 
     * @param testClassName
     * @return
     */
    private String computeTestMethodName(String testClassName) {
        for (StackTraceElement element : new Error().getStackTrace()) {
            if (element.getClassName().equals(testClassName)) {
                return element.getMethodName();
            }
        }
        return null;
    }

    /**
     * Helper method to format node tree.
     * 
     * @param nodes
     * @return
     */
    private String format(List<Node> nodes) {
        Set<TryCatchFinally> tries = new LinkedHashSet();

        for (Node node : nodes) {
            if (node.tries != null) {
                tries.addAll(node.tries);
            }
        }

        // compute max id length
        int max = 1;

        for (Node node : nodes) {
            max = Math.max(max, String.valueOf(node.id).length());
        }

        int incoming = 0;
        int outgoing = 0;
        int backedge = 0;

        for (Node node : nodes) {
            incoming = Math.max(incoming, node.incoming.size() * max + (node.incoming.size() - 1) * 2);
            outgoing = Math.max(outgoing, node.outgoing.size() * max + (node.outgoing.size() - 1) * 2);
            backedge = Math.max(backedge, node.backedges.size() * max + (node.backedges.size() - 1) * 2);
        }

        Formatter format = new Formatter();

        for (Node node : nodes) {
            StringBuilder tryFlow = new StringBuilder();

            for (TryCatchFinally block : tries) {
                if (block.start == node) {
                    tryFlow.append("s");
                } else if (block.end == node) {
                    tryFlow.append("e");
                } else if (block.catcher == node) {
                    tryFlow.append("c");
                } else if (block.exit == node) {
                    tryFlow.append("x");
                } else {
                    tryFlow.append("  ");
                }
            }

            format.write(String.valueOf(node.id), max);
            format.write("  in : ");
            format.formatNode(node.incoming, incoming);
            format.write("out : ");
            format.formatNode(node.outgoing, outgoing);
            format.write("dom : ");
            format.formatNode(list(getDominator(node)), 1);
            format.write("doms : ");
            format.formatNode(node.dominators, node.dominators.size());
            format.write("prev : ");
            format.formatNode(list(node.previous), 1);
            format.write("next : ");
            format.formatNode(list(node.next), 1);
            format.write("dest : ");
            format.formatNode(list(node.destination), 1);

            if (backedge != 0) {
                format.write("back : ");
                format.formatNode(node.backedges, backedge);
            }
            if (!tries.isEmpty()) {
                format.write("try : ");
                format.write(tryFlow.toString(), tries.size() * 2 + 2);
            }
            format.write("code : ");
            format.formatCodeFragment(node.stack);
            format.write("\r\n");
        }
        return format.toString();
    }

    /**
     * Create single item list.
     * 
     * @param node
     * @return
     */
    private List<Node> list(Node node) {
        if (node == null) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(node);
    }

    /**
     * Helper method to compute dominator node.
     * 
     * @param target
     * @return
     */
    private Node getDominator(Node target) {
        if (current().dominatorSafe) {
            return target.getDominator();
        } else {
            return getDominator(target, new HashSet());
        }
    }

    /**
     * Compute the immediate dominator of this node.
     * 
     * @return A dominator node. If this node is root, <code>null</code>.
     */
    private Node getDominator(Node target, Set<Node> nodes) {
        if (!nodes.add(target)) {
            return null;
        }

        // check cache
        // We must search a immediate dominator.
        //
        // At first, we can ignore the older incoming nodes.
        List<Node> candidates = new CopyOnWriteArrayList(target.incoming);

        // compute backedges
        for (Node node : candidates) {
            if (target.backedges.contains(node)) {
                candidates.remove(node);
            }
        }

        int size = candidates.size();

        switch (size) {
        case 0: // this is root node
            return null;

        case 1: // only one incoming node
            return candidates.get(0);

        default: // multiple incoming nodes
            Node candidate = candidates.get(0);

            search: while (candidate != null) {
                for (int i = 1; i < size; i++) {
                    if (!hasDominator(candidates.get(i), candidate, nodes)) {
                        candidate = getDominator(candidate, nodes);
                        continue search;
                    }
                }
                return candidate;
            }
            return null;
        }
    }

    /**
     * Helper method to check whether the specified node dominate this node or not.
     * 
     * @param dominator A dominator node.
     * @return A result.
     */
    private boolean hasDominator(Node target, Node dominator, Set<Node> nodes) {
        Node current = target;

        while (current != null) {
            if (current == dominator) {
                return true;
            }
            current = getDominator(current, nodes);
        }

        // Not Found
        return false;
    }

    /**
     * @version 2018/10/31 11:35:02
     */
    private static final class Formatter {

        /** The actual buffer. */
        private final StringBuilder builder = new StringBuilder();

        /** The tab length. */
        private final int tab = 8;

        /**
         * Helper method to write message.
         * 
         * @param message
         */
        private void write(String message) {
            builder.append(message);
        }

        /**
         * Helper method to write message.
         * 
         * @param message
         */
        private void write(String message, int max) {
            builder.append(message);

            // calcurate required tab
            int requireTab = 1;

            while (requireTab * tab <= max) {
                requireTab++;
            }

            // calcurate remaining spaces
            int remaining = requireTab * tab - message.length();
            int actualTab = 0;

            while (0 < remaining - tab * actualTab) {
                actualTab++;
            }

            for (int i = 0; i < actualTab; i++) {
                builder.append('\t');
            }
        }

        /**
         * Helper method to write message.
         * 
         * @param message
         */
        private void formatNode(List<Node> nodes, int max) {
            StringBuilder builder = new StringBuilder("[");

            for (int i = 0; i < nodes.size(); i++) {
                builder.append(String.valueOf(nodes.get(i).id));

                if (i != nodes.size() - 1) {
                    builder.append(",");
                }
            }
            builder.append("]");

            write(builder.toString(), max + 2);
        }

        /**
         * Helper method to format code fragment.
         * 
         * @param operands
         */
        private void formatCodeFragment(List<Operand> operands) {
            whileDebug = true;
            for (int i = 0; i < operands.size(); i++) {
                Operand operand = operands.get(i);

                builder.append(operand.toString().strip()).append(" [").append(operand.info()).append("]");

                if (i != operands.size() - 1) {
                    builder.append(" ");
                }
            }
            whileDebug = false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return builder.toString();
        }
    }

    /** The compiling route. */
    private Deque<DebugContext> route = new ArrayDeque();

    /**
     * Record starting class compiling.
     * 
     * @param clazz A target class.
     */
    void start(Class clazz) {
        DebugContext context = route.peekFirst();

        if (context == null || context.clazz != clazz) {
            route.addFirst(new DebugContext(clazz));
        }
    }

    /**
     * Record finishing class compiling.
     * 
     * @param clazz A target class.
     */
    void finish(Class clazz) {
        route.pollFirst();
    }

    /**
     * Record starting method compiling.
     * 
     * @param executable A target method, constructor or initializer.
     */
    void startMethod(Executable executable) {
        if (executable != null) {
            DebugContext context = route.peekFirst();
            context.executable = executable;
            context.signature = executable.toString();
            context.line = 1;
        }
    }

    /**
     * Record finishing method compiling.
     */
    void finishMethod() {
        enable = false;
        used = false;
        dominatorSafe = false;
    }

    /**
     * Record line number.
     * 
     * @param line
     */
    void recordLine(int line) {
        DebugContext context = route.peekFirst();

        if (context.line == 1) {
            context.line = line - 1;
        }
        context.lineNow = line;
    }

    /**
     * Retrieve the current compiling class info.
     * 
     * @return The current compiling class.
     */
    Class getTarget() {
        return route.peekFirst().clazz;
    }

    /**
     * Retrieve the current compiling member info.
     * 
     * @return The current compiling member.
     */
    Executable getExecutable() {
        return route.peekFirst().executable;
    }

    /**
     * Retrieve the current compiling class info.
     * 
     * @return The current compiling class.
     */
    String getMethodName() {
        return route.peekFirst().signature;
    }

    /**
     * Retrieve the current compiling class info.
     * 
     * @return The current compiling class.
     */
    int getMethodLine() {
        return route.peekFirst().line;
    }

    /**
     * Retrieve the current compiling class info.
     * 
     * @return The current compiling class.
     */
    int getLine() {
        return route.peekFirst().lineNow;
    }

    /**
     * 
     */
    private static class DebugContext {

        /** The current compiling class. */
        private final Class clazz;

        /** The current compiling member. */
        private Executable executable;

        /** The current compiling method. */
        private String signature;

        /** The current compiling method position. */
        private int line = 1;

        /** The current compiling line position. */
        private int lineNow = 1;

        /**
         * @param clazz An associated clazz.
         */
        private DebugContext(Class clazz) {
            this.clazz = clazz;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder(" at ").append(clazz.getName())
                    .append('.')
                    .append(signature)
                    .append('(')
                    .append(clazz.getSimpleName())
                    .append(".java:")
                    .append(lineNow)
                    .append(')');

            return builder.toString();
        }
    }
}