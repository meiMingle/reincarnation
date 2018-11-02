/*
 * Copyright (C) 2018 Reincarnation Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package reincarnation.structure;

import java.util.Optional;

import reincarnation.Node;
import reincarnation.coder.Coder;

/**
 * @version 2018/10/31 0:36:33
 */
public class Break extends Structure {

    /** The target. */
    private final Breakable breakable;

    /**
     * Build break statement.
     * 
     * @param label A target name.
     */
    public Break(Node that, Breakable breakable) {
        super(that);

        this.breakable = breakable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeCode(Coder coder) {
        coder.writeBreak(Optional.of(breakable.associated.id));
    }
}
