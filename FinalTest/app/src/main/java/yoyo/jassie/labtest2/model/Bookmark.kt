package yoyo.jassie.labtest2.model

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import yoyo.jassie.labtest2.util.ImageUtils


@Entity
data class Bookmark(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var name: String = "",
    var country: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var gender:String = "",
    var birthday:Long = 0L

) {

    fun getImage(context: Context): Bitmap? {
        id?.let {
            return ImageUtils.loadBitmapFromFile(context,
                Bookmark.generateImageFilename(it))
        }
        return null
    }

  fun setImage(image: Bitmap, context: Context) {
    id?.let {
      ImageUtils.saveBitmapToFile(context, image,
          generateImageFilename(it))
    }
  }
  companion object {
    fun generateImageFilename(id: Long): String {
      return "bookmark$id.png"
    }
  }
}
