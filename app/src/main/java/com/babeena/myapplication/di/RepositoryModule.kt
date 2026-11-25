package com.babeena.myapplication.di

import com.babeena.myapplication.data.repository.BookmarkRepositoryIml
import com.babeena.myapplication.data.repository.EventRepositoryImpl
import com.babeena.myapplication.domain.repository.BookMarkRepository
import com.babeena.myapplication.domain.repository.EventRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        implementation: EventRepositoryImpl
    ): EventRepository

    @Binds
    @Singleton
    abstract fun bindBookMarkRepository(
        implementation: BookmarkRepositoryIml
    ): BookMarkRepository

}