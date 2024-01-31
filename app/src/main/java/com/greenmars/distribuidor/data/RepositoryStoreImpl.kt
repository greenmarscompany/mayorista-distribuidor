package com.greenmars.distribuidor.data

import android.util.Log
import com.greenmars.distribuidor.data.database.dao.CartDao
import com.greenmars.distribuidor.data.database.dao.CartItemDao
import com.greenmars.distribuidor.data.database.entity.Cart
import com.greenmars.distribuidor.data.database.entity.CartItem
import com.greenmars.distribuidor.data.network.StoreApi
import com.greenmars.distribuidor.domain.CartStore
import com.greenmars.distribuidor.domain.CartStoreItem
import com.greenmars.distribuidor.domain.Category
import com.greenmars.distribuidor.domain.Company
import com.greenmars.distribuidor.domain.OrderStore
import com.greenmars.distribuidor.domain.ProductStore
import com.greenmars.distribuidor.domain.RepositoryStore
import javax.inject.Inject

class RepositoryStoreImpl @Inject constructor(
    private val apiService: StoreApi,
    private val cartDao: CartDao,
    private val cartItemDao: CartItemDao,
) : RepositoryStore {
    override suspend fun getCategories(): List<Category>? {
        runCatching {
            apiService.getCategories()
        }.onSuccess { data ->
            return data.map { it.toDomain() }
        }.onFailure {
            Log.i("store", "Ha ocurrido un error ${it.message}")
        }

        return null
    }

    override suspend fun getCompanies(idcategoria: Int): List<Company>? {
        runCatching {
            apiService.getCompanies(idcategoria)
        }.onSuccess { res ->
            return res.data.map { it.toDomain() }
        }.onFailure {
            Log.i("store", "Ha ocurrido un error ${it.message}")
        }

        return null
    }

    override suspend fun getProductsCompany(idcompany: String): List<ProductStore>? {
        runCatching {
            apiService.getProductsCompany(idcompany)
        }.onSuccess { res ->
            return res.data.map { it.toDomain() }
        }.onFailure {
            Log.i("store", "Ha ocurrido un error ${it.message}")
        }

        return null
    }

    override suspend fun getCart(companyId: String): CartStore? {
        runCatching {
            cartDao.getCart(companyId)
        }.onSuccess {
            if (it != null) {

                Log.i("repostore", it.toString())
                return it.toDomain()
            }

            return null
        }.onFailure {
            Log.i("store", "Ha ocurrido un error ${it.message}")
        }

        return null
    }

    override suspend fun getCartItems(cartsId: Long): List<CartStoreItem>? {
        runCatching {
            cartItemDao.getCartItemsForCart(cartsId)
        }.onSuccess { res ->
            return res.map { it.toDomain() }
        }.onFailure {
            Log.i("store", "Ha ocurrido un error ${it.message}")
        }

        return null
    }

    override suspend fun saveCart(cart: CartStore) {

        val cartEntity = Cart(
            clientId = cart.clientId,
            companyId = cart.companyId
        )

        runCatching {
            cartDao.insertCart(cartEntity)
        }.onSuccess { res ->
            Log.i("store", "Se ha guardo correctamente $res")
        }.onFailure {
            Log.i("store", "Ha ocurrido un error ${it.message}")
        }

    }

    override suspend fun saveCartItem(cartItem: CartStoreItem) {
        val itemEntity = CartItem(
            cartId = cartItem.cartId,
            product = cartItem.product,
            unitPrice = cartItem.price,
            quantity = cartItem.quantity,
            image = cartItem.image,
            nameProduct = cartItem.nameProduct
        )

        runCatching {
            cartItemDao.insertCartItem(itemEntity)
        }.onSuccess { res ->
            Log.i("store", "Se ha guardo correctamente $res")
        }.onFailure {
            Log.i("store", "Ha ocurrido un error ${it.message}")
        }
    }

    override suspend fun getItemCount(cartsId: Long): Int {
        runCatching {
            cartItemDao.getCountItem(cartsId)
        }.onSuccess { res ->
            return res;
        }.onFailure {
            Log.i("store", "Ha ocurrido un error ${it.message}")
        }

        return 0
    }

    override suspend fun updateQuantityItem(quantity: Int, itemId: Long) {
        runCatching {
            cartItemDao.updateDataItem(quantity, itemId)
        }.onSuccess { res ->
            Log.i("store", "Se ha actualizado correctamente $res")
        }.onFailure {
            Log.i("store", "Ha ocurrido un error ${it.message}")
        }
    }

    override suspend fun deleteCartItem(itemId: Long) {
        runCatching {
            cartItemDao.deleteCartItem(itemId)
        }.onSuccess { res ->
            Log.i("store", "Se ha eliminado correctamente $res")
        }.onFailure {
            Log.i("store", "Ha ocurrido un error ${it.message}")
        }
    }
}