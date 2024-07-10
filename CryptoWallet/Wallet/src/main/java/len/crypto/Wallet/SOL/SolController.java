package len.crypto.Wallet.SOL;

import len.crypto.Wallet.SOL.connector.SolConnector;
import len.crypto.Wallet.DB.entity.Wallet;
import len.crypto.Wallet.DB.service.ServiceWallet;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.HexUtils;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.C;
import org.p2p.solanaj.core.Account;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.core.Transaction;
import org.p2p.solanaj.programs.SystemProgram;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.p2p.solanaj.rpc.types.RpcNotificationResult;
import org.p2p.solanaj.rpc.types.config.Commitment;
import org.p2p.solanaj.rpc.types.config.RpcSendTransactionConfig;
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
    public ModelAndView getAddresses() throws RpcException {
        return updateBalancesAndReturnHome();
    }

    @GetMapping("/update")
    public ModelAndView updateBalancesAndReturnHome() throws RpcException {
        RpcClient rpcClient = new RpcClient(Cluster.DEVNET);
        List<Wallet> wallets = serviceWallet.getAll();
        for (Wallet wallet : wallets) {
            if(wallet.getNet_name().equals("SOL")) {
                PublicKey publicKey = new PublicKey(wallet.getPublicKey());
                double balanceAcc = 0.0;
                try {
                    balanceAcc = rpcClient.getApi().getAccountInfo(publicKey).getValue().getLamports();
                }catch (Exception e){

                }
                wallet.setBalance(balanceAcc);
            }
            else wallet.setBalance(solConnector.getBalance(wallet.getPublicKey()).doubleValue());
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

        RpcClient client = new RpcClient(Cluster.DEVNET);
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
        BigDecimal amountSol = new BigDecimal(amount);
        long lamports = amountSol.multiply(BigDecimal.valueOf(1_000_000_000L)).longValue();
        log.info("send lamports: {}",lamports);
        transaction.addInstruction(SystemProgram.transfer(signer.getPublicKey(), toPublickKey, lamports));

        String signature = client.getApi().sendTransaction(transaction, signer);
        log.info("TxId --->{}", signature);
        return "redirect:/sol/addresses";
    }

    @GetMapping("/airdropIndex")
    public String showAirdropToken() {
        return "airdropToken";
    }

    @PostMapping("/airdrop")
    public String airdpro(@RequestParam String Address, @RequestParam String amount) throws RpcException {
        RpcClient client = new RpcClient(Cluster.DEVNET);
        PublicKey publicKey = new PublicKey(Address);
        BigDecimal amountSol = new BigDecimal(amount);
        long lamports = amountSol.multiply(BigDecimal.valueOf(1_000_000_000L)).longValue();

        try {
            String result = client.getApi().requestAirdrop(publicKey, lamports);
            log.info(result);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "redirect:/sol/addresses";
    }

}