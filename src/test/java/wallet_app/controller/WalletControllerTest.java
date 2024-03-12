package wallet_app.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import wallet_app.entity.WalletResponse;
import wallet_app.service.impl.WalletServiceImpl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
class WalletControllerTest {
    @Mock
    private WalletServiceImpl walletService;

    @InjectMocks
    private WalletController walletController;

    UUID walletId = UUID.randomUUID();

    @Test
    public void testGetBalanceForExistingWallet() {
        WalletResponse mockResponse = new WalletResponse();
        mockResponse.setWalletId(walletId);
        mockResponse.setResult("100.0");

        Mockito.when(walletService.getBalance(String.valueOf(walletId))).thenReturn(mockResponse);

        WalletResponse result = walletController.getBalance(String.valueOf(walletId));

        assertEquals(walletId, result.getWalletId());
        assertEquals("100.0", result.getResult());
    }

    @Test
    public void testGetBalanceForNonExistingWallet() {
        WalletResponse mockResponse = new WalletResponse();
        mockResponse.setWalletId(walletId);
        mockResponse.setError("Wallet not found");

        Mockito.when(walletService.getBalance(String.valueOf(walletId))).thenReturn(mockResponse);

        WalletResponse result = walletController.getBalance(String.valueOf(walletId));

        assertEquals(walletId, result.getWalletId());
        assertEquals("Wallet not found", result.getError());
    }

    @Test
    public void testChangeBalanceDeposit() {
        String requestJson = "{\"walletId\":\"" + walletId + "\",\"operationType\":\"DEPOSIT\",\"amount\":\"50.0\"}";
        WalletResponse mockResponse = new WalletResponse();
        mockResponse.setWalletId(walletId);
        mockResponse.setOperationType("DEPOSIT");
        mockResponse.setResult("Deposit successful. Amount:50.0");

        Mockito.when(walletService.changeBalance(anyString())).thenReturn(mockResponse);

        WalletResponse result = walletController.changeBalance(requestJson);

        assertEquals(walletId, result.getWalletId());
        assertEquals("DEPOSIT", result.getOperationType());
        assertEquals("Deposit successful. Amount:50.0", result.getResult());
    }

    @Test
    public void testChangeBalanceWithdraw() {
        String requestJson = "{\"walletId\":\"" + walletId + "\",\"operationType\":\"WITHDRAW\",\"amount\":\"30.0\"}";
        WalletResponse mockResponse = new WalletResponse();
        mockResponse.setWalletId(walletId);
        mockResponse.setOperationType("WITHDRAW");
        mockResponse.setResult("Withdraw successful. Amount:30.0");

        Mockito.when(walletService.changeBalance(anyString())).thenReturn(mockResponse);

        WalletResponse result = walletController.changeBalance(requestJson);

        assertEquals(walletId, result.getWalletId());
        assertEquals("WITHDRAW", result.getOperationType());
        assertEquals("Withdraw successful. Amount:30.0", result.getResult());
    }

    @Test
    public void testChangeBalanceInvalidJson() {
        String requestJson = "invalid_json";
        WalletResponse mockResponse = new WalletResponse();
        mockResponse.setError("Invalid JSON");

        Mockito.when(walletService.changeBalance(anyString())).thenReturn(mockResponse);

        WalletResponse result = walletController.changeBalance(requestJson);

        assertEquals("Invalid JSON", result.getError());
    }

    @Test
    public void testChangeBalanceInvalidUuid() {
        String requestJson = "{\"walletId\":\"invalid_uuid\",\"operationType\":\"DEPOSIT\",\"amount\":\"50.0\"}";
        WalletResponse mockResponse = new WalletResponse();
        mockResponse.setError("Invalid UUID");

        Mockito.when(walletService.changeBalance(anyString())).thenReturn(mockResponse);

        WalletResponse result = walletController.changeBalance(requestJson);

        assertEquals("Invalid UUID", result.getError());
    }

    @Test
    public void testChangeBalanceInvalidAmount() {
        String requestJson = "{\"walletId\":\"" + walletId + "\",\"operationType\":\"DEPOSIT\",\"amount\":\"invalid_amount\"}";
        WalletResponse mockResponse = new WalletResponse();
        mockResponse.setError("Invalid amount");

        Mockito.when(walletService.changeBalance(anyString())).thenReturn(mockResponse);

        WalletResponse result = walletController.changeBalance(requestJson);

        assertEquals("Invalid amount", result.getError());
    }

    @Test
    public void testChangeBalanceInvalidOperation() {
        String requestJson = "{\"walletId\":\"" + walletId + "\",\"operationType\":\"DASDASDASD\",\"amount\":\"50.0\"}";
        WalletResponse mockResponse = new WalletResponse();
        mockResponse.setError("Invalid operation type");

        Mockito.when(walletService.changeBalance(anyString())).thenReturn(mockResponse);

        WalletResponse result = walletController.changeBalance(requestJson);

        assertEquals("Invalid operation type", result.getError());
    }

    @Test
    public void testChangeBalanceWithdrawInsufficientFunds() {
        String requestJson = "{\"walletId\":\"" + walletId + "\",\"operationType\":\"WITHDRAW\",\"amount\":\"150.0\"}";
        WalletResponse mockResponse = new WalletResponse();
        mockResponse.setError("Insufficient funds");

        Mockito.when(walletService.changeBalance(anyString())).thenReturn(mockResponse);

        WalletResponse result = walletController.changeBalance(requestJson);

        assertEquals("Insufficient funds", result.getError());
    }

    @Test
    public void testChangeBalanceWithdrawNonExistingWallet() {
        String requestJson = "{\"walletId\":\"non_" + walletId + "\",\"operationType\":\"WITHDRAW\",\"amount\":\"50.0\"}";
        WalletResponse mockResponse = new WalletResponse();
        mockResponse.setError("Wallet not found");

        Mockito.when(walletService.changeBalance(anyString())).thenReturn(mockResponse);

        WalletResponse result = walletController.changeBalance(requestJson);

        assertEquals("Wallet not found", result.getError());

    }
}