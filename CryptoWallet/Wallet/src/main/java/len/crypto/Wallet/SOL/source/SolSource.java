package len.crypto.Wallet.SOL.source;

import len.crypto.Wallet.SOL.handler.exception.SolanoRPCExceptionHandler;
import len.crypto.Wallet.SOL.handler.response.SolanoRPCResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolSource {
    public RestTemplate restTemplate;
    private final SolanoRPCExceptionHandler exceptionHandler;
    private final SolanoRPCResponseHandler responseHandler;


    protected String getTemplate() {
//        return "https://api.mainnet-beta.solana.com";
//        return "https://api.devnet.solana.com";
        return "https://api.testnet.solana.com";
    }

    public ResponseEntity<String> getBlockNumber() {
        try {
            return responseHandler.getBlockNumber(execRequest(getTemplate(), String.format("""
                    {"jsonrpc":"2.0","id":1, 
                    "method":"getBlockProduction"}""")));
        } catch (RestClientResponseException exc) {
            return exceptionHandler.getBlockNumber(exc);
        }
    }

    public ResponseEntity<String> getTokenBalance(String address) {
        try {
            ResponseEntity<String> res = responseHandler.getTokenBalance(execRequest(getTemplate(), String.format("""
                    {
                        "jsonrpc": "2.0", "id": 1,
                        "method": "getTokenAccountBalance",
                        "params": [
                          "%s"
                        ]
                      }
                    """, address)));
            return res;
        } catch (RestClientResponseException exc) {
            return exceptionHandler.getTokenBalance(exc);
        }
    }

    public ResponseEntity<String> getLatestBlockhash() {
        try {
            ResponseEntity<String> res = responseHandler.getLatestBlockhash(execRequest(getTemplate(), String.format("""
                    {"jsonrpc":"2.0","id":1, "method":"getRecentBlockhash"}
                    """)));
            return res;
        } catch (RestClientResponseException exc) {
            return exceptionHandler.getLatestBlockhash(exc);
        }
    }

    public ResponseEntity<String> getTransactionCount(String address) {
        return null;
    }

    public ResponseEntity<String> sendRawTransaction(String hexValue) {
        try {
            return responseHandler.sendRawTransaction(execRequest(getTemplate(), String.format("""
                    {
                        "jsonrpc": "2.0",
                        "id": 1,
                        "method": "sendTransaction",
                        "params": [
                          "%s"
                        ]
                      }""", hexValue)));
        } catch (RestClientResponseException exc) {
            return exceptionHandler.sendRawTransaction(exc);
        }
    }

    public ResponseEntity<String> getBalance(String address) {
        try {
            ResponseEntity<String> res = responseHandler.getBalance(execRequest(getTemplate(), String.format("""
                    {
                        "jsonrpc": "2.0", "id": 1,
                        "method": "getBalance",
                        "params": [
                          "%s"
                        ]
                      }""", address)));
            return res;
        } catch (RestClientResponseException exc) {
            return exceptionHandler.getBalance(exc);
        }
    }

    public ResponseEntity<String> getBlockByNumber(long number) {
        try {
            return responseHandler.getBlockByNumber(execRequest(getTemplate(), String.format("""
                    {
                        "jsonrpc": "2.0","id":1,
                        "method":"getBlock",
                        "params": [
                          %d,
                          {
                            "encoding": "json",
                            "maxSupportedTransactionVersion":0,
                            "transactionDetails":"full",
                            "rewards":false
                          }
                        ]
                    }""", number)));
        } catch (RestClientResponseException exc) {
            return exceptionHandler.getBlockByNumber(exc);
        }
    }

    public ResponseEntity<String> getTokenAccountByOwner(String tokenAddress, String ownerAddress) {
        try {
            return responseHandler.getTokenAccountByOwner(execRequest(getTemplate(), String.format("""
                    {
                        "jsonrpc": "2.0",
                        "id": 1,
                        "method": "getTokenAccountsByOwner",
                        "params": [
                          "%s",
                          {
                            "mint": "%s"
                          },
                          {
                            "encoding": "jsonParsed"
                          }
                        ]
                      }
                    """, ownerAddress, tokenAddress)));
        } catch (RestClientResponseException exc) {
            return exceptionHandler.getTokenAccountByOwner(exc);
        }
    }

    public ResponseEntity<String> getTransactionReceipt(String hash) {
        try {
            return responseHandler.getTransactionByHash(execRequest(getTemplate(), String.format("""
                    {
                        "jsonrpc": "2.0",
                        "id": 1,
                        "method": "getSignatureStatuses",
                        "params": [
                          [
                            "%s"
                          ],
                          {
                            "searchTransactionHistory": true
                          }
                        ]
                      }
                    """, hash)));
        } catch (RestClientResponseException exc) {
            return exceptionHandler.getTransactionByHash(exc);
        }
    }

    public ResponseEntity<String> call(String address) {
        try {
            return responseHandler.getTransactionByHash(execRequest(getTemplate(), String.format("""
                    {
                        "jsonrpc": "2.0", "id": 1,
                        "method": "getTokenAccountBalance",
                        "params": [
                          "$s",
                          null
                        ]
                      }
                    """, address)));
        } catch (RestClientResponseException exc) {
            return exceptionHandler.getTransactionByHash(exc);
        }
    }

    public ResponseEntity<String> getTransactionByHash(String txid) {
        try {
            return responseHandler.getTransactionByHash(execRequest(getTemplate(), String.format("""
                    {
                        "jsonrpc": "2.0",
                        "id": 1,
                        "method": "getTransaction",
                        "params": [
                          "%s",
                          "json"
                        ]
                      }
                    """, txid)));
        } catch (RestClientResponseException exc) {
            return exceptionHandler.getTransactionByHash(exc);
        }
    }

    public ResponseEntity<String> getTransactionsInBlock(String txid) {
        try {
            return responseHandler.getTransactionsInBlock(execRequest(getTemplate(), String.format("""
                    {
                        "jsonrpc": "2.0","id":1,
                        "method":"getBlock",
                        "params": [
                          %s,
                          {
                            "encoding": "json",
                            "maxSupportedTransactionVersion":0,
                            "transactionDetails":"signatures",
                            "rewards":false
                          }
                        ]
                      }
                    """, txid)));
        } catch (RestClientResponseException exc) {
            return exceptionHandler.getTransactionsInBlock(exc);
        }
    }


    protected ResponseEntity<String> execRequest(String url, String messageBody) {
        CompletableFuture<ResponseEntity<String>> future = CompletableFuture.supplyAsync(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<String>(messageBody, headers);

            return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        });

        try {
            return future.get(5000L, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            log.debug("Reach timeout {} ms for some post request to node", 5000L);
            throw new RestClientException(String.format("Reach timeout %s ms for some request to node", 5000L));
        } catch (ExecutionException | InterruptedException exc) {
            log.info(exc.toString());
        }
        return null;
    }
}

