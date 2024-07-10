package len.crypto.Wallet;

import len.crypto.Wallet.DB.entity.Wallet;
import len.crypto.Wallet.DB.service.ServiceWallet;
import len.crypto.Wallet.SOL.SolController;
import len.crypto.Wallet.SOL.connector.SolConnector;
import lombok.extern.slf4j.Slf4j;
import org.p2p.solanaj.core.PublicKey;
import org.p2p.solanaj.rpc.Cluster;
import org.p2p.solanaj.rpc.RpcClient;
import org.p2p.solanaj.rpc.RpcException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller()
@RequestMapping("/wallet")
@Slf4j
public class WalletController {

    private SolConnector solConnector;
    private ServiceWallet serviceWallet;

    WalletController(SolConnector solConnector, ServiceWallet serviceWallet){
        this.serviceWallet = serviceWallet;
        this.solConnector = solConnector;
    }

    @GetMapping("/home")
    public ModelAndView home() throws RpcException {
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

    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteWallet(@PathVariable Integer id) throws RpcException {
        log.info("Id deleted walet ---> {}", id);
        serviceWallet.deleteById(id);
        return home();
    }

    @GetMapping("/send-transaction")
    public String sendTx(){
        return "send-transaction";
    }

}
