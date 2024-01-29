package org.hibernate.bugs;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This template demonstrates how to develop a test case for Hibernate ORM, using the Java Persistence API.
 */
public class JPAUnitTestCase {

	private EntityManagerFactory entityManagerFactory;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

	// Entities are auto-discovered, so just add them anywhere on class-path
	// Add your tests, using standard JUnit.
	@Test
	public void hhh123Test() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		SiteUser user = new SiteUser();

		CommunityProfile profile = new CommunityProfile(user, "myProfile");
		user.setCommunityProfile(profile);

		Wallet wallet = new Wallet(user, "usd");
		user.setWallet(wallet);

		entityManager.persist( wallet );
		entityManager.persist( profile );
		entityManager.persist( user );

		entityManager.flush();

		profile.setProfileName("newProfile");

		// first entity with inheritance
		// generates sub-select join as "... from SITE_USER su1_0 join (select * from PROFILE ..."
		// auto-flush not working for profile anymore
		int sizeInheritance = entityManager.createQuery("select u from SiteUser u inner join u.communityProfile as profile where profile.profileName = 'newProfile'", SiteUser.class).getResultList().size();

		assertEquals(1, sizeInheritance);

		wallet.setWalletName("newWallet");

		// now entity without inheritance
		// generates standard join as "... from SITE_USER su1_0 join WALLET ..."
		int size = entityManager.createQuery("select u from SiteUser u inner join u.wallet as wallet where wallet.walletName = 'newWallet'", SiteUser.class).getResultList().size();

		assertEquals(1, size);

		entityManager.getTransaction().commit();
		entityManager.close();
	}
}
