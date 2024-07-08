package len.crypto.Wallet.ADA.connector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import len.crypto.Wallet.ADA.source.AdaSource;
import len.crypto.Wallet.SOL.source.SolSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.p2p.solanaj.rpc.types.Block;
import org.p2p.solanaj.rpc.types.ConfirmedTransaction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdaConnector {

    private final AdaSource adaSource;



    public long getBlockNumber() {
        Supplier<ResponseEntity<String>> func = () -> adaSource.getBlockNumber();

        ResponseEntity<String> response = execute(func);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode result = mapper.readTree(response.getBody()).findValue("result").findValue("context").findValue("slot");
            return Long.parseLong(result.asText());
        } catch (Exception e) {
            log.error("Error during {}. Response = {}. Return 0", "getBlockNumber", response, e);
            return 0L;
        }
    }

    public BigInteger getTransactionCount(String address) {
        Supplier<ResponseEntity<String>> func = () -> adaSource.getTransactionsInBlock(address);
        ResponseEntity<String> response = execute(func);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode node = mapper.readTree(response.getBody());
            JsonNode result = node.get("result");
            if (result == null)
                return BigInteger.ZERO;
            List<String> list = Collections.singletonList(result.get("signatures").asText());
            return BigInteger.valueOf(list.size());
        } catch (Exception e) {
            log.error("Error during {}. Response = {}. Return 0", "getTransactionCount", response, e);
            return BigInteger.ZERO;
        }
    }

    public String getTokenAccountByOwner(String tokenAddress, String ownerAddress) {
        Supplier<ResponseEntity<String>> func = () -> adaSource.getTokenAccountByOwner(tokenAddress, ownerAddress);

        ResponseEntity<String> response = execute(func);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode result = mapper.readTree(response.getBody()).findValue("result").findValue("value").findValue("pubkey");
            return result.asText();
        } catch (Exception e) {
            log.error("Error during {}. Response = {}. Throw Exception", "getTokenAccountByOwner", response, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getLatestBlockhash() {
        Supplier<ResponseEntity<String>> func = () -> adaSource.getLatestBlockhash();

        ResponseEntity<String> response = execute(func);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode result = mapper.readTree(response.getBody()).findValue("result").findValue("value").findValue("blockhash");
            return result.asText();
        } catch (Exception e) {
            log.error("Error during {}. Response = {}. Throw Exception", "getLatestBlockhash", response, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public String sendRawTransaction(String hexValue) throws RuntimeException{
        Supplier<ResponseEntity<String>> func = () -> adaSource.sendRawTransaction(hexValue);

        ResponseEntity<String> response = execute(func);

        try {
            Map<String, String> map = new ObjectMapper().readValue(response.getBody(), new TypeReference<>() {});
            return map.get("result");
        } catch (Exception e) {
            log.error("Error during {}. Response = {}. Throw Exception", "sendRawTransaction", response, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public BigDecimal getBalance(String address) {
        Supplier<ResponseEntity<String>> func = () -> adaSource.getBalance(address);

        ResponseEntity<String> response = execute(func);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode result = mapper.readTree(response.getBody()).findValue("result").findValue("value");
            return mapper.convertValue(result, BigDecimal.class);
        } catch (Exception e) {
            log.error("Error during {}. Response = {}. Return 0", "getBalance", response, e);
            return BigDecimal.ZERO;
        }
    }

    public Block getBlockByNumber(long number) {
        Supplier<ResponseEntity<String>> func = () -> adaSource.getBlockByNumber(number);

        ResponseEntity<String> response = execute(func);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode result = mapper.readTree(response.getBody()).findValue("result");

            return mapper.convertValue(result, Block.class);
        } catch (Exception e) {
            log.error("Error during {}. Response = {}. Return empty bock", "getBlockByNumber", response, e);
            return new Block();
        }
    }

    public List<ConfirmedTransaction> getAllTransactionInBlock(long number) {
        Supplier<ResponseEntity<String>> func = () -> adaSource.getBlockByNumber(number);

        ResponseEntity<String> response = execute(func);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            JsonNode result = mapper.readTree(response.getBody()).findValue("result").findValue("transactions");
            List<ConfirmedTransaction> transactionList = mapper.readValue(result.traverse(), new TypeReference<List<ConfirmedTransaction>>() {});

            for (ConfirmedTransaction transaction : transactionList) {
                Field slotField = transaction.getClass().getDeclaredField("slot");
                slotField.setAccessible(true);
                slotField.setLong(transaction, number);
            }
            return transactionList;
        } catch (Exception e) {
            log.error("Error during {}. Response = {}. Return empty bock", "getAllTransactionInBlock", response, e);
            return new ArrayList<>();
        }
    }

    public ConfirmedTransaction getTransaction(String hash) {
        Supplier<ResponseEntity<String>> func = () -> adaSource.getTransactionByHash(hash);

        ResponseEntity<String> response = execute(func);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode result = mapper.readTree(response.getBody()).findValue("result");

            return mapper.readValue(result.toString(), ConfirmedTransaction.class);
        } catch (Exception e) {
            log.error("Error during {}. Response = {}. Return null", "getTransaction", response, e);
            return null;
        }
    }


    public BigDecimal getTokenBalance(String address) {
        Supplier<ResponseEntity<String>> func = () -> adaSource.getTokenBalance(address);

        ResponseEntity<String> response = execute(func);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode result = mapper.readTree(response.getBody()).findValue("result").findValue("value").findValue("uiAmountString");
            return mapper.convertValue(result, BigDecimal.class);
        } catch (Exception e) {
            log.error("Error during {}. Response = {}. Return 0", "getTokenBalance", response, e);
            return BigDecimal.ZERO;
        }
    }

    public List<String> getInputs(String txid) {
        Supplier<ResponseEntity<String>> func = () -> adaSource.getTransactionsInBlock(txid);

        ResponseEntity<String> response = execute(func);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            JsonNode node = mapper.readTree(response.getBody());
            JsonNode result = node.get("result");
            if (result == null)
                return Collections.emptyList();

            return Collections.singletonList(result.get("signatures").asText());
        } catch (Exception e) {
            log.error("Error during {}. Response = {}. Return empty List", "getInputs", response, e);
            return Collections.emptyList();
        }
    }

    public Boolean isTransactionExists(String txid) {
        Supplier<ResponseEntity<String>> func = () -> adaSource.getTransactionByHash(txid);

        ResponseEntity<String> response = execute(func);

        try {
            Map<String, Object> map = new ObjectMapper().readValue(response.getBody(), new TypeReference<>(){});
            Object res = map.get("result");
            return res != null;
        } catch (Exception e) {
            log.error("Error during {}. Response = {}. Return null", "isTransactionExists", response, e);
            return null;
        }
    }

    public ResponseEntity<String> execute(Supplier<ResponseEntity<String>> func) {
        return func.get();
    }

}