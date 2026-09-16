package utils.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.itanes.appturismo.R
import data.local.entity.Note

class NoteAdapter(
    private val onDelete: (Note) -> Unit,
    private val onEdit: (Note) -> Unit
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view, onDelete, onEdit)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NoteViewHolder(
        itemView: View,
        private val onDelete: (Note) -> Unit,
        private val onEdit: (Note) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.findViewById(R.id.noteTitle)
        private val content: TextView = itemView.findViewById(R.id.noteContent)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteNoteButton)

        fun bind(note: Note) {
            title.text = note.title
            content.text = note.content
            deleteButton.setOnClickListener { onDelete(note) }
            itemView.setOnClickListener { onEdit(note) }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Note>() {
            override fun areItemsTheSame(a: Note, b: Note) = a.noteId == b.noteId
            override fun areContentsTheSame(a: Note, b: Note) = a == b
        }
    }
}