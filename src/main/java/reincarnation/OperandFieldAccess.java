/*
 * Copyright (C) 2018 Reincarnation Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package reincarnation;

import java.util.Objects;

import kiss.I;
import kiss.Signal;
import reincarnation.coder.Coder;

/**
 * @version 2018/10/13 23:34:58
 */
public class OperandFieldAccess extends Operand {

    /** The field. */
    private final Operand field;

    /** The field name. */
    private final String name;

    /**
     * Create field access like <code>owner.field</code>.
     * 
     * @param field A field owner.
     * @param name A field name.
     */
    public OperandFieldAccess(Operand field, String name) {
        this.field = Objects.requireNonNull(field);
        this.name = Objects.requireNonNull(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Signal<Operand> children() {
        return I.signal(field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(Coder coder) {
        coder.writeAccessField(field, name);
    }
}
