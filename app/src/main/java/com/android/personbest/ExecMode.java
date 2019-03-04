package com.android.personbest;

public final class ExecMode {
    enum EMode {
        DEFAULT,
        TEST_CLOUD,
        TEST_LOCAL
    }
    private static EMode execMode = EMode.DEFAULT;
    public static EMode getExecMode() {
        return execMode;
    }
    public static void setExecMode(EMode em) {
        execMode = em;
    }
}
