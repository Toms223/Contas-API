package data.db.entities


import org.ktorm.entity.Entity
import java.time.LocalDate
import java.time.Period

interface Bill: Entity<Bill> {
    companion object : Entity.Factory<Bill>()
    val id: Int
    var account: Account
    var name: String
    var date: LocalDate
    var continuous: Boolean
    var period: Period
    var paid: Boolean

    fun changeName(newName: String){
        name = newName
        this.flushChanges()
    }

    fun changeDate(newDate: LocalDate){
        date = newDate
        this.flushChanges()
    }

    fun continuous(){
        continuous = true
        this.flushChanges()
    }

    fun discontinuous(){
        continuous = false
        this.flushChanges()
    }

    fun pay(){
        paid = true
        if (continuous) {
            date = date.plus(Period.ofMonths(1))
        }
        this.flushChanges()
    }

    fun unpay(){
        paid = false
        this.flushChanges()
    }
}