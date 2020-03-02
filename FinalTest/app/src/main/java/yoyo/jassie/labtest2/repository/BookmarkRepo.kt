package yoyo.jassie.labtest2.repository

import android.content.Context
import androidx.lifecycle.LiveData
import yoyo.jassie.labtest2.db.BookmarkDao
import yoyo.jassie.labtest2.db.MapsDatabase
import yoyo.jassie.labtest2.model.Bookmark

class BookmarkRepo(private val context: Context) {

  private var db: MapsDatabase = MapsDatabase.getInstance(context)
  private var bookmarkDao: BookmarkDao = db.bookmarkDao()

  fun updateBookmark(bookmark: Bookmark) {
    bookmarkDao.updateBookmark(bookmark)
  }

  fun deleteBookmark(bookmark: Bookmark) {
    bookmarkDao.deleteBookmark(bookmark)
  }

  fun getBookmark(bookmarkId: Long): Bookmark {
    return bookmarkDao.loadBookmark(bookmarkId)
  }

  fun addBookmark(bookmark: Bookmark): Long? {
    val newId = bookmarkDao.insertBookmark(bookmark)
    bookmark.id = newId
    return newId
  }

  fun createBookmark(): Bookmark {
    return Bookmark()
  }

  fun getLiveBookmark(bookmarkId: Long): LiveData<Bookmark> {
    val bookmark = bookmarkDao.loadLiveBookmark(bookmarkId)
    return bookmark
  }

  val allBookmarks: LiveData<List<Bookmark>>
    get() {
      return bookmarkDao.loadAll()
    }
}
