package com.usedocetangerinaestoque.di

import com.usedocetangerinaestoque.data.dao.ItemDao
import com.usedocetangerinaestoque.data.dao.UserDao
import com.usedocetangerinaestoque.services.PasswordEncripter
import com.usedocetangerinaestoque.usecases.item.AddNewItemUseCase
import com.usedocetangerinaestoque.usecases.user.RegisterUserUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCasesModule {
    @Provides
    fun provideRegisterUserUseCase(
        userDao: UserDao,
        passwordEncripter: PasswordEncripter
    ): RegisterUserUseCase {
        return RegisterUserUseCase(userDao, passwordEncripter)
    }

//    @Provides
//    fun provideAddNewItemUseCase(itemDao: ItemDao): AddNewItemUseCase {
//        return AddNewItemUseCase(itemDao)
//    }
}