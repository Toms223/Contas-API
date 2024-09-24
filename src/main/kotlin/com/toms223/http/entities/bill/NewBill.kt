package com.toms223.http.entities.bill

import kotlinx.datetime.LocalDate
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Period

@Serializable
data class NewBill(val name: String, val date: LocalDate, val continuous: Boolean, @Serializable(PeriodSerializer::class) val period: Period, val accountId: Int)

object PeriodSerializer : KSerializer<Period> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Period", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Period {
        decoder.decodeString().let {
            return Period.parse(it)
        }
    }

    override fun serialize(encoder: Encoder, value: Period) {
        return encoder.encodeString(value.toString())
    }

}