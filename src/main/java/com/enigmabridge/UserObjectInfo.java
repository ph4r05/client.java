package com.enigmabridge;

import java.io.Serializable;

/**
 * API for UserObject (UO) Info.
 * Provides information to make use of EB user objects.
 *
 * Created by dusanklinec on 26.04.16.
 */
public interface UserObjectInfo extends Serializable {
    /**
     * Returns User object handle / ID.
     * @return UO ID
     */
     long getUoid();

    /**
     * Returns user object type.
     * Required for API Token generation.
     * @return UO type
     */
    int getUserObjectType();

     /**
      * Communication encryption&MAC keys.
      * UO-specific. Strictly required for UO use.
      * @return EBCommKeys
      */
     EBCommKeys getCommKeys();

    /**
     * Returns API key to EB API access.
     * API key can be shared among several UOs.
     * Not UO specific.
     * @return API key
     */
     String getApiKey();

    /**
     * EB Endpoint identification.
     * Not UO specific.
     * @return endpoint info
     */
    EBEndpointInfo getEndpointInfo();
}
