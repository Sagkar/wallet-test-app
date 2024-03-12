package wallet_app.controller;

import org.springframework.web.bind.annotation.*;
import wallet_app.entity.WalletResponse;
import wallet_app.service.impl.WalletServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class WalletController {
    WalletServiceImpl walletService;

    @Autowired
    private WalletController(WalletServiceImpl walletService) {
        this.walletService = walletService;
    }

    @RequestMapping(value = "wallet/{WALLET_UUID}", method = RequestMethod.GET)
    public WalletResponse getBalance(@PathVariable String WALLET_UUID) {
        return walletService.getBalance(WALLET_UUID);
    }

    @RequestMapping(value = "wallet", method = RequestMethod.POST)
    public WalletResponse changeBalance(@RequestBody String request) {
        return walletService.changeBalance(request);
    }
}