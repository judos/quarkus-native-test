package ch.judos.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Gift {
	@GeneratedValue()
	@Id
	var id: Long = 0
	
	lateinit var name: String
}
