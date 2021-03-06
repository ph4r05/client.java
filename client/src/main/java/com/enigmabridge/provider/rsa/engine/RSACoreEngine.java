package com.enigmabridge.provider.rsa.engine;

import com.enigmabridge.EBCryptoException;
import com.enigmabridge.EBDevSettings;
import com.enigmabridge.comm.EBCommStatus;
import com.enigmabridge.comm.EBCorruptedException;
import com.enigmabridge.comm.EBProcessDataCall;
import com.enigmabridge.comm.EBProcessDataResponse;
import com.enigmabridge.create.EBUOHandle;
import com.enigmabridge.provider.parameters.EBRSAKeyParameter;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;

/**
 * this does your basic RSA algorithm.
 */
class RSACoreEngine
{
    private static final Logger LOG = LoggerFactory.getLogger(RSACoreEngine.class);

    private EBRSAKeyParameter key;
    private boolean forEncryption;

    /**
     * initialise the RSA engine.
     *
     * @param forEncryption true if we are encrypting, false otherwise.
     * @param param the necessary RSA key parameters.
     */
    public void init(
        boolean          forEncryption,
        CipherParameters param)
    {
        if (param instanceof ParametersWithRandom) {
            ParametersWithRandom rParam = (ParametersWithRandom) param;
            param = rParam.getParameters();
        }

        key = (EBRSAKeyParameter)param;
        this.forEncryption = forEncryption;
    }

    /**
     * Return the maximum size for an input block to this engine.
     * For RSA this is always one byte less than the key size on
     * encryption, and the same length as the key size on decryption.
     *
     * @return maximum size for an input block.
     */
    public int getInputBlockSize()
    {
        int     bitSize = key.length();

        if (forEncryption)
        {
            return (bitSize + 7) / 8 - 1;
        }
        else
        {
            return (bitSize + 7) / 8;
        }
    }

    /**
     * Return the maximum size for an output block to this engine.
     * For RSA this is always one byte less than the key size on
     * decryption, and the same length as the key size on encryption.
     *
     * @return maximum size for an output block.
     */
    public int getOutputBlockSize()
    {
        int     bitSize = key.length();

        if (forEncryption)
        {
            return (bitSize + 7) / 8;
        }
        else
        {
            return (bitSize + 7) / 8 - 1;
        }
    }

    public BigInteger convertInput(
        byte[]  in,
        int     inOff,
        int     inLen)
    {
        if (inLen > (getInputBlockSize() + 1))
        {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        else if (inLen == (getInputBlockSize() + 1) && !forEncryption)
        {
            throw new DataLengthException("input too large for RSA cipher.");
        }

        byte[]  block;

        if (inOff != 0 || inLen != in.length)
        {
            block = new byte[inLen];

            System.arraycopy(in, inOff, block, 0, inLen);
        }
        else
        {
            block = in;
        }

        BigInteger res = new BigInteger(1, block);
        if (
                    (key.getModulus() != null && res.compareTo(key.getModulus()) >= 0)
                ||  (key.getModulus() == null && res.bitLength() >= key.length())
           )
        {
            throw new DataLengthException("input too large for RSA cipher.");
        }

        return res;
    }

    public byte[] convertOutput(
        BigInteger result)
    {
        byte[]      output = result.toByteArray();

        if (forEncryption)
        {
            if (output[0] == 0 && output.length > getOutputBlockSize())        // have ended up with an extra zero byte, copy down.
            {
                byte[]  tmp = new byte[output.length - 1];

                System.arraycopy(output, 1, tmp, 0, tmp.length);

                return tmp;
            }

            if (output.length < getOutputBlockSize())     // have ended up with less bytes than normal, lengthen
            {
                byte[]  tmp = new byte[getOutputBlockSize()];

                System.arraycopy(output, 0, tmp, tmp.length - output.length, output.length);

                return tmp;
            }
        }
        else
        {
            if (output[0] == 0)        // have ended up with an extra zero byte, copy down.
            {
                byte[]  tmp = new byte[output.length - 1];

                System.arraycopy(output, 1, tmp, 0, tmp.length);

                return tmp;
            }
        }

        return output;
    }

    public byte[] convertOutputForEB(
        BigInteger result)
    {
        byte[]      output = result.toByteArray();

        if (forEncryption)
        {
            if (output[0] == 0 && output.length > getOutputBlockSize())        // have ended up with an extra zero byte, copy down.
            {
                byte[]  tmp = new byte[output.length - 1];

                System.arraycopy(output, 1, tmp, 0, tmp.length);

                return tmp;
            }

            if (output.length < getOutputBlockSize())     // have ended up with less bytes than normal, lengthen
            {
                byte[]  tmp = new byte[getOutputBlockSize()];

                System.arraycopy(output, 0, tmp, tmp.length - output.length, output.length);

                return tmp;
            }
        }
        else
        {
            // EB stuff - keep it N bits all the time.
            if (output[0] == 0 && output.length > getOutputBlockSize() + 1)        // have ended up with an extra zero byte, copy down.
            {
                byte[]  tmp = new byte[output.length - 1];

                System.arraycopy(output, 1, tmp, 0, tmp.length);

                return tmp;
            }

            if (output.length < getOutputBlockSize() + 1)     // have ended up with less bytes than normal, lengthen
            {
                byte[]  tmp = new byte[getOutputBlockSize() + 1];

                System.arraycopy(output, 0, tmp, tmp.length - output.length, output.length);

                return tmp;
            }
        }

        return output;
    }

    public BigInteger processBlock(BigInteger input)
    {
        final EBProcessDataCall call = new EBProcessDataCall.Builder()
                .setKey(key)
                .build();

        byte[] inputBytes = null;
        EBProcessDataResponse response = null;

        try {
            inputBytes = convertOutputForEB(input);
            response = call.doRequest(inputBytes);

            if (!response.isCodeOk()){
                throw new EBCryptoException("Server returned invalid response");
            }

            final byte[] respData = response.getProtectedData();
            return convertInput(respData, 0, respData.length);

        } catch (IOException e) {
            test6f00(call, inputBytes);
            throw new EBCryptoException("ProcessData failed for: " + new EBUOHandle(key.getUserObjectInfo()), e);
        } catch (EBCorruptedException e) {
            test6f00(call, inputBytes);
            throw new EBCryptoException("ProcessData failed for: " + new EBUOHandle(key.getUserObjectInfo()), e);
        }
    }

    private void test6f00(EBProcessDataCall call, byte[] inputBytes){
        EBProcessDataResponse response = call.getPdResponse();
        if (EBDevSettings.shouldLog6f00RequestResponse()
                && response != null
                && response.getStatusCode() == EBCommStatus.ERROR_CLASS_ERR_CHECK_ERRORS_6f){

            // Logging 6f00 errors to detect possible crypto errors / incompatibility.
            LOG.debug(String.format("RSA 0x6f00 error. encryption: %s, input size: %d, leading zero: %s \n" +
                            "  request [%s]\n" +
                            "  response [%s]",
                    forEncryption,
                    inputBytes.length,
                    inputBytes[0] == 0,
                    call.getRawRequest(),
                    call.getRawResponse()));
        }
    }
}
