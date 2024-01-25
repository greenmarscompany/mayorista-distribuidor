package com.greenmars.distribuidor.data.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    private const val CART_DATABASE = "db_cart";

    @Singleton
    @Provides
    fun providesRoom(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, CART_DATABASE)
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideCartDao(db: AppDatabase) = db.getCartDao()

    @Singleton
    @Provides
    fun provideCartItemDao(db: AppDatabase) = db.getCartItemDao()
}