package yoyo.jassie.labtest2

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_details.*
import yoyo.jassie.finaltest.R
import yoyo.jassie.finaltest.ui.maps.MapsFragment
import yoyo.jassie.labtest2.repository.BookmarkRepo
import yoyo.jassie.labtest2.util.ImageUtils
import yoyo.jassie.labtest2.viewmodel.BookmarkDetailsViewModel
import yoyo.jassie.labtest2.viewmodel.MapsViewModel
import yoyo.jassie.labtest2.viewmodel.PhotoOptionDialogFragment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class DetailsActivity : AppCompatActivity(),
    PhotoOptionDialogFragment.PhotoOptionDialogListener {

    var isNew = true
    var bookid = 0L

    private var bookmarkRepo: BookmarkRepo = BookmarkRepo(this)
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var bookmarkDetailsViewModel:
            BookmarkDetailsViewModel
    private var bookmarkDetailsView:
            BookmarkDetailsViewModel.BookmarkDetailsView? = null

    private var photoFile: File? = null

    var spinner1:Spinner? = null
    var spinner2:Spinner? = null

    var myCalendar: Calendar = Calendar.getInstance()
    var editTextDate: EditText? = null

    override fun onCaptureClick() {

        photoFile = null
        try {

            photoFile = ImageUtils.createUniqueImageFile(this)

        } catch (ex: java.io.IOException) {
            return
        }

        photoFile?.let { photoFile ->

            val photoUri = FileProvider.getUriForFile(this,
                "yoyo.jassie.finaltest.fileprovider",
                photoFile)

            val captureIntent =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            captureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
                photoUri)

            val intentActivities = packageManager.queryIntentActivities(
                captureIntent, PackageManager.MATCH_DEFAULT_ONLY)
            intentActivities.map { it.activityInfo.packageName }
                .forEach { grantUriPermission(it, photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION) }

            startActivityForResult(captureIntent, REQUEST_CAPTURE_IMAGE)

        }

    }

    override fun onPickClick() {
        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickIntent, REQUEST_GALLERY_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,
                                  data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == android.app.Activity.RESULT_OK) {

            when (requestCode) {

                REQUEST_CAPTURE_IMAGE -> {

                    val photoFile = photoFile ?: return

                    val uri = FileProvider.getUriForFile(this,
                        "yoyo.jassie.finaltest.fileprovider",
                        photoFile)
                    revokeUriPermission(uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

                    val image = getImageWithPath(photoFile.absolutePath)
                    image?.let { updateImage(it) }
                }

                REQUEST_GALLERY_IMAGE -> if (data != null && data.data != null) {
                    val imageUri = data.data
                    val image = getImageWithAuthority(imageUri!!)
                    image?.let { updateImage(it) }
                }
            }
        }
    }

    private fun getImageWithAuthority(uri: Uri): Bitmap? {
        return ImageUtils.decodeUriStreamToSize(uri,
            resources.getDimensionPixelSize(
                yoyo.jassie.finaltest.R.dimen.default_image_width),
            resources.getDimensionPixelSize(
                yoyo.jassie.finaltest.R.dimen.default_image_height),
            this)
    }

    private fun updateImage(image: Bitmap) {
        if(!isNew){
            val bookmarkView = bookmarkDetailsView ?: return
            imageViewPlace.setImageBitmap(image)
            bookmarkView.setImage(this, image)
        }else{
            imageViewPlace.setImageBitmap(image)
        }
    }

    private fun getImageWithPath(filePath: String): Bitmap? {
        return ImageUtils.decodeFileToSize(filePath,
            resources.getDimensionPixelSize(
                yoyo.jassie.finaltest.R.dimen.default_image_width),
            resources.getDimensionPixelSize(
                yoyo.jassie.finaltest.R.dimen.default_image_height))
    }

    private fun replaceImage() {
        val newFragment = PhotoOptionDialogFragment.newInstance(this)
        newFragment?.show(supportFragmentManager, "photoOptionDialog")
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(yoyo.jassie.finaltest.R.layout.activity_details)
        setupViewModel()
        getIntentData()

        imageViewPlace.setOnClickListener {
            replaceImage()
        }

        spinner1 = findViewById(yoyo.jassie.finaltest.R.id.spinnerCountry) as Spinner
        spinner1?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinner1!!.setSelection(position)
            }
        }

        spinner2 = findViewById(yoyo.jassie.finaltest.R.id.spinnerGender) as Spinner
        spinner2?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                spinner2!!.setSelection(position)
            }
        }

        editTextDate = findViewById(R.id.editTextBirthday)

        // set default date
        updateLabel()

        val date = object : DatePickerDialog.OnDateSetListener {

            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateLabel()
            }
        }

        editTextDate!!.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v:View) {
                // TODO Auto-generated method stub
                DatePickerDialog(this@DetailsActivity, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

    }

    private fun updateLabel() {
        val myFormat = "dd/MMM/yyyy" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        editTextDate?.setText(sdf.format(myCalendar.getTime()))
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu):
            Boolean {
        val inflater = menuInflater
        inflater.inflate(yoyo.jassie.finaltest.R.menu.menu_update_bookmark, menu)
        var itemToHide = menu.findItem(yoyo.jassie.finaltest.R.id.action_delete)
        if (isNew) {
            itemToHide.setVisible(false)
        } else {
            itemToHide.setVisible(true)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            yoyo.jassie.finaltest.R.id.action_save -> {
                if (isNew) {
                    createHandler()
                } else {
                    updateChanges()
                }
                return true
            }

            yoyo.jassie.finaltest.R.id.action_delete -> {
                delete()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun addNew() {
        val name = editTextTitle.text.toString()
        if (name.isEmpty()) {
            return
        }

        val bookmark = bookmarkRepo.createBookmark()
        // bookmark.placeId = "123"

        bookmark.name = editTextTitle.text.toString()
        bookmark.country = spinner1!!.selectedItem.toString() // editTextSubTitle.text.toString()
        bookmark.latitude = java.lang.Double.parseDouble(editTextLat.text.toString())
        bookmark.longitude = java.lang.Double.parseDouble(editTextLong.text.toString())
        bookmark.gender = spinner2!!.selectedItem.toString()
        bookmark.birthday = convertDateToLong(editTextDate?.text.toString())

        mapsViewModel =
            ViewModelProviders.of(this).get(MapsViewModel::class.java)
        mapsViewModel.addBookmarkFromPlace(bookmark,imageViewPlace.drawToBitmap())

        finish()
    }

    fun convertDateToLong(date: String): Long {
        val df = SimpleDateFormat("dd/MMM/yyyy", Locale.US)
        return df.parse(date)!!.time
    }

    fun convertLongToDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("dd/MMM/yyyy", Locale.US)
        return format.format(date)
    }

    private fun createHandler() {
        val thread = object : Thread() {
            public override fun run() {
                Looper.prepare()
                val handler = Handler()
                handler.postDelayed(object : Runnable {
                    public override fun run() {

                        //  mapsViewModel.addBookmarkFromPlace()

                        addNew()


                        handler.removeCallbacks(this)
                        Looper.myLooper()?.quit()
                    }
                }, 10)
                Looper.loop()
            }
        }
        thread.start()
    }

    private fun delete() {

        bookmarkDetailsView?.let { bookmarkView ->
            bookmarkDetailsViewModel.deleteBookmark(bookmarkView)
            bookmarkDetailsViewModel.getBookmark(bookid)?.removeObservers(this);
        }
        finish()
    }


    private fun updateChanges() {
        val name = editTextTitle.text.toString()
        if (name.isEmpty()) {
            return
        }
        bookmarkDetailsView?.let { bookmarkView ->
            bookmarkView.name = editTextTitle.text.toString()
            bookmarkView.country = spinner1!!.selectedItem.toString() // editTextSubTitle.text.toString()
            bookmarkView.latitude = java.lang.Double.parseDouble(editTextLat.text.toString())
            bookmarkView.longitude = java.lang.Double.parseDouble(editTextLong.text.toString())
            bookmarkView.gender = spinner2!!.selectedItem.toString()
            bookmarkView.birthday = convertDateToLong(editTextDate?.text.toString())
            bookmarkDetailsViewModel.updateBookmark(bookmarkView)
        }
        finish()
    }

    private fun getIntentData() {

        isNew = intent.getBooleanExtra("isnew", true)

        if (!isNew) {
            this.setTitle("Update Details")

            val bookmarkId = intent.getLongExtra(
                MapsFragment.EXTRA_BOOKMARK_ID, 0
            )

            bookid = bookmarkId

            bookmarkDetailsViewModel.getBookmark(bookmarkId)?.observe(
                this, Observer<BookmarkDetailsViewModel.BookmarkDetailsView> {

                    it?.let {
                        bookmarkDetailsView = it
                        // Populate fields from bookmark
                        populateFields()
                        populateImageView()
                    }
                })


        }else{
            this.setTitle("Add Details")
        }
    }

    private fun setupViewModel() {
        bookmarkDetailsViewModel =
            ViewModelProviders.of(this).get(
                BookmarkDetailsViewModel::class.java
            )
    }

    private fun populateFields() {
        bookmarkDetailsView?.let { bookmarkView ->
            editTextTitle.setText(bookmarkView.name)
            setSpinText(spinner1!!,bookmarkView.country)
            setSpinText(spinner2!!,bookmarkView.gender)
            editTextDate?.setText(convertLongToDate(bookmarkView.birthday))
            editTextLat.setText(bookmarkView.latitude.toString())
            editTextLong.setText(bookmarkView.longitude.toString())
        }
    }

    fun setSpinText(spin: Spinner, text: String) {
        for (i in 0 until spin.adapter.count) {
            if (spin.adapter.getItem(i).toString().contains(text)) {
                spin.setSelection(i)
            }
        }

    }

    private fun populateImageView() {
        bookmarkDetailsView?.let { bookmarkView ->
            val placeImage = bookmarkView.getImage(this)
            placeImage?.let {
                imageViewPlace.setImageBitmap(placeImage)
            }
        }
        imageViewPlace.setOnClickListener {
            replaceImage()
        }
    }

    companion object {
        private const val REQUEST_CAPTURE_IMAGE = 1
        private const val REQUEST_GALLERY_IMAGE = 2
    }

}

