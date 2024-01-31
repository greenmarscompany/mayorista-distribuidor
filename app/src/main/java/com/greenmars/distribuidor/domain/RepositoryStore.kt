package com.greenmars.distribuidor.domain

import com.greenmars.distribuidor.data.database.entity.Cart

interface RepositoryStore {

    suspend fun getCategories(): List<Category>?
    suspend fun getCompanies(idcategoria: Int): List<Company>?
    suspend fun getProductsCompany(idcompany: String): List<ProductStore>?

    suspend fun getCart(companyId: String): CartStore?
    suspend fun getCartItems(cartsId: Long): List<CartStoreItem>?

    suspend fun saveCart(cart: CartStore)
    suspend fun saveCartItem(cartItem: CartStoreItem)
    suspend fun getItemCount(cartsId: Long): Int
    suspend fun updateQuantityItem(quantity: Int, itemId: Long)
    suspend fun deleteCartItem(itemId: Long)
}