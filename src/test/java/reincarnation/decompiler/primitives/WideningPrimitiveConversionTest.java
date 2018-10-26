/*
 * Copyright (C) 2018 Reincarnation Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package reincarnation.decompiler.primitives;

import org.junit.jupiter.api.Test;

import reincarnation.Code;
import reincarnation.CodeVerifier;

/**
 * @version 2018/10/22 16:36:04
 */
class WideningPrimitiveConversionTest extends CodeVerifier {

    @Test
    void longToDouble() {
        verify(new Code.LongParamDouble() {

            @Override
            public double run(long param) {
                return param / 2;
            }
        });
    }
}
