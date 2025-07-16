package com.usedocetangerinaestoque.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Size::class,
            parentColumns = ["id"],
            childColumns = ["sizeId"],
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
        ),
    ],
    indices = [
        Index(value = ["sizeId"]),
        Index(value = ["categoryId"])
    ]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var active: Boolean = true,
    var createdOn: Date = Date(System.currentTimeMillis()),
    var name: String = "",
    var description: String = "",
    var value: Double = 0.00,
    var quantity: Int = 0,
    var sizeId: Long = 0L,
    var categoryId: Long = 0L,
)
