package com.remote.remotecontrol.activity.ble;

public class AudioException extends  Exception {
    /**
     * @param cause Underlying exception
     */
    public AudioException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message Informational message
     */
    public AudioException(String message)
    {
        super(message);
    }
}
