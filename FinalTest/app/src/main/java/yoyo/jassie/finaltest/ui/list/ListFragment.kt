package yoyo.jassie.finaltest.ui.list

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import yoyo.jassie.labtest2.db.MapsDatabase




class ListFragment : Fragment() {

    var recyclerView: RecyclerView? = null
    var searchView: SearchView? = null
    var con: Activity? = null
    var adp: RecyclerAdapter? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(yoyo.jassie.finaltest.R.layout.fragment_list, container, false)

        con = activity

        recyclerView = root.findViewById(yoyo.jassie.finaltest.R.id.recyclerView) as RecyclerView
        recyclerView?.layoutManager = LinearLayoutManager(con!!)


        searchView = root.findViewById(yoyo.jassie.finaltest.R.id.searchbar) as SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String): Boolean {
                adp?.filter?.filter(text)
                return false
            }

            override fun onQueryTextChange(text: String): Boolean {
                adp?.filter?.filter(text)
                return true
            }
        })

        return root
    }

    override fun onResume() {
        super.onResume()

        getTask()
    }

    override fun onPause() {
        super.onPause()

        searchView?.setQuery("", false)
        searchView?.clearFocus()
    }

    private fun getTask() {
        val thread = object : Thread() {
            public override fun run() {
                Looper.prepare()
                val handler = Handler()
                handler.postDelayed(object : Runnable {
                    public override fun run() {

                        val taskList = MapsDatabase.getInstance(con!!).bookmarkDao().loadAllBookmarks()


                        con!!.runOnUiThread {
                            adp = RecyclerAdapter(con, taskList)
                            recyclerView?.adapter = adp
                        }

                        handler.removeCallbacks(this)
                        Looper.myLooper()?.quit()
                    }
                }, 10)
                Looper.loop()
            }
        }
        thread.start()

    }

}