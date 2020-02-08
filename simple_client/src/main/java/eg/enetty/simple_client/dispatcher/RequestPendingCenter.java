package eg.enetty.simple_client.dispatcher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import eg.enetty.simple_client.common.OperationResult;

public class RequestPendingCenter {

    private Map<Long, OperationResultFuture> map = new ConcurrentHashMap<Long, OperationResultFuture>();

    public void add(Long streamId, OperationResultFuture future) {
        map.put(streamId, future);
    }

    public void set(Long streamId, OperationResult operationResult) {
        OperationResultFuture operationResultFuture = map.get(streamId);

        if ( operationResultFuture != null ) {
            operationResultFuture.setSuccess(operationResult);
            map.remove(streamId);
        }
    }

}
