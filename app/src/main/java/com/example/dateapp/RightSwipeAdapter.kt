package com.example.dateapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RightSwipeAdapter(private val likedUsersList: List<LikedUserData>) : RecyclerView.Adapter<RightSwipeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val likerImageView: ImageView = view.findViewById(R.id.profileImageItem)
        val likerFullNameTextView: TextView = view.findViewById(R.id.fullNameTextItem)
        val likerEmailTextView: TextView = view.findViewById(R.id.emailTextItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.right_swipe_data, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userData = likedUsersList[position]

        if (!userData.likerImageUrl.isNullOrEmpty()) {
            Picasso.get().load(userData.likerImageUrl).into(holder.likerImageView)
        } else {
            // Yedek bir resim yükleyin veya bir hata resmi kullanın
            holder.likerImageView.setImageResource(R.drawable.logo) // `placeholder_image` adında bir yedek resim ekleyin
        }

        holder.likerFullNameTextView.text = userData.likerFullName
        holder.likerEmailTextView.text = userData.likerEmail
    }

    override fun getItemCount(): Int = likedUsersList.size
}