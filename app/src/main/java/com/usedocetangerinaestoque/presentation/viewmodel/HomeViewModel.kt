package com.usedocetangerinaestoque.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.usedocetangerinaestoque.data.relations.CategoryWithFirstItem
import com.usedocetangerinaestoque.data.relations.ItemFull
import com.usedocetangerinaestoque.usecases.category.GetAllCategoriesUseCase
import com.usedocetangerinaestoque.usecases.item.GetAllItensUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getAllItemsUseCase: GetAllItensUseCase
) : ViewModel() {

    private val _categoriesWithImages = MutableLiveData<List<CategoryWithFirstItem>>()
    val categoriesWithImages: LiveData<List<CategoryWithFirstItem>> get() = _categoriesWithImages

    private val _items = MutableLiveData<List<ItemFull>>()
    val items: LiveData<List<ItemFull>> get() = _items

    init {
        viewModelScope.launch {
            combine(
                getAllCategoriesUseCase(),
                getAllItemsUseCase()
            ) { categories, items ->
                Pair(categories, items)
            }.collect { (categories, items) ->
                val grouped = items.groupBy { it.category?.id }
                val result = categories.map { category ->
                    val firstImage = grouped[category.id]?.firstOrNull()?.images?.firstOrNull()?.path
                    CategoryWithFirstItem(category, firstImage)
                }
                _categoriesWithImages.postValue(result)
                _items.postValue(items)
            }
        }
    }
}