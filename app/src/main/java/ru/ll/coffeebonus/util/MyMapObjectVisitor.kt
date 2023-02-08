package ru.ll.coffeebonus.util

import com.yandex.mapkit.map.*

interface MyMapObjectVisitor: MapObjectVisitor {
    override fun onPlacemarkVisited(p0: PlacemarkMapObject) {
    }

    override fun onPolylineVisited(p0: PolylineMapObject) {
    }

    override fun onPolygonVisited(p0: PolygonMapObject) {
    }

    override fun onCircleVisited(p0: CircleMapObject) {
    }

    override fun onCollectionVisitStart(p0: MapObjectCollection): Boolean {
        return true
    }

    override fun onCollectionVisitEnd(p0: MapObjectCollection) {
    }

    override fun onClusterizedCollectionVisitStart(p0: ClusterizedPlacemarkCollection): Boolean {
        return true
    }

    override fun onClusterizedCollectionVisitEnd(p0: ClusterizedPlacemarkCollection) {
    }
}