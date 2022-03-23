package com.kt.gigagenie.pci.data.pci_db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.gnifrix.debug.GLog;

import java.util.ArrayList;

import static java.sql.Types.NULL;

/**
 * Error 보고 등을 위해 사용
 * Created by LeeBaeng on 2018-10-18.
 */
public class PciDB implements PciDBContext {
    public SQLiteDatabase sqLiteDatabase = null;
    private DatabaseHelper databaseHelper = null;
    private Context context;


    public PciDB(Context _context){
        context = _context;
    }

    public void openLocalDB() throws SQLException{
        synchronized (this){
            if(databaseHelper == null){
                databaseHelper = new DatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
            }
            sqLiteDatabase = databaseHelper.getWritableDatabase();
        }
    }

    public boolean isOpened(){
        synchronized(this){
            if(sqLiteDatabase != null){
                return sqLiteDatabase.isOpen();
            }else return false;
        }
    }

    public void closeLocalDB(){
        synchronized (this){
            if(sqLiteDatabase != null){
                sqLiteDatabase.close();
            }
        }
    }


    /**
     * Local DB에 ErrorInfo를 삽입한다.
     */
    public boolean insertNewErrorInfo(ErrorInfo errorInfo){

        synchronized(this){
            if(!isOpened()) openLocalDB();
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY, errorInfo.getCategory());
            values.put(COLUMN_ERRCODE, errorInfo.getErrorCode());
            values.put(COLUMN_ERRCODE_COUNT, errorInfo.getErrorCodeCount());
            values.put(COLUMN_ERRMSG, errorInfo.getErrorMsg());
            values.put(COLUMN_OCCURTIME, errorInfo.getErrorOccurTimeStr());
            long stbDbId = sqLiteDatabase.insert(TABLE_NAME, null, values);
            closeLocalDB();
            GLog.printInfo("PciDB","Insert ErrorInfo to PciDB Complete ! errCount : " + getErrorInfoCountOnDB() + "(errMsg : " + errorInfo.getErrorMsg() + " # errTime : " + errorInfo.getErrorOccurTimeStr() + ")");
            if(stbDbId != -1) {
                errorInfo.setDbId(stbDbId);
                return true;
            }
            return false;
        }
    }

    /**
     * Local DB에 삽입되어 있는 ErrorInfo의 정보를 Update한다.
     * @param id 변경할 ErrorInfo의 DBID
     * @param category null이 아닐 경우 update
     * @param err_code null이 아닐 경우 update
     * @param err_msg null이 아닐 경우 update
     * @param occurTime null이 아닐 경우 update
     */
    public boolean updateColumn(long id , String category, int err_code, String err_msg, String occurTime){
        synchronized (this){
            if(!isOpened()) openLocalDB();
            ContentValues values = new ContentValues();
            values.put(COLUMN_CATEGORY, category);
            values.put(COLUMN_ERRCODE, err_code);
            values.put(COLUMN_ERRMSG, err_msg);
            values.put(COLUMN_OCCURTIME, occurTime);
            boolean rst = sqLiteDatabase.update(TABLE_NAME, values, COLUMN_NO + "=" + id, null) > 0;
            closeLocalDB();
            return rst;
        }
    }


    /**
     * Local DB에 삽입되어 있는 ErrorInfo의 정보를 Update한다.
     * @param errorInfo 업데이트할 errorInfo Object(포함된 모든정보 Update / errorInfo Object내 DbId가 설정되어 있지 않은 경우 업데이트 불가)
     */
    public boolean updateColumn(ErrorInfo errorInfo){
        synchronized(this){
            if(errorInfo.getDbId() != -1){
                if(!isOpened()) openLocalDB();
                ContentValues values = new ContentValues();
                values.put(COLUMN_CATEGORY, errorInfo.getCategory());
                values.put(COLUMN_ERRCODE, errorInfo.getErrorCode());
                values.put(COLUMN_ERRMSG, errorInfo.getErrorMsg());
                values.put(COLUMN_OCCURTIME, errorInfo.getErrorOccurTimeStr());
                boolean rst = sqLiteDatabase.update(TABLE_NAME, values, COLUMN_NO + "=" + errorInfo.getDbId(), null) > 0;
                closeLocalDB();
                return rst;
            }
            return false;
        }
    }


    /**
     * Local DB에 저장된 특정 ID를 가진 ErrorInfo를 삭제한다.
     */
    public boolean deleteErrorInfo(long id){
        synchronized(this){
            if(!isOpened()) openLocalDB();
            boolean rst = sqLiteDatabase.delete(TABLE_NAME, COLUMN_NO + "=" + id, null) > 0;
            closeLocalDB();
            return rst;
        }
    }


    /**
     * Local DB에 저장된 특정 ID를 가진 ErrorInfo를 삭제한다.(param ErrorInfo의 ID 참조, ID가 -1일 경우 삭제 불가)
     */
    public boolean deleteErrorInfo(ErrorInfo errorInfo){
        synchronized (this){
            if(errorInfo.getDbId() != -1){
                if(!isOpened()) openLocalDB();
                boolean rst= sqLiteDatabase.delete(TABLE_NAME, COLUMN_NO + "=" + errorInfo.getDbId(), null) > 0;
                closeLocalDB();
                return rst;
            }
            return false;
        }
    }


    /**
     * Local DB에 저장된 모든 에러정보를 삭제 한다.
     */
    public void clearErrorInfo(){
        synchronized (this){
            if(!isOpened()) openLocalDB();
            sqLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
            sqLiteDatabase.execSQL(QUERY_CREATE_TABLE);
            closeLocalDB();
            GLog.printInfo(this, "clearErrorInfo is complete ! raw Count=" + getErrorInfoCountOnDB());
        }
    }


    /**
     * Local DB에 저장된 모든 ErrorInfo를 List로 반환한다.
     */
    public ArrayList<ErrorInfo> getErrorListFromPciLocalDB(){
        synchronized(this){
            if(!isOpened()) openLocalDB();
            Cursor c = sqLiteDatabase.query(TABLE_NAME, null, null, null, null, null, null);
            if(c != null && c.getCount() > 0){
                ArrayList<ErrorInfo> errorInfoList = new ArrayList<>();
                if(c.moveToFirst()){
                    while(!c.isAfterLast()){
                        ErrorInfo errorInfo = new ErrorInfo(
                                c.getInt(c.getColumnIndex(COLUMN_ERRCODE)),
                                c.getInt(c.getColumnIndex(COLUMN_ERRCODE_COUNT)),
                                c.getString(c.getColumnIndex(COLUMN_ERRMSG)),
                                c.getString(c.getColumnIndex(COLUMN_CATEGORY)),
                                c.getString(c.getColumnIndex(COLUMN_OCCURTIME)));
                        errorInfoList.add(errorInfo);
                        c.moveToNext();
                    }
                }

                c.close();
                if(!isOpened()) openLocalDB();
                return errorInfoList;
            }
            closeLocalDB();
            return null;
        }
    }


    /**
     * Local DB에 저장된 특정 ID를 가진 ErrorInfo를 반환한다.<br>
     */
    public ErrorInfo getErrorInfo(long id){
        synchronized(this){
            if(!isOpened()) openLocalDB();
            Cursor c = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME + "where "+ COLUMN_NO + "="+id, null);
            if(c != null && c.getCount() > 0){
                c.moveToFirst();
                ErrorInfo ei = new ErrorInfo(
                        c.getInt(c.getColumnIndex(COLUMN_ERRCODE)),
                        c.getInt(c.getColumnIndex(COLUMN_ERRCODE_COUNT)),
                        c.getString(c.getColumnIndex(COLUMN_ERRMSG)),
                        c.getString(c.getColumnIndex(COLUMN_CATEGORY)),
                        c.getString(c.getColumnIndex(COLUMN_OCCURTIME)));

                c.close();
                closeLocalDB();
                return ei;
            }
            closeLocalDB();
            return null;
        }
    }

    /**
     * Local DB내 저장된 ErrorInfo 개수를 반환한다.
     */
    public int getErrorInfoCountOnDB(){
        synchronized(this){
            if(!isOpened()) openLocalDB();
            Cursor c = sqLiteDatabase.rawQuery("select count(*) from " + TABLE_NAME, null);
            c.moveToFirst();
            int count = c.getInt(0);
            c.close();
            closeLocalDB();
            return count;
        }
    }
    /** ↓↓↓↓↓ 동일에러 적체방지를 위해 조회함수 생성 - by dalkommJK | 2021-01-28 ↓↓↓↓↓ **/
    public int getErrorCountOnDB(int error_code){
        synchronized(this){
            if(!isOpened()) {
                openLocalDB();
            }
            try {
                Cursor error_c = sqLiteDatabase.rawQuery("select * from " + TABLE_NAME , null);
                error_c.moveToFirst();
                String err_count_colum = error_c.getColumnName(4);
                //Log.e("pci", " " + error_c.getColumnName(0) + " "+ error_c.getColumnName(1)+ " "+ error_c.getColumnName(2)+ " "+ error_c.getColumnName(3)+ " "+ error_c.getColumnName(4));
                if(!err_count_colum.equals(COLUMN_ERRCODE_COUNT)){
                    sqLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
                    sqLiteDatabase.execSQL(QUERY_CREATE_TABLE);
                    //Log.e("pci", "컬럼이 없어 table 새로생성 : " + err_count_colum);
                }

                //Log.e("pci", " " + error_c.getInt(3) + " "+ error_c.getInt(4));
                error_c.close();

                Cursor error_c1 = sqLiteDatabase.rawQuery("select *  from " + TABLE_NAME + " where " + COLUMN_ERRCODE + "= \'" + error_code + "\' ", null);
                error_c1.moveToFirst();
                //Log.e("pci", " " + error_c1.getColumnName(0) );
                int error_count = error_c1.getInt(4);
                error_c1.close();
                //if(error_count == NULL) error_count = 0;
                closeLocalDB();
                return error_count;
            }catch (Exception e){

                sqLiteDatabase.execSQL("DROP TABLE " + TABLE_NAME);
                sqLiteDatabase.execSQL(QUERY_CREATE_TABLE);
                closeLocalDB();
            }
        }
        return 0;
    }

    public boolean updateErrorCount(ErrorInfo errorInfo) {
        synchronized (this) {
            if (!isOpened()) openLocalDB();
            ContentValues values = new ContentValues();
            values.put(COLUMN_ERRCODE_COUNT, errorInfo.getErrorCodeCount());
            values.put(COLUMN_OCCURTIME, errorInfo.getErrorOccurTimeStr());
            //Log.e("pci", "업데이트 --> " +  errorInfo.getErrorCodeCount() + " 업데이트카운트 --> " + errorInfo.getErrorOccurTimeStr() );

            boolean rst = sqLiteDatabase.update(TABLE_NAME, values, COLUMN_ERRCODE + "= \"" + errorInfo.errorCode + "\"", null) > 0;
            //Log.e("pci", "업데이트결과 --> " + "rst" + " 에러코드 : " + errorInfo.errorCode + "time: " + errorInfo.getErrorOccurTimeStr() + " " + TABLE_NAME );
            closeLocalDB();
            return rst;
        }
    }


    /** ↑↑↑↑↑ 동일에러 적체방지를 위해 조회함수 생성 - by dalkommJK | 2021-01-28 ↑↑↑↑↑ **/

    public void destory(){
        synchronized(this){
            closeLocalDB();
            if(databaseHelper != null) databaseHelper.close();
            databaseHelper = null;
            sqLiteDatabase = null;
        }
    }


    private class DatabaseHelper extends SQLiteOpenHelper{
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(QUERY_CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
