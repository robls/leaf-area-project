
package SQLHelper;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Helper extends SQLiteOpenHelper {

    String table = "mydb";

    public Helper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS  "+table+" (name TEXT UNIQUE, t INTEGER, w INTEGER, h INTEGER," +
                " pix BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }

    public void dbput(String name, Mat m) {
        long nbytes = m.total() * m.elemSize();
        m.convertTo(m, CvType.CV_8UC(3));
        byte[] bytes = new byte[ (int)nbytes ];
        m.get(0, 0, bytes);
        dbput(name, m.type(), m.cols(), m.rows(), bytes);
    }

    public void dbput(String name, int t, int w, int h, byte[] bytes) {
        Log.d("dbhelper - ", name + " " + t + " " + w + "x" + h);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("t", t);
        values.put("w", w);
        values.put("h", h);
        values.put("pix", bytes);
        db.insert(table, null, values);
        db.close();
    }

    public Mat dbget(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String [] columns = {"t","w","h","pix"};
        Cursor cursor = db.query(table,columns," name = ?",
                new String[] { name }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        if (cursor != null){
            cursor.moveToFirst();
        }else{
            return null;
        }
        int t = cursor.getInt(0);
        int w = cursor.getInt(1);
        int h = cursor.getInt(2);
        byte[] p = cursor.getBlob(3);
        Mat m = new Mat(h,w,t);
        m.put(0,0, p);
        m.convertTo(m, CvType.CV_64FC1);
        Log.d("dbhelper("+name+")", m.toString());
        return m;
    }

    public boolean isDbEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        long cont = DatabaseUtils.queryNumEntries(db, table);
        Log.d("dbhelper size", String.valueOf(cont));
        if(cont == 0){
            return false;
        }else{
            return true;
        }
    }

    public void limpaTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("delete from "+ table);
    }
};


//Helper sql = new Helper(this,"imgs",null,1);
//Mat m = new Mat(200,400, CvType.CV_8UC3,new Scalar(0,100,0));
//Core.putText(m, "world (~)", new Point(30,80), Core.FONT_HERSHEY_SCRIPT_SIMPLEX, 2.2, new          Scalar(200,200,200));
//sql.dbput("hello",m);
// Mat m = sql.dbget("hello");