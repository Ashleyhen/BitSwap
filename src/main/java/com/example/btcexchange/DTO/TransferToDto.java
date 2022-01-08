package com.example.btcexchange.DTO;

public record TransferToDto(String fromWallet, String toWallet, Double amount) {
}
