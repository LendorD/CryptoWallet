package len.crypto.Wallet.DB.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import len.crypto.Wallet.DB.entity.Wallet;
import len.crypto.Wallet.DB.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceWallet {
    private final WalletRepository walletRepository;

    @Autowired
    public ServiceWallet(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void save(Wallet wallet){
        walletRepository.save(wallet);
    }

    public List<Wallet> getAll(){
        return walletRepository.findAll();
    }

    public Wallet findByPublick_key(String private_key){
        return walletRepository.findByPublicKey(private_key).get();
    }

    public void deleteById(Integer id){
        walletRepository.deleteById(id);
    }

}