package ie.koala.topics.feature.animal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import ie.koala.topics.R
import ie.koala.topics.feature.animal.adapter.AnimalAdapter
import ie.koala.topics.feature.animal.model.AnimalViewModel

class AnimalActivity : AppCompatActivity() {

    private lateinit var animalRecyclerView: RecyclerView
    private lateinit var animalAdapter: AnimalAdapter
    private lateinit var animalViewModel: AnimalViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animal)

        animalRecyclerView = findViewById(R.id.animal_recycler_view)
        animalRecyclerView.layoutManager = LinearLayoutManager(this)
        animalAdapter = AnimalAdapter(this)
        animalViewModel = AnimalViewModel()

        animalRecyclerView.adapter = animalAdapter
        animalRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        animalViewModel.getAnimals()?.observe(this, Observer(animalAdapter::submitList))
    }
}