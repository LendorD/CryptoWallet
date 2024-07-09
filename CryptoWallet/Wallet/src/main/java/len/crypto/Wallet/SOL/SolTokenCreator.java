//package len.crypto.Wallet.SOL;
//
//import org.p2p.solanaj.core.Account;
//import org.p2p.solanaj.core.PublicKey;
//import org.p2p.solanaj.core.Transaction;
//import org.p2p.solanaj.programs.TokenProgram;
//import org.p2p.solanaj.rpc.RpcClient;
//import org.p2p.solanaj.rpc.RpcException;
//import org.p2p.solanaj.rpc.types.ConfirmedTransaction;
//import org.p2p.solanaj.rpc.types.config.Commitment;
//
//public class SolTokenCreator {
//
//    public static void main(String[] args) {
//        // Подключение к Solana RPC клиенту
//        RpcClient client = new RpcClient("https://api.devnet.solana.com");
//
//        // Создание аккаунта минтера
//        Account minterAccount = new Account();
//        System.out.println("Minter Public Key: " + minterAccount.getPublicKey());
//
//        // Создание аккаунта токена
//        Account tokenAccount = new Account();
//        System.out.println("Token Account Public Key: " + tokenAccount.getPublicKey());
//
//        try {
//            // Запрос воздушных капель для финансирования аккаунтов
//            requestAirdrop(client, minterAccount.getPublicKey(), 2_000_000_000L); // 2 SOL
//            requestAirdrop(client, tokenAccount.getPublicKey(), 2_000_000_000L); // 2 SOL
//
//            // Создание токена
//            String tokenMintAddress = createToken(client, minterAccount, tokenAccount);
//            System.out.println("Token Mint Address: " + tokenMintAddress);
//
//            // Создание аккаунта для хранения токенов
//            Account tokenRecipientAccount = createTokenAccount(client, minterAccount, tokenMintAddress);
//            System.out.println("Token Recipient Account: " + tokenRecipientAccount.getPublicKey());
//
//            // Выпуск токенов (mint)
//            mintTokens(client, minterAccount, tokenMintAddress, tokenRecipientAccount.getPublicKey(), 1000);
//            System.out.println("Minted 1000 tokens to " + tokenRecipientAccount.getPublicKey());
//
//        } catch (RpcException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static void requestAirdrop(RpcClient client, PublicKey publicKey, long lamports) throws RpcException {
//        String transactionSignature = client.getApi().requestAirdrop(publicKey, lamports, Commitment.CONFIRMED);
//        waitForConfirmation(client, transactionSignature);
//    }
//
//    private static void waitForConfirmation(RpcClient client, String transactionSignature) throws RpcException {
//        while (true) {
//            ConfirmedTransaction confirmedTransaction = client.getApi().getConfirmedTransaction(transactionSignature);
//            if (confirmedTransaction != null) {
//                break;
//            }
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static String createToken(RpcClient client, Account minterAccount, Account tokenAccount) throws RpcException {
//        Transaction transaction = new Transaction();
//        transaction.addInstruction(TokenProgram.createMint(
//                minterAccount.getPublicKey(),
//                tokenAccount.getPublicKey(),
//                9 // decimals
//        ));
//
//        String signature = client.getApi().sendTransaction(transaction, minterAccount, tokenAccount);
//        waitForConfirmation(client, signature);
//
//        return tokenAccount.getPublicKey().toString();
//    }
//
//    private static Account createTokenAccount(RpcClient client, Account ownerAccount, String tokenMintAddress) throws RpcException {
//        PublicKey tokenMintPublicKey = new PublicKey(tokenMintAddress);
//        Account tokenAccount = new Account();
//
//        Transaction transaction = new Transaction();
//        transaction.addInstruction(TokenProgram.createAccount(
//                tokenAccount.getPublicKey(),
//                tokenMintPublicKey,
//                ownerAccount.getPublicKey()
//        ));
//
//        String signature = client.getApi().sendTransaction(transaction, ownerAccount, tokenAccount);
//        waitForConfirmation(client, signature);
//
//        return tokenAccount;
//    }
//
//    private static void mintTokens(RpcClient client, Account minterAccount, String tokenMintAddress, PublicKey recipientAddress, long amount) throws RpcException {
//        PublicKey tokenMintPublicKey = new PublicKey(tokenMintAddress);
//
//        Transaction transaction = new Transaction();
//        transaction.addInstruction(TokenProgram.mintTo(
//                tokenMintPublicKey,
//                recipientAddress,
//                minterAccount.getPublicKey(),
//                amount
//        ));
//
//        String signature = client.getApi().sendTransaction(transaction, minterAccount);
//        waitForConfirmation(client, signature);
//    }
//}