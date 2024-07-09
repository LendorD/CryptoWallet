package len.crypto.Wallet.ETH.handler.response;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class EthRPCResponseHandler {
    public ResponseEntity<String> getBlockNumber(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }

    public ResponseEntity<String> getTransactionCount(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }
    public ResponseEntity<String> getTransactionsInBlock(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }
    public ResponseEntity<String> sendRawTransaction(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }
    public ResponseEntity<String> getTokenAccountByOwner(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }
    public ResponseEntity<String> getLatestBlockhash(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }

    public ResponseEntity<String> getTokenBalance(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }
    public ResponseEntity<String> getBalance(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }

    public ResponseEntity<String> getBlockByNumber(ResponseEntity<String> resp)  {

        return resp;
    }

    public ResponseEntity<String> getTransactionReceipt(ResponseEntity<String> resp) {

        return resp;
    }

    public ResponseEntity<String> call(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }

    public ResponseEntity<String> getMaxPriorityFeePerGas(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }


    public ResponseEntity<String> estimateGas(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }

    public ResponseEntity<String> getTransactionByHash(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }

    public ResponseEntity<String> getGasPrice(ResponseEntity<String> resp) {
        return getBaseExceptionHandler(resp);
    }

    protected ResponseEntity<String> getBaseExceptionHandler(ResponseEntity<String> resp) {

        return resp;
    }
}
