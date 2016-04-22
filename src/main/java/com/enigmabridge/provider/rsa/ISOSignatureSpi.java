package com.enigmabridge.provider.rsa;

import com.enigmabridge.provider.rsa.engine.RSABlindedEngine;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.*;
import org.bouncycastle.crypto.signers.ISO9796d2Signer;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;

public class ISOSignatureSpi
    extends SignatureSpi
{
    private ISO9796d2Signer signer;

    protected ISOSignatureSpi(
        Digest digest,
        AsymmetricBlockCipher cipher)
    {
        signer = new ISO9796d2Signer(cipher, digest, true);
    }

    protected void engineInitVerify(
        PublicKey publicKey)
        throws InvalidKeyException
    {
        CipherParameters param = RSAUtil.generatePublicKeyParameter((RSAPublicKey)publicKey);

        signer.init(false, param);
    }

    protected void engineInitSign(
        PrivateKey privateKey)
        throws InvalidKeyException
    {
        CipherParameters param = RSAUtil.generatePrivateKeyParameter((RSAPrivateKey)privateKey);

        signer.init(true, param);
    }

    protected void engineUpdate(
        byte    b)
        throws SignatureException
    {
        signer.update(b);
    }

    protected void engineUpdate(
        byte[]  b,
        int     off,
        int     len) 
        throws SignatureException
    {
        signer.update(b, off, len);
    }

    protected byte[] engineSign()
        throws SignatureException
    {
        try
        {
            byte[]  sig = signer.generateSignature();

            return sig;
        }
        catch (Exception e)
        {
            throw new SignatureException(e.toString());
        }
    }

    protected boolean engineVerify(
        byte[]  sigBytes) 
        throws SignatureException
    {
        boolean yes = signer.verifySignature(sigBytes);

        return yes;
    }

    protected void engineSetParameter(
        AlgorithmParameterSpec params)
    {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated replaced with <a href="#engineSetParameter(java.security.spec.AlgorithmParameterSpec)">engineSetParameter(java.security.spec.AlgorithmParameterSpec)</a>
     */
    protected void engineSetParameter(
        String param,
        Object value)
    {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    /**
     * @deprecated
     */
    protected Object engineGetParameter(
        String param)
    {
        throw new UnsupportedOperationException("engineSetParameter unsupported");
    }

    static public class SHA1WithRSAEncryption
        extends ISOSignatureSpi
    {
        public SHA1WithRSAEncryption()
        {
            super(new SHA1Digest(), new RSABlindedEngine());
        }
    }

    static public class MD5WithRSAEncryption
        extends ISOSignatureSpi
    {
        public MD5WithRSAEncryption()
        {
            super(new MD5Digest(), new RSABlindedEngine());
        }
    }

    static public class RIPEMD160WithRSAEncryption
        extends ISOSignatureSpi
    {
        public RIPEMD160WithRSAEncryption()
        {
            super(new RIPEMD160Digest(), new RSABlindedEngine());
        }
    }

    static public class SHA224WithRSAEncryption
        extends ISOSignatureSpi
    {
        public SHA224WithRSAEncryption()
        {
            super(new SHA224Digest(), new RSABlindedEngine());
        }
    }

    static public class SHA256WithRSAEncryption
        extends ISOSignatureSpi
    {
        public SHA256WithRSAEncryption()
        {
            super(new SHA256Digest(), new RSABlindedEngine());
        }
    }

    static public class SHA384WithRSAEncryption
        extends ISOSignatureSpi
    {
        public SHA384WithRSAEncryption()
        {
            super(new SHA384Digest(), new RSABlindedEngine());
        }
    }

    static public class SHA512WithRSAEncryption
        extends ISOSignatureSpi
    {
        public SHA512WithRSAEncryption()
        {
            super(new SHA512Digest(), new RSABlindedEngine());
        }
    }

    static public class SHA512_224WithRSAEncryption
        extends ISOSignatureSpi
    {
        public SHA512_224WithRSAEncryption()
        {
            super(new SHA512tDigest(224), new RSABlindedEngine());
        }
    }

    static public class SHA512_256WithRSAEncryption
        extends ISOSignatureSpi
    {
        public SHA512_256WithRSAEncryption()
        {
            super(new SHA512tDigest(256), new RSABlindedEngine());
        }
    }

    static public class WhirlpoolWithRSAEncryption
        extends ISOSignatureSpi
    {
        public WhirlpoolWithRSAEncryption()
        {
            super(new WhirlpoolDigest(), new RSABlindedEngine());
        }
    }
}
