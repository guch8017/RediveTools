package com.guch8017.redivetools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Query {
    private SQL sql;
    private SQLiteDatabase database;

    public Query(Context mContext){
        sql = new SQL(mContext);
        database = sql.getWritableDatabase();
    }

    @Override
    protected void finalize() throws Throwable {
        database.close();
        sql.close();
        super.finalize();
    }

    /**
     * 插入一条新记录
     * @param log 账号信息
     */
    public void InsertLog(DBAccountData log){
        ContentValues values = new ContentValues();
        values.put("\"M3F1YSNkOnF0\"",log.M3);
        values.put("\"MHx5cg%3D%3D\"",log.MH);
        values.put("\"NnB%2FZDJpMHx5cg%3D%3D\"",log.Nn);
        values.put("\"Description\"",log.description);
        values.put("\"Server\"",log.server);
        database.insert("DATA", null, values);
    }

    /**
     * 查询数据表中的账号数据
     * @return 账号信息列表
     */
    public List<DBAccountData> GetLogs(){
        List<DBAccountData> result = new ArrayList<>();
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM DATA", new String[]{});
            while (cursor.moveToNext()){
                DBAccountData data = new DBAccountData();
                data.M3 = cursor.getString(cursor.getColumnIndex("M3F1YSNkOnF0"));
                data.MH = cursor.getString(cursor.getColumnIndex("MHx5cg%3D%3D"));
                data.Nn = cursor.getString(cursor.getColumnIndex("NnB%2FZDJpMHx5cg%3D%3D"));
                data.description = cursor.getString(cursor.getColumnIndex("Description"));
                data.rowID = cursor.getInt(cursor.getColumnIndex("ID"));
                data.server = cursor.getInt(cursor.getColumnIndex("Server"));
                result.add(data);
            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            return result;
        }
        return result;
    }

    /**
     * 获取指定行号的账号数据
     * @param rowID 行
     * @return 账户数据(若查询失败返回null)
     */
    public DBAccountData GetLog(int rowID){
        DBAccountData data = new DBAccountData();
        try{
            Cursor cursor = database.rawQuery("SELECT * FROM DATA WHERE ID="+rowID, new String[]{});
            if(cursor.moveToNext()){
                data.M3 = cursor.getString(cursor.getColumnIndex("M3F1YSNkOnF0"));
                data.MH = cursor.getString(cursor.getColumnIndex("MHx5cg%3D%3D"));
                data.Nn = cursor.getString(cursor.getColumnIndex("NnB%2FZDJpMHx5cg%3D%3D"));
                data.description = cursor.getString(cursor.getColumnIndex("Description"));
                data.rowID = cursor.getInt(cursor.getColumnIndex("ID"));
                data.server = cursor.getInt(cursor.getColumnIndex("Server"));
                cursor.close();
            }
            else{
                cursor.close();
                return null;
            }
        }catch (Exception e){
            return null;
        }
        return data;
    }

    /**
     * 修改数据表中账号数据
     * 仅修改description，其余不允许修改
     * @param newData 目标数据
     */
    public void EditLog(DBAccountData newData){
        ContentValues values = new ContentValues();
        values.put("Description", newData.description);
        database.update("DATA",values,"ID",new String[]{String.valueOf(newData.rowID)});
    }

    /**
     * 删除一条账号数据
     * @param id 目标行
     */
    public void DeleteLog(int id){
        database.delete("DATA", "ID=?", new String[]{String.valueOf(id)});
    }
}
