package com.trader.api.utils;

public class ConstantesSQL {
    public static final int DB_TIMEOUT = 7;
    public static final String FNSEARCHBANKBYINITKEY = "{? = call  TRADER.PAINFORMATIONUSER.FNSEARCHBANKBYINITKEY(?)}";
    public static final String FNINSERTPERSONALINFO = "{? = call  TRADER.PAINFORMATIONUSER.FNINSERTPERSONALINFO(?, ?, ?, ?, ?, ?, ?)}";
    public static final String FNINSERTPERSONALDOC = "{? = call  TRADER.PAINFORMATIONUSER.FNINSERTPERSONALDOC(?, ?, ?, ?, ?)}";
    public static final String FNVALIDATECERTIFICATE = "{? = call  TRADER.PAINFORMATIONUSER.FNVALIDATECERTIFICATE(?)}";
    public static final String FNINSERTCERTIFICATE = "{? = call  TRADER.PAINFORMATIONUSER.FNINSERTCERTIFICATE(?, ?, ?, ?, ?)}";
    public static final String FNGETTAXDATA = "{? = call  TRADER.PAINFORMATIONUSER.FNGETTAXDATA(?)}";
    public static final String FNGETCERTIFICATE = "{? = call  TRADER.PAINFORMATIONUSER.FNGETCERTIFICATE(?)}";
    public static final String FNVALIDATEUSEREXIST = "{? = call  TRADER.PAUSERS.FNVALIDATEUSEREXIST(?)}";
}