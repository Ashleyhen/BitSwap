package com.example.btcexchange.functions;

import com.example.btcexchange.service.transfer.TransferService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Supplier;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class TransactionEndPoint {

    private final static String swaggerTag = "transaction";

    private final TransferService transactionService;

    @GetMapping("transfer")
    @Description("send transaction")
    @Tag(name = swaggerTag)
    public Supplier<String> terminate(
            @RequestParam("send-to-wallet-address") String walletId,
            @RequestParam("amount") Long passphrase,
            @RequestParam(value = "drain-wallet", required = false, defaultValue = "false") Boolean drain) {
        String coins = transactionService.transferTo(walletId, passphrase, drain);
        return () -> coins;
    }
//    tb1qpt0chc26flarszwprsldtz7xx5srt3g4d09dxz
}
