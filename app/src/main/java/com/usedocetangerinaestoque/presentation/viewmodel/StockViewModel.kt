package com.usedocetangerinaestoque.presentation.viewmodel

import android.provider.SyncStateContract.Helpers.update
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.usedocetangerinaestoque.data.entities.Category
import com.usedocetangerinaestoque.data.entities.Size
import com.usedocetangerinaestoque.data.relations.ItemFull
import com.usedocetangerinaestoque.usecases.category.GetAllCategoriesUseCase
import com.usedocetangerinaestoque.usecases.item.GetAllItensUseCase
import com.usedocetangerinaestoque.usecases.size.GetAllSizesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val getAllItensUseCase: GetAllItensUseCase,
    private val getAllSizesUseCase: GetAllSizesUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase
) : ViewModel() {
    private val items: LiveData<List<ItemFull>> = getAllItensUseCase().asLiveData()
    val sizes: LiveData<List<Size>> = getAllSizesUseCase().asLiveData()
    val categories: LiveData<List<Category>> = getAllCategoriesUseCase().asLiveData()

    private val _selectedSizeId = MutableLiveData<Long?>()
    private val _selectedCategoryId = MutableLiveData<Long?>()
    private val _searchQuery = MutableLiveData<String>("")

    val filteredItems = MediatorLiveData<List<ItemFull>>()

    init {
        filteredItems.addSource(items) { updateFilteredItems() }
        filteredItems.addSource(_selectedSizeId) { updateFilteredItems() }
        filteredItems.addSource(_selectedCategoryId) { updateFilteredItems() }
    }

    private fun updateFilteredItems() {
        val all = items.value ?: return
        val sizeId = _selectedSizeId.value
        val catId = _selectedCategoryId.value
        val query = _searchQuery.value?.trim()?.lowercase() ?: ""

        filteredItems.value = all.filter { item ->
            val matchesSize = sizeId == null || item.item.sizeId == sizeId
            val matchesCategory = catId == null || item.item.categoryId == catId
            val matchesSearch = query.isBlank() || item.item.name.lowercase().contains(query)
            matchesSize && matchesCategory && matchesSearch
        }
    }

    fun setSizeFilter(sizeId: Long?) = _selectedSizeId.postValue(sizeId)
    fun setCategoryFilter(catId: Long?) = _selectedCategoryId.postValue(catId)
    fun setSearchQuery(query: String) {
        _searchQuery.postValue(query)
    }

}