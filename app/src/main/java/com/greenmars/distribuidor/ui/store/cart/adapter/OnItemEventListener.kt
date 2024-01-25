package com.greenmars.distribuidor.ui.store.cart.adapter

import com.greenmars.distribuidor.domain.CartStoreItem

interface OnItemEventListener {

    fun onClickAumentarQuantity(item: CartStoreItem)
    fun onClickDisminuirQuantity(item: CartStoreItem)
    fun onDeleteItem(item: CartStoreItem)

}