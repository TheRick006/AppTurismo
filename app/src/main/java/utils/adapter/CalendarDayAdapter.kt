package com.itanes.appturismo.utils.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.itanes.appturismo.R
import data.local.entity.CalendarDay

class CalendarDayAdapter(
    private val onDayClick: (CalendarDay) -> Unit
) : ListAdapter<CalendarDay, CalendarDayAdapter.DayViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view, onDayClick)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DayViewHolder(
        itemView: View,
        private val onDayClick: (CalendarDay) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val dayText: TextView = itemView.findViewById(R.id.dayText)

        fun bind(day: CalendarDay) {
            dayText.text = day.day.toString()

            val context = itemView.context
            val bg = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
            }

            when {
                day.isSelected -> {
                    bg.setColor(context.getColor(R.color.purple_500))
                    dayText.setTextColor(Color.WHITE)
                }
                day.hasTours -> {
                    bg.setColor(context.getColor(R.color.teal_200))
                    dayText.setTextColor(Color.BLACK)
                }
                day.isToday -> {
                    bg.setColor(Color.TRANSPARENT)
                    bg.setStroke(3, context.getColor(R.color.purple_500))
                    dayText.setTextColor(context.getColor(R.color.purple_500))
                }
                else -> {
                    bg.setColor(Color.TRANSPARENT)
                    dayText.setTextColor(
                        if (day.isCurrentMonth) Color.BLACK else Color.LTGRAY
                    )
                }
            }

            dayText.background = bg

            itemView.setOnClickListener {
                if (day.isCurrentMonth) onDayClick(day)
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<CalendarDay>() {
            override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay) =
                oldItem.year == newItem.year &&
                        oldItem.month == newItem.month &&
                        oldItem.day == newItem.day

            override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay) =
                oldItem == newItem
        }
    }
}