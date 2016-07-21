package com.enigmabridge.comm.retry;

/**
 * Simple callback interface for job to signalize its success or fail.
 *
 * Created by dusanklinec on 21.07.16.
 */
public interface EBCallback<Result, Error> {
    /**
     * Called by job on success.
     * @param result
     */
    void onSuccess(Result result);

    /**
     * Called by job on fail.
     * @param error
     */
    void onFail(Error error);
}
