package com.example.btcexchange.functions;

import com.example.btcexchange.DTO.WalletDto;
import com.example.btcexchange.service.PeerDiscoveryService;
import com.example.btcexchange.service.wallet.IImportExportWallet;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.wallet.Wallet;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RestController
public class BtcWalletEndPoint {
    final static String swaggerTag = "exchange";
    @Qualifier("kit")
    private final IImportExportWallet<WalletAppKit> fromKit;

    @Qualifier("impl")
    private final IImportExportWallet<WalletDto> walletRecordService;

    private final PeerDiscoveryService peerDiscoveryService;
    private final IImportExportWallet<Wallet> walletService;

    @Bean
    @RouterOperation(operation = @Operation(description = "returns of a list of all the current test wallets", operationId = "testEndPoint", tags = "testing-endpoints", responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WalletDto.class))))

    )
    public Supplier<String> getAllWallets() {

        return () -> "hello";
    }


    @GetMapping("createWallet")
    @Tag(name = swaggerTag)
    public Mono<WalletDto> newWallet(@RequestParam("name-id") String nameId, @RequestParam("passphrase") String passphrase) {
        return Mono.create((sink) -> {
            Try<WalletDto> walletValidation = walletRecordService.createWallet(nameId, passphrase);
            walletValidation.onSuccess(sink::success).onFailure(sink::error);
        });
    }

    @GetMapping("import-wallet")
    @Tag(name = swaggerTag)
    public Mono<WalletDto> importWallet(@RequestParam("from-wallet") String walletId, @RequestParam("mnemonic-seed") String mnemonicString, @RequestParam("passphrase") String passphrase) {
        return walletRecordService.importWallet(walletId, mnemonicString, passphrase).fold(Mono::error, Mono::just);
    }

    @GetMapping("extract-wallet")
    @Description("currently passphrase isn't required might be required in the future")
    @Tag(name = swaggerTag)
    public Mono<WalletDto> extractWallet(@RequestParam("from-wallet") String walletId, @RequestParam("passphrase") String passphrase) {
        return walletRecordService.extractWallet(walletId, passphrase).fold(Mono::error, Mono::just);
    }

    @GetMapping("download")
    @Description("just ment to download blockchain")
    @Tag(name = swaggerTag)
    public Mono<WalletDto> download(@RequestParam("from-wallet") String walletId, @RequestParam("passphrase") String passphrase) {
        return walletService.extractWallet(walletId, passphrase).flatMap(wallet -> peerDiscoveryService.addTrackAddress(walletId, wallet)).fold(Mono::error, Mono::just);
    }

    @GetMapping("extract-wallet-from-kit")
    @Description("just ment to download blockchain")
    @Tag(name = swaggerTag)
    public Mono<WalletDto> extractFromKit(@RequestParam("from-wallet") String walletId, @RequestParam("passphrase") String passphrase) {
        return fromKit.extractWallet(walletId, passphrase).map(walletAppKit -> new WalletDto().toWalletDto(walletAppKit.wallet())).fold(Mono::error, Mono::just);
    }


}
