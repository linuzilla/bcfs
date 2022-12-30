package ncu.cc.iota.models;

public class RetrieveProgressAndResult {
    private byte[] data;
    private boolean done;
    private boolean error;
    private String message;

    public static RetrieveProgressAndResult fromError(String message) {
        RetrieveProgressAndResult result = new RetrieveProgressAndResult(message);
        result.setError(true);
        return result;
    }

    public static RetrieveProgressAndResult fromData(byte[] data) {
        RetrieveProgressAndResult result = new RetrieveProgressAndResult(data, true);
        result.setMessage("done");
        return result;
    }

    public static RetrieveProgressAndResult fromMessage(String message) {
        return new RetrieveProgressAndResult(message);
    }

    public RetrieveProgressAndResult() {
    }

    private RetrieveProgressAndResult(String message) {
        this.message = message;
    }

    private RetrieveProgressAndResult(byte[] data, boolean done) {
        this.data = data;
        this.done = done;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
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
