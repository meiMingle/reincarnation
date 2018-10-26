/*
 * Copyright (C) 2018 Reincarnation Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package reincarnation.decompiler.method;

import org.junit.jupiter.api.Test;

import reincarnation.Code;
import reincarnation.CodeVerifier;

/**
 * @version 2018/10/23 15:31:37
 */
class MethodTest extends CodeVerifier {

    @Test
    void Basic() {
        verify(new Basic());
    }

    /**
     * @version 2018/10/23 15:32:19
     */
    private static class Basic implements Code.Int {

        @Override
        public int run() {
            return compute();
        }

        private int compute() {
            return -10;
        }
    }

    @Test
    void Param() {
        verify(new Param());
    }

    /**
     * @version 2018/10/23 15:32:31
     */
    private static class Param implements Code.IntParam {

        @Override
        public int run(int value) {
            return compute(value);
        }

        private int compute(int value) {
            return 100 + value;
        }
    }

    @Test
    void MultipleParams() {
        verify(new MultipleParams());
    }

    /**
     * @version 2018/10/23 15:32:40
     */
    private static class MultipleParams implements Code.IntParam {

        @Override
        public int run(int value) {
            return compute(value, value + 1);
        }

        private int compute(int first, int second) {
            return first * second;
        }
    }

    @Test
    void ArrayParam() {
        verify(new ArrayParam());
    }

    /**
     * @version 2018/10/23 15:32:50
     */
    private static class ArrayParam implements Code.IntParam {

        @Override
        public int run(int value) {
            int[] ints = {value, value + 1, value + 2};

            return compute(ints);
        }

        private int compute(int[] values) {
            int i = 0;

            for (int j = 0; j < values.length; j++) {
                i += values[j];
            }

            return i;
        }
    }

    @Test
    void AssignLocalParam() {
        verify(new AssignLocalParam());
    }

    /**
     * @version 2018/10/23 15:32:58
     */
    private static class AssignLocalParam implements Code.IntParam {

        @Override
        public int run(int value) {
            int local = 0;
            int result = compute(local = value);

            return local + result;
        }

        private int compute(int value) {
            return 100 + value;
        }
    }

    @Test
    void AssignFieldParam() {
        verify(new AssignFieldParam());
    }

    /**
     * @version 2018/10/23 15:33:05
     */
    private static class AssignFieldParam implements Code.IntParam {

        private int local;

        @Override
        public int run(int value) {
            int result = compute(local = value);

            return local + result;
        }

        private int compute(int value) {
            return 100 + value;
        }
    }

    @Test
    void VariableParam() {
        verify(new VariableParam());
    }

    /**
     * @version 2018/10/23 15:33:10
     */
    private static class VariableParam implements Code.IntParam {

        @Override
        public int run(int value) {
            return compute(value, value + 1, value + 2);
        }

        private int compute(int... values) {
            int i = 0;

            for (int j = 0; j < values.length; j++) {
                i += values[j];
            }

            return i;
        }
    }

    @Test
    void VariableParamWithBase() {
        verify(new VariableParamWithBase());
    }

    /**
     * @version 2018/10/23 15:33:14
     */
    private static class VariableParamWithBase implements Code.IntParam {

        @Override
        public int run(int value) {
            return compute(value, value + 1, value + 2);
        }

        private int compute(int base, int... values) {
            int i = base * base;

            for (int j = 0; j < values.length; j++) {
                i += values[j];
            }

            return i;
        }
    }

    @Test
    void VariableParamWithBaseOnly() {
        verify(new VariableParamWithBaseOnly());
    }

    /**
     * @version 2018/10/23 15:33:19
     */
    private static class VariableParamWithBaseOnly implements Code.IntParam {

        @Override
        public int run(int value) {
            return compute(value);
        }

        private int compute(int base, int... values) {
            int i = base * base;

            for (int j = 0; j < values.length; j++) {
                i += values[j];
            }

            return i;
        }
    }

    @Test
    void Nest() {
        verify(new Nest());
    }

    /**
     * @version 2018/10/23 15:33:24
     */
    private static class Nest implements Code.IntParam {

        @Override
        public int run(int value) {
            return compute(value, nest(value));
        }

        private int compute(int first, int second) {
            return first * second;
        }

        private int nest(int value) {
            return value + value;
        }
    }

    @Test
    void Overload() {
        verify(new Overload());
    }

    /**
     * @version 2018/10/23 15:33:29
     */
    private static class Overload implements Code.IntParam {

        @Override
        public int run(int value) {
            return compute(value);
        }

        private int compute(int value) {
            return value * value;
        }

        private String compute(String value) {
            return value.substring(1);
        }
    }

    @Test
    void ExtendPublic() {
        verify(new ExtendPublic());
    }

    /**
     * @version 2018/10/23 15:33:39
     */
    private static class BasePublic {

        public int compute() {
            return 10;
        }
    }

    /**
     * @version 2018/10/23 15:33:36
     */
    private static class ExtendPublic extends BasePublic implements Code.IntParam {

        @Override
        public int run(int value) {
            return value + compute();
        }
    }

    @Test
    void ExtendProtected() {
        verify(new ExtendProtected());
    }

    /**
     * @version 2018/10/23 15:33:47
     */
    private static class BaseProtected {

        protected int compute() {
            return 10;
        }
    }

    /**
     * @version 2018/10/23 15:33:50
     */
    private static class ExtendProtected extends BaseProtected implements Code.IntParam {

        @Override
        public int run(int value) {
            return value + compute();
        }
    }

    @Test
    void ExtendPackage() {
        verify(new ExtendPackage());
    }

    /**
     * @version 2018/10/23 15:33:54
     */
    private static class BasePackage {

        int compute() {
            return 10;
        }
    }

    /**
     * @version 2018/10/23 15:35:06
     */
    private static class ExtendPackage extends BasePackage implements Code.IntParam {

        @Override
        public int run(int value) {
            return value + compute();
        }
    }

    @Test
    void Override() {
        verify(new OverrideChild());
    }

    /**
     * @version 2018/10/23 15:35:11
     */
    private static class OverrideBase {

        public int compute(int value) {
            return value + 1;
        }
    }

    /**
     * @version 2018/10/23 15:35:14
     */
    private static class OverrideChild extends OverrideBase implements Code.IntParam {

        @Override
        public int run(int value) {
            return compute(value);
        }

        @Override
        public int compute(int value) {
            return value - 1;
        }
    }

    @Test
    void Super() {
        verify(new SuperChild());
    }

    /**
     * @version 2018/10/23 15:35:44
     */
    static class SuperBase {

        public int compute(int value) {
            return value + 1;
        }
    }

    /**
     * @version 2018/10/23 15:35:40
     */
    static class SuperChild extends SuperBase implements Code.IntParam {

        @Override
        public int run(int value) {
            return this.compute(value) + super.compute(value);
        }

        @Override
        public int compute(int value) {
            return value - 1;
        }
    }
}
