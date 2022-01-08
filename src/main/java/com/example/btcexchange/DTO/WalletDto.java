package com.example.btcexchange.DTO;

import java.io.Serializable;
import java.util.List;

public record WalletDto(String nameId, String publicKey, List<String> privateKey, String passphrase, long creationTime) implements Serializable {
}