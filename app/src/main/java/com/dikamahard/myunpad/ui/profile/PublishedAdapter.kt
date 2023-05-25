package com.dikamahard.myunpad.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.model.Post

class PublishedAdapter(private val listPost: List<Post>, private val listId: List<String>) : RecyclerView.Adapter<PublishedAdapter.ViewHolder>() {


    //private val listId = mutableListOf<String>()
    private lateinit var onItemClickCallback: PublishedAdapter.OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: PublishedAdapter.OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ViewHolder(v: View): RecyclerView.ViewHolder(v) {
        val tvJudul = v.findViewById<TextView>(R.id.tv_horizontal_title)
        val ivGambar = v.findViewById<ImageView>(R.id.iv_horizontal_poster)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post_horizontal, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.ivGambar.
        holder.tvJudul.text = listPost[position].judul

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listPost[position], listId[position])
        }
    }

    override fun getItemCount(): Int {
        return listPost.size
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Post, id: String)
    }

}

