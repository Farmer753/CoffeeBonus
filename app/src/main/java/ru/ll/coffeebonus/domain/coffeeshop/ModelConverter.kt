package ru.ll.coffeebonus.domain.coffeeshop

import ru.ll.coffeebonus.domain.CoffeeShop

class ModelConverter {

    fun convert(model: FirestoreCoffeeShop): CoffeeShop {
        return CoffeeShop(
            id = model.id,
            name = model.name,
            address = model.address,
            longitude = model.longitude,
            latitude = model.latitude
        )
    }

    fun convert(model: CoffeeShop): FirestoreCoffeeShop {
        return FirestoreCoffeeShop(
            id = model.id,
            name = model.name,
            address = model.address,
            longitude = model.longitude,
            latitude = model.latitude
        )
    }
}