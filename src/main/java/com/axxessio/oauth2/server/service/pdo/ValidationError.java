package com.axxessio.oauth2.server.service.pdo;

public class ValidationError {
    private int errCode;
    private String errMsg;

    public ValidationError(int newErrCode, String newErrMsg) {
        errCode = newErrCode;
        errMsg = newErrMsg;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public String toString() {
        return "ValidationException: Error Code [" + errCode + "] Error Message [" + errMsg + "]";
    }
}
