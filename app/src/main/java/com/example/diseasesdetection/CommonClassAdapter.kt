package com.example.diseasesdetection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommonClassesAdapter(private val data: List<Pair<String, Int>>) :
    RecyclerView.Adapter<CommonClassesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val className: TextView = view.findViewById(R.id.className)
        val classCount: TextView = view.findViewById(R.id.classCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_common_class, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (className, classCount) = data[position]
        holder.className.text = className
        holder.classCount.text = buildString {
        append("Count: ")
        append(classCount)
    }
    }

    override fun getItemCount(): Int = data.size
}
