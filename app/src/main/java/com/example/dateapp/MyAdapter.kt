package com.example.dateapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class MyAdapter(private var context: Context, private var images: List<ImageData>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = images.size

    override fun getItem(position: Int): Any = images[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val view = convertView ?: inflater.inflate(R.layout.item_card, parent, false)

        if (convertView == null) {
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        val imageData = getItem(position) as ImageData
        Picasso.get().load(imageData.imageUrl).into(viewHolder.imageView)
        viewHolder.emailTextView.text = imageData.email
        viewHolder.fullNameTextView.text = imageData.fullName

        return view
    }

    private class ViewHolder(view: View) {
        val imageView: ImageView = view.findViewById(R.id.image)
        val emailTextView: TextView = view.findViewById(R.id.nameText)
        val fullNameTextView: TextView = view.findViewById(R.id.fullnameText)
    }
}
