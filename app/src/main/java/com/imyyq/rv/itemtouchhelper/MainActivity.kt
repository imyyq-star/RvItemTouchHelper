package com.imyyq.rv.itemtouchhelper

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.imyyq.widget.helper.ItemTouchHelper
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val mDataList: MutableList<Data> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        for (i in 0..10) {
            mDataList.add(Data("item $i"))
        }

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = MyAdapter()

        val helper = ItemTouchHelper(MyTouch())
        helper.attachToRecyclerView(rv)
    }

    private inner class MyTouch : ItemTouchHelper.Callback() {
        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
        }

        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            val from = viewHolder.adapterPosition
            val to = target.adapterPosition
            Collections.swap(mDataList, from, to)
            rv.adapter.notifyItemMoved(from, to)
            return true
        }

        override fun onMergeBefore(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) {
            val from = viewHolder.adapterPosition
            val to = target.adapterPosition
            Log.i("MyTouch", "onMergeBefore: from=$from, to=$to")
            mDataList[to].isCanMerge = true
            rv.adapter.notifyItemChanged(to)
        }

        override fun onCancelMerge(position: Int) {
            Log.i("MyTouch", "onCancelMerge: $position")
            mDataList[position].isCanMerge = false
            rv.adapter.notifyItemChanged(position)
        }

        override fun onMerge(recyclerView: RecyclerView?, from: Int, to: Int): Boolean {
            Log.i("MyTouch", "onMerge: from=$from, to=$to")
            val fromData = mDataList[from]
            val toData = mDataList[to]
            val d = Data(fromData.name + " - " + toData.name)
            mDataList.add(to, d)
            mDataList.remove(fromData)
            mDataList.remove(toData)
            rv.adapter.notifyDataSetChanged()
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        }
    }

    private inner class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_item, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = mDataList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val data = mDataList[position]
            holder.tv.text = data.name
            if (data.isCanMerge) {
                holder.itemView.setBackgroundResource(android.R.color.holo_red_dark)
            } else {
                holder.itemView.setBackgroundResource(android.R.color.holo_green_light)
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tv = itemView.findViewById<TextView>(R.id.tv)
        }
    }
}

