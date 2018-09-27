package ie.koala.topics.feature.animal.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ie.koala.topics.R
import ie.koala.topics.feature.animal.model.Animal

import kotlinx.android.synthetic.main.animal_item.view.*

class AnimalAdapter constructor(context: Context) : PagedListAdapter<Animal, AnimalAdapter.AnimalViewHolder>(
        object : DiffUtil.ItemCallback<Animal>() {
            override fun areItemsTheSame(oldItem: Animal, newItem: Animal): Boolean = oldItem == newItem
            override fun areContentsTheSame(oldItem: Animal, newItem: Animal): Boolean = oldItem.content == newItem.content
        }) {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val view = mInflater.inflate(R.layout.animal_item, parent, false)
        return AnimalViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val animal = getItem(position)
        holder.animaTitle?.text = animal?.title
        holder.animaText?.text = animal?.content
    }

    class AnimalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var animaTitle = itemView.name_animal_item
        var animaText = itemView.description_animal_item
    }
}