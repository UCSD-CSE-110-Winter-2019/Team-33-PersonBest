package com.android.personbest;

final class ExecMode {
    enum EMode {
        DEFAULT,
        TEST_CLOUD,
        TEST_LOCAL
    }
    private static EMode execMode = EMode.DEFAULT;
    static EMode getExecMode() {
        return execMode;
    }
    static boolean setExecMode(EMode em) {
        execMode = em;
        return true;
    }
}
