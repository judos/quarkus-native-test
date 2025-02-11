package ch.judos.model

import jakarta.persistence.*

@Entity
class Formula {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	var id: Long = 0
	
	lateinit var name: String
	
	@Column(columnDefinition = "TEXT")
	lateinit var formula: String
}
