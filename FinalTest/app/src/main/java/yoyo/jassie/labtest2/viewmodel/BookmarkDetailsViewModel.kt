package yoyo.jassie.labtest2.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import yoyo.jassie.labtest2.model.Bookmark
import yoyo.jassie.labtest2.repository.BookmarkRepo
import yoyo.jassie.labtest2.util.ImageUtils


class BookmarkDetailsViewModel(application: Application) :
    AndroidViewModel(application) {

  private var bookmarkRepo: BookmarkRepo =
      BookmarkRepo(getApplication())
  private var bookmarkDetailsView: LiveData<BookmarkDetailsView>? = null

  fun getBookmark(bookmarkId: Long): LiveData<BookmarkDetailsView>? {
    if (bookmarkDetailsView == null) {
      mapBookmarkToBookmarkView(bookmarkId)
    }
    return bookmarkDetailsView
  }

  fun updateBookmark(bookmarkDetailsView: BookmarkDetailsView) {

    GlobalScope.launch {
      val bookmark = bookmarkViewToBookmark(bookmarkDetailsView)
      bookmark?.let { bookmarkRepo.updateBookmark(it) }
    }
  }

  fun deleteBookmark(bookmarkDetailsView: BookmarkDetailsView) {

    GlobalScope.launch {
      val bookmark = bookmarkViewToBookmark(bookmarkDetailsView)
      bookmark?.let { bookmarkRepo.deleteBookmark(it) }
    }
  }

  private fun bookmarkViewToBookmark(bookmarkDetailsView: BookmarkDetailsView):
      Bookmark? {
    val bookmark = bookmarkDetailsView.id?.let {
      bookmarkRepo.getBookmark(it)
    }
    if (bookmark != null) {
     // bookmark.
      bookmark.id = bookmarkDetailsView.id
      bookmark.name = bookmarkDetailsView.name
      bookmark.country = bookmarkDetailsView.country
      bookmark.latitude = bookmarkDetailsView.latitude
      bookmark.longitude = bookmarkDetailsView.longitude
      bookmark.gender = bookmarkDetailsView.gender
      bookmark.birthday = bookmarkDetailsView.birthday
    }
    return bookmark
  }

  private fun mapBookmarkToBookmarkView(bookmarkId: Long) {

    val bookmark = bookmarkRepo.getLiveBookmark(bookmarkId)

      bookmarkDetailsView = Transformations.map(bookmark) { repoBookmark -> bookmarkToBookmarkView(repoBookmark)
    }


  }

  private fun bookmarkToBookmarkView(bookmark: Bookmark): BookmarkDetailsView {
    return BookmarkDetailsView(
        bookmark.id,
        bookmark.name,
        bookmark.country,
        bookmark.latitude,
        bookmark.longitude,
        bookmark.gender,
        bookmark.birthday
    )
  }

  data class BookmarkDetailsView(
          var id: Long? = null,
          var name: String = "",
          var country: String = "",
          var latitude: Double = 0.0,
          var longitude: Double =0.0,
          var gender:String = "",
          var birthday:Long = 0L
  )

  {
      fun getImage(context: Context): Bitmap? {
          id?.let {
              return ImageUtils.loadBitmapFromFile(context,
                  Bookmark.generateImageFilename(it))
          }
          return null
      }

      fun setImage(context: Context, image: Bitmap) {
          id?.let {
              ImageUtils.saveBitmapToFile(context, image,
                  Bookmark.generateImageFilename(it))
          }
      }
  }

}
