/*
 * Copyright (C) 2018 Reincarnation Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package reincarnation.decompiler.operator;

import org.junit.jupiter.api.Test;

import reincarnation.Code;
import reincarnation.CodeVerifier;

/**
 * @version 2018/10/23 9:56:30
 */
class DoubleIncrementTest extends CodeVerifier {

    @Test
    void incrementFieldInMethodCall() {
        verify(new Code.Double() {

            private double index = 0;

            @Override
            public double run() {
                return call(index++);
            }

            private double call(double value) {
                return index + value * 10;
            }
        });
    }

    @Test
    void decrementFieldInMethodCall() {
        verify(new Code.Double() {

            private double index = 0;

            @Override
            public double run() {
                return call(index--);
            }

            private double call(double value) {
                return index + value * 10;
            }
        });
    }

    @Test
    void preincrementFieldInMethodCall() {
        verify(new Code.Double() {

            private double index = 0;

            @Override
            public double run() {
                return call(++index);
            }

            private double call(double value) {
                return index + value * 10;
            }
        });
    }

    @Test
    void predecrementFieldInMethodCall() {
        verify(new Code.Double() {

            private double index = 0;

            @Override
            public double run() {
                return call(--index);
            }

            private double call(double value) {
                return index + value * 10;
            }
        });
    }

    @Test
    void incrementFieldInFieldAccess() {
        verify(new Code.Double() {

            private double index = 1;

            private double count = 2;

            @Override
            public double run() {
                index = count++;

                return count + index * 10;
            }
        });
    }

    @Test
    void decrementFieldInFieldAccess() {
        verify(new Code.Double() {

            private double index = 1;

            private double count = 2;

            @Override
            public double run() {
                index = count--;

                return count + index * 10;
            }
        });
    }

    @Test
    void preincrementFieldInFieldAccess() {
        verify(new Code.Double() {

            private double index = 1;

            private double count = 2;

            @Override
            public double run() {
                index = ++count;

                return count + index * 10;
            }
        });
    }

    @Test
    void predecrementFieldInFieldAccess() {
        verify(new Code.Double() {

            private double index = 1;

            private double count = 2;

            @Override
            public double run() {
                index = --count;

                return count + index * 10;
            }
        });
    }
}
