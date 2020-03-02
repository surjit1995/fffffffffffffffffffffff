package yoyo.jassie.labtest2.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import yoyo.jassie.labtest2.model.Bookmark


@Database(entities = arrayOf(Bookmark::class), version = 1)
abstract class MapsDatabase : RoomDatabase() {

  abstract fun bookmarkDao(): BookmarkDao

  companion object {

    private var instance: MapsDatabase? = null

    fun getInstance(context: Context): MapsDatabase {
      if (instance == null) {
        instance = Room.databaseBuilder(context.applicationContext,
            MapsDatabase::class.java, "Maps")
            .fallbackToDestructiveMigration()
            .build()
      }
      return instance as MapsDatabase

    }
  }
}
