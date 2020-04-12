/*
 * Copyright (c) 2018, Xyneex Technologies. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * You are not meant to edit or modify this source code unless you are
 * authorized to do so.
 *
 * Please contact Xyneex Technologies, #1 Orok Orok Street, Calabar, Nigeria.
 * or visit www.xyneex.com if you need additional information or have any
 * questions.
 */
package com.xyneex.webpush;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

/**
 *
 * @author Jevison7x
 * @since Apr 7, 2020 12:54:12 PM
 */
public class Subscription
{
    private String endpoint;
    private String key;
    private String auth;

    public Subscription()
    {
        // Add BouncyCastle as an algorithm provider
        if(Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null)
            Security.addProvider(new BouncyCastleProvider());
    }

    public void setAuth(String auth)
    {
        this.auth = auth;
    }

    public String getAuth()
    {
        return auth;
    }

    /**
     * Returns the base64 encoded auth string as a byte[]
     *
     * @return
     */
    public byte[] getAuthAsBytes()
    {
        return Base64.getDecoder().decode(getAuth());
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    /**
     * Returns the base64 encoded public key string as a byte[]
     *
     * @return
     */
    public byte[] getKeyAsBytes()
    {
        return Base64.getDecoder().decode(getKey());
    }

    /**
     * Returns the base64 encoded public key as a PublicKey object
     *
     * @return
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.spec.InvalidKeySpecException
     * @throws java.security.NoSuchProviderException
     */
    public PublicKey getUserPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException
    {
        KeyFactory kf = KeyFactory.getInstance("ECDH", BouncyCastleProvider.PROVIDER_NAME);
        ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("secp256r1");
        ECPoint point = ecSpec.getCurve().decodePoint(getKeyAsBytes());
        ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, ecSpec);

        return kf.generatePublic(pubSpec);
    }

    public void setEndpoint(String endpoint)
    {
        this.endpoint = endpoint;
    }

    public String getEndpoint()
    {
        return endpoint;
    }
}
