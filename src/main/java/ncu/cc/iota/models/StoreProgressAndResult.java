package ncu.cc.iota.models;

public class StoreProgressAndResult {
    private String bundle;
    private boolean done;
    private boolean error;
    private String message;

    public static StoreProgressAndResult fromError(String message) {
        StoreProgressAndResult result = new StoreProgressAndResult(message);
        result.setError(true);
        return  result;
    }

    public static StoreProgressAndResult fromResult(String bundle) {
        return new StoreProgressAndResult(bundle, true);
    }

    public static StoreProgressAndResult fromMessage(String message) {
        return new StoreProgressAndResult(message);
    }

    public StoreProgressAndResult() {
    }

    private StoreProgressAndResult(String message) {
        this.message = message;
    }

    private StoreProgressAndResult(String bundle, boolean done) {
        this.bundle = bundle;
        this.done = done;
    }

    public String getBundle() {
        return bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
