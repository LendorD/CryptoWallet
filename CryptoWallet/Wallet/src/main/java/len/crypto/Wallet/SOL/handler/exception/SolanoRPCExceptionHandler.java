package len.crypto.Wallet.SOL.handler.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class SolanoRPCExceptionHandler {

    public ResponseEntity<String> getBlockNumber(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }

    public ResponseEntity<String> getTokenBalance(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }
    public ResponseEntity<String> getLatestBlockhash(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }

    public ResponseEntity<String> getTokenAccountByOwner(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }
    public ResponseEntity<String> getTransactionCount(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }
    public ResponseEntity<String> getTransactionsInBlock(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }
    public ResponseEntity<String> sendRawTransaction(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }

    public ResponseEntity<String> getBalance(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }

    public ResponseEntity<String> getBlockByNumber(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }

    public ResponseEntity<String> getTransactionReceipt(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }

    public ResponseEntity<String> call(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }

    public ResponseEntity<String> getMaxPriorityFeePerGas(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }


    public ResponseEntity<String> estimateGas(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }

    public ResponseEntity<String> getTransactionByHash(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }

    public ResponseEntity<String> getGasPrice(RestClientResponseException exception) {
        return getBaseExceptionHandler(exception);
    }

    protected ResponseEntity<String> getBaseExceptionHandler(RestClientResponseException exception) {
        // getBlock should send 500 if error in request
        if (exception.getStatusCode() != HttpStatusCode.valueOf(500)) {
            throw new HttpServerErrorException(exception.getStatusCode());
        }

        return ResponseEntity.ok(exception.getResponseBodyAsString());
    }
}

