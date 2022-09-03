package org.apache.coyote.support;

public enum HttpStatus {

    OK(200, "OK"),
    BAD_REQUEST(400, "BAD REQUEST"),
    NOT_FOUND(404, "NOT FOUND"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

    private final int code;
    private final String reason;

    HttpStatus(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public String toStatusLine() {
        return String.format("%s %d %s", HttpVersion.HTTP11.getValue(), code, reason);
    }
}
