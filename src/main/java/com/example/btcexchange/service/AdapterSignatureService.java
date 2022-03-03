package com.example.btcexchange.service;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.spec.ECPoint;

@Service
public class AdapterSignatureService {

    public void preSign() {

//        NativeSecp256k1.schnorrSign()
//        NativeSecp256k1.privKeyTweakAdd()


        BigInteger generatedPoint = new BigInteger(new SecureRandom().toString());
        BigInteger privateKey = BigInteger.probablePrime(64, new SecureRandom());

        ECPoint publicKey = new ECPoint(generatedPoint, privateKey);


    }

    public void preVerify(String message, String preSignature, String statement) {
    }

    private void adapt(String preSignature, String statement) {
    }

    public void verify(String message, String fullSignature, String statement) {
    }

    public void extract(String preSignature, String signature, String statement) {
    }
}
