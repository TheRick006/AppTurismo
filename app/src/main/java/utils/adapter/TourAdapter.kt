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
import data.local.entity.Tour
import utils.extensions.loadImage

class TourAdapter(
    private val onItemClick: (Tour) -> Unit
) : ListAdapter<Tour, TourAdapter.TourViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tour, parent, false)
        return TourViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TourViewHolder(
        itemView: View,
        private val onItemClick: (Tour) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tourImage: ImageView = itemView.findViewById(R.id.tourImage)
        private val tourName: TextView = itemView.findViewById(R.id.tourName)
        private val tourDescription: TextView = itemView.findViewById(R.id.tourDescription)

        fun bind(tour: Tour) {
            tourName.text = tour.name
            tourDescription.text = tour.description

            tourImage.loadImage(tour.imageUrl)

            itemView.setOnClickListener { onItemClick(tour) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Tour>() {
            override fun areItemsTheSame(oldItem: Tour, newItem: Tour) =
                oldItem.tourId == newItem.tourId

            override fun areContentsTheSame(oldItem: Tour, newItem: Tour) =
                oldItem == newItem
        }
    }
}
