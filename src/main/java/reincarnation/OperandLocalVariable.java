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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import kiss.Ⅱ;
import reincarnation.coder.Coder;

public class OperandLocalVariable extends Operand {

    /** The variable index. */
    final int index;

    /** The variable name. */
    final String name;

    /** Holds all nodes that refer to this variable. */
    final Set<Ⅱ<Node, Class>> referrers = new HashSet();

    /**
     * Create local variable with index.
     */
    OperandLocalVariable(Class type, int index, String name) {
        this.name = Objects.requireNonNull(name);
        this.index = index;
        this.type.set(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeCode(Coder coder) {
        if (name.equals("this")) {
            coder.writeThis();
        } else {
            coder.writeLocalVariable(type.v, name);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof OperandLocalVariable op ? name.equals(op.name) : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        // don't call #write, it will throw error in debug mode.
        return name;
    }
}