package com.trader.api.security;

import com.trader.core.utils.EnvironmentData;
import com.trader.core.utils.GsonParserUtils;
import com.trader.core.utils.SecretManagerAWSUtils;

public class Credentials {
    public static final DBConfig DB_CONFIG = initCredentials("OraCredKey", DBConfig.class);
    public static final TraderKeys ENCRYPTION_KEYS = initCredentials("EncryptionKey", TraderKeys.class);

    private static <TType> TType initCredentials(String keyName, Class<TType> clasType)  {
        try {
            String encryption = SecretManagerAWSUtils.getParameter(EnvironmentData.getPropertyValue(keyName));
            return GsonParserUtils.getGson().fromJson(encryption, clasType);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
