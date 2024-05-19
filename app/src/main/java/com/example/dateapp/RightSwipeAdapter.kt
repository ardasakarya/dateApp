package com.example.dateapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RightSwipeAdapter(private val dataList: List<ImageData>) : RecyclerView.Adapter<RightSwipeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullNameText: TextView = view.findViewById(R.id.fullNameTextItem)
        val emailText: TextView = view.findViewById(R.id.emailTextItem)
        val profileImage: ImageView = view.findViewById(R.id.profileImageItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.right_swipe_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.fullNameText.text = data.fullName
        holder.emailText.text = data.email
        Glide.with(holder.profileImage.context).load(data.imageUrl).into(holder.profileImage)
    }

    override fun getItemCount() = dataList.size
}
