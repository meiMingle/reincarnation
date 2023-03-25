/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package reincarnation.decompiler.flow;

import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import reincarnation.CodeVerifier;
import reincarnation.Debuggable;
import reincarnation.TestCode;

@Debuggable
class SwitchExpressionTest extends CodeVerifier {

    @Test
    void assignToVariable() {
        verify(new TestCode.IntParam() {

            @Override
            public int run(@Param(from = 0, to = 5) int param) {
                int value = switch (param) {
                case 0 -> 10;
                case 1 -> 15;
                default -> param;
                };
                return value;
            }
        });
    }

    @Test
    void methodParameter() {
        verify(new TestCode.IntParam() {

            @Override
            public int run(@Param(from = 0, to = 5) int param) {
                return value(switch (param) {
                case 0 -> 10;
                case 1 -> 15;
                default -> param;
                });
            }

            private int value(int value) {
                return value;
            }
        });
    }

    @Test
    void conditionByChar() {
        verify(new TestCode.CharParam() {

            @Override
            public char run(@Param(chars = {'a', 'b', 'c', 'd', 'e'}) char param) {
                return switch (param) {
                case 'a' -> 'A';
                case 'b' -> 'B';
                default -> param;
                };
            }
        });
    }

    @Test
    void conditionByCharMultiple() {
        verify(new TestCode.CharParam() {

            @Override
            public char run(@Param(chars = {'a', 'b', 'c', 'd', 'e'}) char param) {
                return switch (param) {
                case 'a', 'b' -> 'X';
                case 'c', 'd' -> 'Y';
                default -> param;
                };
            }
        });
    }

    @Test
    void conditionByEnum() {
        verify(new TestCode.IntParam() {

            @Override
            public int run(@Param(from = 0, to = 3) int param) {
                return switch (RetentionPolicy.values()[param]) {
                case CLASS -> 10;
                case RUNTIME -> 20;
                default -> 30;
                };
            }
        });
    }

    @Test
    void conditionByEnumMultiple() {
        verify(new TestCode.IntParam() {

            @Override
            public int run(@Param(from = 0, to = 3) int param) {
                return switch (RetentionPolicy.values()[param]) {
                case CLASS, RUNTIME -> 10;
                default -> 20;
                };
            }
        });
    }

    @Test
    @Disabled
    void conditionByString() {
        verify(new TestCode.TextParam() {

            @Override
            public String run(@Param(strings = {"a", "b", "c", "d", "e"}) String param) {
                return switch (param) {
                case "a" -> "A";
                case "b" -> "B";
                default -> param;
                };
            }
        });
    }
}