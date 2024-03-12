package wallet_app.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import wallet_app.entity.Wallet;
import wallet_app.entity.WalletResponse;
import wallet_app.exception.InsufficientFundsException;
import wallet_app.exception.InvalidJSONException;
import wallet_app.exception.InvalidOperationTypeException;
import wallet_app.exception.WalletNotFoundException;
import wallet_app.repo.WalletRepository;
import wallet_app.service.WalletService;

import java.util.Optional;
import java.util.UUID;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public WalletResponse getBalance(String wallet_id) {
        WalletResponse walletResponse = new WalletResponse();
        walletResponse.setWalletId(UUID.fromString(wallet_id));
        walletResponse.setOperationType("Getting balance");

        Optional<Wallet> wallet = walletRepository.findById(UUID.fromString(wallet_id));
        if (wallet.isEmpty()) {
            WalletNotFoundException e = new WalletNotFoundException("Cannot find wallet with id: " + wallet_id);
            walletResponse.failure(e.getMessage());
            return walletResponse;
        }
        Wallet resultWallet = wallet.get();

        walletResponse.setResult(String.valueOf(resultWallet.balance));
        return walletResponse;
    }

    @Override
    public WalletResponse changeBalance(String walletRequestJson) {
        WalletResponse response = new WalletResponse();
        Gson gson = new Gson();
        try {
        JsonObject json = gson.fromJson(walletRequestJson, JsonObject.class);
        //Ensure what json have necessary fields
        if (json.has("walletId") &&
                json.has("operationType") &&
                json.has("amount")) {

            UUID id = UUID.fromString(json.get("walletId").getAsString());
            String type = json.get("operationType").getAsString();
            Long amount = Long.valueOf(json.get("amount").getAsString());
            response.setWalletId(id);

                switch (type) {
                    case "DEPOSIT":
                        response.setOperationType("DEPOSIT");
                        deposit(id, amount);
                        response.setResult("Deposit successful. Amount:" + amount);
                        break;
                    case "WITHDRAW":
                        response.setOperationType("WITHDRAW");
                        withdraw(id, amount);
                        response.setResult("Withdraw successful. Amount:" + amount);
                        break;
                    default:
                        response.setOperationType(type);
                        throw new InvalidOperationTypeException("Invalid operation type");
                }
            } else throw new InvalidJSONException("Request must contain fields: \"walletId\", \"operationType\", \"amount\"");
        } catch (InsufficientFundsException |
                 WalletNotFoundException |
                 InvalidOperationTypeException |
                 JsonSyntaxException |
                 InvalidJSONException |
                 IllegalArgumentException e) {
            response.failure(e.getMessage());
        }
        return response;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Wallet deposit(UUID walletId, float amount){
        Optional<Wallet> optionalWallet = walletRepository.findById(walletId);
        if (optionalWallet.isPresent()) {
            Wallet wallet = optionalWallet.get();

            float currentBalance = wallet.getBalance();
            float newBalance = currentBalance + amount;
            wallet.setBalance(newBalance);

            return walletRepository.save(wallet);
        } else {
            throw new WalletNotFoundException("Wallet not found with ID: " + walletId);
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Wallet withdraw(UUID walletId, float amount) {
        Optional<Wallet> optionalWallet = walletRepository.findById(walletId);
        if (optionalWallet.isPresent()) {
            Wallet wallet = optionalWallet.get();

            float currentBalance = wallet.getBalance();
            if (currentBalance >= amount) {
                float newBalance = currentBalance - amount;
                wallet.setBalance(newBalance);

                return walletRepository.save(wallet);
            } else {
                throw new InsufficientFundsException("Insufficient funds for withdrawal from wallet with ID: " + walletId);
            }
        } else {
            throw new WalletNotFoundException("Wallet not found with ID: " + walletId);
        }
    }
}