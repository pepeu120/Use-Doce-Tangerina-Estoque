package com.usedocetangerinaestoque.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.usedocetangerinaestoque.data.entities.Category
import com.usedocetangerinaestoque.data.entities.Image
import com.usedocetangerinaestoque.data.entities.Item
import com.usedocetangerinaestoque.data.entities.Size
import com.usedocetangerinaestoque.data.relations.ItemFull
import com.usedocetangerinaestoque.exceptions.ItemAlreadyExistsException
import com.usedocetangerinaestoque.usecases.category.AddCategoryUseCase
import com.usedocetangerinaestoque.usecases.category.GetAllCategoriesUseCase
import com.usedocetangerinaestoque.usecases.item.AddNewItemUseCase
import com.usedocetangerinaestoque.usecases.item.GetItemFullByIdUseCase
import com.usedocetangerinaestoque.usecases.item.UpdateItemUseCase
import com.usedocetangerinaestoque.usecases.size.AddSizeUseCase
import com.usedocetangerinaestoque.usecases.size.GetAllSizesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val addNewItemUseCase: AddNewItemUseCase,
    private val getAllSizesUseCase: GetAllSizesUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val addSizeUseCase: AddSizeUseCase,
    private val getItemFullByIdUseCase: GetItemFullByIdUseCase,
    private val updateItemUseCase: UpdateItemUseCase
) : ViewModel() {
    val sizes = getAllSizesUseCase().asLiveData()
    val categories = getAllCategoriesUseCase().asLiveData()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _editItem = MutableLiveData<ItemFull?>()
    val editItem: LiveData<ItemFull?> get() = _editItem

    fun loadItemForEdit(itemId: Long) {
        viewModelScope.launch {
            try {
                val item = getItemFullByIdUseCase(itemId)
                _editItem.postValue(item)
            } catch (e: Exception) {
                _error.postValue("Erro ao carregar item para edição.")
            }
        }
    }

    fun saveItem(
        name: String,
        description: String,
        value: Double,
        quantity: Int,
        sizeId: Long,
        categoryId: Long,
        imagePaths: List<String>
    ) {
        viewModelScope.launch {
            if (name.isBlank() || description.isBlank()) {
                _error.value = "Nome e descrição não podem ficar em branco."
                return@launch
            }

            _loading.value = true
            _error.value = null

            try {
                val item = Item(
                    name = name,
                    description = description,
                    value = value,
                    quantity = quantity,
                    sizeId = sizeId,
                    categoryId = categoryId)

                val images = imagePaths.map { path ->
                    Image(path = path)
                }

                val itemFull = ItemFull(item = item, images = images)

                addNewItemUseCase(itemFull)
                _saveStatus.value = true
            } catch (e: Exception) {
                _error.value = e.message ?: "Erro ao salvar item."
                _saveStatus.value = false
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateItem(
        id: Long,
        name: String,
        description: String,
        value: Double,
        quantity: Int,
        sizeId: Long,
        categoryId: Long,
        imagePaths: List<String>
    ) {
        viewModelScope.launch {
            try {
                val item = Item(
                    id = id,
                    name = name,
                    description = description,
                    value = value,
                    quantity = quantity,
                    sizeId = sizeId,
                    categoryId = categoryId)

                val images = imagePaths.map { path ->
                    Image(path = path)
                }

                val itemFull = ItemFull(item = item, images = images)

                updateItemUseCase(itemFull)
                _saveStatus.postValue(true)
            } catch (ex: ItemAlreadyExistsException) {
                _error.postValue(ex.message)
            }
            catch (ex: Exception) {
                _error.postValue("Erro ao atualizar item.")
            }
        }
    }

    fun addSize(name: String) = viewModelScope.launch {
        if (name.isBlank()) {
            _error.value = "Nome do tamanho não pode ficar em branco."
            return@launch
        }
        try {
            addSizeUseCase(Size(name = name))
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao adicionar tamanho."
        }
    }

    fun addCategory(name: String) = viewModelScope.launch {
        if (name.isBlank()) {
            _error.value = "Nome da categoria não pode ficar em branco."
            return@launch
        }
        try {
            addCategoryUseCase(Category(name = name))
        } catch (e: Exception) {
            _error.value = e.message ?: "Erro ao adicionar categoria."
        }
    }

    fun clearError() {
        _error.value = null
    }
}