package com.example.dateapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.squareup.picasso.Picasso

class MyAdapter(private var context: Context, private var images: List<String>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = images.size

    override fun getItem(position: Int): Any = images[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder

        // Check if the existing view is being reused, otherwise inflate the view
        val view = convertView ?: inflater.inflate(R.layout.item_card, parent, false)
        viewHolder = ViewHolder(view)

        // Get the image URL for the current position and load it into the image view using Picasso
        val imageUrl = getItem(position) as String
        Picasso.get().load(imageUrl).into(viewHolder.imageView)

        return view
    }

    // ViewHolder pattern to hold views
    private class ViewHolder(view: View) {
        val imageView: ImageView = view.findViewById(R.id.image)
    }
}
