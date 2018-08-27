package com.vktiwari.pagingretrofitrxjava.network;

public class NetworkState {
    public static final NetworkState LOADING;
    public static final NetworkState LOADED;

    public enum Status {
        RUNNING,
        SUCCESS,
        FAILED
    }

    private Status status;
    private String message;

    private NetworkState(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    private NetworkState(Status status) {
        this.status = status;
    }

    static {
        LOADED = new NetworkState(Status.SUCCESS);
        LOADING = new NetworkState(Status.RUNNING,"Loading please wait");
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public static NetworkState error(String message) {
        return new NetworkState(Status.FAILED, message == null ? "unknown error" : message);
    }
}
