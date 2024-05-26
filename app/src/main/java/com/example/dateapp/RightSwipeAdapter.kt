package com.example.dateapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RightSwipeAdapter(private val dataList: List<LikedUserData>) : RecyclerView.Adapter<RightSwipeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullNameText: TextView = view.findViewById(R.id.fullNameTextItem)
        val likerEmailText: TextView = view.findViewById(R.id.emailTextItem)
        val profileImage: ImageView = view.findViewById(R.id.profileImageItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.right_swipe_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.fullNameText.text = data.likerFullName
        holder.likerEmailText.text = data.likerEmail
        Glide.with(holder.profileImage.context).load(data.likerImageUrl).into(holder.profileImage)
    }

    override fun getItemCount() = dataList.size
}
