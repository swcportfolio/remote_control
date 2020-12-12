package com.remote.remotecontrol.activity.ble;

import java.io.InputStream;

public interface StreamableDecoder {
    /**
     * Initialises the decoder. This must be called precisely once per decoder.
     * You cannot reuse a decoder.
     * @param is Stream containing input data
     * @throws AudioException If there's any problem
     */
    public void init(InputStream is) throws AudioException;

    /**
     * Retrieves audio data. Blocks until sufficient data is available from the
     * InputStream.
     * @return Decoded data in 16-bit 44.1kHz stereo little-endian, null at EOF
     * @throws AudioException If there's any problem
     */
    public byte[] decode() throws AudioException;
}
