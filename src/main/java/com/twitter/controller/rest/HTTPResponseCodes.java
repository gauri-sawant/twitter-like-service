package com.twitter.controller.rest;

public final class HTTPResponseCodes {

    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int NOT_AUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
    public static final int NOT_ALLOWED = 405;
    public static final int CONFLICT = 409;
    public static final int ACCEPTED = 202;
    public static final int NO_CONTENT = 204;
    public static final int CREATED = 201;
    public static final int OK = 200;
    
    public static final String FAULT_CLASS = "HTTP_STATUS";
    public static final String FAULT_PARAMETER = "CODE";


	private HTTPResponseCodes() {
        throw new AssertionError();
    }
}
