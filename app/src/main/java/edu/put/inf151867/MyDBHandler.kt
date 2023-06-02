package edu.put.inf151867

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version:Int) : SQLiteOpenHelper(
    context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        val DATABASE_NAME = "bgcDB.db"
        val TABLE_GAMES = "games"
        val COLUMN_ID = "_id"
        val COLUMN_TYPE = "type"
        val COLUMN_TITLE = "title"
        val COLUMN_YEAR = "publishedYear"
        val COLUMN_BGG_ID = "bggID"
        val COLUMN_THUMBNAIL = "thumbnail"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_GAMES_TABLE = ("CREATE TABLE " + TABLE_GAMES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_YEAR + " INTEGER, " +
                COLUMN_BGG_ID + " INTEGER, " +
                COLUMN_THUMBNAIL + " TEXT" + ")"
                )
        db.execSQL(CREATE_GAMES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES")
        onCreate(db)
    }

    fun addGame(game: Game) {
        val values = ContentValues()
        values.put(COLUMN_TYPE, game.type)
        values.put(COLUMN_TITLE, game.title)
        values.put(COLUMN_YEAR, game.publishedYear)
        values.put(COLUMN_BGG_ID, game.bggID)
        values.put(COLUMN_THUMBNAIL, game.thumbnail)
        val db = this.writableDatabase
        db.insert(TABLE_GAMES, null, values)
        db.close()
    }

    fun findGames(category: String, sort: String?) : MutableList<Game> {
        val query = if (sort != null) {
            "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_TYPE LIKE \"$category\"" +
                    " ORDER BY $sort"
        } else {
            "SELECT * FROM $TABLE_GAMES WHERE $COLUMN_TYPE LIKE \"$category\""
        }
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        val gamesList: MutableList<Game> = mutableListOf()
        while (cursor.moveToNext()) {
            val type = cursor.getString(1)
            val title = cursor.getString(2)
            val year = cursor.getInt(3)
            val bggId = cursor.getInt(4)
            val thumbnail = cursor.getString(5)
            gamesList.add(Game(type,title,year,bggId,thumbnail))
        }
        cursor.close()
        db.close()
        return gamesList
    }

    fun countGames(category: String) : Int {
        val query = "SELECT COUNT(*) FROM $TABLE_GAMES WHERE $COLUMN_TYPE LIKE \"$category\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query,null)
        var number = 0
        if (cursor.moveToFirst()) {
            number = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return number
    }
}