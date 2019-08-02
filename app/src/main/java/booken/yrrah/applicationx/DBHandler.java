package booken.yrrah.applicationx;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import models.CategoryModel;
import models.ExpenditureModel;

public class DBHandler extends SQLiteOpenHelper{

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ApplicationXDB";
    // table names
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_EXPENDITURE = "expenditure"; //weak
    // Category Table Column names
    private static final String KEY_NAME = "name"; //key
    private static final String COL_TOTAL_AMOUNT = "totalamount";
    private static final String COL_DATE = "categoryDate";
    // SubAmount Table Column names
    private static final String KEY_ID = "subAmountId"; //key <-- could be the same as KEY_NAME
    private static final String COL_AMOUNT = "amount";
    private static final String COL_EVENT = "event";
    private static final String COL_REFID = "refid";
    private static final String COL_SUB_DATE = "subDate";
    // Trigger
    private static final String TRIGGER_UPDATE_AMOUNT = "updateTotalAmountTrigger";


    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //<editor-fold desc="Category methods">
    // Adding new Category
    public void addCategory(CategoryModel cm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, cm.getName()); // Category Name
        values.put(COL_TOTAL_AMOUNT, cm.getTotalAmount()); // Total Amount
        values.put(COL_DATE, cm.getDate()); // Date
        // Inserting Row
        db.insert(TABLE_CATEGORY, null, values);
        db.close(); // Closing database connection
    }

    // Getting one Category TODO: Restructure this, like SAM, IF I need it...

    /**
     * This method returns a Category specified with 'name' if it exists in the database.
     * @param name The Name of the CategoryModel you want returned. Name is of course unique
     * @return If found return a CategoryModel, if not, return null
     */
    public CategoryModel getCategoryModel(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORY, new String[] { KEY_NAME, COL_TOTAL_AMOUNT },
                KEY_NAME + "=?",
                new String[] { name }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();}

        CategoryModel contact = null;
        if (cursor != null) {
            contact = new CategoryModel(cursor.getString(0), Integer.parseInt(cursor.getString(1)));
        }
        if (cursor != null) {
            cursor.close();
        }
        // Kenny recommended this
        if(db.isOpen()){
            db.close();
        }
        // return Category
        return contact;
    }

    // Getting All Categories
    public List<CategoryModel> getAllCategories() {
        List<CategoryModel> cmList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CategoryModel cm = new CategoryModel();
                cm.setName(cursor.getString(0));
                cm.setTotalAmount(Integer.parseInt(cursor.getString(1)));
                cm.setDate(Integer.parseInt(cursor.getString(2)));
                cmList.add(cm);
            } while (cursor.moveToNext());
        }
        // return contact list
        cursor.close();
        //Kenny recommended this
        if(db.isOpen()){
            db.close();
        }
        return cmList;
    }

    // Getting Categories count, TODO: not sure if I need this... Check! (Only used in testCases for now)

    /**
     * getCategoriesCount()
     * Returns the amount of Categories in the database. NOT the combined values of the Categories.
     * @return int
     */
    public int getCategoriesCount() {
        int count;
        String countQuery = "SELECT * FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();
        //Kenny recommended this
        if(db.isOpen()){
            db.close();
        }
        return count;
    }

    // Updating a Category
    // TODO : What the hell is this updating and how??
    public int updateCategory(CategoryModel cm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TOTAL_AMOUNT, cm.getTotalAmount());
        // updating row
        int returnValue = db.update(TABLE_CATEGORY, values, KEY_NAME + " = ?",
                new String[]{String.valueOf(cm.getName())});
        //Kenny recommended this
        if(db.isOpen()){
            db.close();
        }
        return returnValue;
    }

    // Deleting a Category

    /**
     * deleteCategory()
     * This method deletes a given Category in the database based on it's key name.
     * @param categoryToBeDeleted - String
     * @return True if deletion went through. False if something went wrong
     */
    public boolean deleteCategory(String categoryToBeDeleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deleted = db.delete(TABLE_CATEGORY, KEY_NAME + " = ?", new String[] { categoryToBeDeleted });

        if(db.isOpen()){
            db.close();
        }

        return deleted > 0;
    }

    public int totalAmount(){
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT SUM("+COL_TOTAL_AMOUNT+") AS 'sum' FROM "+TABLE_CATEGORY;
        Cursor c = db.rawQuery(countQuery, null);

        if(c != null){
            c.moveToFirst();
            int totalAmount = c.getInt(c.getColumnIndex("sum"));
            c.close();
            db.close();
            return totalAmount;
        }

        if(db.isOpen()){
            db.close();
        }
        return -1;
    }

    public void deleteAllCategoryData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENDITURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);

        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_NAME + " VARCHAR(50) PRIMARY KEY," + COL_TOTAL_AMOUNT + " INTEGER)";

        String CREATE_SUBAMOUNT_TABLE = "CREATE TABLE " + TABLE_EXPENDITURE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + COL_AMOUNT + " INTEGER,"
                + COL_EVENT + " VARCHAR(50)," + COL_REFID + " VARCHAR(50),"
                + "CONSTRAINT fk FOREIGN KEY(" + COL_REFID
                + ") REFERENCES "+ TABLE_CATEGORY + "(" + KEY_NAME + "))";

        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_SUBAMOUNT_TABLE);
    }
    //</editor-fold>

    //<editor-fold desc="SubAmount methods">
    //=================================================
    // Adding new SubAmount
    public void addExpenditure(ExpenditureModel expModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_AMOUNT, expModel.getAmount());
        values.put(COL_EVENT, expModel.getEvent());
        values.put(COL_REFID, expModel.getRefID());
        values.put(COL_SUB_DATE, expModel.getDate());
        // Inserting Row
        db.insert(TABLE_EXPENDITURE, null, values);
        db.close(); // Closing database connection
    }

    public ExpenditureModel getExpenditureModel(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_EXPENDITURE + " WHERE "
                + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);
        ExpenditureModel expModel = new ExpenditureModel();
        if (c != null) {
            c.moveToFirst();

            expModel.setExpenditureId(c.getInt(c.getColumnIndex(KEY_ID)));
            expModel.setAmount(c.getInt(c.getColumnIndex(COL_AMOUNT)));
            expModel.setEvent(c.getString(c.getColumnIndex(COL_EVENT)));
            expModel.setRefID(c.getString(c.getColumnIndex(COL_REFID)));
            c.close();
        }
        // Kenny recommended this
        if(db.isOpen()){
            db.close();
        }
        return expModel;
    }

    public List<ExpenditureModel> getAllExpAmounts() {
        List<ExpenditureModel> expenditureModelList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENDITURE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ExpenditureModel expModel = new ExpenditureModel();
                expModel.setExpenditureId(c.getInt(c.getColumnIndex(KEY_ID)));
                expModel.setAmount(c.getInt(c.getColumnIndex(COL_AMOUNT)));
                expModel.setEvent(c.getString(c.getColumnIndex(COL_EVENT)));
                expModel.setRefID(c.getString(c.getColumnIndex(COL_REFID)));
                expModel.setDate(c.getLong(c.getColumnIndex(COL_SUB_DATE)));

                expenditureModelList.add(expModel);
            } while (c.moveToNext());
        }

        c.close();
        // Kenny recommended this
        if(db.isOpen()){
            db.close();
        }
        return expenditureModelList;
    }

    public List<ExpenditureModel> getAllExpToCategory(String category) {
        List<ExpenditureModel> expenditureModelList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_EXPENDITURE + " WHERE " +
                TABLE_EXPENDITURE + "." + COL_REFID + " = " + "'" + category + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ExpenditureModel expModel = new ExpenditureModel();
                expModel.setExpenditureId(c.getInt(c.getColumnIndex(KEY_ID)));
                expModel.setAmount(c.getInt(c.getColumnIndex(COL_AMOUNT)));
                expModel.setEvent(c.getString(c.getColumnIndex(COL_EVENT)));
                expModel.setRefID(c.getString(c.getColumnIndex(COL_REFID)));
                expModel.setDate(c.getLong(c.getColumnIndex(COL_SUB_DATE)));

                expenditureModelList.add(expModel);
            } while (c.moveToNext());
        }

        c.close();
        // Kenny recommended this
        if(db.isOpen()){
            db.close();
        }
        return expenditureModelList;
    }

    public int updateExpenditure(ExpenditureModel expModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_EVENT, expModel.getEvent());
        values.put(COL_AMOUNT, expModel.getAmount());

        // updating row
        int returnValue = db.update(TABLE_EXPENDITURE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(expModel.getExpenditureId())});
        // Kenny recommended this
        if(db.isOpen()){
            db.close();
        }
        return returnValue;
    }

    // TODO: Fix this! It doesn't delete properly OR it takes too long time to delete (doubtful), Could maybe have something to do with the fact that SubAmount is a weak entity.
    public void deleteExpenditure(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        /* String sqlCommand = "DELETE FROM " + TABLE_EXPENDITURE + " WHERE " + KEY_ID +
                "=" + id + ";";
        db.execSQL(sqlCommand); */

        Integer idInt = id;
        int deleted = db.delete(TABLE_EXPENDITURE, KEY_ID + " = ?", new String[] {idInt.toString()});
        /*db.delete(TABLE_EXPENDITURE, KEY_ID + " = ?",
                new String[] { String.valueOf(id) }); */
        if(db.isOpen()){
            db.close();
        }
    }

    //</editor-fold>

    //<editor-fold desc="New Field">
    // New Field



    //</editor-fold>

    // CREATE TABLE test (keyName VARCHAR(50), totalAmount INTEGER, timeInfo DATE FORMAT 'YYYY-MM-DD')
    // DATE format seems to have no affect at all. I would need another trigger to format it.. but I
    // would rather do that in Java instead.
    // Will be considering DATE as a normal INT and use that as a 'DATE' instead.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO : Save these strings somewhere else, they are also used in deleteAllCategoryData. Maybe call them from their models??
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_NAME + " VARCHAR(50) PRIMARY KEY," + COL_TOTAL_AMOUNT + " INTEGER,"
                + COL_DATE + " INTEGER)";

        String CREATE_SUBAMOUNT_TABLE = "CREATE TABLE " + TABLE_EXPENDITURE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + COL_AMOUNT + " INTEGER,"
                + COL_EVENT + " VARCHAR(50)," + COL_REFID + " VARCHAR(50),"
                + COL_SUB_DATE + " LONG,"
                + "CONSTRAINT fk FOREIGN KEY(" + COL_REFID
                + ") REFERENCES "+ TABLE_CATEGORY + "(" + KEY_NAME + "))";

        String CREATE_TRIGGER = "CREATE TRIGGER " + TRIGGER_UPDATE_AMOUNT + " AFTER INSERT ON "
                + TABLE_EXPENDITURE + " BEGIN UPDATE " + TABLE_CATEGORY + " SET " + COL_TOTAL_AMOUNT
                + " = (SELECT SUM(" + COL_TOTAL_AMOUNT + ") FROM " + TABLE_CATEGORY + " WHERE " + KEY_NAME
                + " = NEW."+COL_REFID+") + NEW."+COL_AMOUNT + ","
                + COL_DATE + " = (SELECT " + COL_DATE + " FROM " + TABLE_CATEGORY + " WHERE " + KEY_NAME + " = NEW."+COL_REFID+ ") + 1"
                + " WHERE " + KEY_NAME + " = NEW."+COL_REFID+ " AND NEW." + COL_EVENT + " = 'Expenditure Event'" + ";END;";

        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_SUBAMOUNT_TABLE);
        db.execSQL(CREATE_TRIGGER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TRIGGER IF EXISTS " + TRIGGER_UPDATE_AMOUNT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENDITURE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        // Creating tables again
        onCreate(db);
    }

    public void fyllDb(){
        CategoryModel cm = new CategoryModel("TestCat", 0);
        addCategory(cm);
        cm = new CategoryModel("Newtestcat",0);
        addCategory(cm);
        cm = new CategoryModel("Hyra",0);
        addCategory(cm);
        cm = new CategoryModel("Spel",0);
        addCategory(cm);
        cm = new CategoryModel("Mat",0);
        addCategory(cm);
        cm = new CategoryModel("Hygien",0);
        addCategory(cm);
        cm = new CategoryModel("Transport",0);
        addCategory(cm);
        cm = new CategoryModel("Jobb",0);
        addCategory(cm);
        cm = new CategoryModel("Övrigt",0);
        addCategory(cm);

        ExpenditureModel expModel = new ExpenditureModel(123,10337,"Expenditure Event", "TestCat",1564305463126L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(124,1524,"Expenditure Event","Hyra",1564305463126L-2592000000L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(125,165,"Expenditure Event","Spel",1564305463126L-5184000000L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(126,1234,"Expenditure Event","Mat",1564305463126L-5184000000L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(127,1684,"Expenditure Event","Hygien",1564305463126L-5184000000L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(128,1693,"Expenditure Event","Transport",1564305463126L-12960000000L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(129,1774,"Expenditure Event","Jobb",1564305463126L-15552000000L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1210,152,"Expenditure Event","Övrigt",1564305463126L-18144000000L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1211,15,"Expenditure Event","Spel",1564305463126L-20736000000L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1212,1524,"Expenditure Event","Spel",1564305463126L-23328000000L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1213,1567,"Expenditure Event","Spel",1564305463126L-25920000000L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1214,1561,"Expenditure Event","Mat",1564305463126L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1215,1524,"Expenditure Event","Newtestcat",1564305463126L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1216,1500,"Expenditure Event","Spel",1564305463126L);
        addExpenditure(expModel);

        expModel = new ExpenditureModel(1217,152,"Expenditure Event","Övrigt",1565610426455L-18144000000L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1218,15,"Expenditure Event","Spel",1565610426455L-20736000000L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1219,1524,"Expenditure Event","Spel",1565610426455L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1220,1567,"Expenditure Event","Spel",1565610426455L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1221,1561,"Expenditure Event","Mat",1565610426455L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1222,1524,"Expenditure Event","Newtestcat",1565610426455L);
        addExpenditure(expModel);
        expModel = new ExpenditureModel(1216,1500,"Expenditure Event","Jobb",1565610426455L);
        addExpenditure(expModel);
    }
}