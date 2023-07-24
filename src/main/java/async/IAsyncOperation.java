package async;

public interface IAsyncOperation {

    default int getBindID() {
        return 0;
    }


    void doAsync();

    default void doFinish() {
    }
}
