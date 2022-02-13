package com.example.btcexchange.DTO;

public record TransferDto(String fromWallet, String toWallet, Double amount) {
}
