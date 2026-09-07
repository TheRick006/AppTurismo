package utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.itanes.appturismo.R
import com.itanes.appturismo.data.local.entity.TouristPoint
import com.itanes.appturismo.utils.extensions.getImageList

import utils.extensions.loadImage

class TouristPointAdapter(
    private val onItemClick: (TouristPoint) -> Unit
) : ListAdapter<TouristPoint, TouristPointAdapter.PointViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tourist_point, parent, false)
        return PointViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PointViewHolder(
        itemView: View,
        private val onItemClick: (TouristPoint) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val pointImage: ImageView = itemView.findViewById(R.id.pointImage)
        private val pointName: TextView = itemView.findViewById(R.id.pointName)
        private val pointDescription: TextView = itemView.findViewById(R.id.pointDescription)

        fun bind(point: TouristPoint) {
            pointName.text = point.name
            pointDescription.text = point.description

            val images = point.getImageList()
            if (images.isNotEmpty()) {
                pointImage.loadImage(images[0])
            }

            itemView.setOnClickListener { onItemClick(point) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TouristPoint>() {
            override fun areItemsTheSame(oldItem: TouristPoint, newItem: TouristPoint) =
                oldItem.touristPointId == newItem.touristPointId

            override fun areContentsTheSame(oldItem: TouristPoint, newItem: TouristPoint) =
                oldItem == newItem
        }
    }
}