package len.crypto.Wallet.ADA;

import com.bloxbean.cardano.client.account.Account;
import com.bloxbean.cardano.client.api.model.Amount;
import com.bloxbean.cardano.client.api.model.Result;
import com.bloxbean.cardano.client.backend.api.BackendService;
import com.bloxbean.cardano.client.backend.blockfrost.common.Constants;
import com.bloxbean.cardano.client.backend.blockfrost.service.BFBackendService;
import com.bloxbean.cardano.client.common.model.Networks;
import com.bloxbean.cardano.client.function.helper.SignerProviders;
import com.bloxbean.cardano.client.quicktx.QuickTxBuilder;
import com.bloxbean.cardano.client.quicktx.Tx;
import com.bloxbean.cardano.client.transaction.model.PaymentTransaction;
import len.crypto.Wallet.ADA.connector.AdaConnector;
import len.crypto.Wallet.DB.entity.Wallet;
import len.crypto.Wallet.DB.service.ServiceWallet;
import len.crypto.Wallet.SOL.connector.SolConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;

@Controller()
@RequestMapping("/ada")
@Slf4j
public class AdaController {

    BackendService backendService =
            new BFBackendService(Constants.BLOCKFROST_PREPROD_URL, "preprodRFTy0kC2O4ZD9UKQA76RnjNamQ9eyx3D");
    private AdaConnector adaConnector;
    private ServiceWallet serviceWallet;

    public AdaController(AdaConnector adaConnector, ServiceWallet serviceWallet) {
        this.adaConnector = adaConnector;
        this.serviceWallet = serviceWallet;
    }


    @GetMapping("/NewAddress/{walletName}")
    public String createAddress(@PathVariable String walletName){
        com.bloxbean.cardano.client.account.Account account = new Account(Networks.preprod());
        Wallet wallet = new Wallet();
        wallet.setPublicKey(account.baseAddress());
        wallet.setPrivateKey(account.mnemonic());
        wallet.setNet_name("ADA");
        wallet.setBalance(adaConnector.getBalance(account.baseAddress()).doubleValue());
        wallet.setWalletName(walletName);
        serviceWallet.save(wallet);
        return "redirect:/wallet/home";
    }

    @GetMapping("/addresses")
    public ModelAndView getAddresses() {
        return updateBalancesAndReturnHome();
    }

    @GetMapping("/update")
    public ModelAndView updateBalancesAndReturnHome() {
        List<Wallet> wallets = serviceWallet.getAll();
        for (Wallet wallet : wallets) {
            wallet.setBalance(adaConnector.getBalance(wallet.getPublicKey()).doubleValue());
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
    public String  sendTransaction(@RequestParam String fromAddress, @RequestParam String toAddress, @RequestParam String amount) throws RpcException {
        Tx tx1 = new Tx()
                .payToAddress(toAddress, Amount.ada(Double.valueOf(amount)))
                .from(fromAddress);

        Account account = new Account(Networks.preprod(), serviceWallet.findByPublick_key(fromAddress).getPrivateKey());
        QuickTxBuilder quickTxBuilder = new QuickTxBuilder(backendService);

        Result<String> result = quickTxBuilder
                .compose(tx1)
                .feePayer(fromAddress)
                .withSigner(SignerProviders.signerFrom(account))
                .completeAndWait(System.out::println);

        log.info("TxId --->{}", result);
        return "redirect:/wallet/home";
    }
    @GetMapping("/airdropIndex")
    public String showAirdropToken() {
        return "airdropToken";
    }
    @PostMapping("/airdrop")
    public String airdpro(@RequestParam String Address, @RequestParam String amount) throws RpcException {

        return "redirect:/wallet/home";
    }


}