package com.example.assignment5

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment5.databinding.GameRecyclerItemBinding
import com.example.assignment5.models.basketball.Response
import java.text.DecimalFormat

class CustomAdapter(var dataSet: ArrayList<Response>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>(){
    private lateinit var binding: GameRecyclerItemBinding
    private var mListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        binding = GameRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val item: Response = dataSet[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = dataSet.size

    inner class ViewHolder(private val binding: GameRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    if (mListener != null) {
                        mListener!!.onItemClick(it, pos)
                    }
                }
            }
        }

        fun bind(data: Response) {
            //종목명, 팀명
            binding.athleticsNameTv.text = data.league.name
            binding.homeTeamTv.text = data.teams.home.name
            binding.awayTeamTv.text = data.teams.away.name
            //시간, 날짜
            val days = data.date.split("T")[0].split("-")
            binding.endDateTv.text = "${days[1]}/${days[2]}"
            binding.endTimeTv.text = data.time
            //승무패
            val diff = data.scores.home.total - data.scores.away.total
            val higher = makeRatio(data.scores.home.total, if (diff < 0) -diff else diff)
            val lower = makeRatio(data.scores.home.total, data.scores.home.total + if (diff < 0) diff else -diff)
            binding.homeWinRatioTv.text = if (diff < 0) higher else lower
            binding.drawRatioTv.text = makeRatio((higher.split(".")[0].toInt() - lower.split(".")[0].toInt()) / 2, lower.split(".")[0].toInt())
            binding.awayWinRatioTv.text = if (diff < 0) lower else higher
            //핸디캡

            //기준 점수
            val score = (data.scores.home.total + data.scores.away.total) * 3 / 4
            binding.scoreStandardTv.text = "기준점 $score"
            binding.scoreLowRatioTv.text = makeRatio(score, data.scores.home.total)
            binding.scoreHighRatioTv.text = makeRatio(score, data.scores.away.total)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    fun makeRatio(target: Int, stand: Int) : String {
        val value = target.toDouble() / stand.toDouble()
        val formatter = DecimalFormat("0.00")
        return formatter.format(value).toString()
    }
}

class RecyclerViewDecoration(private val divHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = divHeight
    }
}