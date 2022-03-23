package com.kt.gigagenie.pci.data.pci_db;

/**
 * Created by LeeBaeng on 2018-10-18.
 */

public interface PciDBContext {
    String DATABASE_NAME = "pciapp.db";
    int DATABASE_VERSION = 1;
    String TABLE_NAME = "pciapp_errolog";
    String COLUMN_NO = "no";
    String COLUMN_CATEGORY = "category";
    String COLUMN_ERRCODE = "error_code";
    String COLUMN_ERRCODE_COUNT = "error_code_count";  // 중복에러 처리하기 위한 로직 by dalkommjk | 2021-01-28
    String COLUMN_ERRMSG = "error_message";
    String COLUMN_OCCURTIME = "error_occurtime";
    String QUERY_CREATE_TABLE =
            "create table "+TABLE_NAME+"("
                    +COLUMN_NO+" integer primary key autoincrement, "
                    +COLUMN_CATEGORY+" text not null , "
                    +COLUMN_OCCURTIME+" text not null , "
                    +COLUMN_ERRCODE+" integer not null , "
                    +COLUMN_ERRCODE_COUNT+" integer not null , " // 중복에러 처리하기 위한 로직 by dalkommjk | 2021-01-28
                    +COLUMN_ERRMSG+" text not null );";
}
