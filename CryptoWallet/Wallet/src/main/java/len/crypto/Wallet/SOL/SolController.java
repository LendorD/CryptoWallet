package len.crypto.Wallet.SOL;

import len.crypto.Wallet.SOL.connector.SolConnector;
import len.crypto.Wallet.DB.entity.Wallet;
import len.crypto.Wallet.DB.service.ServiceWallet;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.p2p.solanaj.core.Account;
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
@RequestMapping("/sol")
@Slf4j
public class SolController {
    private SolConnector solConnector;
    private ServiceWallet serviceWallet;

    public SolController(SolConnector solConnector, ServiceWallet serviceWallet) {
        this.solConnector = solConnector;
        this.serviceWallet = serviceWallet;
    }


    @GetMapping("/NewAddress/{walletName}")
    public String createAddress(@PathVariable String walletName){
        Account newAccount = new Account();
        String address = newAccount.getPublicKey().toBase58();
        double balance = solConnector.getBalance(address).doubleValue();
        Wallet wallet = new Wallet();
        wallet.setPublicKey(address);
        wallet.setPrivateKey(HexUtils.toHexString(newAccount.getSecretKey()));
        wallet.setNet_name("SOL");
        wallet.setBalance(balance);
        wallet.setWalletName(walletName);
        serviceWallet.save(wallet);
        return "redirect:/wallet/home";
//        return updateBalancesAndReturnHome();
    }

    @GetMapping("/addresses")
    public ModelAndView getAddresses() {
        return updateBalancesAndReturnHome();
    }

    @GetMapping("/update")
    public ModelAndView updateBalancesAndReturnHome() {
        List<Wallet> wallets = serviceWallet.getAll();
        for (Wallet wallet : wallets) {
            wallet.setBalance(solConnector.getBalance(wallet.getPublicKey()).doubleValue());
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

        RpcClient client = new RpcClient(Cluster.TESTNET);
        Wallet wallet = new Wallet();
        try{
            wallet = serviceWallet.findByPublick_key(fromAddress);
        }catch (Exception e){
            return "redirect:/sol/addresses";
        }
        Account signer = new Account(HexUtils.fromHexString(wallet.getPrivateKey()));
        log.info("privKey ----> {}", HexUtils.fromHexString(wallet.getPrivateKey()));
        Transaction transaction = new Transaction();
        PublicKey toPublickKey = new PublicKey(toAddress);
        Long amount1 = Long.parseLong(amount);
        BigDecimal amount2 = BigDecimal.valueOf(amount1.doubleValue());
        transaction.addInstruction(SystemProgram.transfer(signer.getPublicKey(), toPublickKey, amount2.toBigInteger().longValue()));

        String signature = client.getApi().sendTransaction(transaction, signer);
        log.info("TxId --->{}", signature);
        return "redirect:/sol/addresses";
    }

}