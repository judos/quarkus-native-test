package ch.judos

import ch.judos.model.Gift
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional


@ApplicationScoped
class GiftService {
	
	@Inject
	lateinit var em: EntityManager
	
	@Transactional
	fun createGift(giftDescription: String) {
		val gift = Gift()
		gift.name = giftDescription
		em.persist(gift)
	}
}
