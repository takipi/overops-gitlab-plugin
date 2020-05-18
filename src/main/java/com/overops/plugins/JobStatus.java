package com.overops.plugins;

public enum JobStatus {
    PASS(0),
    FAIL(1);

    private final int statusCode;

    JobStatus(int statusCode) {
        this.statusCode = statusCode;
    }

    public int statusCode(){
        return this.statusCode;
    }
}
