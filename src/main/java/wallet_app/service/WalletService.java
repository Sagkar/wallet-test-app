package wallet_app.service;

import org.json.JSONException;
import org.springframework.stereotype.Service;
import wallet_app.entity.WalletResponse;

@Service
public interface WalletService {
    WalletResponse getBalance(String wallet_id);

    WalletResponse changeBalance(String walletRequest) throws JSONException;
}
