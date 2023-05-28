package com.dikamahard.myunpad.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.model.Post
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class PostAdapter(private val listPost: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback
    val storage = Firebase.storage


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.tv_item_title)
        val tvKonten: TextView = view.findViewById(R.id.tv_item_konten)
        val ivGambar: ImageView = view.findViewById(R.id.img_poster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvJudul.text = listPost[position].judul
        holder.tvKonten.text = listPost[position].konten

        val imageRef = storage.reference.child("post/${listPost[position].gambar}")
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            val imgUrl = uri.toString()
            Glide.with(holder.ivGambar)
                .load(imgUrl)
                .into(holder.ivGambar)
        }


        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listPost[position])
        }
    }

    override fun getItemCount(): Int {
        return listPost.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Post)
    }

}