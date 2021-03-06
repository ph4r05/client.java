package com.enigmabridge.provider.aes;

/**
 * Created by dusanklinec on 12.07.16.
 */

import com.enigmabridge.provider.EnigmaProvider;
import org.bouncycastle.crypto.engines.RFC3394WrapEngine;

/**
 * an implementation of the AES Key Wrapper from the NIST Key Wrap
 * Specification.
 * <p>
 * For further details see: <a href="http://csrc.nist.gov/encryption/kms/key-wrap.pdf">http://csrc.nist.gov/encryption/kms/key-wrap.pdf</a>.
 */
public class AESWrapEngine
        extends RFC3394WrapEngine
{
    /**
     * Create a regular AESWrapEngine specifying the encrypt for wrapping, decrypt for unwrapping.
     */
    public AESWrapEngine()
    {
        super(new AESEngine());
    }
    /**
     * Create a regular AESWrapEngine specifying the encrypt for wrapping, decrypt for unwrapping.
     */
    public AESWrapEngine(EnigmaProvider provider)
    {
        super(new AESEngine(provider));
    }

    /**
     * Create an AESWrapEngine where the underlying cipher is set to decrypt for wrapping, encrypt for unwrapping.
     *
     * @param useReverseDirection true if underlying cipher should be used in decryption mode, false otherwise.
     */
    public AESWrapEngine(boolean useReverseDirection)
    {
        super(new AESEngine(), useReverseDirection);
    }
}