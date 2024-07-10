package len.crypto.Wallet.ETH;


import len.crypto.Wallet.DB.entity.Wallet;
import len.crypto.Wallet.DB.service.ServiceWallet;
import lombok.extern.slf4j.Slf4j;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Controller()
@RequestMapping("/eth")
@Slf4j
public class EthController {
    private final Web3j web3j;
    private final ServiceWallet serviceWallet;

    public EthController(ServiceWallet serviceWallet) {
        this.web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/957264d063634a9894bed050851a79d8"));
        this.serviceWallet = serviceWallet;
    }

    @GetMapping("/NewAddress/{walletName}")
    public String createAddress(@PathVariable String walletName) {
        try {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            String privateKey = ecKeyPair.getPrivateKey().toString(16);
            String publicKey = "0x" + Keys.getAddress(ecKeyPair);

            // Проверка баланса
            EthGetBalance ethGetBalance = web3j.ethGetBalance(publicKey, DefaultBlockParameterName.LATEST).send();
            BigDecimal balance = new BigDecimal(ethGetBalance.getBalance());

            Wallet wallet = new Wallet();
            wallet.setPublicKey(publicKey);
            wallet.setPrivateKey(privateKey);
            wallet.setNet_name("ETH");
            wallet.setBalance(balance.doubleValue());
            wallet.setWalletName(walletName);
            serviceWallet.save(wallet);

            return "redirect:/wallet/home";
        } catch (Exception e) {
            log.error("Error creating new Ethereum address", e);
            return "redirect:/wallet/home";
        }
    }

    @GetMapping("/addresses")
    public ModelAndView getAddresses() {
        return updateBalancesAndReturnHome();
    }

    @GetMapping("/update")
    public ModelAndView updateBalancesAndReturnHome() {
        List<Wallet> wallets = serviceWallet.getAll();
        for (Wallet wallet : wallets) {
            try {
                EthGetBalance ethGetBalance = web3j.ethGetBalance(wallet.getPublicKey(), DefaultBlockParameterName.LATEST).send();
                wallet.setBalance(new BigDecimal(ethGetBalance.getBalance()).doubleValue());
            } catch (Exception e) {
                log.error("Error updating balance for wallet: " + wallet.getPublicKey(), e);
            }
        }

        ModelAndView modelAndView = new ModelAndView("home");
        modelAndView.addObject("wallets", wallets);
        return modelAndView;
    }

    @GetMapping("/send-transaction")
    public String showSendTransactionForm() {
        return "send-transaction";
    }

    @PostMapping("/send-raw-transaction")
    public String sendTransaction(@RequestParam String fromAddress, @RequestParam String toAddress, @RequestParam String amount) {
        try {
            Wallet wallet = serviceWallet.findByPublick_key(fromAddress);
            Credentials credentials = Credentials.create(wallet.getPrivateKey());
            BigDecimal amountInEther = new BigDecimal(amount);

            RawTransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            TransactionReceipt receipt = Transfer.sendFunds(web3j, credentials, toAddress, amountInEther, Convert.Unit.ETHER).send();
            log.info("Transaction successful with hash: " + receipt.getTransactionHash());

            return "redirect:/wallet/home";
        } catch (Exception e) {
            log.error("Error sending Ethereum transaction", e);
            return "redirect:/wallet/home";
        }
    }

    @GetMapping("/airdropIndex")
    public String showAirdropToken() {
        return "airdropToken";
    }

    @PostMapping("/airdrop")
    public String airdrop(@RequestParam String Address, @RequestParam String amount) throws RpcException {
        return "redirect:/wallet/home";
    }
}