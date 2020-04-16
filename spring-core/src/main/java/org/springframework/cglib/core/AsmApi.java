

package org.springframework.cglib.core;

import org.springframework.asm.Opcodes;

final class AsmApi {

    /**
     * SPRING PATCH: always returns ASM7.
     */
    static int value() {
        return Opcodes.ASM7;
    }

    private AsmApi() {
    }

}
