package com.example.btcexchange.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class WalletDto implements Serializable {
    private String nameId;
    private String publicKey;
    private List<String> privateKey;
    private String passphrase;
    private long creationTime;
}