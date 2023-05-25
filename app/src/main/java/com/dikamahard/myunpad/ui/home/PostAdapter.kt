package com.dikamahard.myunpad.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dikamahard.myunpad.R
import com.dikamahard.myunpad.model.Post

class PostAdapter(private val listPost: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback


    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.tv_item_title)
        val tvKonten: TextView = view.findViewById(R.id.tv_item_konten)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvJudul.text = listPost[position].judul
        holder.tvKonten.text = listPost[position].konten

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