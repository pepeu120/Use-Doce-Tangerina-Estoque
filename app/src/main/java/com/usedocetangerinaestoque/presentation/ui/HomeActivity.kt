package com.usedocetangerinaestoque.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.usedocetangerinaestoque.R
import com.usedocetangerinaestoque.presentation.viewmodel.HomeViewModel

class HomeActivity : BaseDrawerActivity() {

    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var containerCategories: LinearLayout
    private lateinit var containerEstoque: LinearLayout
    private lateinit var welcomeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        containerCategories = findViewById(R.id.containerCategories)
        containerEstoque = findViewById(R.id.containerEstoque)
        welcomeText = findViewById(R.id.welcomeText)

        welcomeText.text = "Bem-vindo(a)"

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.categoriesWithImages.observe(this) { list ->
            containerCategories.removeAllViews()
            list.forEach { category ->
                val card = layoutInflater.inflate(R.layout.card_category, containerCategories, false)

                val img = card.findViewById<ImageView>(R.id.imageView)
                val name = card.findViewById<TextView>(R.id.textName)

                name.text = category.category.name

                Glide.with(this)
                    .load(category.firstItemImagePath ?: R.drawable.produto_exemplo)
                    .centerCrop()
                    .into(img)

                card.setOnClickListener {
                    val intent = Intent(this, StockActivity::class.java)
                    intent.putExtra("filterCategoryId", category.category.id)
                    startActivity(intent)
                }

                containerCategories.addView(card)
            }
        }

        viewModel.items.observe(this) { list ->
            containerEstoque.removeAllViews()
            list.forEach { item ->
                val card = layoutInflater.inflate(R.layout.card_item, containerEstoque, false)

                val img = card.findViewById<ImageView>(R.id.imageView)
                val name = card.findViewById<TextView>(R.id.textName)
                val value = card.findViewById<TextView>(R.id.textValue)

                name.text = item.item.name
                value.text = "R$ ${item.item.value}"

                Glide.with(this)
                    .load(item.images.first().path)
                    .centerCrop()
                    .into(img)

                card.setOnClickListener {
                    val intent = Intent(this, ItemDetailActivity::class.java)
                    intent.putExtra("itemId", item.item.id)
                    startActivity(intent)
                }

                containerEstoque.addView(card)
            }
        }
    }
}