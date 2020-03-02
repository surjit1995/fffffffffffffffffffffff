package yoyo.jassie.labtest2.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.android.gms.maps.model.LatLng
import yoyo.jassie.labtest2.model.Bookmark
import yoyo.jassie.labtest2.repository.BookmarkRepo
import yoyo.jassie.labtest2.util.ImageUtils

class MapsViewModel(application: Application) : AndroidViewModel(application) {

  private val TAG = "MapsViewModel"

  private var bookmarkRepo: BookmarkRepo = BookmarkRepo(
      getApplication())
  private var bookmarks: LiveData<List<BookmarkMarkerView>>?
      = null

  fun addBookmarkFromPlace(bookmark: Bookmark,image: Bitmap?) {

    val newId = bookmarkRepo.addBookmark(bookmark)
      image?.let { bookmark.setImage(it, getApplication()) }
    Log.i(TAG, "New bookmark $newId added to the database.")
  }

  fun getBookmarkMarkerViews() :
      LiveData<List<BookmarkMarkerView>>? {
    if (bookmarks == null) {
      mapBookmarksToMarkerView()
    }
    return bookmarks
  }

  private fun mapBookmarksToMarkerView() {
    bookmarks = Transformations.map(bookmarkRepo.allBookmarks) { repoBookmarks ->
      repoBookmarks.map { bookmark ->
        bookmarkToMarkerView(bookmark)
      }
    }
  }

  private fun bookmarkToMarkerView(bookmark: Bookmark):
      MapsViewModel.BookmarkMarkerView {
    return MapsViewModel.BookmarkMarkerView(
        bookmark.id,
        LatLng(bookmark.latitude, bookmark.longitude),
        bookmark.name,
        bookmark.country,
        bookmark.gender,
        bookmark.birthday)
  }

  data class BookmarkMarkerView(
      var id: Long? = null,
      var location: LatLng = LatLng(0.0, 0.0),
      var name: String = "",
      var country: String = "",
      var gender: String = "",
      var birthday:Long = 0L) {
    fun getImage(context: Context): Bitmap? {
      id?.let {
        return ImageUtils.loadBitmapFromFile(context,
            Bookmark.generateImageFilename(it))
      }
      return null
    }
  }
}
