package com.usedocetangerinaestoque.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var active: Boolean = true,
    var createdOn: Date = Date(System.currentTimeMillis()),
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var userIdentifier: UUID = UUID.randomUUID()
)
