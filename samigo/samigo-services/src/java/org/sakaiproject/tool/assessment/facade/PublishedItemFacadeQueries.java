/**
 * Copyright (c) 2005-2017 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.tool.assessment.facade;

import static org.sakaiproject.tool.assessment.facade.ItemHashUtil.ALL_HASH_BACKFILLABLE_ITEM_IDS_HQL;
import static org.sakaiproject.tool.assessment.facade.ItemHashUtil.ID_PARAMS_PLACEHOLDER;
import static org.sakaiproject.tool.assessment.facade.ItemHashUtil.ITEMS_BY_ID_HQL;
import static org.sakaiproject.tool.assessment.facade.ItemHashUtil.TOTAL_HASH_BACKFILLABLE_ITEM_COUNT_HQL;
import static org.sakaiproject.tool.assessment.facade.ItemHashUtil.TOTAL_ITEM_COUNT_HQL;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedAnswer;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemAttachment;
import org.sakaiproject.tool.assessment.data.dao.assessment.PublishedItemData;
import org.sakaiproject.tool.assessment.data.ifc.assessment.ItemDataIfc;
import org.sakaiproject.tool.assessment.integration.helper.ifc.TagServiceHelper;
import org.sakaiproject.tool.assessment.osid.shared.impl.IdImpl;
import org.sakaiproject.tool.assessment.services.PersistenceService;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublishedItemFacadeQueries extends HibernateDaoSupport implements
		PublishedItemFacadeQueriesAPI {

	private ItemHashUtil itemHashUtil;

	public void setItemHashUtil(ItemHashUtil itemHashUtil) {
		this.itemHashUtil = itemHashUtil;
	}

	public IdImpl getItemId(String id) {
		return new IdImpl(id);
	}

	public IdImpl getItemId(Long id) {
		return new IdImpl(id);
	}

	public IdImpl getItemId(long id) {
		return new IdImpl(id);
	}

	public PublishedItemFacade getItem(Long itemId, String agent) {
		PublishedItemData item = (PublishedItemData) getHibernateTemplate()
				.load(PublishedItemData.class, itemId);
		return new PublishedItemFacade(item);
	}
	
	public PublishedItemFacade getItem(String itemId) {
		PublishedItemData item = (PublishedItemData) getHibernateTemplate()
				.load(PublishedItemData.class, Long.valueOf(itemId));
		return new PublishedItemFacade(item);
	}

	public Boolean itemExists(String itemId) {
		try {
			if (getHibernateTemplate().get(PublishedItemData.class,  Long.valueOf(itemId))==null){
				return false;
			}else{
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	public Map<String, ItemFacade> getPublishedItemsByHash(String hash) {
		final HibernateCallback<List<PublishedItemData>> hcb = session -> {
				Query q = session.createQuery("from PublishedItemData where hash = ? ");
				q.setString(0, hash);
				return q.list();
		};
		List<PublishedItemData> list1 = getHibernateTemplate().execute(hcb);

		Map<String, ItemFacade> itemFacadeMap = new HashMap();

		for (int i = 0; i < list1.size(); i++) {
			PublishedItemData a = (PublishedItemData) list1.get(i);
			ItemFacade f = new ItemFacade(a);
			itemFacadeMap.put(f.getItemIdString(),f);
		}
		return itemFacadeMap;
	}

	public void deleteItemContent(Long itemId, String agent) {
		PublishedItemData item = (PublishedItemData) getHibernateTemplate().load(PublishedItemData.class,
				itemId);

		int retryCount = PersistenceService.getInstance().getPersistenceHelper().getRetryCount()
				.intValue();
		while (retryCount > 0) {
			try {
				if (item != null) { // need to dissociate with item before deleting in Hibernate 3
					Set set = item.getItemTextSet();
					item.setItemTextSet(new HashSet());
					getHibernateTemplate().deleteAll(set);
					retryCount = 0;
				} else
					retryCount = 0;
			} catch (Exception e) {
				log.warn("problem deleteItemTextSet: " + e.getMessage());
				retryCount = PersistenceService.getInstance().getPersistenceHelper().retryDeadlock(e,
						retryCount);
			}
		}

		retryCount = PersistenceService.getInstance().getPersistenceHelper().getRetryCount()
				.intValue();
		while (retryCount > 0) {
			try {
				if (item != null) { // need to dissociate with item before deleting in Hibernate 3
					Set set = item.getItemMetaDataSet();
					item.setItemMetaDataSet(new HashSet());
					getHibernateTemplate().deleteAll(set);
					retryCount = 0;
				} else
					retryCount = 0;
			} catch (Exception e) {
				log.warn("problem deleteItemMetaDataSet: " + e.getMessage());
				retryCount = PersistenceService.getInstance().getPersistenceHelper().retryDeadlock(e,
						retryCount);
			}
		}

		retryCount = PersistenceService.getInstance().getPersistenceHelper().getRetryCount()
				.intValue();
		while (retryCount > 0) {
			try {
				if (item != null) { // need to dissociate with item before deleting in Hibernate 3
					Set set = item.getItemFeedbackSet();
					item.setItemFeedbackSet(new HashSet());
					getHibernateTemplate().deleteAll(set);
					retryCount = 0;
				} else
					retryCount = 0;
			} catch (Exception e) {
				log.warn("problem deleting ItemFeedbackSet: " + e.getMessage());
				retryCount = PersistenceService.getInstance().getPersistenceHelper().retryDeadlock(e,
						retryCount);
			}
		}
	}

	@Override
	public void updateItemTagBindingsHavingTag(TagServiceHelper.TagView tagView) {
		// TODO when we add item search indexing, this is going to have to change to
		// first read in all the affected item IDs so we can generate events for each
		// (similar to what we do in the tag service)
		getHibernateTemplate().bulkUpdate("update PublishedItemTag it " +
				"set it.tagLabel = ?, it.tagCollectionId = ?, it.tagCollectionName = ? " +
				"where it.tagId = ?",
				tagView.tagLabel, tagView.tagCollectionId, tagView.tagCollectionName, tagView.tagId);
	}

	@Override
	public void deleteItemTagBindingsHavingTagId(String tagId) {
		// TODO when we add item search indexing, this is going to have to change to
		// first read in all the affected item IDs so we can generate events for each
		// (similar to what we do in the tag service)
		getHibernateTemplate().bulkUpdate("delete PublishedItemTag it where it.tagId = ?", tagId);
	}

	@Override
	public void updateItemTagBindingsHavingTagCollection(TagServiceHelper.TagCollectionView tagCollectionView) {
		// TODO when we add item search indexing, this is going to have to change to
		// first read in all the affected item IDs so we can generate events for each
		// (similar to what we do in the tag service)
		getHibernateTemplate().bulkUpdate("update PublishedItemTag it " +
						"set it.tagCollectionName = ? " +
						"where it.tagCollectionId = ?",
				tagCollectionView.tagCollectionName, tagCollectionView.tagCollectionId);
	}

	@Override
	public void deleteItemTagBindingsHavingTagCollectionId(String tagCollectionId) {
		// TODO when we add item search indexing, this is going to have to change to
		// first read in all the affected item IDs so we can generate events for each
		// (similar to what we do in the tag service)
		getHibernateTemplate().bulkUpdate("delete PublishedItemTag it where it.tagCollectionId = ?", tagCollectionId);
	}

	private static final Map<String,String> BACKFILL_ALL_HASHES_HQL = new HashMap<String,String>() {{
		this.put(TOTAL_ITEM_COUNT_HQL, "select count(*) from PublishedItemData");
		this.put(TOTAL_HASH_BACKFILLABLE_ITEM_COUNT_HQL, "select count(*) from PublishedItemData as item where item.hash is null or item.itemHash is null");
		this.put(ALL_HASH_BACKFILLABLE_ITEM_IDS_HQL, "select item.id from PublishedItemData as item where item.hash is null or item.itemHash is null");
		this.put(ITEMS_BY_ID_HQL, "select item from PublishedItemData as item where item.id in (" + ID_PARAMS_PLACEHOLDER + ")");
	}};

	private static final Map<String,String> BACKFILL_CURRENT_HASHES_HQL = new HashMap<String,String>() {{
		this.put(TOTAL_ITEM_COUNT_HQL, "select count(*) from PublishedItemData");
		this.put(TOTAL_HASH_BACKFILLABLE_ITEM_COUNT_HQL, "select count(*) from PublishedItemData as item where item.hash is null");
		this.put(ALL_HASH_BACKFILLABLE_ITEM_IDS_HQL, "select item.id from PublishedItemData as item where item.hash is null");
		this.put(ITEMS_BY_ID_HQL, "select item from PublishedItemData as item where item.id in (" + ID_PARAMS_PLACEHOLDER + ")");
	}};

	@Override
	public BackfillItemHashResult backfillItemHashes(int batchSize, boolean backfillBaselineHashes) {
		return itemHashUtil.backfillItemHashes(
				batchSize,
				backfillBaselineHashes ? BACKFILL_ALL_HASHES_HQL : BACKFILL_CURRENT_HASHES_HQL,
				PublishedItemData.class,
				i -> {
					final String hash = itemHashUtil.hashItemUnchecked(i);
					if ( StringUtils.isEmpty(i.getHash()) ) {
						i.setHash(hash);
					}
					if ( backfillBaselineHashes ) {
						if (StringUtils.isEmpty(((PublishedItemData) i).getItemHash())) {
							((PublishedItemData) i).setItemHash(hash);
						}
					}
					return i;
				},
				getHibernateTemplate());
	}

	@Override
	public List<Long> getPublishedItemsIdsByHash(String hash) {
		final HibernateCallback<List<Long>> hcb = session -> {
				Query q = session.createQuery("select ab.itemId from PublishedItemData ab where ab.hash = ? ");
					q.setString(0, hash);
				return q.list();
		};
		List<Long> list1 = getHibernateTemplate().execute(hcb);
		return list1;
	}

	@Override
	public Long getPublishedAssessmentId(Long itemId) {
		final HibernateCallback<List<Long>> hcb = session -> {
			Query q = session.createQuery("select s.assessment.publishedAssessmentId from PublishedSectionData s, PublishedItemData i where s.id = i.section AND i.itemId = ?");
			q.setLong(0, itemId);
			return q.list();
		};
		List<Long> list1 = getHibernateTemplate().execute(hcb);
		if (list1.isEmpty()) {
			return -1L;
		} else {
			return (Long) list1.get(0);
		}
	}

	@Override
 	public void removeItemAttachment(Long itemAttachmentId) {
		PublishedItemAttachment itemAttachment = getHibernateTemplate().load(PublishedItemAttachment.class, itemAttachmentId);
		ItemDataIfc item = itemAttachment.getItem();
		int retryCount = PersistenceService.getInstance().getPersistenceHelper().getRetryCount();
		while (retryCount > 0) {
			try {
				if (item != null) {
					Set set = item.getItemAttachmentSet();
					set.remove(itemAttachment);
					getHibernateTemplate().delete(getHibernateTemplate().merge(itemAttachment));
					retryCount = 0;
				}
			} catch (Exception e) {
				log.warn("Error while trying to delete PublishedItemAttachment: " + e.getMessage());
				retryCount = PersistenceService.getInstance().getPersistenceHelper().retryDeadlock(e, retryCount);
			}
		}
	}

	// For module TOEIC
    @Override
    public PublishedAnswer getPublishedAnswer(Long publishedAnswerId) {
        PublishedAnswer answer = null;
        
        final HibernateCallback<List<PublishedAnswer>> hcb = session -> {
            Query q = session.createQuery(
                    "from PublishedAnswer p where p.id = :id");
            q.setLong("id", publishedAnswerId);
            
            return q.list();
        };
        List<PublishedAnswer> answers = getHibernateTemplate().execute(hcb);

        if (!answers.isEmpty()) {
            answer = answers.get(0);
        }
        
        return answer;
    }
    
    /**
     * [Explain the description for this method here].
     * @param publishedAnswerIds
     * @return
     * @see org.sakaiproject.tool.assessment.facade.PublishedItemFacadeQueriesAPI#getPublishedAnswers(java.util.List)
     * @aut
     */
    @Override
    public List<PublishedAnswer> getPublishedAnswers(List<Long> publishedAnswerIds) {
        List<PublishedAnswer> answers = null;
        
        final HibernateCallback<List<PublishedAnswer>> hcb = session -> {
            Query q = session.createQuery(
                    "from PublishedAnswer p where p.id IN (:ids)");
            q.setParameterList("ids", publishedAnswerIds);
            
            return q.list();
        };
        answers = getHibernateTemplate().execute(hcb);

        return answers;
    }
    
   @Override
    public PublishedAnswer getCorrectPublishedAnswerByItemId(Long publishedItemId) {
        PublishedAnswer answer = null;
        
        final HibernateCallback<List<PublishedAnswer>> hcb = session -> {
            Query q = session.createQuery(
                    "from PublishedAnswer p where p.item.itemId = :id AND p.isCorrect is true");
            q.setLong("id", publishedItemId);
            
            return q.list();
        };
        List<PublishedAnswer> answers = getHibernateTemplate().execute(hcb);

        if (!answers.isEmpty()) {
            answer = answers.get(0);
        }
        
        return answer;
    }
   
   /**
   * [Explain the description for this method here].
   * @param publishedItemId
   * @return
   * @see org.sakaiproject.tool.assessment.facade.PublishedItemFacadeQueriesAPI#getCorrectPublishedAnswerIdByItemId(java.lang.Long)
   * @author NamTang
   */
   @Override
   public Long getCorrectPublishedAnswerIdByItemId(Long publishedItemId) {
       Long result = null;
       
       final HibernateCallback<List<Long>> hcb = session -> {
           Query q = session.createQuery(
                   "select p.id from PublishedAnswer p where p.item.itemId = :id AND p.isCorrect is true");
           q.setLong("id", publishedItemId);
           
           return q.list();
       };
       List<Long> answerIds = getHibernateTemplate().execute(hcb);

       if (!answerIds.isEmpty()) {
           result = answerIds.get(0);
       }
       
       return result;
   }
   
   /**
   * [Explain the description for this method here].
   * @param publishedItemIds
   * @return
   * @see org.sakaiproject.tool.assessment.facade.PublishedItemFacadeQueriesAPI#getCorrectPublishedAnswerByItemIds(java.util.List)
   * @author ThachLN
   */
    @Override
    public List<PublishedAnswer> getCorrectPublishedAnswerByItemIds(List<Long> publishedItemIds) {
        List<PublishedAnswer> answers;

        final HibernateCallback<List<PublishedAnswer>> hcb = session -> {
            Query q = session.createQuery("from PublishedAnswer p where p.item.itemId IN (:ids) AND p.isCorrect is true");
            q.setParameterList("ids", publishedItemIds);

            return q.list();
        };
        answers = getHibernateTemplate().execute(hcb);

        return answers;
    }
    
    @Override
    public List<Long> getAssessmentCorrectPublishedAnswerIds(Long publishedAssessmentId) {
        
        final HibernateCallback<List<Long>> hcb = session -> {
            Query q = session.createQuery(
                    "select a.id from PublishedAnswer a, PublishedSectionData s, PublishedItemData i" 
                            + " where s.id = i.section AND i.itemId = a.item AND s.assessment.publishedAssessmentId = :id AND a.isCorrect = true");
            q.setLong("id", publishedAssessmentId);
            
            return q.list();
        };
        List<Long> answers = getHibernateTemplate().execute(hcb);
        
        return answers;
    }
}
