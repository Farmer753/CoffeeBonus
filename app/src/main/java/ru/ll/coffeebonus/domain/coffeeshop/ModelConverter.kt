package ru.ll.coffeebonus.domain.coffeeshop

import ru.ll.coffeebonus.domain.CoffeeShop
import ru.ll.coffeebonus.ui.profile.CoffeeShopUiItem

class ModelConverter {

//    fun convert(model: FirestoreCoffeeShop): CoffeeShop {
//        return CoffeeShop(
//            firestoreId = model.firestoreId,
//            id = model.id,
//            name = model.name,
//            address = model.address,
//            longitude = model.longitude,
//            latitude = model.latitude
//        )
//    }

    fun convert(model: CoffeeShop): FirestoreCoffeeShop {
        return FirestoreCoffeeShop(
            firestoreId = model.firestoreId ?: "",
            id = model.id,
            name = model.name,
            address = model.address,
            longitude = model.longitude,
            latitude = model.latitude
        )
    }

    fun convert(model: FirestoreCoffeeShop): CoffeeShopUiItem {
        return CoffeeShopUiItem(
            firestoreId = model.firestoreId,
            id = model.id,
            name = model.name,
            address = model.address,
            longitude = model.longitude,
            latitude = model.latitude
        )
    }

    fun convert(model: CoffeeShopUiItem): CoffeeShop {
        return CoffeeShop(
            firestoreId = model.firestoreId,
            id = model.id,
            name = model.name,
            address = model.address,
            longitude = model.longitude,
            latitude = model.latitude
        )
    }
}