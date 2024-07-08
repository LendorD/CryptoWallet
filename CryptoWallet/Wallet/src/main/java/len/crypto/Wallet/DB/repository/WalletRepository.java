package len.crypto.Wallet.DB.repository;

import len.crypto.Wallet.DB.entity.Wallet;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Optional<Wallet> findByPublicKey(String private_key);

    @NotNull
    List<Wallet> findAll();


}