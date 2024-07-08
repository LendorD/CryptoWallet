package len.crypto.Wallet.DB.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="wallets")
@Getter
@Setter
public class Wallet {

    public Wallet() {

    }

    public Wallet(String pubKey, String privKey, Double balance, String name){
        this.publicKey = pubKey;
        this.privateKey = privKey;
        this.balance = balance;
        this.net_name = name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "net_name")
    private String net_name;

    @Column(name = "private_key")
    private String privateKey;

    @Column(name = "public_key")
    private String publicKey;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "wallet_name")
    private String walletName;



}