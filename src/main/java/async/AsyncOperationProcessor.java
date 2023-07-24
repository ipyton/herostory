package async;

import utils.MainThreadProcessor;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AsyncOperationProcessor {
    static private final AsyncOperationProcessor _instance  = new AsyncOperationProcessor();

    private final ExecutorService[] _esArray = new ExecutorService[8];

    private AsyncOperationProcessor() {
        for (int i = 0; i < _esArray.length; i ++){

            final String threadName = MessageFormat.format("AyncProcessor", i);
            _esArray[i] = Executors.newSingleThreadExecutor((r) -> {
               Thread t = new Thread(r);
               t.setName(threadName);
               return t;
            });
        }
    }

    static public AsyncOperationProcessor getInstance(){
        return _instance;
    }

    public void process(IAsyncOperation op) {
        if (null == op) return;

        int bindID = Math.abs(op.getBindID());
        int esIndex = bindID % _esArray.length;

        _esArray[esIndex].submit(() -> {
            op.doAsync();

            MainThreadProcessor.getInstance().process(op::doFinish);
        });
    }

}
