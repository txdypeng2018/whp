package com.proper.enterprise.isj.support;

public enum VersionEnum {
    
    V1_0_0(VersionEnum.CODE_V1_0_0),
    V3_0_0(VersionEnum.CODE_V1_0_0),
    V3_1_0(VersionEnum.CODE_V1_0_0),
    V3_2_0(VersionEnum.CODE_V1_0_0);
    
    public static final int POS_MASTER = 48;
    public static final int POS_SUB = 32;
    public static final long CODE_MASK_MASTER_VERSION = 0xFFFF000000000000L;
    public static final long CODE_MASK_SUB_VERSION = 0x0000FFFF00000000L;
    public static final long  CODE_MASK_MODIFY_VERSION = 0x00000000FFFFFFFFL;
    public static final long CODE_V1_0_0 = 1L<<POS_MASTER|0L<<POS_SUB|0L;
    public static final long CODE_V3_0_0 = 3L<<POS_MASTER|0L<<POS_SUB|0L;
    public static final long CODE_V3_1_0 = 3L<<POS_MASTER|1L<<POS_SUB|0L;
    public static final long CODE_V3_2_0 = 3L<<POS_MASTER|2L<<POS_SUB|0L;
    
    private long code;
    
    VersionEnum(long code) {
        this.code = code;
    }
    
    public long getCode(){
        return code;
    }
    
    public int getMasterVersion(){
        return (int)((code&CODE_MASK_MASTER_VERSION)>>POS_MASTER);
    }
    public int getSubVersion(){
        return (int)((code&CODE_MASK_SUB_VERSION)>>POS_SUB);
    }
    public int getModifyVersion(){
        return (int)(code&CODE_MASK_MODIFY_VERSION);
    }
    
    public static long currentCode(){
        return CODE_V3_2_0;
    }

}
