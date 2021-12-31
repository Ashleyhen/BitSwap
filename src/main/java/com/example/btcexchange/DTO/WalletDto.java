package com.example.btcexchange.DTO;

import java.io.Serializable;

public record WalletDto(String name, String publicKey, String privateKey) implements Serializable { }