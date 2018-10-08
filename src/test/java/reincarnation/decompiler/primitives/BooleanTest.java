/*
 * Copyright (C) 2016 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package reincarnation.decompiler.primitives;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import reincarnation.Code;
import reincarnation.CodeVerifier;
import reincarnation.Debuggable;

/**
 * @version 2018/10/06 1:06:47
 */
class BooleanTest extends CodeVerifier {

    @Test
    void value() {
        verify(new Code.BooleanParam() {

            @Override
            public boolean run(boolean value) {
                return value;
            }
        });
    }

    @Test
    void negate() {
        verify(new Code.BooleanParam() {

            @Override
            public boolean run(boolean value) {
                return !value;
            }
        });
    }

    @Test
    void negateTwice() {
        verify(new Code.BooleanParam() {

            @Override
            public boolean run(boolean value) {
                return !!value;
            }
        });
    }

    @Test
    void negateInVariable() {
        verify(new Code.BooleanParam() {

            @Debuggable
            @Override
            public boolean run(boolean value) {
                value = !value;

                return value;
            }
        });
    }

    @Test
    @Disabled
    void negateTwiceInVariable() {
        verify(new Code.BooleanParam() {

            @Override
            public boolean run(boolean value) {
                value = !!value;

                return value;
            }
        });
    }

    @Test
    void classEquality() {
        verify(new Code.Boolean() {

            @Override
            public boolean run() {
                return boolean.class == boolean.class;
            }
        });
    }

    @Test
    @Disabled
    void arrayClassEquality() {
        verify(new Code.Boolean() {

            @Override
            public boolean run() {
                boolean[] array = {};
                return boolean[].class == array.getClass();
            }
        });
    }
}
