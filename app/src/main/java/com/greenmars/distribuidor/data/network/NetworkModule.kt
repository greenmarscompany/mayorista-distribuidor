package com.greenmars.distribuidor.data.network

import android.content.Context
import android.util.Log
import com.greenmars.distribuidor.data.RepositoryStoreImpl
import com.greenmars.distribuidor.Variable
import com.greenmars.distribuidor.data.RepositoryImpl
import com.greenmars.distribuidor.data.RepositoryProveedorImpl
import com.greenmars.distribuidor.data.database.dao.CartDao
import com.greenmars.distribuidor.data.database.dao.CartItemDao
import com.greenmars.distribuidor.data.database.dao.ProveedorDao
import com.greenmars.distribuidor.domain.Repository
import com.greenmars.distribuidor.domain.RepositoryProveedor
import com.greenmars.distribuidor.domain.RepositoryStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    @Provides
    @Singleton
    fun providesRetrofit(@ApplicationContext context: Context, ): Retrofit {
        val interceptorL = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            /*.addInterceptor(Interceptor { chain ->
                val sharedPreferences = context.getSharedPreferences("mi_pref", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token", "")
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "jwt $token")
                    .build()
                chain.proceed(newRequest)
            })*/
            .authenticator(TokenAuthenticator(context))
            .addInterceptor(interceptorL)
            .build()

        return Retrofit
            .Builder()
            .client(client)
            .baseUrl(Variable.HOST_RETROFIT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    @Provides
    fun provideFabricanteApi(retrofit: Retrofit): FabricanteApi {
        return retrofit.create(FabricanteApi::class.java)
    }

    @Provides
    fun provideRepository(apiService: FabricanteApi): Repository {
        return RepositoryImpl(apiService)
    }

    @Provides
    fun provideStoreApi(retrofit: Retrofit): StoreApi {
        return retrofit.create(StoreApi::class.java)
    }

    @Provides
    fun provideRepositoryStore(
        apiService: StoreApi,
        cartDao: CartDao,
        cartItemDao: CartItemDao
    ): RepositoryStore {
        return RepositoryStoreImpl(apiService, cartDao, cartItemDao)
    }

    @Provides
    fun provideProveedorApi(retrofit: Retrofit): ProveedorApi {
        return retrofit.create(ProveedorApi::class.java)
    }

    @Provides
    fun provideRepositoryProveedor(
        apiService: ProveedorApi,
        database: ProveedorDao,
    ): RepositoryProveedor {
        return RepositoryProveedorImpl(apiService, database)
    }

}