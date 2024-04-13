package com.example.gridimageloader

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.LruCache
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    private lateinit var gv_imageloader: GridView
    private lateinit var mImageAdapter: GridImageAdapter
    private lateinit var mRequestQueue: RequestQueue
    private lateinit var mImageCache: LruCache<String, Bitmap>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        if (Utils.isNetworkAvailable(this@MainActivity)) {
            getImages()
        } else {
            Toast.makeText(this@MainActivity, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    fun init() {
        gv_imageloader = findViewById(R.id.gv_imageloader)
        mRequestQueue = Volley.newRequestQueue(this)
        mImageCache = LruCache(20)
    }

    fun getImages() {
        val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, Utils.url, null,
            { response ->
                val imageUrls = ArrayList<String>()
                for (i in 0 until response.length()) {
                    try {
                        val imageUrl =
                            response.getJSONObject(i).getJSONObject("urls").getString("regular")
                        imageUrls.add(imageUrl)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                mImageAdapter = GridImageAdapter(imageUrls)
                gv_imageloader.adapter = mImageAdapter
            },
            { error ->
                // Handle error
                Toast.makeText(this@MainActivity, "Error loading images", Toast.LENGTH_SHORT).show()
            })
        mRequestQueue.add(jsonArrayRequest)
    }

    private inner class GridImageAdapter(private val mImageUrls: ArrayList<String>) :
        BaseAdapter() {
        override fun getCount(): Int {
            return mImageUrls.size
        }

        override fun getItem(position: Int): Any {
            return mImageUrls[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val imageView: ImageView
            if (convertView == null) {
                imageView = ImageView(this@MainActivity)
                imageView.layoutParams = ViewGroup.LayoutParams(350, 350)
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                imageView = convertView as ImageView
            }

            val imageUrl = mImageUrls[position]
            val imageBitmap = mImageCache.get(imageUrl)
            if (imageBitmap != null) {
                imageView.setImageBitmap(imageBitmap)
            } else {
                val imageRequest = ImageRequest(imageUrl,
                    { response ->
                        imageView.setImageBitmap(response)
                        mImageCache.put(imageUrl, response)
                    },
                    0,
                    0,
                    ImageView.ScaleType.CENTER_CROP,
                    Bitmap.Config.RGB_565,
                    { error ->

                        imageView.setImageResource(R.drawable.placeholder_error)
                    })
                mRequestQueue.add(imageRequest)
            }

            return imageView
        }
    }
}