package com.example.healthapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthapp.R
import com.example.healthapp.dataModel.ActivityModel
import com.example.healthapp.databinding.HrListItemBinding
import com.example.healthapp.view.hr.HrChartActivity

class ActivityRVAdapter(
    val context: Context,
    val itemList : ArrayList<ActivityModel>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    private lateinit var binding : HrListItemBinding
    inner class Viewholder1(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(item : ActivityModel){
            val content1 = itemView.findViewById<TextView>(R.id.content1)
            val content2 = itemView.findViewById<TextView>(R.id.content2)

            content1.text = item.content1
            content2.text = item.content2


//            Glide.with(context)
//                .load(item.image)
//                .into(imageView)
        }

    }
    inner class Viewholder2(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(item : ActivityModel){
            val content1 = itemView.findViewById<TextView>(R.id.content1)
            val content2 = itemView.findViewById<TextView>(R.id.content2)

            content1.text = item.content1
            content2.text = item.content2

//            Glide.with(context)
//                .load(item.image)
//                .into(imageView)
        }

    }
    inner class Viewholder3(itemView: HrListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item : ActivityModel){
            binding.content1.text = item.content1

            binding.hrToBtn.setOnClickListener {
                val intent = Intent(context, HrChartActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }


//            Glide.with(context)
//                .load(item.image)
//                .into(imageView)
        }

    }
    inner class Viewholder4(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(item : ActivityModel){


//            Glide.with(context)
//                .load(item.image)
//                .into(imageView)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return itemList[position].type
    } // onCreateViewHolder 함수가 호출되기 전에 먼저 호출되는 함수로 먼저 viewType을 지정해주어 원활하게 각각의 viewHolder를 쓸 수 있도록 함

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when(viewType){
            1-> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.step_list_item,parent,false)
                Viewholder1(v)
            }
            2 ->{
                val v = LayoutInflater.from(parent.context).inflate(R.layout.sleep_list_item,parent,false)
                Viewholder2(v)
            }
            3 -> {
                binding = HrListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                Viewholder3(binding)
            }
            else -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.audio_list_item,parent,false)
                Viewholder4(v)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(itemList[position].type){
            1 -> {
                (holder as Viewholder1).bind(itemList[position])
            }
            2 -> {
                (holder as Viewholder2).bind(itemList[position])
            }
            3 -> {
                (holder as Viewholder3).bind(itemList[position])
            }
            else ->{
                (holder as Viewholder4).bind(itemList[position])
            }

        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}