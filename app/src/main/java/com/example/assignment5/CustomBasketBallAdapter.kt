package com.example.assignment5

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment5.databinding.GameRecyclerItemBinding
import com.example.assignment5.models.basketball.Response
import java.text.DecimalFormat

class CustomAdapter(var dataSet: ArrayList<MyResource>, var context: Context) : RecyclerView.Adapter<CustomAdapter.ViewHolder>(){
    private lateinit var binding: GameRecyclerItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.ViewHolder {
        binding = GameRecyclerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomAdapter.ViewHolder, position: Int) {
        val item: MyResource = dataSet[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = dataSet.size

    inner class ViewHolder(private val binding: GameRecyclerItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.homeWinLo.setOnClickListener {
                for (i in 0..5) {
                    if (i == 0) continue
                    dataSet[adapterPosition].isChecked[i] = false
                }
                dataSet[adapterPosition].isChecked[0] = !dataSet[adapterPosition].isChecked[0]
                notifyItemChanged(adapterPosition)
            }

            binding.drawTopLo.setOnClickListener {
                for (i in 0..5) {
                    if (i == 1) continue
                    dataSet[adapterPosition].isChecked[i] = false
                }
                dataSet[adapterPosition].isChecked[1] = !dataSet[adapterPosition].isChecked[1]
                notifyItemChanged(adapterPosition)
            }

            binding.awayWinLo.setOnClickListener {
                for (i in 0..5) {
                    if (i == 2) continue
                    dataSet[adapterPosition].isChecked[i] = false
                }
                dataSet[adapterPosition].isChecked[2] = !dataSet[adapterPosition].isChecked[2]
                notifyItemChanged(adapterPosition)
            }

            binding.handWinLo.setOnClickListener {
                for (i in 0..5) {
                    if (i == 3) continue
                    dataSet[adapterPosition].isChecked[i] = false
                }
                dataSet[adapterPosition].isChecked[3] = !dataSet[adapterPosition].isChecked[3]
                notifyItemChanged(adapterPosition)
            }

            binding.handDrawLo.setOnClickListener {
                for (i in 0..5) {
                    if (i == 4) continue
                    dataSet[adapterPosition].isChecked[i] = false
                }
                dataSet[adapterPosition].isChecked[4] = !dataSet[adapterPosition].isChecked[4]
                notifyItemChanged(adapterPosition)
            }

            binding.handAwayWinLo.setOnClickListener {
                for (i in 0..5) {
                    if (i == 5) continue
                    dataSet[adapterPosition].isChecked[i] = false
                }
                dataSet[adapterPosition].isChecked[6] = !dataSet[adapterPosition].isChecked[6]
                notifyItemChanged(adapterPosition)
            }
            //scoreLow
            binding.scoreSumLowLo.setOnClickListener {
                dataSet[adapterPosition].isChecked[7] = false
                dataSet[adapterPosition].isChecked[6] = !dataSet[adapterPosition].isChecked[6]
                notifyItemChanged(adapterPosition)
            }
            //scoreHigh
            binding.scoreSumHighLo.setOnClickListener {
                dataSet[adapterPosition].isChecked[6] = false
                dataSet[adapterPosition].isChecked[7] = !dataSet[adapterPosition].isChecked[7]
                notifyItemChanged(adapterPosition)
            }
        }

        fun bind(data: MyResource) {
            //종목명, 팀명
            binding.athleticsNameTv.text = data.leagueName
            binding.homeTeamTv.text = data.homeName
            binding.awayTeamTv.text = data.awayName
            //시간, 날짜
            val days = data.date.split("T")[0].split("-")
            binding.endDateTv.text = "${days[1]}/${days[2]}"
            binding.endTimeTv.text = data.time
            /*
            //승무패
            val diff = data.homeTotal - data.awayTotal
            val higher = makeRatio(data.homeTotal, if (diff < 0) -diff else diff)
            val lower = makeRatio(data.homeTotal, data.homeTotal + if (diff < 0) diff else -diff)
            binding.homeWinRatioTv.text = if (diff < 0) higher else lower
            binding.drawRatioTv.text = makeRatio((higher.split(".")[0].toInt() - lower.split(".")[0].toInt()) / 2, lower.split(".")[0].toInt())
            binding.awayWinRatioTv.text = if (diff < 0) lower else higher
            //핸디캡
            binding.handHomeWinRatioTv.text = if (diff < 0) makeRatio(data.homeTotal + 1, -diff) else makeRatio(data.homeTotal - 2, diff)
            binding.handDrawRatioTv.text = makeRatio(data.homeTotal + 2, if (diff < 0) -diff else diff)
            binding.handAwayWinRatioTv.text = if (diff < 0) makeRatio(-diff - 1, 1) else makeRatio(data.homeTotal + 1, data.awayTotal - diff)
            //기준 점수
            val score = (data.homeTotal + data.awayTotal) * 3 / 4
            binding.scoreStandardTv.text = "기준점 $score"
            binding.scoreLowRatioTv.text = makeRatio(score, data.homeTotal)
            binding.scoreHighRatioTv.text = makeRatio(score, data.awayTotal)*/
            //승무패
            binding.homeWinRatioTv.text = "1.63"
            binding.drawRatioTv.text = "3.63"
            binding.awayWinRatioTv.text = "6.87"
            //핸디캡
            binding.handHomeWinRatioTv.text = "2.9"
            binding.handDrawRatioTv.text = "3.51"
            binding.handAwayWinRatioTv.text = "2.48"
            //기준 점수
            val score = (data.homeTotal + data.awayTotal) * 3 / 4
            binding.scoreStandardTv.text = "기준점 $score"
            binding.scoreLowRatioTv.text = makeRatio(score, data.homeTotal)
            binding.scoreHighRatioTv.text = makeRatio(score, data.awayTotal)
            //check에 따라 색상 변경
            //initialCheck(data.isChecked)
            //남은 시간 설정
            binding.remainingTimeTv.text = makeRemaining(data.remaining - time)
            //모양 변경
            binding.sportIv.setImageResource(when(data.type){
                0 -> R.drawable.ic_baseball_default
                1 -> R.drawable.ic_basketball_default
                2 -> R.drawable.ic_soccer_default
                else -> R.drawable.ic_baseball_default
            })
        }
    }

    fun makeRatio(target: Int, stand: Int) : String {
        val value = target.toDouble() / stand.toDouble()
        val formatter = DecimalFormat("0.00")
        return formatter.format(value).toString()
    }

    private fun initialCheck(data: ArrayList<Boolean>) {
        //homeWin
        binding.homeWinLo.background = ContextCompat.getDrawable(context, if(data[0]) R.drawable.selected_item else R.drawable.grey_border)
        binding.homeWinTv.setTextColor(ContextCompat.getColor(context, if (data[0]) R.color.white else R.color.black))
        binding.homeWinRatioTv.setTextColor(ContextCompat.getColor(context, if (data[0]) R.color.white else R.color.black))
        //draw
        binding.drawTopLo.background = ContextCompat.getDrawable(context, if(data[1]) R.drawable.selected_item else R.drawable.grey_border)
        binding.drawTopTv.setTextColor(ContextCompat.getColor(context, if (data[1]) R.color.white else R.color.black))
        binding.drawRatioTv.setTextColor(ContextCompat.getColor(context, if (data[1]) R.color.white else R.color.black))
        //awayWin
        binding.awayWinLo.background = ContextCompat.getDrawable(context, if(data[2]) R.drawable.selected_item else R.drawable.grey_border)
        binding.awayWinTv.setTextColor(ContextCompat.getColor(context, if (data[2]) R.color.white else R.color.black))
        binding.awayWinRatioTv.setTextColor(ContextCompat.getColor(context, if (data[2]) R.color.white else R.color.black))
        //handHome
        binding.handWinLo.background = ContextCompat.getDrawable(context, if(data[3]) R.drawable.selected_item else R.drawable.grey_border)
        binding.handHomeWinTv.setTextColor(ContextCompat.getColor(context, if (data[3]) R.color.white else R.color.black))
        binding.handHomeWinRatioTv.setTextColor(ContextCompat.getColor(context, if (data[3]) R.color.white else R.color.black))
        //handDraw
        binding.handDrawLo.background = ContextCompat.getDrawable(context, if(data[4]) R.drawable.selected_item else R.drawable.grey_border)
        binding.handDrawTv.setTextColor(ContextCompat.getColor(context, if (data[4]) R.color.white else R.color.black))
        binding.handDrawRatioTv.setTextColor(ContextCompat.getColor(context, if (data[4]) R.color.white else R.color.black))
        //handAway
        binding.handAwayWinLo.background = ContextCompat.getDrawable(context, if(data[5]) R.drawable.selected_item else R.drawable.grey_border)
        binding.handAwayWinTv.setTextColor(ContextCompat.getColor(context, if (data[5]) R.color.white else R.color.black))
        binding.handAwayWinRatioTv.setTextColor(ContextCompat.getColor(context, if (data[5]) R.color.white else R.color.black))
        //scoreLow
        binding.scoreSumHighLo.background = ContextCompat.getDrawable(context, if(data[6]) R.drawable.selected_item else R.drawable.grey_border)
        binding.scoreHighTv.setTextColor(ContextCompat.getColor(context, if (data[6]) R.color.white else R.color.black))
        binding.scoreHighRatioTv.setTextColor(ContextCompat.getColor(context, if (data[6]) R.color.white else R.color.black))
        //scoreHigh
        binding.scoreSumLowLo.background = ContextCompat.getDrawable(context, if(data[7]) R.drawable.selected_item else R.drawable.grey_border)
        binding.scoreLowTv.setTextColor(ContextCompat.getColor(context, if (data[7]) R.color.white else R.color.black))
        binding.scoreLowRatioTv.setTextColor(ContextCompat.getColor(context, if (data[7]) R.color.white else R.color.black))
    }
}

fun makeRemaining(time: Long) : String{
    val hour = time / 3600
    val minute = (time % 3600) / 60
    val second = (time % 3600) % 60

    val formatter = DecimalFormat("00")
    formatter.format(hour)

    return String.format("${formatter.format(hour)}:${formatter.format(minute)}:${formatter.format(second)} 남음")
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