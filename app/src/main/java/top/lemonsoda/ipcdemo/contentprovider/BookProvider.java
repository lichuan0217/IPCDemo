package top.lemonsoda.ipcdemo.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class BookProvider extends ContentProvider {
    private final static String TAG = BookProvider.class.getCanonicalName();

    public static final String AUTHONRITY = "top.lemonsoda.ipcdemo.book.provider";
    public static final Uri BOOK_CONTENT_URI = Uri.parse("content://" + AUTHONRITY + "/book");
    public static final Uri USER_CONTENT_URI = Uri.parse("content://" + AUTHONRITY + "/user");
    public static final int BOOK_URI_CODE = 0;
    public static final int USER_URI_CODE = 1;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private Context context;
    private SQLiteDatabase db;

    static {
        uriMatcher.addURI(AUTHONRITY, "book", BOOK_URI_CODE);
        uriMatcher.addURI(AUTHONRITY, "user", USER_URI_CODE);
    }

    public BookProvider() {
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate, current thread: " + Thread.currentThread().getName());
        context = getContext();
        initProviderData();
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete, current thread: " + Thread.currentThread().getName());
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int count = db.delete(table, selection, selectionArgs);
        if (count > 0) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {
        Log.d(TAG, "getType, current thread: " + Thread.currentThread().getName());
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert, current thread: " + Thread.currentThread().getName());
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        db.insert(table, null, values);
        context.getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query, current thread: " + Thread.currentThread().getName());
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        return db.query(table, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.d(TAG, "update, current thread: " + Thread.currentThread().getName());
        String table = getTableName(uri);
        if (table == null) {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        int row = db.update(table, values, selection, selectionArgs);
        if (row > 0) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return row;
    }

    private void initProviderData() {
        db = new DbOpenHelper(context).getWritableDatabase();
        db.execSQL("delete from " + DbOpenHelper.BOOK_TABLE_NAME);
        db.execSQL("delete from " + DbOpenHelper.USER_TABLE_NAME);
        db.execSQL("insert into book values(3, 'Android');");
        db.execSQL("insert into book values(4, 'IOS');");
        db.execSQL("insert into book values(5, 'Python');");
        db.execSQL("insert into user values(1, 'Jake', 1);");
        db.execSQL("insert into user values(2, 'Jasmine', 0);");
    }

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (uriMatcher.match(uri)) {
            case BOOK_URI_CODE:
                tableName = DbOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = DbOpenHelper.USER_TABLE_NAME;
                break;
            default:
                break;
        }
        return tableName;
    }
}
