package com.sap.hana.cloud.samples.benefits.persistence;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hana.cloud.samples.benefits.persistence.manager.EntityManagerProvider;
import com.sap.hana.cloud.samples.benefits.persistence.model.BenefitInfo;
import com.sap.hana.cloud.samples.benefits.persistence.model.BenefitType;

@SuppressWarnings("nls")
public class BenefitDAO extends BasicDAO<BenefitInfo> {

	private final Logger logger = LoggerFactory.getLogger(BenefitDAO.class);

	public BenefitDAO() {
		super(EntityManagerProvider.getInstance());
	}

	@Override
	public BenefitInfo save(BenefitInfo benefitInfo) {
		BenefitInfo existingBenefit = getByName(benefitInfo.getName());
		if (existingBenefit == null) {
			saveNew(benefitInfo);
			return benefitInfo;
		}

		final BenefitTypeDAO benefitTypeDAO = new BenefitTypeDAO();
		final Collection<BenefitType> types = benefitInfo.getTypes();
		for (BenefitType benefitType : types) {
			benefitType.setBenefitInfo(existingBenefit);
			benefitTypeDAO.saveNew(benefitType);
		}

		return existingBenefit;
	}

	private BenefitInfo getByName(String name) {
		BenefitInfo benefitInfo = null;
		final EntityManager em = emProvider.get();
		try {
			final Query query = em.createQuery("select b from BenefitInfo b where b.name = :name");
			query.setParameter("name", name);

			benefitInfo = (BenefitInfo) query.getSingleResult();
		} catch (NoResultException e) {
			logger.warn("Could not retrieve entity {} from table {}.  Maybe the benefit info doesn't exist yet.", name, "Benefits info");
		} catch (NonUniqueResultException e) {
			throw new IllegalStateException(String.format("More than one entity %s from table Benefits info.", name)); //$NON-NLS-1$
		}

		return benefitInfo;
	}

}
